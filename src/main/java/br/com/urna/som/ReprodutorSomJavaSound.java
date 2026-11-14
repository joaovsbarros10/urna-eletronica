/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ReprodutorSomJavaSound.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.som;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Toca os efeitos com tons senoidais gerados por código, via {@code javax.sound.sampled}.
 * <p>
 * Gerar o tom evita depender de gravação de terceiros e dispensa arquivos de
 * áudio no JAR. Os três tons são sintetizados uma vez, no construtor, e tocados
 * numa thread própria em fila: toques rápidos se enfileiram em vez de travar a tela.
 * A linha de áudio fica aberta entre um toque e outro, para a tecla soar sem atraso,
 * mas é parada ao fim de cada efeito, para não ficar tocando sozinha.
 *
 * @author João
 * @since 1.0
 */
public class ReprodutorSomJavaSound implements ReprodutorSom {

    private static final float TAXAAMOSTRAGEM = 44100f;

    private static final double AMPLITUDE = 0.6;

    /* Subida e descida de 5 ms nas pontas do tom; sem isso o corte seco do seno estala. */
    private static final double SUAVIZACAOEMSEGUNDOS = 0.005;

    private static final AudioFormat FORMATO = new AudioFormat(TAXAAMOSTRAGEM, 16, 1, true, false);

    private final Map<EfeitoSonoro, byte[]> amostrasPorEfeito = new EnumMap<>(EfeitoSonoro.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor(tarefa -> {
        Thread thread = new Thread(tarefa, "som-da-urna");
        thread.setDaemon(true);
        return thread;
    });

    private SourceDataLine linha;

    /** Pré-sintetiza os três efeitos. */
    public ReprodutorSomJavaSound() {
        for (EfeitoSonoro efeito : EfeitoSonoro.values()) {
            amostrasPorEfeito.put(efeito, sintetizar(efeito));
        }
    }

    // ---- Disponibilidade ----

    /**
     * Informa se a máquina tem saída de áudio capaz de tocar os tons. Sem ela,
     * a aplicação usa o {@link ReprodutorSomMudo}.
     *
     * @return {@code true} se há uma linha de saída compatível
     */
    public static boolean estaDisponivel() {
        try {
            return AudioSystem.getMixerInfo().length > 0
                    && AudioSystem.isLineSupported(new DataLine.Info(SourceDataLine.class, FORMATO));
        } catch (RuntimeException erro) {
            return false;
        }
    }

    // ---- Reprodução ----

    @Override
    public void reproduzir(EfeitoSonoro efeito) {
        byte[] amostras = amostrasPorEfeito.get(efeito);
        executor.submit(() -> tocar(amostras));
    }

    /*
     * Roda só na thread "som-da-urna", então a linha não precisa de sincronização.
     * A linha é parada depois de cada efeito: no Windows, uma linha ligada e sem
     * dados novos repete o último trecho do buffer, e o tom vira um apito sem fim.
     */
    private void tocar(byte[] amostras) {
        try {
            if (linha == null) {
                linha = AudioSystem.getSourceDataLine(FORMATO);
                linha.open(FORMATO);
            }
            linha.start();
            linha.write(amostras, 0, amostras.length);
            linha.drain();
            linha.stop();
            linha.flush();
        } catch (LineUnavailableException | IllegalArgumentException | SecurityException erro) {
            // Sem placa de som: a urna continua funcionando, só que muda.
            linha = null;
        }
    }

    // ---- Síntese do tom ----

    private byte[] sintetizar(EfeitoSonoro efeito) {
        int quantidadeDeAmostras = (int) (TAXAAMOSTRAGEM * efeito.getDuracaoEmMilissegundos() / 1000);
        int amostrasDeSuavizacao = (int) (TAXAAMOSTRAGEM * SUAVIZACAOEMSEGUNDOS);
        byte[] amostras = new byte[quantidadeDeAmostras * 2];
        for (int i = 0; i < quantidadeDeAmostras; i++) {
            double fase = 2 * Math.PI * efeito.getFrequenciaEmHertz() * i / TAXAAMOSTRAGEM;
            double envelope = Math.min(1.0,
                    Math.min(i, quantidadeDeAmostras - 1 - i) / (double) amostrasDeSuavizacao);
            short valor = (short) (Math.sin(fase) * envelope * AMPLITUDE * Short.MAX_VALUE);
            amostras[2 * i] = (byte) (valor & 0xFF);
            amostras[2 * i + 1] = (byte) ((valor >> 8) & 0xFF);
        }
        return amostras;
    }
}
