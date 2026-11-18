/*
 * Urna Eletrônica
 * Arquivo: Aplicacao.java
 * Autor: João
 */

package br.com.urna;

import br.com.urna.controle.ControladorAdministracao;
import br.com.urna.controle.ControladorVotacao;
import br.com.urna.excecao.UrnaException;
import br.com.urna.negocio.FachadaUrna;
import br.com.urna.persistencia.RepositorioUrnaFirebase;
import br.com.urna.persistencia.RepositorioUrnaMemoria;
import br.com.urna.som.ReprodutorSom;
import br.com.urna.visao.JanelaAdministracao;
import br.com.urna.visao.JanelaUrna;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * Ponto de entrada. Cria o repositório, a fachada, os controladores e as janelas
 * e liga tudo. Argumentos aceitos:
 * <ul>
 *   <li>nenhum: abre a urna, usando o Firebase;</li>
 *   <li>{@code administracao}: abre o cadastro e a apuração, usando o Firebase;</li>
 *   <li>{@code demonstracao}: abre a urna e a administração com dados em memória.</li>
 * </ul>
 *
 * @author João
 */
public final class Aplicacao {

    private Aplicacao() {
    }

    /**
     * Abre a urna. As janelas são criadas na thread de eventos do Swing.
     *
     * @param argumentos modo de abertura
     */
    public static void main(String[] argumentos) {
        List<String> opcoes = List.of(argumentos);
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception erro) {
                System.err.println("Visual Metal indisponível: " + erro.getMessage());
            }
            if (opcoes.contains("demonstracao")) {
                FachadaUrna fachada = new FachadaUrna(RepositorioUrnaMemoria.criarComDadosDeDemonstracao());
                abrirAdministracao(fachada, WindowConstants.DISPOSE_ON_CLOSE);
                abrirUrna(fachada, null);
            } else if (opcoes.contains("administracao")) {
                abrirAdministracao(criarFachada(), WindowConstants.EXIT_ON_CLOSE);
            } else {
                abrirUrna(criarFachada(), new FachadaUrna(RepositorioUrnaMemoria.criarComDadosDeDemonstracao()));
            }
        });
    }

    private static FachadaUrna criarFachada() {
        try {
            return new FachadaUrna(RepositorioUrnaFirebase.carregar());
        } catch (UrnaException erro) {
            JOptionPane.showMessageDialog(null, erro.getMessage() + "\n\nA urna vai abrir com dados de demonstração.");
            return new FachadaUrna(RepositorioUrnaMemoria.criarComDadosDeDemonstracao());
        }
    }

    private static void abrirUrna(FachadaUrna fachada, FachadaUrna fachadaDemonstracao) {
        ControladorVotacao controlador = new ControladorVotacao(fachada, new ReprodutorSom());
        controlador.definirFachadaDemonstracao(fachadaDemonstracao);
        JanelaUrna janela = new JanelaUrna(controlador);
        controlador.definirVisao(janela);
        janela.setVisible(true);
        controlador.iniciar();
    }

    private static void abrirAdministracao(FachadaUrna fachada, int operacaoAoFechar) {
        JanelaAdministracao janela = new JanelaAdministracao(new ControladorAdministracao(fachada));
        janela.setDefaultCloseOperation(operacaoAoFechar);
        janela.setVisible(true);
        janela.atualizarAbaSelecionada();
    }
}
