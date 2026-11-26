/*
 * Urna Eletrônica
 * Arquivo: PainelDisplay.java
 * Autor: João
 */

package urna.visao;

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
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import urna.modelo.Candidato;
import urna.modelo.SessaoVotacao;

/**
 * Display da urna. Lê a sessão e desenha: cargo, caixas dos dígitos, dados e
 * foto do candidato, aviso e instruções. Não toma nenhuma decisão de votação.
 *
 * @author João
 */
public final class PainelDisplay extends JPanel {

    private final CardLayout cartoes = new CardLayout();

    private final JLabel rotuloMensagem = new JLabel("", SwingConstants.CENTER);

    private final JLabel rotuloCargo = criarRotulo(Font.BOLD, 26);

    private final JPanel painelDigitos = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));

    private final JLabel rotuloNome = criarRotulo(Font.PLAIN, 18);

    private final JLabel rotuloPartido = criarRotulo(Font.PLAIN, 18);

    private final JPanel painelLinhasAdicionais = new JPanel();

    private final JLabel rotuloAviso = criarRotulo(Font.BOLD, 20);

    private final JLabel rotuloAvisoDestaque = criarRotulo(Font.BOLD, 36);

    private final JLabel rotuloFoto = new JLabel("", SwingConstants.CENTER);

    private final JPanel painelInstrucao = new JPanel();

    /** Monta o display vazio. */
    public PainelDisplay() {
        setLayout(cartoes);
        setPreferredSize(new Dimension(640, 440));
        JPanel painelMensagem = new JPanel(new BorderLayout());
        painelMensagem.setBackground(TemaVisual.FUNDODISPLAY);
        painelMensagem.add(rotuloMensagem, BorderLayout.CENTER);
        add(montarPainelVotacao(), "votacao");
        add(painelMensagem, "mensagem");
    }

    private JPanel montarPainelVotacao() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(TemaVisual.FUNDODISPLAY);
        painel.setBorder(BorderFactory.createEmptyBorder(16, 22, 12, 22));

        JLabel rotuloSeuVotoPara = criarRotulo(Font.PLAIN, 14);
        rotuloSeuVotoPara.setText("SEU VOTO PARA");
        rotuloSeuVotoPara.setForeground(TemaVisual.DETALHE);
        JPanel linhaNumero = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaNumero.setOpaque(false);
        JLabel rotuloNumero = criarRotulo(Font.PLAIN, 18);
        rotuloNumero.setText("Número: ");
        linhaNumero.add(rotuloNumero);
        linhaNumero.add(painelDigitos);
        painelDigitos.setOpaque(false);
        painelLinhasAdicionais.setLayout(new BoxLayout(painelLinhasAdicionais, BoxLayout.Y_AXIS));
        painelLinhasAdicionais.setOpaque(false);
        rotuloCargo.setHorizontalAlignment(SwingConstants.CENTER);
        rotuloAviso.setHorizontalAlignment(SwingConstants.CENTER);
        rotuloAvisoDestaque.setHorizontalAlignment(SwingConstants.CENTER);
        rotuloFoto.setPreferredSize(new Dimension(120, 160));

        adicionar(painel, rotuloSeuVotoPara, 0, false, 0);
        adicionar(painel, rotuloCargo, 1, true, 10);
        adicionar(painel, linhaNumero, 2, false, 18);
        adicionar(painel, rotuloNome, 3, false, 16);
        adicionar(painel, rotuloPartido, 4, false, 6);
        adicionar(painel, painelLinhasAdicionais, 5, false, 6);
        adicionar(painel, rotuloAviso, 6, true, 10);
        adicionar(painel, rotuloAvisoDestaque, 7, true, 4);

        GridBagConstraints restricao = new GridBagConstraints();
        restricao.gridy = 8;
        restricao.weighty = 1;
        painel.add(new JLabel(), restricao);

        restricao = new GridBagConstraints();
        restricao.gridx = 1;
        restricao.gridheight = 6;
        restricao.anchor = GridBagConstraints.NORTHEAST;
        painel.add(rotuloFoto, restricao);

        montarInstrucao();
        restricao = new GridBagConstraints();
        restricao.gridy = 9;
        restricao.gridwidth = 2;
        restricao.fill = GridBagConstraints.HORIZONTAL;
        painel.add(painelInstrucao, restricao);
        return painel;
    }

    private void montarInstrucao() {
        painelInstrucao.setLayout(new BoxLayout(painelInstrucao, BoxLayout.Y_AXIS));
        painelInstrucao.setOpaque(false);
        painelInstrucao.add(new JSeparator());
        for (String linha : new String[] {"Aperte a tecla:", "    VERDE para CONFIRMAR este voto",
                "    LARANJA para REINICIAR este voto"}) {
            JLabel rotulo = criarRotulo(Font.PLAIN, 14);
            rotulo.setText(linha);
            painelInstrucao.add(rotulo);
        }
    }

    private static void adicionar(JPanel painel, Component componente, int linha, boolean centralizado, int espaco) {
        GridBagConstraints restricao = new GridBagConstraints();
        restricao.gridx = 0;
        restricao.gridy = linha;
        restricao.weightx = 1;
        restricao.anchor = centralizado ? GridBagConstraints.CENTER : GridBagConstraints.WEST;
        restricao.fill = centralizado ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        restricao.insets = new Insets(espaco, 0, 0, 0);
        painel.add(componente, restricao);
    }

    private static JLabel criarRotulo(int estilo, int tamanho) {
        JLabel rotulo = new JLabel(" ");
        rotulo.setFont(new Font(Font.SANS_SERIF, estilo, tamanho));
        rotulo.setForeground(TemaVisual.TEXTODISPLAY);
        return rotulo;
    }

    /**
     * Redesenha o display a partir da sessão.
     *
     * @param sessao sessão do eleitor atual
     */
    public void atualizar(SessaoVotacao sessao) {
        if (sessao.estaEncerrada()) {
            exibirMensagem(sessao.getAviso(), 140);
            return;
        }
        cartoes.show(this, "votacao");
        rotuloCargo.setText(sessao.getCargoAtual().getDescricao());
        exibirDigitos(sessao.getDigitos(), sessao.getCargoAtual().getQuantidadeDigitos());
        exibirCandidato(sessao.getCandidatoEncontrado().orElse(null));
        String[] avisos = sessao.getAviso().split("\n");
        rotuloAviso.setText(avisos.length > 1 ? avisos[0] : " ");
        rotuloAvisoDestaque.setText(avisos[avisos.length - 1].isEmpty() ? " " : avisos[avisos.length - 1]);
        painelInstrucao.setVisible(sessao.permiteConfirmar());
        revalidate();
        repaint();
    }

    /** Mostra a mensagem de carregamento. */
    public void exibirCarregando() {
        exibirMensagem("CARREGANDO...", 40);
    }

    private void exibirMensagem(String texto, int tamanhoDaFonte) {
        rotuloMensagem.setFont(new Font(Font.SANS_SERIF, Font.BOLD, tamanhoDaFonte));
        rotuloMensagem.setText(texto);
        cartoes.show(this, "mensagem");
    }

    private void exibirDigitos(String digitos, int quantidade) {
        painelDigitos.removeAll();
        for (int i = 0; i < quantidade; i++) {
            JLabel caixa = new JLabel(i < digitos.length() ? String.valueOf(digitos.charAt(i)) : "",
                    SwingConstants.CENTER);
            caixa.setPreferredSize(new Dimension(42, 54));
            caixa.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
            caixa.setBorder(BorderFactory.createLineBorder(TemaVisual.TEXTODISPLAY, i == digitos.length() ? 3 : 1));
            painelDigitos.add(caixa);
        }
    }

    private void exibirCandidato(Candidato candidato) {
        painelLinhasAdicionais.removeAll();
        if (candidato == null) {
            rotuloNome.setText(" ");
            rotuloPartido.setText(" ");
            rotuloFoto.setIcon(null);
            rotuloFoto.setBorder(null);
            return;
        }
        rotuloNome.setText("Nome: " + candidato.getNome());
        rotuloPartido.setText("Partido: " + candidato.getPartido().getSigla());
        for (String linha : candidato.obterLinhasAdicionais()) {
            JLabel rotulo = criarRotulo(Font.PLAIN, 16);
            rotulo.setText(linha);
            painelLinhasAdicionais.add(rotulo);
        }
        rotuloFoto.setBorder(BorderFactory.createLineBorder(TemaVisual.DETALHE));
        rotuloFoto.setIcon(criarIconeDaFoto(candidato.getFoto()));
    }

    /**
     * Reduz a foto para caber em 120 por 160 pixels, sem distorcer. Também é
     * usado pelo cadastro de candidatos.
     *
     * @param foto bytes de uma imagem JPEG ou PNG
     * @return ícone da foto, ou {@code null} se a imagem não puder ser lida
     */
    static ImageIcon criarIconeDaFoto(byte[] foto) {
        try {
            BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(foto));
            if (imagem == null) {
                return null;
            }
            double escala = Math.min(120.0 / imagem.getWidth(), 160.0 / imagem.getHeight());
            int largura = Math.max(1, (int) (imagem.getWidth() * escala));
            int altura = Math.max(1, (int) (imagem.getHeight() * escala));
            return new ImageIcon(imagem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH));
        } catch (IOException erro) {
            return null;
        }
    }
}
