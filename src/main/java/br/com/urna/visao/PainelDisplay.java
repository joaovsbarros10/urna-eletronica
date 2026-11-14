/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: PainelDisplay.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.visao;

import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.sessao.SessaoVotacao;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * Display da urna: cargo, caixas de dígito, foto, nome, partido, linhas
 * adicionais (vice ou suplentes), aviso e instruções (seção 9.3).
 * <p>
 * O painel não decide nada: lê a sessão e desenha. As linhas de vice ou
 * suplentes vêm de {@link Candidato#obterLinhasAdicionais()}, sem {@code if}
 * por cargo, e o aviso vem de {@link SessaoVotacao#getAvisoPrincipal()}.
 * Os textos fixos estão concentrados nas constantes abaixo.
 *
 * @author João
 * @since 1.0
 */
public class PainelDisplay extends JPanel {

    private static final String TEXTOSEUVOTOPARA = "SEU VOTO PARA";

    private static final String TEXTONUMERO = "Número:";

    private static final String TEXTONOME = "Nome: ";

    private static final String TEXTOPARTIDO = "Partido: ";

    private static final String TEXTOAPERTEATECLA = "Aperte a tecla:";

    private static final String TEXTOCONFIRMAR = "VERDE para CONFIRMAR este voto";

    private static final String TEXTOREINICIAR = "LARANJA para REINICIAR este voto";

    private static final String TEXTOCARREGANDO = "CARREGANDO...";

    private static final String TEXTOSEMFOTO = "sem foto";

    private static final String CARTAOVOTACAO = "votacao";

    private static final String CARTAOMENSAGEM = "mensagem";

    private static final int LARGURAFOTO = 120;

    private static final int ALTURAFOTO = 160;

    private static final String FONTE = Font.SANS_SERIF;

    private final CardLayout cartoes = new CardLayout();

    private final JPanel painelVotacao = new JPanel(new GridBagLayout());

    private final JLabel rotuloMensagemCentral = new JLabel("", SwingConstants.CENTER);

    private final JLabel rotuloCargo = criarRotulo(Font.BOLD, 26);

    private final JPanel painelCamposDigito = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));

    private final List<CampoDigito> camposDigito = new ArrayList<>();

    private final JLabel rotuloNome = criarRotulo(Font.PLAIN, 18);

    private final JLabel rotuloPartido = criarRotulo(Font.PLAIN, 18);

    private final JLabel rotuloFoto = new JLabel("", SwingConstants.CENTER);

    private final JPanel painelLinhasAdicionais = new JPanel();

    private final JLabel rotuloAviso = criarRotulo(Font.BOLD, 20);

    private final JLabel rotuloAvisoDestaque = criarRotulo(Font.BOLD, 36);

    private final JPanel painelInstrucao = new JPanel(new BorderLayout());

    private final JLabel rotuloInstrucao = criarRotulo(Font.PLAIN, 14);

    /** Monta o display vazio. */
    public PainelDisplay() {
        setLayout(cartoes);
        setPreferredSize(new Dimension(640, 440));
        setBackground(TemaVisual.FUNDODISPLAY);
        montarPainelVotacao();
        rotuloMensagemCentral.setForeground(TemaVisual.TEXTODISPLAY);
        JPanel painelMensagem = new JPanel(new BorderLayout());
        painelMensagem.setBackground(TemaVisual.FUNDODISPLAY);
        painelMensagem.add(rotuloMensagemCentral, BorderLayout.CENTER);
        add(painelVotacao, CARTAOVOTACAO);
        add(painelMensagem, CARTAOMENSAGEM);
    }

    // ---- Montagem ----

    private void montarPainelVotacao() {
        painelVotacao.setBackground(TemaVisual.FUNDODISPLAY);
        painelVotacao.setBorder(BorderFactory.createEmptyBorder(16, 22, 12, 22));

        JLabel rotuloSeuVotoPara = criarRotulo(Font.PLAIN, 14);
        rotuloSeuVotoPara.setText(TEXTOSEUVOTOPARA);
        rotuloSeuVotoPara.setForeground(TemaVisual.DETALHEDISPLAY);
        rotuloCargo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel rotuloNumero = criarRotulo(Font.PLAIN, 18);
        rotuloNumero.setText(TEXTONUMERO);
        JPanel linhaNumero = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaNumero.setOpaque(false);
        painelCamposDigito.setOpaque(false);
        linhaNumero.add(rotuloNumero);
        linhaNumero.add(painelCamposDigito);

        painelLinhasAdicionais.setLayout(new BoxLayout(painelLinhasAdicionais, BoxLayout.Y_AXIS));
        painelLinhasAdicionais.setOpaque(false);
        rotuloAviso.setHorizontalAlignment(SwingConstants.CENTER);
        rotuloAvisoDestaque.setHorizontalAlignment(SwingConstants.CENTER);

        rotuloFoto.setPreferredSize(new Dimension(LARGURAFOTO, ALTURAFOTO));
        rotuloFoto.setForeground(TemaVisual.DETALHEDISPLAY);

        montarInstrucao();

        adicionar(rotuloSeuVotoPara, 0, GridBagConstraints.WEST, 0);
        adicionar(rotuloCargo, 1, GridBagConstraints.CENTER, 10);
        adicionar(linhaNumero, 2, GridBagConstraints.WEST, 18);
        adicionar(rotuloNome, 3, GridBagConstraints.WEST, 16);
        adicionar(rotuloPartido, 4, GridBagConstraints.WEST, 6);
        adicionar(painelLinhasAdicionais, 5, GridBagConstraints.WEST, 6);
        adicionar(rotuloAviso, 6, GridBagConstraints.CENTER, 10);
        adicionar(rotuloAvisoDestaque, 7, GridBagConstraints.CENTER, 4);

        GridBagConstraints restricaoEspaco = new GridBagConstraints();
        restricaoEspaco.gridx = 0;
        restricaoEspaco.gridy = 8;
        restricaoEspaco.weighty = 1;
        painelVotacao.add(new JLabel(), restricaoEspaco);

        GridBagConstraints restricaoFoto = new GridBagConstraints();
        restricaoFoto.gridx = 1;
        restricaoFoto.gridy = 0;
        restricaoFoto.gridheight = 6;
        restricaoFoto.anchor = GridBagConstraints.NORTHEAST;
        painelVotacao.add(rotuloFoto, restricaoFoto);

        GridBagConstraints restricaoInstrucao = new GridBagConstraints();
        restricaoInstrucao.gridx = 0;
        restricaoInstrucao.gridy = 9;
        restricaoInstrucao.gridwidth = 2;
        restricaoInstrucao.fill = GridBagConstraints.HORIZONTAL;
        painelVotacao.add(painelInstrucao, restricaoInstrucao);
    }

    private void montarInstrucao() {
        painelInstrucao.setOpaque(false);
        JSeparator separador = new JSeparator();
        separador.setForeground(TemaVisual.BORDACAMPO);
        painelInstrucao.add(separador, BorderLayout.NORTH);

        JPanel linhasDaInstrucao = new JPanel();
        linhasDaInstrucao.setLayout(new BoxLayout(linhasDaInstrucao, BoxLayout.Y_AXIS));
        linhasDaInstrucao.setOpaque(false);
        linhasDaInstrucao.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        rotuloInstrucao.setText(TEXTOAPERTEATECLA);
        linhasDaInstrucao.add(rotuloInstrucao);
        for (String texto : List.of(TEXTOCONFIRMAR, TEXTOREINICIAR)) {
            JLabel rotulo = criarRotulo(Font.PLAIN, 14);
            rotulo.setText(texto);
            rotulo.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
            linhasDaInstrucao.add(rotulo);
        }
        painelInstrucao.add(linhasDaInstrucao, BorderLayout.CENTER);
    }

    private void adicionar(Component componente, int linha, int ancora, int espacoAcima) {
        GridBagConstraints restricao = new GridBagConstraints();
        restricao.gridx = 0;
        restricao.gridy = linha;
        restricao.weightx = 1;
        restricao.anchor = ancora;
        restricao.fill = ancora == GridBagConstraints.CENTER ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        restricao.insets = new Insets(espacoAcima, 0, 0, 0);
        painelVotacao.add(componente, restricao);
    }

    private static JLabel criarRotulo(int estilo, int tamanho) {
        JLabel rotulo = new JLabel();
        rotulo.setFont(new Font(FONTE, estilo, tamanho));
        rotulo.setForeground(TemaVisual.TEXTODISPLAY);
        return rotulo;
    }

    // ---- Desenho a partir da sessão ----

    /**
     * Redesenha o display a partir do estado da sessão. Chame na thread de eventos.
     *
     * @param sessao sessão a desenhar
     */
    public void atualizar(SessaoVotacao sessao) {
        if (sessao.estaEncerrada()) {
            exibirMensagemCentral(sessao.getAvisoPrincipal(), 140);
            return;
        }
        cartoes.show(this, CARTAOVOTACAO);
        rotuloCargo.setText(sessao.getCargoAtual().getDescricao());
        exibirDigitos(sessao.getDigitosDigitados(), sessao.getCargoAtual().getQuantidadeDigitos());
        exibirCandidato(sessao.getCandidatoEncontrado());
        exibirAviso(sessao.getAvisoPrincipal());
        painelInstrucao.setVisible(sessao.permiteConfirmar());
        revalidate();
        repaint();
    }

    /** Mostra a tela de carregamento, enquanto a urna lê os candidatos. */
    public void exibirCarregando() {
        exibirMensagemCentral(TEXTOCARREGANDO, 40);
    }

    private void exibirMensagemCentral(String texto, int tamanhoDaFonte) {
        rotuloMensagemCentral.setFont(new Font(FONTE, Font.BOLD, tamanhoDaFonte));
        rotuloMensagemCentral.setText(texto);
        cartoes.show(this, CARTAOMENSAGEM);
    }

    private void exibirDigitos(String digitos, int quantidade) {
        if (camposDigito.size() != quantidade) {
            reconstruirCamposDigito(quantidade);
        }
        for (int i = 0; i < quantidade; i++) {
            CampoDigito campo = camposDigito.get(i);
            if (i < digitos.length()) {
                campo.exibirDigito(digitos.charAt(i));
            } else {
                campo.limpar();
            }
            campo.destacar(i == digitos.length());
        }
    }

    private void reconstruirCamposDigito(int quantidade) {
        painelCamposDigito.removeAll();
        camposDigito.clear();
        for (int i = 0; i < quantidade; i++) {
            CampoDigito campo = new CampoDigito();
            camposDigito.add(campo);
            painelCamposDigito.add(campo);
        }
    }

    private void exibirCandidato(Optional<Candidato> candidatoEncontrado) {
        painelLinhasAdicionais.removeAll();
        if (candidatoEncontrado.isEmpty()) {
            rotuloNome.setText(" ");
            rotuloPartido.setText(" ");
            exibirFoto(null);
            return;
        }
        Candidato candidato = candidatoEncontrado.get();
        rotuloNome.setText(TEXTONOME + candidato.getNome());
        rotuloPartido.setText(TEXTOPARTIDO + candidato.getPartido().getSigla());
        for (String linha : candidato.obterLinhasAdicionais()) {
            adicionarLinhaAdicional(linha);
        }
        exibirFoto(candidato.getFoto());
    }

    private void adicionarLinhaAdicional(String linha) {
        JLabel rotulo = criarRotulo(Font.PLAIN, 16);
        rotulo.setText(linha);
        painelLinhasAdicionais.add(rotulo);
    }

    private void exibirAviso(String aviso) {
        String[] linhas = aviso.isEmpty() ? new String[0] : aviso.split("\n");
        if (linhas.length == 0) {
            rotuloAviso.setText(" ");
            rotuloAvisoDestaque.setText(" ");
            return;
        }
        rotuloAvisoDestaque.setText(linhas[linhas.length - 1]);
        rotuloAviso.setText(linhas.length > 1 ? String.join(" ", List.of(linhas).subList(0, linhas.length - 1)) : " ");
    }

    private void exibirFoto(byte[] foto) {
        if (foto == null) {
            rotuloFoto.setIcon(null);
            rotuloFoto.setText("");
            rotuloFoto.setBorder(null);
            return;
        }
        rotuloFoto.setBorder(BorderFactory.createLineBorder(TemaVisual.BORDACAMPO));
        try {
            BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(foto));
            if (imagem == null) {
                rotuloFoto.setIcon(null);
                rotuloFoto.setText(TEXTOSEMFOTO);
                return;
            }
            double escala = Math.min((double) LARGURAFOTO / imagem.getWidth(), (double) ALTURAFOTO / imagem.getHeight());
            int largura = Math.max(1, (int) (imagem.getWidth() * escala));
            int altura = Math.max(1, (int) (imagem.getHeight() * escala));
            rotuloFoto.setText("");
            rotuloFoto.setIcon(new ImageIcon(imagem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH)));
        } catch (IOException erro) {
            rotuloFoto.setIcon(null);
            rotuloFoto.setText(TEXTOSEMFOTO);
        }
    }
}
