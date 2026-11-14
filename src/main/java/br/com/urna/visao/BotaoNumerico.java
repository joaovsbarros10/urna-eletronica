/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: BotaoNumerico.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorVotacao;

/**
 * Tecla de um dígito, de 0 a 9.
 *
 * @author João
 * @since 1.0
 */
public class BotaoNumerico extends BotaoTecla {

    private final char digito;

    /**
     * Cria a tecla do dígito.
     *
     * @param digito dígito de '0' a '9'
     */
    public BotaoNumerico(char digito) {
        super(String.valueOf(digito), TemaVisual.TECLANUMERICA, TemaVisual.TEXTOTECLANUMERICA);
        this.digito = digito;
    }

    // ---- Ação da tecla ----

    @Override
    public void acionar(ControladorVotacao controlador) {
        controlador.aoPressionarDigito(digito);
    }
}
