/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ServicoCadastro.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.negocio;

import br.com.urna.excecao.RegraNegocioException;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FabricaCandidato;
import br.com.urna.modelo.candidato.FichaCandidato;
import br.com.urna.persistencia.RepositorioCandidato;
import br.com.urna.persistencia.RepositorioPartido;
import java.util.Locale;

/**
 * Cadastro de partidos e candidatos. Valida as regras RN02 a RN07 antes de
 * gravar, recarrega o catálogo depois de cada mudança e impede excluir partido
 * que ainda tem candidatos. Tem acesso de pacote.
 *
 * @author João
 * @since 1.0
 */
final class ServicoCadastro {

    static final int TAMANHOMAXIMOFOTO = 200 * 1024;

    private static final int TAMANHOMAXIMOSIGLA = 10;

    private static final int TAMANHOMAXIMONOME = 120;

    private final RepositorioPartido repositorioPartido;

    private final RepositorioCandidato repositorioCandidato;

    private final CatalogoEleitoral catalogoEleitoral;

    ServicoCadastro(RepositorioPartido repositorioPartido, RepositorioCandidato repositorioCandidato,
                    CatalogoEleitoral catalogoEleitoral) {
        this.repositorioPartido = repositorioPartido;
        this.repositorioCandidato = repositorioCandidato;
        this.catalogoEleitoral = catalogoEleitoral;
    }

    // ---- Catálogo ----

    void recarregarCatalogo() {
        catalogoEleitoral.carregar(repositorioPartido.listarTodos(), repositorioCandidato.listarTodos());
    }

    // ---- Partidos ----

    void salvarPartido(Partido partido) {
        if (partido == null) {
            throw new RegraNegocioException("Informe o partido.");
        }
        exigirTamanho(partido.getSigla(), TAMANHOMAXIMOSIGLA, "A sigla");
        exigirTamanho(partido.getNome(), TAMANHOMAXIMONOME, "O nome do partido");
        for (Partido outro : catalogoEleitoral.listarPartidos()) {
            if (outro.getIdentificador() == partido.getIdentificador()) {
                exigirNumeroEstavel(outro, partido);
                continue;
            }
            if (outro.getNumero() == partido.getNumero()) {
                throw new RegraNegocioException("Já existe partido com o número " + partido.getNumero() + " (RN03).");
            }
            if (outro.getSigla().toUpperCase(Locale.ROOT).equals(partido.getSigla().toUpperCase(Locale.ROOT))) {
                throw new RegraNegocioException("Já existe partido com a sigla " + partido.getSigla() + ".");
            }
        }
        repositorioPartido.salvar(partido);
        recarregarCatalogo();
    }

    void excluirPartido(Partido partido) {
        if (partido == null) {
            throw new RegraNegocioException("Selecione o partido a excluir.");
        }
        if (catalogoEleitoral.possuiCandidatos(partido)) {
            throw new RegraNegocioException("O partido " + partido.getSigla()
                    + " ainda tem candidatos. Exclua os candidatos antes (RN02).");
        }
        repositorioPartido.excluir(partido);
        recarregarCatalogo();
    }

    private void exigirNumeroEstavel(Partido gravado, Partido alterado) {
        if (gravado.getNumero() != alterado.getNumero() && catalogoEleitoral.possuiCandidatos(gravado)) {
            throw new RegraNegocioException("Não é possível trocar o número de um partido que já tem candidatos (RN04).");
        }
    }

    // ---- Candidatos ----

    void salvarCandidato(FichaCandidato ficha) {
        if (ficha == null) {
            throw new RegraNegocioException("Informe os dados do candidato.");
        }
        Partido partido = buscarPartidoCadastrado(ficha.getPartido());
        Candidato candidato = criarCandidato(new FichaCandidato.Montador(ficha.getCargo())
                .definirIdentificador(ficha.getIdentificador())
                .definirNome(ficha.getNome())
                .definirNumero(ficha.getNumero())
                .definirPartido(partido)
                .definirFoto(ficha.getFoto())
                .definirNomeVice(ficha.getNomeVice())
                .definirNomesDosSuplentes(ficha.getNomePrimeiroSuplente(), ficha.getNomeSegundoSuplente())
                .montar());
        validarFoto(candidato.getFoto());
        exigirTamanho(candidato.getNome(), TAMANHOMAXIMONOME, "O nome do candidato");
        exigirNumeroLivreNoCargo(candidato);
        repositorioCandidato.salvar(candidato);
        recarregarCatalogo();
    }

    void excluirCandidato(Candidato candidato) {
        if (candidato == null) {
            throw new RegraNegocioException("Selecione o candidato a excluir.");
        }
        repositorioCandidato.excluir(candidato);
        recarregarCatalogo();
    }

    private Candidato criarCandidato(FichaCandidato ficha) {
        try {
            return FabricaCandidato.criar(ficha);
        } catch (IllegalArgumentException erro) {
            throw new RegraNegocioException(erro.getMessage(), erro);
        }
    }

    private Partido buscarPartidoCadastrado(Partido partido) {
        if (partido == null) {
            throw new RegraNegocioException("Escolha o partido do candidato (RN02).");
        }
        return catalogoEleitoral.listarPartidos().stream()
                .filter(cadastrado -> cadastrado.getIdentificador() == partido.getIdentificador())
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException("O partido " + partido + " não está cadastrado (RN02)."));
    }

    private void exigirNumeroLivreNoCargo(Candidato candidato) {
        catalogoEleitoral.buscarCandidato(candidato.getCargo(), candidato.getNumero())
                .filter(existente -> existente.getIdentificador() != candidato.getIdentificador())
                .ifPresent(existente -> {
                    throw new RegraNegocioException("Já existe candidato a " + candidato.getCargo().getDescricao()
                            + " com o número " + candidato.getNumero() + ": " + existente.getNome() + " (RN05).");
                });
    }

    // ---- Validações de formato ----

    static void validarFoto(byte[] foto) {
        if (foto == null || foto.length == 0) {
            throw new RegraNegocioException("A foto do candidato é obrigatória (RN06).");
        }
        if (foto.length > TAMANHOMAXIMOFOTO) {
            throw new RegraNegocioException("A foto tem " + (foto.length / 1024)
                    + " KB; o limite é 200 KB (RN06).");
        }
        boolean jpeg = foto.length > 3 && (foto[0] & 0xFF) == 0xFF && (foto[1] & 0xFF) == 0xD8
                && (foto[2] & 0xFF) == 0xFF;
        boolean png = foto.length > 4 && (foto[0] & 0xFF) == 0x89 && foto[1] == 'P' && foto[2] == 'N'
                && foto[3] == 'G';
        if (!jpeg && !png) {
            throw new RegraNegocioException("A foto deve ser JPEG ou PNG (RN06).");
        }
    }

    private static void exigirTamanho(String texto, int tamanhoMaximo, String descricao) {
        if (texto.length() > tamanhoMaximo) {
            throw new RegraNegocioException(descricao + " pode ter no máximo " + tamanhoMaximo + " caracteres.");
        }
    }
}
