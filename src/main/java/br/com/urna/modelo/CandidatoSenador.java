/*
 * Urna Eletrônica
 * Arquivo: CandidatoSenador.java
 * Autor: João
 */

package br.com.urna.modelo;

import java.util.List;

/**
 * Candidato a Senador, que concorre com 1º e 2º suplentes.
 *
 * @author João
 */
public class CandidatoSenador extends Candidato {

    private final String nomePrimeiroSuplente;

    private final String nomeSegundoSuplente;

    CandidatoSenador(long identificador, String numero, String nome, Partido partido, byte[] foto,
                     String nomePrimeiroSuplente, String nomeSegundoSuplente) {
        super(identificador, Cargo.SENADOR, numero, nome, partido, foto);
        this.nomePrimeiroSuplente = exigirTexto(nomePrimeiroSuplente, "o nome do 1º suplente");
        this.nomeSegundoSuplente = exigirTexto(nomeSegundoSuplente, "o nome do 2º suplente");
    }

    @Override
    public List<String> obterLinhasAdicionais() {
        return List.of("1º Suplente: " + nomePrimeiroSuplente, "2º Suplente: " + nomeSegundoSuplente);
    }

    @Override
    public List<String> getComplementos() {
        return List.of(nomePrimeiroSuplente, nomeSegundoSuplente);
    }
}
