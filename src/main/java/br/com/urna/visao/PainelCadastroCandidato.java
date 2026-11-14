/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: PainelCadastroCandidato.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.controle.ControladorAdministracao;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FichaCandidato;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Aba de cadastro de candidatos: incluir, editar e excluir, com foto
 * obrigatória (RF11 e RN06).
 * <p>
 * Os campos de vice e de suplentes só ficam habilitados para os cargos que os
 * exigem (RN07). A foto é escolhida com {@link JFileChooser}, conferida com
 * {@link ImageIO} e, se passar de 200 KB ou não for JPEG nem PNG, a aba
 * oferece reduzi-la para 300 por 400 pixels em JPEG.
 *
 * @author João
 * @since 1.0
 */
public class PainelCadastroCandidato extends PainelAdministrativo {

    private static final int TAMANHOMAXIMOFOTO = 200 * 1024;

    private static final int LARGURAFOTOREDUZIDA = 300;

    private static final int ALTURAFOTOREDUZIDA = 400;

    private static final int LARGURAPREVIA = 120;

    private static final int ALTURAPREVIA = 160;

    private final DefaultTableModel modeloTabela =
            criarModeloSomenteLeitura("Cargo", "Número", "Nome", "Partido", "Vice ou suplentes");

    private final JTable tabelaCandidatos = new JTable(modeloTabela);

    private final JComboBox<Cargo> seletorCargo =
            new JComboBox<>(Cargo.listarEmOrdemDeVotacao().toArray(new Cargo[0]));

    private final DefaultComboBoxModel<Partido> modeloPartidos = new DefaultComboBoxModel<>();

    private final JComboBox<Partido> seletorPartido = new JComboBox<>(modeloPartidos);

    private final JTextField campoNumero = new JTextField(6);

    private final JTextField campoNome = new JTextField(28);

    private final JTextField campoNomeVice = new JTextField(28);

    private final JTextField campoNomePrimeiroSuplente = new JTextField(28);

    private final JTextField campoNomeSegundoSuplente = new JTextField(28);

    private final JLabel rotuloPrevia = new JLabel("sem foto", SwingConstants.CENTER);

    private final List<Candidato> candidatosExibidos = new ArrayList<>();

    private Candidato candidatoSelecionado;

    private byte[] fotoEscolhida;

