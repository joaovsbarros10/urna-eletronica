/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: TemaVisual.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import java.awt.Color;

/**
 * Cores da urna. É uma interface só de constantes porque o item 3.6 do
 * checklist manda: classe com apenas campos {@code static final} vira interface.
 * Confira as cores no simulador do TSE e ajuste aqui (seção 9).
 *
 * @author João
 * @since 1.0
 */
public interface TemaVisual {

    /** Fundo do display. */
    Color FUNDODISPLAY = new Color(0xF4F4EE);

    /** Texto principal do display. */
    Color TEXTODISPLAY = new Color(0x161616);

    /** Texto secundário do display, como "SEU VOTO PARA". */
    Color DETALHEDISPLAY = new Color(0x5F5F5F);

    /** Moldura escura em volta do display. */
    Color MOLDURADISPLAY = new Color(0x3B3B3B);

    /** Borda das caixas de dígito. */
    Color BORDACAMPO = new Color(0x4A4A4A);

    /** Corpo bege da urna. */
    Color FUNDOCORPO = new Color(0xD9D3C6);

    /** Área escura do teclado. */
    Color FUNDOTECLADO = new Color(0x2B2B2B);

    /** Teclas numéricas. */
    Color TECLANUMERICA = new Color(0x141414);

    /** Texto das teclas numéricas. */
    Color TEXTOTECLANUMERICA = new Color(0xF2F2F2);

    /** Tecla BRANCO. */
    Color TECLABRANCO = new Color(0xF7F7F7);

    /** Tecla CORRIGE (laranja). */
    Color TECLACORRIGE = new Color(0xE87722);

    /** Tecla CONFIRMA (verde). */
    Color TECLACONFIRMA = new Color(0x2E9D4B);

    /** Texto das teclas BRANCO, CORRIGE e CONFIRMA. */
    Color TEXTOTECLAACAO = new Color(0x111111);
}
