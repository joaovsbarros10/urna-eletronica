/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ReprodutorSom.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.som;

/**
 * Toca os efeitos sonoros da urna. A implementação real usa
 * {@code javax.sound.sampled}; a muda serve para testes e máquinas sem som.
 *
 * @author João
 * @since 1.0
 */
public interface ReprodutorSom {

    /**
     * Toca o efeito sem bloquear quem chamou: a tecla nunca espera o áudio (RNF06).
     *
     * @param efeito efeito a tocar
     */
    void reproduzir(EfeitoSonoro efeito);
}
