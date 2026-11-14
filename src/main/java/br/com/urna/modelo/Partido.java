/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: Partido.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo;

import java.util.Objects;

/**
 * Partido político. Todo candidato pertence a exatamente um partido, e o
 * número do candidato começa com o número do partido (regras RN02 e RN04).
 * <p>
 * A classe é imutável: depois de criado, um partido não muda. O identificador
 * vale 0 enquanto o partido ainda não foi gravado no banco.
 *
 * @author João
 * @since 1.0
 */
public final class Partido {

    /* Os números de partido têm sempre 2 dígitos (RN03). */

    /** Quantidade de dígitos do número de partido; é também o tamanho do prefixo do candidato (RN04). */
    public static final int QUANTIDADEDIGITOS = 2;

    private static final int NUMEROMINIMO = 10;

    private static final int NUMEROMAXIMO = 99;

    private final long identificador;

    private final int numero;

    private final String sigla;

    private final String nome;

    /**
     * Cria um partido.
     *
     * @param identificador identificador gravado no banco; 0 para partido ainda não gravado
     * @param numero        número de 2 dígitos, de 10 a 99
     * @param sigla         sigla do partido; não pode ser vazia
     * @param nome          nome por extenso; não pode ser vazio
     * @throws IllegalArgumentException se o número estiver fora da faixa ou se sigla ou nome forem vazios
     */
    public Partido(long identificador, int numero, String sigla, String nome) {
        if (numero < NUMEROMINIMO || numero > NUMEROMAXIMO) {
            throw new IllegalArgumentException("O número do partido deve ter 2 dígitos (de 10 a 99): " + numero);
        }
        this.identificador = identificador;
        this.numero = numero;
        this.sigla = exigirTexto(sigla, "sigla");
        this.nome = exigirTexto(nome, "nome");
    }

    // ---- Dados do partido ----

    public long getIdentificador() {
        return identificador;
    }

    public int getNumero() {
        return numero;
    }

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }

    // ---- Regra de prefixo (RN04) ----

    /**
     * Informa se o número de um candidato começa com o número deste partido.
     *
     * @param numeroCandidato número do candidato, só com dígitos; pode ser {@code null}
     * @return {@code true} se o número começa com o número do partido
     */
    public boolean correspondeAoPrefixo(String numeroCandidato) {
        return numeroCandidato != null && numeroCandidato.startsWith(String.valueOf(numero));
    }

    // ---- Validação interna ----

    private static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo " + campo + " do partido é obrigatório.");
        }
        return valor.strip();
    }

    // ---- Identidade do objeto ----

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Partido)) {
            return false;
        }
        return numero == ((Partido) outro).numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return sigla + " (" + numero + ")";
    }
}
