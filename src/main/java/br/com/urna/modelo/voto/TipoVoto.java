/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: TipoVoto.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.voto;

/**
 * Tipo do voto, gravado na coluna {@code tipo} da tabela {@code voto}.
 * O repositório pergunta o tipo ao próprio voto ({@link Voto#getTipo()}),
 * sem olhar a subclasse.
 *
 * @author João
 * @since 1.0
 */
public enum TipoVoto {

    /** Voto em um candidato. */
    CANDIDATO,

    /** Voto em branco (tecla BRANCO com o display vazio). */
    BRANCO,

    /** Voto nulo (número completo sem candidato, RN09). */
    NULO
}
