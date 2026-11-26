/*
 * Urna Eletrônica
 * Arquivo: PainelCadastroPartido.java
 * Autor: João
 */

package urna.visao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import urna.controle.ControladorAdministracao;
import urna.modelo.Partido;

/**
 * Aba de cadastro de partidos: incluir, editar e excluir.
 *
 * @author João
 */
public final class PainelCadastroPartido extends PainelAdministrativo {

    private final DefaultTableModel modeloTabela = criarModeloTabela("Número", "Sigla", "Nome");

    private final JTable tabela = new JTable(modeloTabela);

    private final JTextField campoNumero = new JTextField(4);

    private final JTextField campoSigla = new JTextField(8);

    private final JTextField campoNome = new JTextField(25);

    private List<Partido> partidos = List.of();

    private Partido partidoSelecionado;

    /**
     * Monta a aba.
     *
     * @param controlador controlador da administração
     */
    public PainelCadastroPartido(ControladorAdministracao controlador) {
        super(controlador);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(evento -> selecionarPartido());

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do partido"));
        formulario.add(new JLabel("Número:"));
        formulario.add(campoNumero);
        formulario.add(new JLabel("Sigla:"));
        formulario.add(campoSigla);
        formulario.add(new JLabel("Nome:"));
        formulario.add(campoNome);
        formulario.add(criarBotao("Novo", this::limparFormulario));
        formulario.add(criarBotao("Salvar", this::salvar));
        formulario.add(criarBotao("Excluir", this::excluir));

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(formulario, BorderLayout.SOUTH);
    }

    @Override
    public String getTitulo() {
        return "Partidos";
    }

    @Override
    public void atualizar() {
        executarEmSegundoPlano(() -> {
            getControlador().carregarDados();
            return getControlador().listarPartidos();
        }, this::exibirPartidos);
    }

    private void exibirPartidos(List<Partido> lista) {
        partidos = lista;
        modeloTabela.setRowCount(0);
        for (Partido partido : lista) {
            modeloTabela.addRow(new Object[] {partido.getNumero(), partido.getSigla(), partido.getNome()});
        }
        limparFormulario();
    }

    private void selecionarPartido() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            partidoSelecionado = partidos.get(linha);
            campoNumero.setText(String.valueOf(partidoSelecionado.getNumero()));
            campoSigla.setText(partidoSelecionado.getSigla());
            campoNome.setText(partidoSelecionado.getNome());
        }
    }

    private void limparFormulario() {
        partidoSelecionado = null;
        tabela.clearSelection();
        campoNumero.setText("");
        campoSigla.setText("");
        campoNome.setText("");
    }

    private void salvar() {
        long identificador = partidoSelecionado == null ? 0 : partidoSelecionado.getIdentificador();
        String numero = campoNumero.getText().strip();
        String sigla = campoSigla.getText();
        String nome = campoNome.getText();
        executarEmSegundoPlano(() -> {
            if (!numero.matches("[0-9]{2}")) {
                throw new IllegalArgumentException("O número do partido deve ter 2 dígitos.");
            }
            getControlador().salvarPartido(new Partido(identificador, Integer.parseInt(numero), sigla, nome));
            return getControlador().listarPartidos();
        }, this::exibirPartidos);
    }

    private void excluir() {
        Partido partido = partidoSelecionado;
        if (partido == null || JOptionPane.showConfirmDialog(this, "Excluir o partido " + partido + "?",
                getTitulo(), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        executarEmSegundoPlano(() -> {
            getControlador().excluirPartido(partido);
            return getControlador().listarPartidos();
        }, this::exibirPartidos);
    }
}
