/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: Aplicacao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna;

import br.com.urna.controle.ControladorAdministracao;
import br.com.urna.controle.ControladorVotacao;
import br.com.urna.excecao.ConfiguracaoException;
import br.com.urna.negocio.FachadaUrna;
import br.com.urna.persistencia.CargaDemonstracao;
import br.com.urna.persistencia.RepositorioCandidatoMemoria;
import br.com.urna.persistencia.RepositorioPartidoMemoria;
import br.com.urna.persistencia.RepositorioVotoMemoria;
import br.com.urna.persistencia.supabase.ConexaoBancoDados;
import br.com.urna.persistencia.supabase.ConfiguracaoSupabase;
import br.com.urna.persistencia.supabase.RepositorioCandidatoSupabase;
import br.com.urna.persistencia.supabase.RepositorioPartidoSupabase;
import br.com.urna.persistencia.supabase.RepositorioVotoSupabase;
import br.com.urna.som.ReprodutorSom;
import br.com.urna.som.ReprodutorSomJavaSound;
import br.com.urna.som.ReprodutorSomMudo;
import br.com.urna.visao.JanelaAdministracao;
import br.com.urna.visao.JanelaUrna;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

/**
 * Raiz de composição e ponto de entrada da urna.
 * <p>
 * Lê a configuração, cria os repositórios, a Fachada, os controladores e as
 * janelas, e liga tudo por construtor; não há framework de injeção de
 * dependência. É a única classe que importa de todos os pacotes. Modos de abertura:
 * <ul>
 *   <li>sem argumento: a urna de votação, com o Supabase (role {@code urna_votacao});</li>
 *   <li>{@code administracao}: o módulo administrativo, com o Supabase (role {@code urna_administracao});</li>
 *   <li>{@code demonstracao}: a urna e o módulo administrativo juntos, com dados fictícios em memória.</li>
 * </ul>
 *
 * @author João
 * @since 1.0
 */
public final class Aplicacao {

    private static final String ARGUMENTOADMINISTRACAO = "administracao";

    private static final String ARGUMENTODEMONSTRACAO = "demonstracao";

    private Aplicacao() {
    }

    // ---- Ponto de entrada ----

    /**
     * Abre a urna. Toda a interface é criada na thread de eventos do Swing (RNF06).
     *
     * @param argumentos vazio, {@code administracao} ou {@code demonstracao}
     */
    public static void main(String[] argumentos) {
        List<String> opcoes = List.of(argumentos).stream().map(opcao -> opcao.toLowerCase(Locale.ROOT)).toList();
        SwingUtilities.invokeLater(() -> abrir(opcoes));
    }

    private static void abrir(List<String> opcoes) {
        aplicarAparencia();
        if (opcoes.contains(ARGUMENTODEMONSTRACAO)) {
            abrirDemonstracaoCompleta();
        } else if (opcoes.contains(ARGUMENTOADMINISTRACAO)) {
            abrirAdministracao();
        } else {
            abrirUrna();
        }
    }

    /* Metal em todo sistema: em alguns temas nativos o JButton ignora a cor de fundo (seção 9.1). */
    private static void aplicarAparencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException erro) {
            // Sem o Metal, a urna abre com a aparência padrão do Java.
        }
    }

    // ---- Modos de abertura ----

    private static void abrirUrna() {
        FachadaUrna fachadaDemonstracao = criarFachadaDemonstracao();
        FachadaUrna fachadaPrincipal;
        try {
            fachadaPrincipal = criarFachadaSupabase(ConfiguracaoSupabase.carregar());
        } catch (ConfiguracaoException erro) {
            avisarModoDemonstracao(erro.getMessage());
            fachadaPrincipal = fachadaDemonstracao;
        }
        ControladorVotacao controlador = new ControladorVotacao(fachadaPrincipal, criarReprodutorSom());
        controlador.definirFachadaDeDemonstracao(fachadaDemonstracao);
        abrirJanelaUrna(controlador);
    }

    private static void abrirAdministracao() {
        FachadaUrna fachada;
        try {
            fachada = criarFachadaSupabase(ConfiguracaoSupabase.carregarAdministracao());
        } catch (ConfiguracaoException erro) {
            avisarModoDemonstracao(erro.getMessage());
            fachada = criarFachadaDemonstracao();
        }
        JanelaAdministracao janela = new JanelaAdministracao(new ControladorAdministracao(fachada));
        janela.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        janela.setVisible(true);
    }

    private static void abrirDemonstracaoCompleta() {
        FachadaUrna fachada = criarFachadaDemonstracao();
        JanelaAdministracao janelaAdministracao = new JanelaAdministracao(new ControladorAdministracao(fachada));
        janelaAdministracao.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        janelaAdministracao.setVisible(true);
        abrirJanelaUrna(new ControladorVotacao(fachada, criarReprodutorSom()));
    }

    private static void abrirJanelaUrna(ControladorVotacao controlador) {
        JanelaUrna janela = new JanelaUrna(controlador);
        controlador.definirVisao(janela);
        janela.setVisible(true);
        controlador.iniciar();
    }

    private static void avisarModoDemonstracao(String motivo) {
        JOptionPane.showMessageDialog(null, motivo + "\n\nA urna vai abrir com os dados de demonstração em memória.",
                "Urna eletrônica", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---- Composição ----

    private static FachadaUrna criarFachadaSupabase(ConfiguracaoSupabase configuracao) {
        ConexaoBancoDados conexao = new ConexaoBancoDados(configuracao);
        RepositorioCandidatoSupabase repositorioCandidato = new RepositorioCandidatoSupabase(conexao);
        return new FachadaUrna(new RepositorioPartidoSupabase(conexao), repositorioCandidato,
                new RepositorioVotoSupabase(conexao, repositorioCandidato));
    }

    private static FachadaUrna criarFachadaDemonstracao() {
        RepositorioPartidoMemoria repositorioPartido = new RepositorioPartidoMemoria();
        RepositorioCandidatoMemoria repositorioCandidato = new RepositorioCandidatoMemoria();
        CargaDemonstracao.preencher(repositorioPartido, repositorioCandidato);
        return new FachadaUrna(repositorioPartido, repositorioCandidato, new RepositorioVotoMemoria());
    }

    private static ReprodutorSom criarReprodutorSom() {
        return ReprodutorSomJavaSound.estaDisponivel() ? new ReprodutorSomJavaSound() : new ReprodutorSomMudo();
    }
}
