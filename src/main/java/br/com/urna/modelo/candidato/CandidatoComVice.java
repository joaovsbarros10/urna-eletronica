/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CandidatoComVice.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import java.util.List;

/**
 * Candidato que concorre com um vice (Presidente e Governador, RN07).
 * <p>
 * Monta a linha "Rótulo: nome do vice" do display com o rótulo que a
 * subclasse informa em {@link #obterRotuloDoVice()}.
 *
 * @author João
 * @since 1.0
 */
public abstract class CandidatoComVice extends Candidato {

    private final String nomeVice;

    /**
     * Cria o candidato e exige o nome do vice.
     *
     * @param ficha dados do candidato, com o nome do vice preenchido
     * @throws IllegalArgumentException se faltar o vice ou se a ficha for inválida para {@link Candidato}
     */
    protected CandidatoComVice(FichaCandidato ficha) {
        super(ficha);
        this.nomeVice = exigirTexto(ficha.getNomeVice(), "o nome do vice (RN07)");
    }

    // ---- Dados do vice ----

    public String getNomeVice() {
        return nomeVice;
    }

    /**
     * Devolve o rótulo do vice no display, por exemplo "Vice-Presidente".
     *
     * @return rótulo do vice
     */
    protected abstract String obterRotuloDoVice();

    // ---- Exibição e ficha ----

    /**
     * Devolve uma única linha, com o rótulo e o nome do vice.
     *
     * @return lista com a linha do vice
     */
    @Override
    public List<String> obterLinhasAdicionais() {
        return List.of(obterRotuloDoVice() + ": " + nomeVice);
    }

    @Override
    protected void completarFicha(FichaCandidato.Montador montador) {
        montador.definirNomeVice(nomeVice);
    }
}
