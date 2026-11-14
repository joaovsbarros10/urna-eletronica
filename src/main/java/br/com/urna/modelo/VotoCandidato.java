/*
 * Urna Eletrônica
 * Arquivo: VotoCandidato.java
 * Autor: João
 */

package br.com.urna.modelo;

/**
 * Voto em um candidato do mesmo cargo.
 *
 * @author João
 */
public class VotoCandidato extends Voto {

    private final Candidato candidato;

    /**
     * Cria o voto no candidato.
     *
     * @param candidato candidato escolhido
     * @throws IllegalArgumentException se o candidato for nulo
     */
    public VotoCandidato(Candidato candidato) {
        super(candidato == null ? null : candidato.getCargo());
        this.candidato = candidato;
    }

    @Override
    public Candidato getCandidato() {
        return candidato;
    }

    @Override
    public String getTipo() {
        return "CANDIDATO";
    }

    @Override
    public void contabilizarEm(ApuracaoCargo apuracao) {
        apuracao.registrarVotoParaCandidato(candidato);
    }
}
