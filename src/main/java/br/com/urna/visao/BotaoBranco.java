/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: BotaoBranco.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorVotacao;

/**
 * Tecla BRANCO (branca).
 *
 * @author João
 * @since 1.0
 */
public class BotaoBranco extends BotaoTecla {

    /** Cria a tecla BRANCO. */
    public BotaoBranco() {
        super("BRANCO", TemaVisual.TECLABRANCO, TemaVisual.TEXTOTECLAACAO);
    }

    // ---- Ação da tecla ----

    @Override
    public void acionar(ControladorVotacao controlador) {
        controlador.aoPressionarBranco();
    }
}
