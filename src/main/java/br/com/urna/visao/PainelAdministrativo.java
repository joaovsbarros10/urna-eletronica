/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: PainelAdministrativo.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorAdministracao;
import br.com.urna.excecao.UrnaException;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Base das abas do módulo administrativo: título, atualização, mensagens e
 * execução de tarefas de banco fora da thread de eventos (RNF06).
 * <p>
 * A {@link JanelaAdministracao} guarda as abas como {@code PainelAdministrativo}
 * e chama {@link #atualizar()} quando o usuário troca de aba, sem saber qual
 * painel é qual (polimorfismo na visão).
 *
 * @author João
 * @since 1.0
 */
public abstract class PainelAdministrativo extends JPanel {

    private final ControladorAdministracao controlador;

    /**
     * Cria a aba ligada ao controlador.
     *
     * @param controlador controlador do módulo administrativo
     */
    protected PainelAdministrativo(ControladorAdministracao controlador) {
        this.controlador = controlador;
    }

    // ---- Contrato das abas ----

    /**
     * Devolve o título da aba.
     *
     * @return texto da aba
     */
    public abstract String getTitulo();

    /** Relê os dados e redesenha a aba. É chamado quando a aba é selecionada. */
    public abstract void atualizar();

    // ---- Recursos comuns ----

    protected ControladorAdministracao getControlador() {
        return controlador;
    }

    /**
     * Mostra uma mensagem de erro.
     *
     * @param mensagem texto do erro
     */
    protected void exibirErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, getTitulo(), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra uma mensagem informativa.
     *
     * @param mensagem texto da mensagem
     */
    protected void exibirInformacao(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, getTitulo(), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Pede confirmação ao usuário.
     *
     * @param pergunta pergunta de sim ou não
     * @return {@code true} se a resposta for sim
     */
    protected boolean confirmar(String pergunta) {
        return JOptionPane.showConfirmDialog(this, pergunta, getTitulo(), JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    /**
     * Executa a tarefa fora da thread de eventos e entrega o resultado na thread
     * de eventos. Erros de regra, de banco e de dado inválido viram mensagem na tela.
     *
     * @param tarefa      trabalho demorado, por exemplo uma chamada ao banco
     * @param aoConcluir  o que fazer com o resultado, já na thread de eventos
     * @param <Resultado> tipo do resultado da tarefa
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
                    Throwable causa = erro.getCause();
                    boolean previsto = causa instanceof UrnaException || causa instanceof IllegalArgumentException;
                    exibirErro(previsto ? causa.getMessage() : "Erro inesperado: " + causa);
                } catch (InterruptedException erro) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    /**
     * Converte o texto de um campo numérico, com mensagem clara se não for número.
     *
     * @param texto     texto digitado
     * @param descricao nome do campo, para a mensagem de erro
     * @return o número
     * @throws IllegalArgumentException se o texto não for um número inteiro
     */
    protected static int converterNumero(String texto, String descricao) {
        try {
            return Integer.parseInt(texto.strip());
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("Informe " + descricao + " só com dígitos.", erro);
        }
    }

    /**
     * Cria uma tabela de dados que o usuário não edita diretamente.
     *
     * @param colunas títulos das colunas
     * @return modelo de tabela somente leitura
     */
    protected static DefaultTableModel criarModeloSomenteLeitura(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    /**
     * Cria a restrição de posição de um campo de formulário.
     *
     * @param coluna coluna da grade
     * @param linha  linha da grade
     * @return restrição com espaçamento padrão
     */
    protected static GridBagConstraints criarRestricao(int coluna, int linha) {
        GridBagConstraints restricao = new GridBagConstraints();
        restricao.gridx = coluna;
        restricao.gridy = linha;
        restricao.insets = new Insets(4, 6, 4, 6);
        restricao.anchor = GridBagConstraints.WEST;
        restricao.fill = coluna % 2 == 1 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        restricao.weightx = coluna % 2 == 1 ? 1 : 0;
        return restricao;
    }
}
