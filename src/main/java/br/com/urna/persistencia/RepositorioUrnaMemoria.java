/*
 * Urna Eletrônica
 * Arquivo: RepositorioUrnaMemoria.java
 * Autor: João
 */

package br.com.urna.persistencia;

import br.com.urna.excecao.UrnaException;
import br.com.urna.modelo.Candidato;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.Voto;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Repositório em listas na memória. Serve para a demonstração sem internet e
 * para os testes. Os métodos são sincronizados porque a tela lê e grava em
 * threads diferentes.
 *
 * @author João
 */
public class RepositorioUrnaMemoria implements RepositorioUrna {

    private final List<Partido> partidos = new ArrayList<>();

    private final List<Candidato> candidatos = new ArrayList<>();

    private final List<Voto> votos = new ArrayList<>();

    private long ultimoIdentificador;

    /**
     * Cria o repositório com dois partidos e dez candidatos fictícios, um par
     * para cada cargo. As fotos são desenhadas na hora.
     *
     * @return repositório preenchido
     */
    public static RepositorioUrnaMemoria criarComDadosDeDemonstracao() {
        RepositorioUrnaMemoria repositorio = new RepositorioUrnaMemoria();
        Partido aurora = repositorio.salvarPartido(new Partido(0, 31, "PAU", "Partido Aurora"));
        Partido cerrado = repositorio.salvarPartido(new Partido(0, 47, "PCV", "Partido Cerrado Verde"));
        Color laranja = new Color(0xC8642A);
        Color verde = new Color(0x2F7D4A);
        repositorio.incluir(Cargo.PRESIDENTE, "31", "Helena Marques", aurora, laranja, "Otávio Rangel");
        repositorio.incluir(Cargo.PRESIDENTE, "47", "Rui Albuquerque", cerrado, verde, "Sônia Tavares");
        repositorio.incluir(Cargo.GOVERNADOR, "31", "Marcos Vilela", aurora, laranja, "Beatriz Nunes");
        repositorio.incluir(Cargo.GOVERNADOR, "47", "Elisa Prado", cerrado, verde, "Hugo Matos");
        repositorio.incluir(Cargo.SENADOR, "312", "Clara Duarte", aurora, laranja, "Paulo Seixas", "Irene Lobo");
        repositorio.incluir(Cargo.SENADOR, "475", "Davi Sampaio", cerrado, verde, "Nair Costa", "Jorge Leal");
        repositorio.incluir(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", aurora, laranja);
        repositorio.incluir(Cargo.DEPUTADOFEDERAL, "4721", "Lívia Castro", cerrado, verde);
        repositorio.incluir(Cargo.DEPUTADOESTADUAL, "31042", "Renata Bastos", aurora, laranja);
        repositorio.incluir(Cargo.DEPUTADOESTADUAL, "47388", "Caio Pimentel", cerrado, verde);
        return repositorio;
    }

    private void incluir(Cargo cargo, String numero, String nome, Partido partido, Color cor, String... complementos) {
        salvarCandidato(Candidato.criar(0, cargo, numero, nome, partido, desenharFoto(nome, cor), List.of(complementos)));
    }

    @Override
    public synchronized List<Partido> listarPartidos() {
        List<Partido> copia = new ArrayList<>(partidos);
        copia.sort(Comparator.comparingInt(Partido::getNumero));
        return copia;
    }

    @Override
    public synchronized Partido salvarPartido(Partido partido) {
        Partido gravado = partido.getIdentificador() != 0 ? partido
                : new Partido(++ultimoIdentificador, partido.getNumero(), partido.getSigla(), partido.getNome());
        partidos.removeIf(existente -> existente.getIdentificador() == gravado.getIdentificador());
        partidos.add(gravado);
        return gravado;
    }

    @Override
    public synchronized void excluirPartido(Partido partido) {
        partidos.removeIf(existente -> existente.getIdentificador() == partido.getIdentificador());
    }

    @Override
    public synchronized List<Candidato> listarCandidatos() {
        return new ArrayList<>(candidatos);
    }

    @Override
    public synchronized Candidato salvarCandidato(Candidato candidato) {
        Candidato gravado = candidato.getIdentificador() != 0 ? candidato
                : candidato.copiarComIdentificador(++ultimoIdentificador);
        candidatos.removeIf(existente -> existente.getIdentificador() == gravado.getIdentificador());
        candidatos.add(gravado);
        return gravado;
    }

    @Override
    public synchronized void excluirCandidato(Candidato candidato) {
        candidatos.removeIf(existente -> existente.getIdentificador() == candidato.getIdentificador());
    }

    @Override
    public synchronized void gravarVotos(List<Voto> votosDoEleitor) {
        votos.addAll(votosDoEleitor);
    }

    @Override
    public synchronized List<Voto> listarVotos(Cargo cargo, List<Candidato> candidatosDoCargo) {
        return votos.stream().filter(voto -> voto.getCargo() == cargo).toList();
    }

    /**
     * Desenha uma foto 3x4 com fundo na cor do partido e as iniciais do nome.
     *
     * @param nome nome do candidato
     * @param cor  cor de fundo
     * @return bytes de uma imagem PNG de 300 por 400 pixels
     */
    static byte[] desenharFoto(String nome, Color cor) {
        BufferedImage imagem = new BufferedImage(300, 400, BufferedImage.TYPE_INT_RGB);
        Graphics2D pincel = imagem.createGraphics();
        pincel.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pincel.setColor(cor);
        pincel.fillRect(0, 0, 300, 400);
        pincel.setColor(new Color(255, 255, 255, 200));
        pincel.fillOval(95, 70, 110, 130);
        pincel.fillRoundRect(40, 215, 220, 230, 120, 120);
        String[] palavras = nome.split(" ");
        String iniciais = palavras[0].substring(0, 1) + palavras[palavras.length - 1].substring(0, 1);
        pincel.setColor(cor.darker());
        pincel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 54));
        pincel.drawString(iniciais, 150 - pincel.getFontMetrics().stringWidth(iniciais) / 2, 330);
        pincel.dispose();
        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            ImageIO.write(imagem, "png", saida);
            return saida.toByteArray();
        } catch (IOException erro) {
            throw new UrnaException("Não foi possível desenhar a foto de demonstração.", erro);
        }
    }
}
