/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: UrnaException.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.excecao;

/**
 * Base dos erros da aplicação. Estende {@link RuntimeException}, então os
 * métodos não precisam declarar {@code throws}; quem trata é o controlador,
 * que mostra a mensagem na tela. A mensagem é sempre legível pelo usuário.
 *
 * @author João
 * @since 1.0
 */
public abstract class UrnaException extends RuntimeException {

    /**
     * Cria o erro só com a mensagem.
     *
     * @param mensagem mensagem para o usuário
     */
    protected UrnaException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria o erro com a mensagem e a causa original.
     *
     * @param mensagem mensagem para o usuário
     * @param causa    exceção que originou o erro
     */
    protected UrnaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
