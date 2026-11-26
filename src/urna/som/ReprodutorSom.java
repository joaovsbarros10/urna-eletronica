/*
 * Urna Eletrônica
 * Arquivo: ReprodutorSom.java
 * Autor: João
 */

package urna.som;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Toca os sons da urna: tecla, confirmação e fim. Os tons são gerados por código
 * e tocados numa thread separada, para a tecla não esperar o som. Sem placa de
 * som, a urna continua funcionando em silêncio.
 *
 * @author João
 */
public class ReprodutorSom {

    private static final float TAXAAMOSTRAGEM = 44100f;

    private static final AudioFormat FORMATO = new AudioFormat(TAXAAMOSTRAGEM, 16, 1, true, false);

    private final ExecutorService fila = Executors.newSingleThreadExecutor(tarefa -> {
        Thread thread = new Thread(tarefa, "som-da-urna");
        thread.setDaemon(true);
        return thread;
    });

    private SourceDataLine linha;

    /** Toca o bipe curto das teclas numéricas, BRANCO e CORRIGE. */
    public void tocarTecla() {
        tocar(1000, 80);
    }

    /** Toca o som de confirmação de um cargo. */
    public void tocarConfirmacao() {
        tocar(800, 350);
    }

    /** Toca o som longo do fim da votação. */
    public void tocarFim() {
        tocar(700, 1200);
    }

    private void tocar(int frequencia, int duracaoEmMilissegundos) {
        byte[] amostras = gerarTom(frequencia, duracaoEmMilissegundos);
        fila.submit(() -> enviarParaPlaca(amostras));
    }

    private void enviarParaPlaca(byte[] amostras) {
        try {
            if (linha == null) {
                linha = AudioSystem.getSourceDataLine(FORMATO);
                linha.open(FORMATO);
            }
            linha.start();
            linha.write(amostras, 0, amostras.length);
            linha.drain();
            linha.stop();
        } catch (LineUnavailableException | IllegalArgumentException erro) {
            linha = null;
        }
    }

    private static byte[] gerarTom(int frequencia, int duracaoEmMilissegundos) {
        int quantidade = (int) (TAXAAMOSTRAGEM * duracaoEmMilissegundos / 1000);
        int suavizacao = (int) (TAXAAMOSTRAGEM * 0.005);
        byte[] amostras = new byte[quantidade * 2];
        for (int i = 0; i < quantidade; i++) {
            double envelope = Math.min(1.0, Math.min(i, quantidade - 1 - i) / (double) suavizacao);
            short valor = (short) (Math.sin(2 * Math.PI * frequencia * i / TAXAAMOSTRAGEM) * envelope * 0.6
                    * Short.MAX_VALUE);
            amostras[2 * i] = (byte) valor;
            amostras[2 * i + 1] = (byte) (valor >> 8);
        }
        return amostras;
    }
}
