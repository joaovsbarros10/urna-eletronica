/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: FachadaUrna.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.negocio;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FichaCandidato;
import br.com.urna.modelo.sessao.SessaoVotacao;
import br.com.urna.modelo.voto.ApuracaoCargo;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.persistencia.RepositorioCandidato;
import br.com.urna.persistencia.RepositorioPartido;
import br.com.urna.persistencia.RepositorioVoto;
import java.util.List;

/**
 * Única porta de entrada da camada de negócio (padrão Fachada, item 2.0 do checklist).
 * <p>
 * Recebe os repositórios no construtor e cria os serviços por dentro. Como os
 * serviços têm acesso de pacote, a visão e o controle só conseguem falar com
 * esta classe. Os métodos que acessam o banco podem demorar: chame-os fora da
 * thread de eventos do Swing (RNF06). Erros chegam como
 * {@link br.com.urna.excecao.UrnaException}.
 *
 * @author João
 * @since 1.0
 */
public class FachadaUrna {

    private final CatalogoEleitoral catalogoEleitoral = new CatalogoEleitoral();

    private final ServicoVotacao servicoVotacao;

    private final ServicoCadastro servicoCadastro;

    private final ServicoApuracao servicoApuracao;

    /**
     * Monta a camada de negócio sobre os repositórios informados (Supabase ou memória).
     *
     * @param repositorioPartido   repositório de partidos
     * @param repositorioCandidato repositório de candidatos
     * @param repositorioVoto      repositório de votos
     * @throws IllegalArgumentException se algum repositório for {@code null}
     */
    public FachadaUrna(RepositorioPartido repositorioPartido, RepositorioCandidato repositorioCandidato,
                       RepositorioVoto repositorioVoto) {
        if (repositorioPartido == null || repositorioCandidato == null || repositorioVoto == null) {
            throw new IllegalArgumentException("A fachada precisa dos três repositórios.");
        }
        this.servicoVotacao = new ServicoVotacao(repositorioVoto);
        this.servicoCadastro = new ServicoCadastro(repositorioPartido, repositorioCandidato, catalogoEleitoral);
        this.servicoApuracao = new ServicoApuracao(repositorioVoto, catalogoEleitoral);
    }

    // ---- Votação ----

    /**
     * Lê partidos, candidatos e fotos do banco e guarda tudo em memória. Rode
     * uma vez na abertura, fora da thread de eventos.
     *
     * @throws br.com.urna.excecao.PersistenciaException se o banco não responder
     */
    public void carregarDadosEleitorais() {
        servicoCadastro.recarregarCatalogo();
    }

    /**
     * Começa a votação de um novo eleitor no primeiro cargo. A sessão consulta
     * o catálogo em memória, sem ir ao banco por tecla (RNF05).
     *
     * @return sessão nova, sem dígitos e sem votos
     */
    public SessaoVotacao iniciarSessaoVotacao() {
        return new SessaoVotacao(catalogoEleitoral);
    }

    /**
     * Grava os votos de um eleitor numa única transação (RN10 e RN11).
     *
     * @param votos um voto para cada cargo
     * @throws br.com.urna.excecao.RegraNegocioException se faltar cargo ou houver cargo repetido
     * @throws br.com.urna.excecao.PersistenciaException se a gravação falhar; nesse caso nada foi gravado
     */
    public void registrarVotos(List<Voto> votos) {
        servicoVotacao.registrarVotos(votos);
    }

    // ---- Cadastro de partidos ----

    /**
     * Lista os partidos carregados, pelo número.
     *
     * @return lista imutável de partidos
     */
    public List<Partido> listarPartidos() {
        return catalogoEleitoral.listarPartidos();
    }

    /**
     * Inclui ou altera um partido, conferindo número e sigla únicos (RN03).
     *
     * @param partido partido com identificador 0 para incluir, ou o identificador gravado para alterar
     * @throws br.com.urna.excecao.RegraNegocioException se violar uma regra de cadastro
     * @throws br.com.urna.excecao.PersistenciaException se a gravação falhar
     */
    public void salvarPartido(Partido partido) {
        servicoCadastro.salvarPartido(partido);
    }

    /**
     * Exclui um partido sem candidatos.
     *
     * @param partido partido a excluir
     * @throws br.com.urna.excecao.RegraNegocioException se o partido ainda tiver candidatos
     * @throws br.com.urna.excecao.PersistenciaException se a exclusão falhar
     */
    public void excluirPartido(Partido partido) {
        servicoCadastro.excluirPartido(partido);
    }

    // ---- Cadastro de candidatos ----

    /**
     * Lista os candidatos carregados, na ordem de votação dos cargos e pelo número.
     *
     * @return lista imutável de candidatos
     */
    public List<Candidato> listarCandidatos() {
        return catalogoEleitoral.listarCandidatos();
    }

    /**
     * Inclui ou altera um candidato, conferindo as regras RN01 a RN07.
     *
     * @param ficha dados do candidato, com identificador 0 para incluir
     * @throws br.com.urna.excecao.RegraNegocioException se violar uma regra de cadastro
     * @throws br.com.urna.excecao.PersistenciaException se a gravação falhar
     */
    public void salvarCandidato(FichaCandidato ficha) {
        servicoCadastro.salvarCandidato(ficha);
    }

    /**
     * Exclui um candidato.
     *
     * @param candidato candidato a excluir
     * @throws br.com.urna.excecao.PersistenciaException se a exclusão falhar, por exemplo se ele já tiver votos
     */
    public void excluirCandidato(Candidato candidato) {
        servicoCadastro.excluirCandidato(candidato);
    }

    // ---- Apuração ----

    /**
     * Apura os votos de um cargo: votos por candidato, brancos, nulos e total (RF12).
     *
     * @param cargo cargo apurado
     * @return boletim do cargo
     * @throws br.com.urna.excecao.PersistenciaException se a leitura dos votos falhar ou não for permitida
     */
    public ApuracaoCargo apurarVotos(Cargo cargo) {
        return servicoApuracao.apurar(cargo);
    }
}
