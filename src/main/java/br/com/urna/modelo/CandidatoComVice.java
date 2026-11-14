/*
 * Urna Eletrônica
 * Arquivo: CandidatoComVice.java
 * Autor: João
 */

package br.com.urna.modelo;

import java.util.List;

/**
 * Candidato a Presidente ou Governador, que concorre com um vice.
 *
 * @author João
 */
public class CandidatoComVice extends Candidato {

    private final String nomeVice;

    CandidatoComVice(long identificador, Cargo cargo, String numero, String nome, Partido partido, byte[] foto,
                     String nomeVice) {
        super(identificador, cargo, numero, nome, partido, foto);
        this.nomeVice = exigirTexto(nomeVice, "o nome do vice");
    }

    public String getNomeVice() {
        return nomeVice;
    }

    @Override
    public List<String> obterLinhasAdicionais() {
        return List.of("Vice-" + getCargo().getDescricao() + ": " + nomeVice);
    }

    @Override
    public List<String> getComplementos() {
        return List.of(nomeVice);
    }
}
