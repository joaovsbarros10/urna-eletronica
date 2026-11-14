/*
 * Urna Eletrônica
 * Arquivo: JanelaAdministracao.java
 * Autor: João
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorAdministracao;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Janela da administração, com as abas de partidos, candidatos e apuração.
 *
 * @author João
 */
public final class JanelaAdministracao extends JFrame {

    private final JTabbedPane abas = new JTabbedPane();

    private final List<PainelAdministrativo> paineis;

    /**
     * Monta a janela com as três abas.
     *
     * @param controlador controlador da administração
     */
    public JanelaAdministracao(ControladorAdministracao controlador) {
        super("Urna Eletrônica - Administração");
        paineis = List.of(new PainelCadastroPartido(controlador), new PainelCadastroCandidato(controlador),
                new PainelApuracao(controlador));
        for (PainelAdministrativo painel : paineis) {
            abas.addTab(painel.getTitulo(), painel);
        }
        abas.addChangeListener(evento -> atualizarAbaSelecionada());
        setContentPane(abas);
        setSize(1000, 680);
        setLocationByPlatform(true);
    }

    /** Pede à aba visível que releia os dados. */
    public void atualizarAbaSelecionada() {
        paineis.get(abas.getSelectedIndex()).atualizar();
    }
}
