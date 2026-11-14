/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: JanelaUrna.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorVotacao;
import br.com.urna.controle.VisaoVotacao;
import br.com.urna.modelo.sessao.SessaoVotacao;
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
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Janela principal da urna: display à esquerda e teclado à direita, com
 * tamanho fixo. Implementa {@link VisaoVotacao}, a interface pela qual o
 * controlador fala com a tela, e se redesenha a cada aviso da sessão (Observer).
 * <p>
 * O corpo da urna deixa claro que é um simulador sem valor oficial, e a janela
 * não usa brasão nem logotipo da Justiça Eleitoral (seção 14.1).
 *
 * @author João
 * @since 1.0
 */
public class JanelaUrna extends JFrame implements VisaoVotacao {

    private static final String TITULO = "Urna";

    private static final String TEXTOCORPO = "SIMULADOR DE URNA ELETRÔNICA  -  SEM VALOR OFICIAL";

    private final PainelDisplay painelDisplay = new PainelDisplay();

    private final PainelTeclado painelTeclado;

    private final ControladorVotacao controlador;

    /**
     * Monta a janela e liga o teclado ao controlador. Crie na thread de eventos.
     *
     * @param controlador controlador da votação
     */
    public JanelaUrna(ControladorVotacao controlador) {
        super(TITULO);
        this.controlador = controlador;
        this.painelTeclado = new PainelTeclado(controlador);
        montarCorpo();
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

    // ---- Montagem ----

    private void montarCorpo() {
        JPanel corpo = new JPanel(new BorderLayout(22, 12));
        corpo.setBackground(TemaVisual.FUNDOCORPO);
        corpo.setBorder(BorderFactory.createEmptyBorder(24, 24, 14, 24));

        JPanel moldura = new JPanel(new BorderLayout());
        moldura.setBorder(BorderFactory.createLineBorder(TemaVisual.MOLDURADISPLAY, 8));
        moldura.add(painelDisplay, BorderLayout.CENTER);

        JLabel rotuloCorpo = new JLabel(TEXTOCORPO, SwingConstants.CENTER);
        rotuloCorpo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        rotuloCorpo.setForeground(TemaVisual.DETALHEDISPLAY);

        corpo.add(moldura, BorderLayout.CENTER);
        corpo.add(painelTeclado, BorderLayout.EAST);
        corpo.add(rotuloCorpo, BorderLayout.SOUTH);
        setContentPane(corpo);
    }

    // ---- VisaoVotacao ----

    @Override
    public void aoAtualizarSessao(SessaoVotacao sessao) {
        executarNaThreadDeEventos(() -> painelDisplay.atualizar(sessao));
    }

    @Override
    public void exibirCarregando() {
        executarNaThreadDeEventos(painelDisplay::exibirCarregando);
    }

    @Override
    public void exibirErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Urna", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public boolean perguntarSeTentaNovamente(String mensagem) {
        Object[] opcoes = {"Tentar de novo", "Não"};
        int escolha = JOptionPane.showOptionDialog(this, mensagem, "Urna", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[0]);
        return escolha == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean perguntarSeUsaDemonstracao() {
        Object[] opcoes = {"Usar dados de demonstração", "Fechar a urna"};
        int escolha = JOptionPane.showOptionDialog(this,
                "Abrir a urna com partidos e candidatos fictícios guardados só na memória?\n"
                        + "Os votos dessa demonstração não vão para o banco.",
                "Urna", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
        return escolha == JOptionPane.YES_OPTION;
    }

    @Override
    public void encerrar() {
        dispose();
        System.exit(0);
    }

    // ---- Fechamento ----

    private void confirmarFechamento() {
        if (controlador.possuiVotosNaoGravados()) {
            int escolha = JOptionPane.showConfirmDialog(this,
                    "Há votos de um eleitor que ainda não foram gravados no banco.\n"
                            + "Se fechar agora, eles serão perdidos. Fechar mesmo assim?",
                    "Urna", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (escolha != JOptionPane.YES_OPTION) {
                return;
            }
        }
        encerrar();
    }

    private static void executarNaThreadDeEventos(Runnable tarefa) {
        if (SwingUtilities.isEventDispatchThread()) {
            tarefa.run();
        } else {
            SwingUtilities.invokeLater(tarefa);
        }
    }
}
