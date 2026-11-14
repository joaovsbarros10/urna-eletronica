/*
 * Urna Eletrônica
 * Arquivo: VotoBranco.java
 * Autor: João
 */

package br.com.urna.modelo;

/**
 * Voto em branco: a tecla BRANCO foi aceita com o display vazio.
 *
 * @author João
 */
public class VotoBranco extends Voto {

    /**
     * Cria o voto em branco.
     *
     * @param cargo cargo votado
     */
    public VotoBranco(Cargo cargo) {
        super(cargo);
    }

    @Override
    public String getTipo() {
        return "BRANCO";
    }

    @Override
    public void contabilizarEm(ApuracaoCargo apuracao) {
        apuracao.registrarVotoBranco();
    }
}
