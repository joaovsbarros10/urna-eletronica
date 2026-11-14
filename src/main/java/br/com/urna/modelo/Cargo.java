/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: Cargo.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Cargo em disputa na eleição (tabela 2.1 do documento de projeto).
 * <p>
 * Cada constante guarda a quantidade de dígitos do número do candidato (RN01),
 * a descrição mostrada no display, a posição do cargo na ordem de votação e o
 * complemento que o candidato do cargo exige (RN07). Se a ordem da urna oficial
 * for outra, basta ajustar o terceiro argumento de cada constante.
 *
 * @author João
 * @since 1.0
 */
public enum Cargo {

    /** Presidente da República: 2 dígitos, com vice. */
    PRESIDENTE(2, "Presidente", 5, Complemento.VICE),

    /** Governador do estado: 2 dígitos, com vice. */
    GOVERNADOR(2, "Governador", 4, Complemento.VICE),

    /** Senador: 3 dígitos, com 1º e 2º suplentes. */
    SENADOR(3, "Senador", 3, Complemento.SUPLENTES),

    /** Deputado Federal: 4 dígitos, sem complemento. */
    DEPUTADOFEDERAL(4, "Deputado Federal", 1, Complemento.NENHUM),

    /** Deputado Estadual: 5 dígitos, sem complemento. */
    DEPUTADOESTADUAL(5, "Deputado Estadual", 2, Complemento.NENHUM);

    private final int quantidadeDigitos;

    private final String descricao;

    private final int ordemDeVotacao;

    private final Complemento complemento;

    Cargo(int quantidadeDigitos, String descricao, int ordemDeVotacao, Complemento complemento) {
        this.quantidadeDigitos = quantidadeDigitos;
        this.descricao = descricao;
        this.ordemDeVotacao = ordemDeVotacao;
        this.complemento = complemento;
    }

    // ---- Dados do cargo ----

    /**
     * Informa quantos dígitos tem o número de um candidato a este cargo (RN01).
     *
     * @return quantidade de dígitos, de 2 a 5
     */
    public int getQuantidadeDigitos() {
        return quantidadeDigitos;
    }

    /**
     * Devolve o nome do cargo como aparece no display, por exemplo "Deputado Federal".
     *
     * @return descrição do cargo
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Informa a posição do cargo na sessão de votação; o primeiro cargo votado tem ordem 1.
     *
     * @return posição de 1 a 5
     */
    public int getOrdemDeVotacao() {
        return ordemDeVotacao;
    }

    // ---- Complemento exigido (RN07) ----

    /**
     * Informa se o candidato a este cargo tem vice (Presidente e Governador).
     *
     * @return {@code true} se o cadastro do candidato exige o nome do vice
     */
    public boolean possuiVice() {
        return complemento == Complemento.VICE;
    }

    /**
     * Informa se o candidato a este cargo tem 1º e 2º suplentes (Senador).
     *
     * @return {@code true} se o cadastro do candidato exige os dois suplentes
     */
    public boolean possuiSuplentes() {
        return complemento == Complemento.SUPLENTES;
    }

    // ---- Ordem de votação ----

    /**
     * Devolve os cinco cargos na ordem em que o eleitor vota (tabela 2.1).
     *
     * @return lista imutável, do primeiro ao último cargo
     */
    public static List<Cargo> listarEmOrdemDeVotacao() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(Cargo::getOrdemDeVotacao))
                .toList();
    }

    // ---- Representação ----

    @Override
    public String toString() {
        return descricao;
    }

    private enum Complemento {
        NENHUM,
        VICE,
        SUPLENTES
    }
}
