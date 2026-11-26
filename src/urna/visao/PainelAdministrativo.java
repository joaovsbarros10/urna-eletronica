/*
 * Urna Eletrônica
 * Arquivo: PainelAdministrativo.java
 * Autor: João
 */

package urna.visao;

import java.awt.Cursor;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import urna.controle.ControladorAdministracao;

/**
 * Base das abas da administração. Cada aba tem um título e sabe se atualizar;
 * a janela chama {@link #atualizar()} sem saber qual aba é.
 *
 * @author João
 */
public abstract class PainelAdministrativo extends JPanel {

    private final ControladorAdministracao controlador;

    /**
     * Cria a aba ligada ao controlador.
     *
     * @param controlador controlador da administração
     */
    protected PainelAdministrativo(ControladorAdministracao controlador) {
        this.controlador = controlador;
    }

    /**
     * Devolve o título da aba.
     *
     * @return texto da aba
     */
    public abstract String getTitulo();

    /** Relê os dados e redesenha a aba. */
    public abstract void atualizar();

    protected ControladorAdministracao getControlador() {
        return controlador;
    }

    /**
     * Mostra uma mensagem para o usuário.
     *
     * @param mensagem texto da mensagem
     */
    protected void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, getTitulo(), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Executa uma tarefa de banco fora da thread de eventos e entrega o resultado
     * na tela. Se der erro, mostra a mensagem.
     *
     * @param tarefa      tarefa demorada
     * @param aoConcluir  o que fazer com o resultado
     * @param <Resultado> tipo do resultado
     */
    protected <Resultado> void executarEmSegundoPlano(Callable<Resultado> tarefa, Consumer<Resultado> aoConcluir) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<Resultado, Void>() {
            @Override
            protected Resultado doInBackground() throws Exception {
                return tarefa.call();
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    aoConcluir.accept(get());
                } catch (ExecutionException erro) {
                    exibirMensagem(erro.getCause().getMessage());
                } catch (InterruptedException erro) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    /**
     * Cria um botão que executa a ação ao ser clicado.
     *
     * @param texto texto do botão
     * @param acao  ação do botão
     * @return o botão
     */
    protected static JButton criarBotao(String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.addActionListener(evento -> acao.run());
        return botao;
    }

    /**
     * Cria o modelo de uma tabela que o usuário não edita direto.
     *
     * @param colunas títulos das colunas
     * @return modelo de tabela somente leitura
     */
    protected static DefaultTableModel criarModeloTabela(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }
}
