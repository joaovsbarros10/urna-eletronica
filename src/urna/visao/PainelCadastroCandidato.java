/*
 * Urna Eletrônica
 * Arquivo: PainelCadastroCandidato.java
 * Autor: João
 */

package urna.visao;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import urna.controle.ControladorAdministracao;
import urna.modelo.Candidato;
import urna.modelo.Cargo;
import urna.modelo.Partido;

/**
 * Aba de cadastro de candidatos, com foto. Os campos de vice e de suplentes só
 * ficam habilitados para os cargos que os exigem.
 *
 * @author João
 */
public final class PainelCadastroCandidato extends PainelAdministrativo {

    private final DefaultTableModel modeloTabela =
            criarModeloTabela("Cargo", "Número", "Nome", "Partido", "Vice ou suplentes");

    private final JTable tabela = new JTable(modeloTabela);

    private final JComboBox<Cargo> seletorCargo = new JComboBox<>(Cargo.listarEmOrdemDeVotacao().toArray(new Cargo[0]));

    private final DefaultComboBoxModel<Partido> modeloPartidos = new DefaultComboBoxModel<>();

    private final JTextField campoNumero = new JTextField();

    private final JTextField campoNome = new JTextField();

    private final JTextField campoVice = new JTextField();

    private final JTextField campoPrimeiroSuplente = new JTextField();

    private final JTextField campoSegundoSuplente = new JTextField();

    private final JLabel rotuloFoto = new JLabel("sem foto", SwingConstants.CENTER);

    private List<Candidato> candidatos = List.of();

    private Candidato candidatoSelecionado;

    private byte[] foto;

    /**
     * Monta a aba.
     *
     * @param controlador controlador da administração
     */
    public PainelCadastroCandidato(ControladorAdministracao controlador) {
        super(controlador);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(evento -> selecionarCandidato());
        seletorCargo.addActionListener(evento -> habilitarComplementos());

        JPanel campos = new JPanel(new GridLayout(0, 2, 6, 4));
        campos.add(new JLabel("Cargo:"));
        campos.add(seletorCargo);
        campos.add(new JLabel("Partido:"));
        campos.add(new JComboBox<>(modeloPartidos));
        campos.add(new JLabel("Número:"));
        campos.add(campoNumero);
        campos.add(new JLabel("Nome na urna:"));
        campos.add(campoNome);
        campos.add(new JLabel("Nome do vice:"));
        campos.add(campoVice);
        campos.add(new JLabel("1º suplente:"));
        campos.add(campoPrimeiroSuplente);
        campos.add(new JLabel("2º suplente:"));
        campos.add(campoSegundoSuplente);

        rotuloFoto.setPreferredSize(new Dimension(120, 160));
        rotuloFoto.setBorder(BorderFactory.createLineBorder(TemaVisual.DETALHE));
        JPanel painelFoto = new JPanel(new BorderLayout(0, 6));
        painelFoto.add(rotuloFoto, BorderLayout.CENTER);
        painelFoto.add(criarBotao("Escolher foto...", this::escolherFoto), BorderLayout.SOUTH);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(criarBotao("Novo", this::limparFormulario));
        botoes.add(criarBotao("Salvar", this::salvar));
        botoes.add(criarBotao("Excluir", this::excluir));

        JPanel formulario = new JPanel(new BorderLayout(10, 0));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do candidato"));
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(painelFoto, BorderLayout.EAST);
        formulario.add(botoes, BorderLayout.SOUTH);

        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(formulario, BorderLayout.SOUTH);
        habilitarComplementos();
    }

    @Override
    public String getTitulo() {
        return "Candidatos";
    }

    @Override
    public void atualizar() {
        executarEmSegundoPlano(() -> {
            getControlador().carregarDados();
            return getControlador().listarCandidatos();
        }, this::exibirCandidatos);
    }

