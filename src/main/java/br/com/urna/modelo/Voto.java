/*
 * Urna Eletrônica
 * Arquivo: Voto.java
 * Autor: João
 */

package br.com.urna.modelo;

/**
 * Voto registrado para um cargo. Não guarda eleitor, horário nem ordem de chegada.
 * Cada subclasse sabe em qual contador da apuração deve ser somada.
 *
 * @author João
 */
public abstract class Voto {

    private final Cargo cargo;

    /**
     * Cria o voto para o cargo.
     *
     * @param cargo cargo votado
     * @throws IllegalArgumentException se o cargo for nulo
     */
    protected Voto(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("O voto precisa de um cargo.");
        }
        this.cargo = cargo;
    }

    public Cargo getCargo() {
        return cargo;
    }

    /**
     * Devolve o candidato votado.
     *
     * @return o candidato, ou {@code null} em voto branco ou nulo
     */
    public Candidato getCandidato() {
        return null;
    }

    /**
     * Devolve o tipo gravado na coluna {@code tipo} do banco.
     *
     * @return CANDIDATO, BRANCO ou NULO
     */
    public abstract String getTipo();

    /**
     * Soma este voto no contador certo da apuração.
     *
     * @param apuracao apuração do mesmo cargo
     */
    public abstract void contabilizarEm(ApuracaoCargo apuracao);
}