    /**
     * Monta a aba.
     *
     * @param controlador controlador do módulo administrativo
     */
    public PainelCadastroCandidato(ControladorAdministracao controlador) {
        super(controlador);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabelaCandidatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaCandidatos.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting()) {
                selecionarCandidato();
            }
        });
        seletorCargo.addActionListener(evento -> habilitarCamposDoCargo());
        seletorPartido.addActionListener(evento -> sugerirPrefixoDoPartido());
        add(new JScrollPane(tabelaCandidatos), BorderLayout.CENTER);
        add(montarFormulario(), BorderLayout.SOUTH);
        habilitarCamposDoCargo();
    }

    // ---- Montagem ----

    private JPanel montarFormulario() {
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBorder(BorderFactory.createTitledBorder("Dados do candidato"));
        adicionarCampo(campos, 0, "Cargo:", seletorCargo);
        adicionarCampo(campos, 1, "Partido:", seletorPartido);
        adicionarCampo(campos, 2, "Número:", campoNumero);
        adicionarCampo(campos, 3, "Nome na urna:", campoNome);
        adicionarCampo(campos, 4, "Nome do vice:", campoNomeVice);
        adicionarCampo(campos, 5, "1º suplente:", campoNomePrimeiroSuplente);
        adicionarCampo(campos, 6, "2º suplente:", campoNomeSegundoSuplente);

        rotuloPrevia.setPreferredSize(new Dimension(LARGURAPREVIA, ALTURAPREVIA));
        rotuloPrevia.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JButton botaoEscolherFoto = new JButton("Escolher foto...");
        botaoEscolherFoto.setToolTipText("JPEG ou PNG de até 200 KB, de preferência 300 x 400 pixels");
        botaoEscolherFoto.addActionListener(evento -> escolherFoto());
        JPanel painelFoto = new JPanel(new BorderLayout(0, 6));
        painelFoto.setBorder(BorderFactory.createTitledBorder("Foto 3x4"));
        JPanel moldura = new JPanel(new FlowLayout(FlowLayout.CENTER));
        moldura.add(rotuloPrevia);
        painelFoto.add(moldura, BorderLayout.CENTER);
        painelFoto.add(botaoEscolherFoto, BorderLayout.SOUTH);

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

        JPanel formulario = new JPanel(new BorderLayout(8, 0));
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(painelFoto, BorderLayout.EAST);
        formulario.add(botoes, BorderLayout.SOUTH);
        return formulario;
    }

    private static void adicionarCampo(JPanel campos, int linha, String rotulo, Component campo) {
        campos.add(new JLabel(rotulo), criarRestricao(0, linha));
        campos.add(campo, criarRestricao(1, linha));
    }

    // ---- Contrato da aba ----

    @Override
    public String getTitulo() {
        return "Candidatos";
    }

    @Override
    public void atualizar() {
        executarEmSegundoPlano(() -> {
            getControlador().carregarDadosEleitorais();
            return getControlador().listarCandidatos();
        }, this::exibirCandidatos);
    }

    // ---- Tabela e formulário ----

    private void exibirCandidatos(List<Candidato> candidatos) {
        candidatosExibidos.clear();
        candidatosExibidos.addAll(candidatos);
        modeloTabela.setRowCount(0);
        for (Candidato candidato : candidatos) {
            modeloTabela.addRow(new Object[] {candidato.getCargo().getDescricao(), candidato.getNumero(),
                    candidato.getNome(), candidato.getPartido().getSigla(),
                    String.join("  |  ", candidato.obterLinhasAdicionais())});
        }
        modeloPartidos.removeAllElements();
        modeloPartidos.addAll(getControlador().listarPartidos());
        limparFormulario();
    }

    private void selecionarCandidato() {
        int linha = tabelaCandidatos.getSelectedRow();
        if (linha < 0) {
            return;
        }
        candidatoSelecionado = candidatosExibidos.get(tabelaCandidatos.convertRowIndexToModel(linha));
        FichaCandidato ficha = candidatoSelecionado.gerarFicha();
        seletorCargo.setSelectedItem(ficha.getCargo());
        seletorPartido.setSelectedItem(ficha.getPartido());
        campoNumero.setText(ficha.getNumero());
        campoNome.setText(ficha.getNome());
        campoNomeVice.setText(ficha.getNomeVice());
        campoNomePrimeiroSuplente.setText(ficha.getNomePrimeiroSuplente());
        campoNomeSegundoSuplente.setText(ficha.getNomeSegundoSuplente());
        definirFoto(ficha.getFoto());
    }

    private void limparFormulario() {
        candidatoSelecionado = null;
        tabelaCandidatos.clearSelection();
        campoNumero.setText("");
        campoNome.setText("");
        campoNomeVice.setText("");
        campoNomePrimeiroSuplente.setText("");
        campoNomeSegundoSuplente.setText("");
        definirFoto(null);
        if (modeloPartidos.getSize() > 0) {
            seletorPartido.setSelectedIndex(0);
        }
    }

    private void habilitarCamposDoCargo() {
        Cargo cargo = (Cargo) seletorCargo.getSelectedItem();
        campoNomeVice.setEnabled(cargo != null && cargo.possuiVice());
        campoNomePrimeiroSuplente.setEnabled(cargo != null && cargo.possuiSuplentes());
        campoNomeSegundoSuplente.setEnabled(cargo != null && cargo.possuiSuplentes());
    }

    private void sugerirPrefixoDoPartido() {
        Partido partido = (Partido) seletorPartido.getSelectedItem();
        if (partido != null && campoNumero.getText().isBlank()) {
            campoNumero.setText(String.valueOf(partido.getNumero()));
        }
    }

    // ---- Foto ----

    private void escolherFoto() {
        JFileChooser seletorArquivo = new JFileChooser();
        seletorArquivo.setFileFilter(new FileNameExtensionFilter("Imagens JPEG ou PNG", "jpg", "jpeg", "png"));
        if (seletorArquivo.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(seletorArquivo.getSelectedFile().toPath());
            BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(bytes));
            if (imagem == null) {
                exibirErro("O arquivo escolhido não é uma imagem válida.");
                return;
            }
            if (bytes.length > TAMANHOMAXIMOFOTO || !possuiFormatoAceito(bytes)) {
                String pergunta = "A foto tem " + (bytes.length / 1024) + " KB ou não é JPEG nem PNG.\n"
                        + "O limite é 200 KB (RN06). Reduzir automaticamente para 300 x 400 pixels em JPEG?";
                if (!confirmar(pergunta)) {
                    return;
                }
                bytes = reduzirFoto(imagem);
            }
            definirFoto(bytes);
        } catch (IOException erro) {
            exibirErro("Não foi possível ler a foto: " + erro.getMessage());
        }
    }

    private void definirFoto(byte[] foto) {
        fotoEscolhida = foto;
        if (foto == null) {
            rotuloPrevia.setIcon(null);
            rotuloPrevia.setText("sem foto");
            return;
        }
        try {
            BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(foto));
            if (imagem == null) {
                rotuloPrevia.setIcon(null);
                rotuloPrevia.setText("foto inválida");
                return;
            }
            double escala = Math.min((double) LARGURAPREVIA / imagem.getWidth(),
                    (double) ALTURAPREVIA / imagem.getHeight());
            int largura = Math.max(1, (int) (imagem.getWidth() * escala));
            int altura = Math.max(1, (int) (imagem.getHeight() * escala));
            rotuloPrevia.setText("");
            rotuloPrevia.setIcon(new ImageIcon(imagem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH)));
        } catch (IOException erro) {
            rotuloPrevia.setIcon(null);
            rotuloPrevia.setText("foto inválida");
        }
    }

    private static boolean possuiFormatoAceito(byte[] bytes) {
        boolean jpeg = bytes.length > 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8;
        boolean png = bytes.length > 4 && (bytes[0] & 0xFF) == 0x89 && bytes[1] == 'P' && bytes[2] == 'N';
        return jpeg || png;
    }

    private static byte[] reduzirFoto(BufferedImage original) throws IOException {
        double proporcaoAlvo = (double) LARGURAFOTOREDUZIDA / ALTURAFOTOREDUZIDA;
        int largura = original.getWidth();
        int altura = original.getHeight();
        int larguraRecorte = largura;
        int alturaRecorte = (int) Math.round(largura / proporcaoAlvo);
        if (alturaRecorte > altura) {
            alturaRecorte = altura;
            larguraRecorte = (int) Math.round(altura * proporcaoAlvo);
        }
        int inicioHorizontal = (largura - larguraRecorte) / 2;
        int inicioVertical = (altura - alturaRecorte) / 2;

        BufferedImage reduzida = new BufferedImage(LARGURAFOTOREDUZIDA, ALTURAFOTOREDUZIDA, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = reduzida.createGraphics();
        try {
            pincel.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            pincel.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            pincel.setColor(Color.WHITE);
            pincel.fillRect(0, 0, LARGURAFOTOREDUZIDA, ALTURAFOTOREDUZIDA);
            pincel.drawImage(original, 0, 0, LARGURAFOTOREDUZIDA, ALTURAFOTOREDUZIDA,
                    inicioHorizontal, inicioVertical, inicioHorizontal + larguraRecorte,
                    inicioVertical + alturaRecorte, null);
        } finally {
            pincel.dispose();
        }
        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            ImageIO.write(reduzida, "jpg", saida);
            return saida.toByteArray();
        }
    }

    // ---- Ações ----

    private void salvar() {
        Cargo cargo = (Cargo) seletorCargo.getSelectedItem();
        FichaCandidato ficha = new FichaCandidato.Montador(cargo)
                .definirIdentificador(candidatoSelecionado == null ? 0 : candidatoSelecionado.getIdentificador())
                .definirPartido((Partido) seletorPartido.getSelectedItem())
                .definirNumero(campoNumero.getText().strip())
                .definirNome(campoNome.getText())
                .definirFoto(fotoEscolhida)
                .definirNomeVice(campoNomeVice.getText())
                .definirNomesDosSuplentes(campoNomePrimeiroSuplente.getText(), campoNomeSegundoSuplente.getText())
                .montar();
        executarEmSegundoPlano(() -> {
            getControlador().salvarCandidato(ficha);
            return getControlador().listarCandidatos();
        }, candidatos -> {
            exibirCandidatos(candidatos);
            exibirInformacao("Candidato salvo.");
        });
    }

    private void excluir() {
        if (candidatoSelecionado == null) {
            exibirErro("Selecione na tabela o candidato a excluir.");
            return;
        }
        Candidato candidato = candidatoSelecionado;
        if (!confirmar("Excluir o candidato " + candidato.getNumero() + " - " + candidato.getNome() + "?")) {
            return;
        }
        executarEmSegundoPlano(() -> {
            getControlador().excluirCandidato(candidato);
            return getControlador().listarCandidatos();
        }, this::exibirCandidatos);
    }
}