    private void exibirCandidatos(List<Candidato> lista) {
        candidatos = lista;
        modeloTabela.setRowCount(0);
        for (Candidato candidato : lista) {
            modeloTabela.addRow(new Object[] {candidato.getCargo(), candidato.getNumero(), candidato.getNome(),
                    candidato.getPartido().getSigla(), String.join(" | ", candidato.obterLinhasAdicionais())});
        }
        modeloPartidos.removeAllElements();
        modeloPartidos.addAll(getControlador().listarPartidos());
        limparFormulario();
    }

    private void selecionarCandidato() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        candidatoSelecionado = candidatos.get(linha);
        Cargo cargo = candidatoSelecionado.getCargo();
        List<String> complementos = candidatoSelecionado.getComplementos();
        seletorCargo.setSelectedItem(cargo);
        modeloPartidos.setSelectedItem(candidatoSelecionado.getPartido());
        campoNumero.setText(candidatoSelecionado.getNumero());
        campoNome.setText(candidatoSelecionado.getNome());
        campoVice.setText(cargo.possuiVice() ? complementos.get(0) : "");
        campoPrimeiroSuplente.setText(cargo.possuiSuplentes() ? complementos.get(0) : "");
        campoSegundoSuplente.setText(cargo.possuiSuplentes() ? complementos.get(1) : "");
        definirFoto(candidatoSelecionado.getFoto());
    }

    private void limparFormulario() {
        candidatoSelecionado = null;
        tabela.clearSelection();
        for (JTextField campo : List.of(campoNumero, campoNome, campoVice, campoPrimeiroSuplente, campoSegundoSuplente)) {
            campo.setText("");
        }
        if (modeloPartidos.getSize() > 0) {
            modeloPartidos.setSelectedItem(modeloPartidos.getElementAt(0));
        }
        definirFoto(null);
    }

    private void habilitarComplementos() {
        Cargo cargo = (Cargo) seletorCargo.getSelectedItem();
        campoVice.setEnabled(cargo.possuiVice());
        campoPrimeiroSuplente.setEnabled(cargo.possuiSuplentes());
        campoSegundoSuplente.setEnabled(cargo.possuiSuplentes());
    }

    private void escolherFoto() {
        JFileChooser seletor = new JFileChooser();
        seletor.setFileFilter(new FileNameExtensionFilter("Imagens JPEG ou PNG", "jpg", "jpeg", "png"));
        if (seletor.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            definirFoto(Files.readAllBytes(seletor.getSelectedFile().toPath()));
        } catch (IOException erro) {
            exibirMensagem("Não foi possível ler a foto.");
        }
    }

    private void definirFoto(byte[] bytes) {
        foto = bytes;
        rotuloFoto.setIcon(bytes == null ? null : PainelDisplay.criarIconeDaFoto(bytes));
        rotuloFoto.setText(rotuloFoto.getIcon() != null ? "" : bytes == null ? "sem foto" : "foto inválida");
    }

    private void salvar() {
        Cargo cargo = (Cargo) seletorCargo.getSelectedItem();
        Partido partido = (Partido) modeloPartidos.getSelectedItem();
        long identificador = candidatoSelecionado == null ? 0 : candidatoSelecionado.getIdentificador();
        List<String> complementos = cargo.possuiVice() ? List.of(campoVice.getText())
                : cargo.possuiSuplentes() ? List.of(campoPrimeiroSuplente.getText(), campoSegundoSuplente.getText())
                : List.of();
        String numero = campoNumero.getText().strip();
        String nome = campoNome.getText();
        byte[] fotoEscolhida = foto;
        executarEmSegundoPlano(() -> {
            getControlador().salvarCandidato(
                    Candidato.criar(identificador, cargo, numero, nome, partido, fotoEscolhida, complementos));
            return getControlador().listarCandidatos();
        }, this::exibirCandidatos);
    }

    private void excluir() {
        Candidato candidato = candidatoSelecionado;
        if (candidato == null || JOptionPane.showConfirmDialog(this, "Excluir o candidato " + candidato + "?",
                getTitulo(), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        executarEmSegundoPlano(() -> {
            getControlador().excluirCandidato(candidato);
            return getControlador().listarCandidatos();
        }, this::exibirCandidatos);
    }
}
