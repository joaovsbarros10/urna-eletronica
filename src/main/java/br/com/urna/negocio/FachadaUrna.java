/*
 * Urna Eletrônica
 * Arquivo: FachadaUrna.java
 * Autor: João
 */

package br.com.urna.negocio;

import br.com.urna.excecao.UrnaException;
import br.com.urna.modelo.ApuracaoCargo;
import br.com.urna.modelo.Candidato;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.SessaoVotacao;
import br.com.urna.modelo.Voto;
import br.com.urna.persistencia.RepositorioUrna;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Única porta de entrada da camada de negócio (padrão Fachada). A visão e o
 * controle só falam com esta classe, que aplica as regras e usa o repositório.
 * <p>
 * Partidos e candidatos ficam em memória depois de {@link #carregarDados()},
 * para que a urna não consulte o banco a cada tecla. Os métodos que acessam o
 * banco devem ser chamados fora da thread de eventos do Swing.
 *
 * @author João
 */
public class FachadaUrna {

    private static final int TAMANHOMAXIMOFOTO = 200 * 1024;

    private final RepositorioUrna repositorio;

    private List<Partido> partidos = List.of();

    private List<Candidato> candidatos = List.of();

    /**
     * Cria a fachada sobre o repositório (Supabase ou memória).
     *
     * @param repositorio onde os dados são guardados
     */
    public FachadaUrna(RepositorioUrna repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Lê partidos e candidatos do repositório e guarda em memória.
     *
     * @throws UrnaException se o banco não responder
     */
    public synchronized void carregarDados() {
        partidos = List.copyOf(repositorio.listarPartidos());
        candidatos = List.copyOf(repositorio.listarCandidatos());
    }

    /**
     * Começa a votação de um novo eleitor no primeiro cargo.
     *
     * @return sessão nova, sem votos
     */
    public synchronized SessaoVotacao iniciarSessao() {
        return new SessaoVotacao(partidos, candidatos);
    }

    /**
     * Grava os votos de um eleitor. Exige exatamente um voto para cada cargo.
     *
     * @param votos votos da sessão encerrada
     * @throws UrnaException se faltar cargo, houver cargo repetido ou a gravação falhar
     */
    public void registrarVotos(List<Voto> votos) {
        Set<Cargo> cargosVotados = EnumSet.noneOf(Cargo.class);
        for (Voto voto : votos) {
            cargosVotados.add(voto.getCargo());
        }
        if (votos.size() != Cargo.values().length || cargosVotados.size() != Cargo.values().length) {
            throw new UrnaException("A votação precisa de exatamente um voto para cada cargo.");
        }
        repositorio.gravarVotos(votos);
    }

    public synchronized List<Partido> listarPartidos() {
        return partidos;
    }

    public synchronized List<Candidato> listarCandidatos() {
        return candidatos;
    }

    /**
     * Inclui ou altera um partido. Número e sigla não podem se repetir, e um
     * partido com candidatos não pode trocar de número.
     *
     * @param partido partido a gravar
     * @throws UrnaException se alguma regra for violada ou a gravação falhar
     */
    public void salvarPartido(Partido partido) {
        for (Partido outro : listarPartidos()) {
            if (outro.getIdentificador() == partido.getIdentificador()) {
                if (outro.getNumero() != partido.getNumero() && possuiCandidatos(outro)) {
                    throw new UrnaException("Não é possível trocar o número de um partido que já tem candidatos.");
                }
            } else if (outro.getNumero() == partido.getNumero()
                    || outro.getSigla().equalsIgnoreCase(partido.getSigla())) {
                throw new UrnaException("Já existe partido com esse número ou sigla.");
            }
        }
        repositorio.salvarPartido(partido);
        carregarDados();
    }

    /**
     * Exclui um partido que não tem candidatos.
     *
     * @param partido partido a excluir
     * @throws UrnaException se o partido ainda tiver candidatos
     */
    public void excluirPartido(Partido partido) {
        if (possuiCandidatos(partido)) {
            throw new UrnaException("O partido " + partido.getSigla() + " ainda tem candidatos.");
        }
        repositorio.excluirPartido(partido);
        carregarDados();
    }

    /**
     * Inclui ou altera um candidato. A foto deve ser JPEG ou PNG de até 200 KB,
     * e o número não pode se repetir dentro do cargo.
     *
     * @param candidato candidato a gravar, já validado pelo modelo
     * @throws UrnaException se alguma regra for violada ou a gravação falhar
     */
    public void salvarCandidato(Candidato candidato) {
        validarFoto(candidato.getFoto());
        for (Candidato outro : listarCandidatos()) {
            if (outro.getIdentificador() != candidato.getIdentificador() && outro.getCargo() == candidato.getCargo()
                    && outro.getNumero().equals(candidato.getNumero())) {
                throw new UrnaException("Já existe candidato a " + candidato.getCargo() + " com o número "
                        + candidato.getNumero() + ".");
            }
        }
        repositorio.salvarCandidato(candidato);
        carregarDados();
    }

    /**
     * Exclui um candidato. O banco recusa candidato que já recebeu votos.
     *
     * @param candidato candidato a excluir
     */
    public void excluirCandidato(Candidato candidato) {
        repositorio.excluirCandidato(candidato);
        carregarDados();
    }

    /**
     * Apura um cargo: cada voto lido do banco se soma no contador certo.
     *
     * @param cargo cargo apurado
     * @return resultado do cargo
     */
    public ApuracaoCargo apurar(Cargo cargo) {
        List<Candidato> candidatosDoCargo = listarCandidatos().stream()
                .filter(candidato -> candidato.getCargo() == cargo)
                .toList();
        ApuracaoCargo apuracao = new ApuracaoCargo(cargo, candidatosDoCargo);
        for (Voto voto : repositorio.listarVotos(cargo, candidatosDoCargo)) {
            voto.contabilizarEm(apuracao);
        }
        return apuracao;
    }

    private boolean possuiCandidatos(Partido partido) {
        return listarCandidatos().stream()
                .anyMatch(candidato -> candidato.getPartido().getIdentificador() == partido.getIdentificador());
    }

    private static void validarFoto(byte[] foto) {
        if (foto.length > TAMANHOMAXIMOFOTO) {
            throw new UrnaException("A foto tem " + foto.length / 1024 + " KB; o limite é 200 KB.");
        }
        boolean jpeg = (foto[0] & 0xFF) == 0xFF && (foto[1] & 0xFF) == 0xD8;
        boolean png = (foto[0] & 0xFF) == 0x89 && foto[1] == 'P';
        if (!jpeg && !png) {
            throw new UrnaException("A foto deve ser JPEG ou PNG.");
        }
    }
}
