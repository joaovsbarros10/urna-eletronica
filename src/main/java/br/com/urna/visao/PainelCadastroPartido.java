/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: PainelCadastroPartido.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorAdministracao;
import br.com.urna.modelo.Partido;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Aba de cadastro de partidos: incluir, editar e excluir (RF11).
 * O número tem 2 dígitos e é único (RN03); partido com candidatos não pode
 * ser excluído nem trocar de número.
 *
 * @author João
 * @since 1.0
 */
public class PainelCadastroPartido extends PainelAdministrativo {

    private final DefaultTableModel modeloTabela = criarModeloSomenteLeitura("Número", "Sigla", "Nome");

    private final JTable tabelaPartidos = new JTable(modeloTabela);

    private final JTextField campoNumero = new JTextField(4);

    private final JTextField campoSigla = new JTextField(10);

    private final JTextField campoNome = new JTextField(30);

    private final List<Partido> partidosExibidos = new ArrayList<>();

    private Partido partidoSelecionado;

    /**
     * Monta a aba.
     *
     * @param controlador controlador do módulo administrativo
     */
    public PainelCadastroPartido(ControladorAdministracao controlador) {
        super(controlador);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabelaPartidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPartidos.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                selecionarPartido();
            }
        });
        add(new JScrollPane(tabelaPartidos), BorderLayout.CENTER);
        add(montarFormulario(), BorderLayout.SOUTH);
    }

    // ---- Montagem ----

    private JPanel montarFormulario() {
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBorder(BorderFactory.createTitledBorder("Dados do partido"));
        campos.add(new JLabel("Número (2 dígitos):"), criarRestricao(0, 0));
        campos.add(campoNumero, criarRestricao(1, 0));
        campos.add(new JLabel("Sigla:"), criarRestricao(0, 1));
        campos.add(campoSigla, criarRestricao(1, 1));
        campos.add(new JLabel("Nome:"), criarRestricao(0, 2));
        campos.add(campoNome, criarRestricao(1, 2));

        JButton botaoNovo = new JButton("Novo");
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoExcluir = new JButton("Excluir");
        botaoNovo.addActionListener(evento -> limparFormulario());
        botaoSalvar.addActionListener(evento -> salvar());
        botaoExcluir.addActionListener(evento -> excluir());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(botaoNovo);
        botoes.add(botaoSalvar);
        botoes.add(botaoExcluir);

        JPanel formulario = new JPanel(new BorderLayout());
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(botoes, BorderLayout.SOUTH);
        return formulario;
    }

    // ---- Contrato da aba ----

    @Override
    public String getTitulo() {
        return "Partidos";
    }

    @Override
    public void atualizar() {
        executarEmSegundoPlano(() -> {
            getControlador().carregarDadosEleitorais();
            return getControlador().listarPartidos();
        }, this::exibirPartidos);
    }

    // ---- Tabela e formulário ----

    private void exibirPartidos(List<Partido> partidos) {
        partidosExibidos.clear();
        partidosExibidos.addAll(partidos);
        modeloTabela.setRowCount(0);
        for (Partido partido : partidos) {
            modeloTabela.addRow(new Object[] {partido.getNumero(), partido.getSigla(), partido.getNome()});
        }
        limparFormulario();
    }

    private void selecionarPartido() {
        int linha = tabelaPartidos.getSelectedRow();
        if (linha < 0) {
            return;
        }
        partidoSelecionado = partidosExibidos.get(tabelaPartidos.convertRowIndexToModel(linha));
        campoNumero.setText(String.valueOf(partidoSelecionado.getNumero()));
        campoSigla.setText(partidoSelecionado.getSigla());
        campoNome.setText(partidoSelecionado.getNome());
    }

    private void limparFormulario() {
        partidoSelecionado = null;
        tabelaPartidos.clearSelection();
        campoNumero.setText("");
        campoSigla.setText("");
        campoNome.setText("");
        campoNumero.requestFocusInWindow();
    }

    // ---- Ações ----

    private void salvar() {
        long identificador = partidoSelecionado == null ? 0 : partidoSelecionado.getIdentificador();
        String numeroDigitado = campoNumero.getText();
        String sigla = campoSigla.getText();
        String nome = campoNome.getText();
        executarEmSegundoPlano(() -> {
            Partido partido = new Partido(identificador, converterNumero(numeroDigitado, "o número do partido"),
                    sigla, nome);
            getControlador().salvarPartido(partido);
            return getControlador().listarPartidos();
        }, partidos -> {
            exibirPartidos(partidos);
            exibirInformacao("Partido salvo.");
        });
    }

    private void excluir() {
        if (partidoSelecionado == null) {
            exibirErro("Selecione na tabela o partido a excluir.");
            return;
        }
        Partido partido = partidoSelecionado;
        if (!confirmar("Excluir o partido " + partido.getSigla() + " (" + partido.getNumero() + ")?")) {
            return;
        }
        executarEmSegundoPlano(() -> {
            getControlador().excluirPartido(partido);
            return getControlador().listarPartidos();
        }, this::exibirPartidos);
    }
}
