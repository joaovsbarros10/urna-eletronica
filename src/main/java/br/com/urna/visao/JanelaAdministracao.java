/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: JanelaAdministracao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorAdministracao;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Janela do módulo administrativo, com uma aba para partidos, uma para
 * candidatos e uma para a apuração (RF11 e RF12).
 *
 * @author João
 * @since 1.0
 */
public class JanelaAdministracao extends JFrame {

    private static final String TITULO = "Urna Eletrônica - Administração";

    private final JTabbedPane abas = new JTabbedPane();

    private final List<PainelAdministrativo> paineis = new ArrayList<>();

    /**
     * Monta a janela com as três abas. Crie na thread de eventos.
     *
     * @param controlador controlador do módulo administrativo
     */
    public JanelaAdministracao(ControladorAdministracao controlador) {
        super(TITULO);
        adicionarPainel(new PainelCadastroPartido(controlador));
        adicionarPainel(new PainelCadastroCandidato(controlador));
        adicionarPainel(new PainelApuracao(controlador));
        abas.addChangeListener(evento -> atualizarPainelSelecionado());
        setContentPane(abas);
        setMinimumSize(new Dimension(900, 600));
        setSize(1000, 680);
        setLocationByPlatform(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent evento) {
                atualizarPainelSelecionado();
            }
        });
    }

    // ---- Abas ----

    /**
     * Acrescenta uma aba à janela.
     *
     * @param painel painel da aba
     */
    public void adicionarPainel(PainelAdministrativo painel) {
        paineis.add(painel);
        abas.addTab(painel.getTitulo(), painel);
    }

    /** Pede à aba visível que releia os dados, sem saber qual aba é. */
    public void atualizarPainelSelecionado() {
        int posicao = abas.getSelectedIndex();
        if (posicao >= 0) {
            paineis.get(posicao).atualizar();
        }
    }
}
