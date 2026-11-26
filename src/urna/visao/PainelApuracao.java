/*
 * Urna Eletrônica
 * Arquivo: PainelApuracao.java
 * Autor: João
 */

package urna.visao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import urna.controle.ControladorAdministracao;
import urna.modelo.ApuracaoCargo;
import urna.modelo.Candidato;
import urna.modelo.Cargo;

/**
 * Aba de apuração: votos e porcentagem de cada candidato do cargo escolhido,
 * brancos, nulos e total.
 *
 * @author João
 */
public final class PainelApuracao extends PainelAdministrativo {

    private final JComboBox<Cargo> seletorCargo = new JComboBox<>(Cargo.listarEmOrdemDeVotacao().toArray(new Cargo[0]));

    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private final DefaultTableModel modeloTabela =
            criarModeloTabela("Número", "Candidato", "Partido", "Votos", "% dos válidos");

    private final JLabel rotuloResumo = new JLabel(" ");

    /**
     * Monta a aba.
     *
     * @param controlador controlador da administração
     */
    public PainelApuracao(ControladorAdministracao controlador) {
        super(controlador);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        seletorCargo.addActionListener(evento -> atualizar());

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JLabel("Cargo:"));
        barra.add(seletorCargo);
        barra.add(criarBotao("Apurar", this::atualizar));
        rotuloResumo.setFont(rotuloResumo.getFont().deriveFont(Font.BOLD, 14f));

        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modeloTabela)), BorderLayout.CENTER);
        add(rotuloResumo, BorderLayout.SOUTH);
    }

    @Override
    public String getTitulo() {
        return "Apuração";
    }

    @Override
    public void atualizar() {
        Cargo cargo = (Cargo) seletorCargo.getSelectedItem();
        executarEmSegundoPlano(() -> {
            getControlador().carregarDados();
            return getControlador().apurar(cargo);
        }, this::exibirApuracao);
    }

    private void exibirApuracao(ApuracaoCargo apuracao) {
        modeloTabela.setRowCount(0);
        for (Candidato candidato : apuracao.listarCandidatosPorVotos()) {
            modeloTabela.addRow(new Object[] {candidato.getNumero(), candidato.getNome(),
                    candidato.getPartido().getSigla(), apuracao.getVotosDoCandidato(candidato),
                    formatarPercentual(apuracao.calcularPercentualDoCandidato(candidato))});
        }
        rotuloResumo.setText(apuracao.getCargo()
                + "    Válidos: " + descrever(apuracao, apuracao.getVotosValidos())
                + "    Brancos: " + descrever(apuracao, apuracao.getVotosBrancos())
                + "    Nulos: " + descrever(apuracao, apuracao.getVotosNulos())
                + "    Total: " + apuracao.getTotalDeVotos());
    }

    /** Mostra a quantidade e a porcentagem sobre o total, por exemplo "3 (75,00%)". */
    private static String descrever(ApuracaoCargo apuracao, int votos) {
        return votos + " (" + formatarPercentual(apuracao.calcularPercentualDoTotal(votos)) + ")";
    }

    private static String formatarPercentual(double percentual) {
        return String.format(BRASIL, "%.2f%%", percentual);
    }
}
