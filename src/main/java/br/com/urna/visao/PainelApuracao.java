/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: PainelApuracao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorAdministracao;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.voto.ApuracaoCargo;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Aba de apuração (RF12): boletim simplificado de um cargo, com votos por
 * candidato, brancos, nulos e total.
 *
 * @author João
 * @since 1.0
 */
public class PainelApuracao extends PainelAdministrativo {

    private final JComboBox<Cargo> seletorCargo =
            new JComboBox<>(Cargo.listarEmOrdemDeVotacao().toArray(new Cargo[0]));

    private final DefaultTableModel modeloTabela =
            criarModeloSomenteLeitura("Número", "Candidato", "Partido", "Votos", "% dos válidos");

    private final JTable tabelaResultados = new JTable(modeloTabela);

    private final JLabel rotuloResumo = new JLabel(" ");

    /**
     * Monta a aba.
     *
     * @param controlador controlador do módulo administrativo
     */
    public PainelApuracao(ControladorAdministracao controlador) {
        super(controlador);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton botaoApurar = new JButton("Apurar");
        botaoApurar.addActionListener(evento -> atualizar());
        seletorCargo.addActionListener(evento -> atualizar());
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JLabel("Cargo:"));
        barra.add(seletorCargo);
        barra.add(botaoApurar);

        rotuloResumo.setFont(rotuloResumo.getFont().deriveFont(Font.BOLD, 14f));
        rotuloResumo.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(tabelaResultados), BorderLayout.CENTER);
        add(rotuloResumo, BorderLayout.SOUTH);
    }

    // ---- Contrato da aba ----

    @Override
    public String getTitulo() {
        return "Apuração";
    }

    @Override
    public void atualizar() {
        Cargo cargo = (Cargo) seletorCargo.getSelectedItem();
        executarEmSegundoPlano(() -> {
            getControlador().carregarDadosEleitorais();
            return getControlador().apurarVotos(cargo);
        }, this::exibirApuracao);
    }

    // ---- Boletim ----

    private void exibirApuracao(ApuracaoCargo apuracao) {
        modeloTabela.setRowCount(0);
        int votosValidos = apuracao.getVotosValidos();
        for (Candidato candidato : apuracao.listarCandidatosEmOrdemDeVotos()) {
            int votos = apuracao.getVotosDoCandidato(candidato);
            modeloTabela.addRow(new Object[] {candidato.getNumero(), candidato.getNome(),
                    candidato.getPartido().getSigla(), votos, formatarPercentual(votos, votosValidos)});
        }
        rotuloResumo.setText(apuracao.getCargo().getDescricao()
                + "    Válidos: " + votosValidos
                + "    Brancos: " + apuracao.getVotosBrancos()
                + "    Nulos: " + apuracao.getVotosNulos()
                + "    Total: " + apuracao.getTotalDeVotos());
    }

    private static String formatarPercentual(int parte, int total) {
        if (total == 0) {
            return "-";
        }
        return String.format(Locale.forLanguageTag("pt-BR"), "%.1f%%", 100.0 * parte / total);
    }
}
