/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ControladorAdministracao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.controle;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FichaCandidato;
import br.com.urna.modelo.voto.ApuracaoCargo;
import br.com.urna.negocio.FachadaUrna;
import java.util.List;

/**
 * Controlador do módulo administrativo: repassa à {@link FachadaUrna} o
 * cadastro de partidos e candidatos e a apuração.
 * <p>
 * Os métodos que acessam o banco podem demorar; os painéis os chamam fora da
 * thread de eventos (RNF06). Erros chegam como {@link br.com.urna.excecao.UrnaException}.
 *
 * @author João
 * @since 1.0
 */
public class ControladorAdministracao {

    private final FachadaUrna fachada;

    /**
     * Cria o controlador.
     *
     * @param fachada porta de entrada da camada de negócio
     * @throws IllegalArgumentException se a fachada for {@code null}
     */
    public ControladorAdministracao(FachadaUrna fachada) {
        if (fachada == null) {
            throw new IllegalArgumentException("O controlador precisa da fachada.");
        }
        this.fachada = fachada;
    }

    // ---- Dados atualizados ----

    /**
     * Relê partidos e candidatos do banco, para os painéis mostrarem dados atuais.
     *
     * @throws br.com.urna.excecao.PersistenciaException se o banco não responder
     */
    public void carregarDadosEleitorais() {
        fachada.carregarDadosEleitorais();
    }

    // ---- Partidos ----

    /**
     * Lista os partidos já carregados, pelo número.
     *
     * @return lista imutável de partidos
     */
    public List<Partido> listarPartidos() {
        return fachada.listarPartidos();
    }

    /**
     * Inclui ou altera um partido (RN03).
     *
     * @param partido partido a gravar
     * @throws br.com.urna.excecao.UrnaException se violar uma regra ou a gravação falhar
     */
    public void salvarPartido(Partido partido) {
        fachada.salvarPartido(partido);
    }

    /**
     * Exclui um partido sem candidatos.
     *
     * @param partido partido a excluir
     * @throws br.com.urna.excecao.UrnaException se o partido tiver candidatos ou a exclusão falhar
     */
    public void excluirPartido(Partido partido) {
        fachada.excluirPartido(partido);
    }

    // ---- Candidatos ----

    /**
     * Lista os candidatos já carregados.
     *
     * @return lista imutável de candidatos
     */
    public List<Candidato> listarCandidatos() {
        return fachada.listarCandidatos();
    }

    /**
     * Inclui ou altera um candidato (RN01 a RN07).
     *
     * @param ficha dados do candidato
     * @throws br.com.urna.excecao.UrnaException se violar uma regra ou a gravação falhar
     */
    public void salvarCandidato(FichaCandidato ficha) {
        fachada.salvarCandidato(ficha);
    }

    /**
     * Exclui um candidato.
     *
     * @param candidato candidato a excluir
     * @throws br.com.urna.excecao.UrnaException se a exclusão falhar
     */
    public void excluirCandidato(Candidato candidato) {
        fachada.excluirCandidato(candidato);
    }

    // ---- Apuração ----

    /**
     * Apura os votos de um cargo (RF12).
     *
     * @param cargo cargo apurado
     * @return boletim do cargo
     * @throws br.com.urna.excecao.UrnaException se a leitura dos votos falhar
     */
    public ApuracaoCargo apurarVotos(Cargo cargo) {
        return fachada.apurarVotos(cargo);
    }
}
