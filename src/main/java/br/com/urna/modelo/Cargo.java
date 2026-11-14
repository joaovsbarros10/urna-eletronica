/*
 * Urna Eletrônica
 * Arquivo: Cargo.java
 * Autor: João
 */

package br.com.urna.modelo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Cargos da eleição, com a quantidade de dígitos do número, a ordem de votação
 * e o complemento que o candidato precisa ter (vice ou suplentes).
 *
 * @author João
 */
public enum Cargo {

    /** Presidente: 2 dígitos, com vice. */
    PRESIDENTE(2, "Presidente", 5, 1),

    /** Governador: 2 dígitos, com vice. */
    GOVERNADOR(2, "Governador", 4, 1),

    /** Senador: 3 dígitos, com dois suplentes. */
    SENADOR(3, "Senador", 3, 2),

    /** Deputado Federal: 4 dígitos. */
    DEPUTADOFEDERAL(4, "Deputado Federal", 1, 0),

    /** Deputado Estadual: 5 dígitos. */
    DEPUTADOESTADUAL(5, "Deputado Estadual", 2, 0);

    private final int quantidadeDigitos;

    private final String descricao;

    private final int ordemDeVotacao;

    private final int quantidadeComplementos;

    Cargo(int quantidadeDigitos, String descricao, int ordemDeVotacao, int quantidadeComplementos) {
        this.quantidadeDigitos = quantidadeDigitos;
        this.descricao = descricao;
        this.ordemDeVotacao = ordemDeVotacao;
        this.quantidadeComplementos = quantidadeComplementos;
    }

    public int getQuantidadeDigitos() {
        return quantidadeDigitos;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getOrdemDeVotacao() {
        return ordemDeVotacao;
    }

    /**
     * Informa se o candidato deste cargo concorre com vice.
     *
     * @return {@code true} para Presidente e Governador
     */
    public boolean possuiVice() {
        return quantidadeComplementos == 1;
    }

    /**
     * Informa se o candidato deste cargo tem 1º e 2º suplentes.
     *
     * @return {@code true} para Senador
     */
    public boolean possuiSuplentes() {
        return quantidadeComplementos == 2;
    }

    /**
     * Devolve os cargos na ordem em que o eleitor vota.
     *
     * @return lista do primeiro ao último cargo
     */
    public static List<Cargo> listarEmOrdemDeVotacao() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(Cargo::getOrdemDeVotacao))
                .toList();
    }

    @Override
    public String toString() {
        return descricao;
    }
}
