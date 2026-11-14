/*
 * Urna Eletrônica
 * Arquivo: Partido.java
 * Autor: João
 */

package br.com.urna.modelo;

import java.util.Objects;

/**
 * Partido político. O número tem 2 dígitos e é o começo do número de todos os
 * seus candidatos. O identificador vale 0 enquanto o partido não foi gravado.
 *
 * @author João
 */
public final class Partido {

    private final long identificador;

    private final int numero;

    private final String sigla;

    private final String nome;

    /**
     * Cria um partido.
     *
     * @param identificador identificador no banco, ou 0 se ainda não foi gravado
     * @param numero        número de 10 a 99
     * @param sigla         sigla do partido
     * @param nome          nome por extenso
     * @throws IllegalArgumentException se o número estiver fora da faixa ou faltar sigla ou nome
     */
    public Partido(long identificador, int numero, String sigla, String nome) {
        if (numero < 10 || numero > 99) {
            throw new IllegalArgumentException("O número do partido deve ter 2 dígitos (de 10 a 99).");
        }
        if (sigla == null || sigla.isBlank() || nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe a sigla e o nome do partido.");
        }
        this.identificador = identificador;
        this.numero = numero;
        this.sigla = sigla.strip();
        this.nome = nome.strip();
    }

    public long getIdentificador() {
        return identificador;
    }

    public int getNumero() {
        return numero;
    }

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }

    /**
     * Informa se o número de um candidato começa com o número deste partido.
     *
     * @param numeroCandidato número do candidato
     * @return {@code true} se o número começa com o número do partido
     */
    public boolean correspondeAoPrefixo(String numeroCandidato) {
        return numeroCandidato != null && numeroCandidato.startsWith(String.valueOf(numero));
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Partido partido && partido.numero == numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return sigla + " (" + numero + ")";
    }
}
