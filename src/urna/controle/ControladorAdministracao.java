/*
 * Urna Eletrônica
 * Arquivo: ControladorAdministracao.java
 * Autor: João
 */

package urna.controle;

import java.util.List;
import urna.modelo.ApuracaoCargo;
import urna.modelo.Candidato;
import urna.modelo.Cargo;
import urna.modelo.Partido;
import urna.negocio.FachadaUrna;

/**
 * Controlador do módulo administrativo: repassa à fachada o cadastro de
 * partidos e candidatos e a apuração.
 *
 * @author João
 */
public class ControladorAdministracao {

    private final FachadaUrna fachada;

    /**
     * Cria o controlador.
     *
     * @param fachada porta de entrada da camada de negócio
     */
    public ControladorAdministracao(FachadaUrna fachada) {
        this.fachada = fachada;
    }

    /** Relê partidos e candidatos do banco. */
    public void carregarDados() {
        fachada.carregarDados();
    }

    public List<Partido> listarPartidos() {
        return fachada.listarPartidos();
    }

    public List<Candidato> listarCandidatos() {
        return fachada.listarCandidatos();
    }

    /**
     * Inclui ou altera um partido.
     *
     * @param partido partido a gravar
     */
    public void salvarPartido(Partido partido) {
        fachada.salvarPartido(partido);
    }

    /**
     * Exclui um partido sem candidatos.
     *
     * @param partido partido a excluir
     */
    public void excluirPartido(Partido partido) {
        fachada.excluirPartido(partido);
    }

    /**
     * Inclui ou altera um candidato.
     *
     * @param candidato candidato a gravar
     */
    public void salvarCandidato(Candidato candidato) {
        fachada.salvarCandidato(candidato);
    }

    /**
     * Exclui um candidato.
     *
     * @param candidato candidato a excluir
     */
    public void excluirCandidato(Candidato candidato) {
        fachada.excluirCandidato(candidato);
    }

    /**
     * Apura os votos de um cargo.
     *
     * @param cargo cargo apurado
     * @return resultado do cargo
     */
    public ApuracaoCargo apurar(Cargo cargo) {
        return fachada.apurar(cargo);
    }
}
