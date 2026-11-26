/*
 * Urna Eletrônica
 * Arquivo: TemaVisual.java
 * Autor: João
 */

package urna.visao;

import java.awt.Color;

/**
 * Cores da urna. Como só tem constantes, é uma interface (item 3.6 do checklist).
 *
 * @author João
 */
public interface TemaVisual {

    /** Fundo do display. */
    Color FUNDODISPLAY = new Color(0xF4F4EE);

    /** Texto do display. */
    Color TEXTODISPLAY = new Color(0x161616);

    /** Textos secundários e bordas. */
    Color DETALHE = new Color(0x5F5F5F);

    /** Corpo bege da urna. */
    Color FUNDOCORPO = new Color(0xD9D3C6);

    /** Área escura do teclado. */
    Color FUNDOTECLADO = new Color(0x2B2B2B);

    /** Teclas numéricas. */
    Color TECLANUMERICA = new Color(0x141414);

    /** Tecla BRANCO. */
    Color TECLABRANCO = new Color(0xF7F7F7);

    /** Tecla CORRIGE. */
    Color TECLACORRIGE = new Color(0xE87722);

    /** Tecla CONFIRMA. */
    Color TECLACONFIRMA = new Color(0x2E9D4B);
}
