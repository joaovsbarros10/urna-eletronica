/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CargaDemonstracao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import br.com.urna.excecao.PersistenciaException;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.FabricaCandidato;
import br.com.urna.modelo.candidato.FichaCandidato;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;

/**
 * Preenche repositórios em memória com os mesmos partidos e candidatos
 * fictícios de {@code banco/03-dados-ficticios.sql}, para a demonstração sem
 * internet e para os testes.
 * <p>
 * As fotos são desenhadas por código (silhueta com as iniciais, 300 por 400
 * pixels, PNG), para não usar imagem de pessoa real nem arquivo de terceiros.
 *
 * @author João
 * @since 1.0
 */
public final class CargaDemonstracao {

    private static final int LARGURAFOTO = 300;

    private static final int ALTURAFOTO = 400;

    private static final Color CORAURORA = new Color(0xC8642A);

    private static final Color CORCERRADO = new Color(0x2F7D4A);

    private CargaDemonstracao() {
    }

    // ---- Carga dos dados fictícios ----

    /**
     * Grava os dois partidos e os dez candidatos fictícios nos repositórios.
     * Use repositórios vazios: os números se repetiriam.
     *
     * @param repositorioPartido   repositório que recebe os partidos
     * @param repositorioCandidato repositório que recebe os candidatos
     * @throws PersistenciaException se a foto de demonstração não puder ser gerada
     */
    public static void preencher(RepositorioPartido repositorioPartido, RepositorioCandidato repositorioCandidato) {
        Partido aurora = repositorioPartido.salvar(new Partido(0, 31, "PAU", "Partido Aurora"));
        Partido cerrado = repositorioPartido.salvar(new Partido(0, 47, "PCV", "Partido Cerrado Verde"));

        gravar(repositorioCandidato, iniciarFicha(Cargo.PRESIDENTE, "31", "Helena Marques", aurora, CORAURORA)
                .definirNomeVice("Otávio Rangel"));
        gravar(repositorioCandidato, iniciarFicha(Cargo.PRESIDENTE, "47", "Rui Albuquerque", cerrado, CORCERRADO)
                .definirNomeVice("Sônia Tavares"));
        gravar(repositorioCandidato, iniciarFicha(Cargo.GOVERNADOR, "31", "Marcos Vilela", aurora, CORAURORA)
                .definirNomeVice("Beatriz Nunes"));
        gravar(repositorioCandidato, iniciarFicha(Cargo.GOVERNADOR, "47", "Elisa Prado", cerrado, CORCERRADO)
                .definirNomeVice("Hugo Matos"));
        gravar(repositorioCandidato, iniciarFicha(Cargo.SENADOR, "312", "Clara Duarte", aurora, CORAURORA)
                .definirNomesDosSuplentes("Paulo Seixas", "Irene Lobo"));
        gravar(repositorioCandidato, iniciarFicha(Cargo.SENADOR, "475", "Davi Sampaio", cerrado, CORCERRADO)
                .definirNomesDosSuplentes("Nair Costa", "Jorge Leal"));
        gravar(repositorioCandidato, iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", aurora, CORAURORA));
        gravar(repositorioCandidato, iniciarFicha(Cargo.DEPUTADOFEDERAL, "4721", "Lívia Castro", cerrado, CORCERRADO));
        gravar(repositorioCandidato, iniciarFicha(Cargo.DEPUTADOESTADUAL, "31042", "Renata Bastos", aurora, CORAURORA));
        gravar(repositorioCandidato, iniciarFicha(Cargo.DEPUTADOESTADUAL, "47388", "Caio Pimentel", cerrado, CORCERRADO));
    }

    private static FichaCandidato.Montador iniciarFicha(Cargo cargo, String numero, String nome, Partido partido,
                                                        Color cor) {
        return new FichaCandidato.Montador(cargo)
                .definirNumero(numero)
                .definirNome(nome)
                .definirPartido(partido)
                .definirFoto(desenharFoto(nome, cor));
    }

    private static void gravar(RepositorioCandidato repositorioCandidato, FichaCandidato.Montador montador) {
        repositorioCandidato.salvar(FabricaCandidato.criar(montador.montar()));
    }

    // ---- Foto desenhada por código ----

    /**
     * Desenha uma foto de demonstração 3x4: fundo na cor do partido, silhueta e iniciais do nome.
     *
     * @param nome nome do candidato, de onde saem as iniciais
     * @param cor  cor de fundo
     * @return bytes de uma imagem PNG de 300 por 400 pixels
     * @throws PersistenciaException se a imagem não puder ser codificada
     */
    public static byte[] desenharFoto(String nome, Color cor) {
        BufferedImage imagem = new BufferedImage(LARGURAFOTO, ALTURAFOTO, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = imagem.createGraphics();
        try {
            pincel.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pincel.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            pincel.setPaint(new GradientPaint(0, 0, cor.brighter(), 0, ALTURAFOTO, cor.darker()));
            pincel.fillRect(0, 0, LARGURAFOTO, ALTURAFOTO);

            Color corSilhueta = new Color(255, 255, 255, 190);
            pincel.setColor(corSilhueta);
            pincel.fill(new Ellipse2D.Double(95, 70, 110, 130));
            pincel.fill(new RoundRectangle2D.Double(40, 215, 220, 230, 120, 120));
            pincel.setColor(cor.darker());
            pincel.setStroke(new BasicStroke(4f));
            pincel.draw(new Ellipse2D.Double(95, 70, 110, 130));

            String iniciais = extrairIniciais(nome);
            pincel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 54));
            FontMetrics medidas = pincel.getFontMetrics();
            pincel.setColor(cor.darker().darker());
            pincel.drawString(iniciais, (LARGURAFOTO - medidas.stringWidth(iniciais)) / 2, 330);
        } finally {
            pincel.dispose();
        }
        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            ImageIO.write(imagem, "png", saida);
            return saida.toByteArray();
        } catch (IOException erro) {
            throw new PersistenciaException("Não foi possível gerar a foto de demonstração.", erro);
        }
    }

    private static String extrairIniciais(String nome) {
        String[] palavras = nome.strip().split("\\s+");
        String primeira = palavras[0].substring(0, 1);
        String ultima = palavras.length > 1 ? palavras[palavras.length - 1].substring(0, 1) : "";
        return (primeira + ultima).toUpperCase(Locale.ROOT);
    }
}
