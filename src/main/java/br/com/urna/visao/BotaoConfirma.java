/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: BotaoConfirma.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorVotacao;

/**
 * Tecla CONFIRMA (verde).
 *
 * @author João
 * @since 1.0
 */
public class BotaoConfirma extends BotaoTecla {

    /** Cria a tecla CONFIRMA. */
    public BotaoConfirma() {
        super("CONFIRMA", TemaVisual.TECLACONFIRMA, TemaVisual.TEXTOTECLAACAO);
    }

    // ---- Ação da tecla ----

    @Override
    public void acionar(ControladorVotacao controlador) {
        controlador.aoPressionarConfirma();
    }
}
