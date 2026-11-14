/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: BotaoCorrige.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorVotacao;

/**
 * Tecla CORRIGE (laranja).
 *
 * @author João
 * @since 1.0
 */
public class BotaoCorrige extends BotaoTecla {

    /** Cria a tecla CORRIGE. */
    public BotaoCorrige() {
        super("CORRIGE", TemaVisual.TECLACORRIGE, TemaVisual.TEXTOTECLAACAO);
    }

    // ---- Ação da tecla ----

    @Override
    public void acionar(ControladorVotacao controlador) {
        controlador.aoPressionarCorrige();
    }
}
