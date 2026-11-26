/*
 * Urna Eletrônica
 * Arquivo: PainelTeclado.java
 * Autor: João
 */

package urna.visao;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import urna.controle.ControladorVotacao;

/**
 * Teclado da urna: dígitos de 0 a 9, BRANCO, CORRIGE e CONFIRMA. O teclado do
 * computador também funciona: dígitos, Enter para CONFIRMA e Backspace para CORRIGE.
 *
 * @author João
 */
public final class PainelTeclado extends JPanel {

    /**
     * Monta as 13 teclas e liga cada uma ao controlador.
     *
     * @param controlador controlador da votação
     */
    public PainelTeclado(ControladorVotacao controlador) {
        super(new GridLayout(5, 3, 10, 10));
        setBackground(TemaVisual.FUNDOTECLADO);
        setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        setPreferredSize(new Dimension(330, 440));

        for (char digito : "1234567890".toCharArray()) {
            if (digito == '0') {
                add(new JLabel());
            }
            add(new BotaoTecla(String.valueOf(digito), TemaVisual.TECLANUMERICA,
                    () -> controlador.aoPressionarDigito(digito)));
            ligarTecla(KeyEvent.VK_0 + (digito - '0'), () -> controlador.aoPressionarDigito(digito));
            ligarTecla(KeyEvent.VK_NUMPAD0 + (digito - '0'), () -> controlador.aoPressionarDigito(digito));
        }
        add(new JLabel());
        add(new BotaoTecla("BRANCO", TemaVisual.TECLABRANCO, controlador::aoPressionarBranco));
        add(new BotaoTecla("CORRIGE", TemaVisual.TECLACORRIGE, controlador::aoPressionarCorrige));
        add(new BotaoTecla("CONFIRMA", TemaVisual.TECLACONFIRMA, controlador::aoPressionarConfirma));
        ligarTecla(KeyEvent.VK_ENTER, controlador::aoPressionarConfirma);
        ligarTecla(KeyEvent.VK_BACK_SPACE, controlador::aoPressionarCorrige);
    }

    private void ligarTecla(int codigoDaTecla, Runnable acao) {
        String nome = "tecla" + codigoDaTecla;
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(codigoDaTecla, 0), nome);
        getActionMap().put(nome, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                acao.run();
            }
        });
    }
}
