/*
 * Urna Eletrônica
 * Arquivo: JanelaUrna.java
 * Autor: João
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorVotacao;
import br.com.urna.controle.VisaoVotacao;
import br.com.urna.modelo.SessaoVotacao;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Janela da urna: display à esquerda e teclado à direita. É a visão que o
 * {@link ControladorVotacao} controla, pela interface {@link VisaoVotacao}.
 *
 * @author João
 */
public final class JanelaUrna extends JFrame implements VisaoVotacao {

    private final PainelDisplay painelDisplay = new PainelDisplay();

    private final ControladorVotacao controlador;

    /**
     * Monta a janela e liga o teclado ao controlador.
     *
     * @param controlador controlador da votação
     */
    public JanelaUrna(ControladorVotacao controlador) {
        super("Urna");
        this.controlador = controlador;

        JPanel moldura = new JPanel(new BorderLayout());
        moldura.setBorder(BorderFactory.createLineBorder(TemaVisual.TEXTODISPLAY, 8));
        moldura.add(painelDisplay);
        JLabel aviso = new JLabel("SIMULADOR DE URNA ELETRÔNICA  -  SEM VALOR OFICIAL", SwingConstants.CENTER);
        aviso.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        aviso.setForeground(TemaVisual.DETALHE);

        JPanel corpo = new JPanel(new BorderLayout(22, 12));
        corpo.setBackground(TemaVisual.FUNDOCORPO);
        corpo.setBorder(BorderFactory.createEmptyBorder(24, 24, 14, 24));
        corpo.add(moldura, BorderLayout.CENTER);
        corpo.add(new PainelTeclado(controlador), BorderLayout.EAST);
        corpo.add(aviso, BorderLayout.SOUTH);
        setContentPane(corpo);

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                confirmarFechamento();
            }
        });
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void atualizar(SessaoVotacao sessao) {
        painelDisplay.atualizar(sessao);
    }

    @Override
    public void exibirCarregando() {
        painelDisplay.exibirCarregando();
    }

    @Override
    public void exibirErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Urna", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public boolean perguntarSeTentaNovamente(String mensagem) {
        Object[] opcoes = {"Tentar de novo", "Não"};
        return JOptionPane.showOptionDialog(this, mensagem, "Urna", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[0]) == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean perguntarSeUsaDemonstracao() {
        Object[] opcoes = {"Usar dados de demonstração", "Fechar a urna"};
        return JOptionPane.showOptionDialog(this, "Abrir a urna com dados fictícios guardados só na memória?",
                "Urna", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0])
                == JOptionPane.YES_OPTION;
    }

    @Override
    public void encerrar() {
        dispose();
        System.exit(0);
    }

    private void confirmarFechamento() {
        if (controlador.possuiVotosNaoGravados() && JOptionPane.showConfirmDialog(this,
                "Há votos que ainda não foram gravados. Fechar mesmo assim?", "Urna",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        encerrar();
    }
}
