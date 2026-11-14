/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: BotaoTecla.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorVotacao;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Tecla da urna. Dá a aparência comum às 13 teclas e define o contrato
 * {@link #acionar(ControladorVotacao)}: cada tecla sabe o que pedir ao
 * controlador, então o teclado registra o mesmo tratador em todas e não tem
 * {@code if} por tecla (polimorfismo na visão).
 *
 * @author João
 * @since 1.0
 */
public abstract class BotaoTecla extends JButton {

    private static final int TAMANHOFONTEDIGITO = 28;

    private static final int TAMANHOFONTEACAO = 13;

    /**
     * Cria a tecla com o rótulo e as cores informados.
     *
     * @param rotulo     texto da tecla
     * @param corDeFundo cor da tecla
     * @param corDoTexto cor do rótulo
     */
    protected BotaoTecla(String rotulo, Color corDeFundo, Color corDoTexto) {
        super(rotulo);
        int tamanhoDaFonte = rotulo.length() == 1 ? TAMANHOFONTEDIGITO : TAMANHOFONTEACAO;
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, tamanhoDaFonte));
        setBackground(corDeFundo);
        setForeground(corDoTexto);
        setOpaque(true);
        setFocusable(false);
        setFocusPainted(false);
        setMargin(new Insets(0, 0, 0, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corDeFundo.darker(), 2),
                BorderFactory.createEmptyBorder(8, 2, 8, 2)));
    }

    // ---- Ação da tecla ----

    /**
     * Pede ao controlador a ação desta tecla.
     *
     * @param controlador controlador da votação
     */
    public abstract void acionar(ControladorVotacao controlador);
}
