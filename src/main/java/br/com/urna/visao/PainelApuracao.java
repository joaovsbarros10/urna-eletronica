/*
 * Urna Eletrônica
 * Arquivo: PainelApuracao.java
 * Autor: João
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorAdministracao;
import br.com.urna.modelo.ApuracaoCargo;
import br.com.urna.modelo.Candidato;
import br.com.urna.modelo.Cargo;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Aba de apuração: votos de cada candidato do cargo escolhido, brancos, nulos e total.
 *
 * @author João
 */
public final class PainelApuracao extends PainelAdministrativo {

    private final JComboBox<Cargo> seletorCargo = new JComboBox<>(Cargo.listarEmOrdemDeVotacao().toArray(new Cargo[0]));

    private final DefaultTableModel modeloTabela = criarModeloTabela("Número", "Candidato", "Partido", "Votos");

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
                    candidato.getPartido().getSigla(), apuracao.getVotosDoCandidato(candidato)});
        }
        rotuloResumo.setText(apuracao.getCargo() + "    Válidos: " + apuracao.getVotosValidos()
                + "    Brancos: " + apuracao.getVotosBrancos() + "    Nulos: " + apuracao.getVotosNulos()
                + "    Total: " + apuracao.getTotalDeVotos());
    }
}
