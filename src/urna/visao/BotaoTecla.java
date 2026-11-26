/*
 * Urna Eletrônica
 * Arquivo: BotaoTecla.java
 * Autor: João
 */

package urna.visao;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Tecla da urna, com a aparência comum a todas: fonte grande, cor de fundo e sem
 * foco de teclado, para o teclado físico continuar funcionando.
 *
 * @author João
 */
public final class BotaoTecla extends JButton {

    /**
     * Cria a tecla.
     *
     * @param rotulo     texto da tecla
     * @param corDeFundo cor da tecla
     * @param acao       o que acontece ao apertar
     */
    public BotaoTecla(String rotulo, Color corDeFundo, Runnable acao) {
        super(rotulo);
        boolean numerica = rotulo.length() == 1;
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, numerica ? 28 : 13));
        setBackground(corDeFundo);
        setForeground(numerica ? Color.WHITE : Color.BLACK);
        setFocusable(false);
        setMargin(new Insets(0, 0, 0, 0));
        setBorder(BorderFactory.createLineBorder(corDeFundo.darker(), 2));
        addActionListener(evento -> acao.run());
    }
}
