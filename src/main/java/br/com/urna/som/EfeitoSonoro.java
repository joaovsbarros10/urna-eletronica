/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: EfeitoSonoro.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.som;

/**
 * Os três sons da urna (RF10), cada um com a frequência e a duração do tom.
 * <p>
 * Os valores são um ponto de partida, não medidos na urna real: compare de
 * ouvido com o simulador do TSE e ajuste aqui (seção 10 do projeto).
 *
 * @author João
 * @since 1.0
 */
public enum EfeitoSonoro {

    /** A cada clique em dígito, BRANCO ou CORRIGE. */
    TECLA(1000, 80),

    /** CONFIRMA aceito em um cargo que não é o último. */
    CONFIRMACAO(800, 350),

    /** CONFIRMA aceito no último cargo. */
    FIM(700, 1200);

    private final int frequenciaEmHertz;

    private final int duracaoEmMilissegundos;

    EfeitoSonoro(int frequenciaEmHertz, int duracaoEmMilissegundos) {
        this.frequenciaEmHertz = frequenciaEmHertz;
        this.duracaoEmMilissegundos = duracaoEmMilissegundos;
    }

    // ---- Dados do tom ----

    public int getFrequenciaEmHertz() {
        return frequenciaEmHertz;
    }

    public int getDuracaoEmMilissegundos() {
        return duracaoEmMilissegundos;
    }
}
