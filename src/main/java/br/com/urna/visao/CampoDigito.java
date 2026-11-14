/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CampoDigito.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * Uma caixa de dígito do número do candidato. O display tem uma caixa por
 * dígito do cargo (RF03) e destaca a próxima a ser preenchida.
 *
 * @author João
 * @since 1.0
 */
public class CampoDigito extends JLabel {

    private static final Border BORDANORMAL = new LineBorder(TemaVisual.BORDACAMPO, 1);

    private static final Border BORDADESTAQUE = new LineBorder(TemaVisual.TEXTODISPLAY, 3);

    /** Cria a caixa vazia. */
    public CampoDigito() {
        super("", SwingConstants.CENTER);
        setPreferredSize(new Dimension(42, 54));
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        setForeground(TemaVisual.TEXTODISPLAY);
        setBackground(TemaVisual.FUNDODISPLAY);
        setOpaque(true);
        setBorder(BORDANORMAL);
    }

    // ---- Conteúdo da caixa ----

    /**
     * Mostra o dígito na caixa.
     *
     * @param digito dígito de '0' a '9'
     */
    public void exibirDigito(char digito) {
        setText(String.valueOf(digito));
    }

    /** Deixa a caixa vazia. */
    public void limpar() {
        setText("");
    }

    // ---- Aparência ----

    /**
     * Destaca a caixa como a próxima a ser preenchida.
     *
     * @param destaque {@code true} para destacar
     */
    public void destacar(boolean destaque) {
        setBorder(destaque ? BORDADESTAQUE : BORDANORMAL);
    }
}
