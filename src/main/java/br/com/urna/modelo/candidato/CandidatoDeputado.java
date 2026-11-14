/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CandidatoDeputado.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import java.util.List;

/**
 * Candidato sem complemento (deputados federal e estadual, RN07): o display
 * não mostra linhas abaixo do partido.
 *
 * @author João
 * @since 1.0
 */
public abstract class CandidatoDeputado extends Candidato {

    /**
     * Cria o candidato a deputado.
     *
     * @param ficha dados do candidato; vice e suplentes são ignorados
     * @throws IllegalArgumentException se a ficha for inválida para {@link Candidato}
     */
    protected CandidatoDeputado(FichaCandidato ficha) {
        super(ficha);
    }

    // ---- Exibição ----

    /**
     * Devolve lista vazia: deputado não tem vice nem suplentes.
     *
     * @return lista vazia
     */
    @Override
    public List<String> obterLinhasAdicionais() {
        return List.of();
    }
}
