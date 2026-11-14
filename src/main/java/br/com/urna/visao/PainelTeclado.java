/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: PainelTeclado.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorVotacao;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * Teclado da urna: dígitos de 0 a 9, BRANCO, CORRIGE e CONFIRMA (RF01).
 * <p>
 * Todas as 13 teclas recebem o mesmo tratador, que chama
 * {@link BotaoTecla#acionar(ControladorVotacao)}. O teclado do computador
 * também funciona (seção 9.4): dígitos, Enter para CONFIRMA e Backspace para
 * CORRIGE, ligados por {@link InputMap} e {@link ActionMap} aos mesmos
 * métodos do controlador.
 *
 * @author João
 * @since 1.0
 */
public class PainelTeclado extends JPanel {

    private static final String DIGITOS = "1234567890";

    /**
     * Monta o teclado e liga as teclas ao controlador.
     *
     * @param controlador controlador da votação
     */
    public PainelTeclado(ControladorVotacao controlador) {
        super(new GridLayout(5, 3, 10, 10));
        setBackground(TemaVisual.FUNDOTECLADO);
        setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        setPreferredSize(new Dimension(330, 440));
        montarBotoes(controlador);
        ligarTecladoFisico(controlador);
    }

    // ---- Teclas na tela ----

    private void montarBotoes(ControladorVotacao controlador) {
        ActionListener tratador = evento -> ((BotaoTecla) evento.getSource()).acionar(controlador);
        for (int i = 0; i < 9; i++) {
            adicionarTecla(new BotaoNumerico(DIGITOS.charAt(i)), tratador);
        }
        add(criarEspacoVazio());
        adicionarTecla(new BotaoNumerico('0'), tratador);
        add(criarEspacoVazio());
        adicionarTecla(new BotaoBranco(), tratador);
        adicionarTecla(new BotaoCorrige(), tratador);
        adicionarTecla(new BotaoConfirma(), tratador);
    }

    private void adicionarTecla(BotaoTecla botao, ActionListener tratador) {
        botao.addActionListener(tratador);
        add(botao);
    }

    private static JLabel criarEspacoVazio() {
        return new JLabel();
    }

    // ---- Teclado do computador (opcional, seção 9.4) ----

    private void ligarTecladoFisico(ControladorVotacao controlador) {
        InputMap mapaDeEntrada = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap mapaDeAcoes = getActionMap();
        for (int i = 0; i <= 9; i++) {
            char digito = (char) ('0' + i);
            String nomeDaAcao = "digito" + digito;
            mapaDeEntrada.put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), nomeDaAcao);
            mapaDeEntrada.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + i, 0), nomeDaAcao);
            mapaDeAcoes.put(nomeDaAcao, criarAcao(() -> controlador.aoPressionarDigito(digito)));
        }
        mapaDeEntrada.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirma");
        mapaDeAcoes.put("confirma", criarAcao(controlador::aoPressionarConfirma));
        mapaDeEntrada.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "corrige");
        mapaDeAcoes.put("corrige", criarAcao(controlador::aoPressionarCorrige));
    }

    private static AbstractAction criarAcao(Runnable tarefa) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                tarefa.run();
            }
        };
    }
}
