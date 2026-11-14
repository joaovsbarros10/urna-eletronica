/*
 * Urna Eletrônica
 * Arquivo: UrnaException.java
 * Autor: João
 */

package br.com.urna.excecao;

/**
 * Erro da aplicação com mensagem para o usuário: regra de negócio violada,
 * falha no banco ou configuração ausente.
 *
 * @author João
 */
public class UrnaException extends RuntimeException {

    /**
     * Cria o erro com a mensagem.
     *
     * @param mensagem mensagem para o usuário
     */
    public UrnaException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria o erro com a mensagem e a causa original.
     *
     * @param mensagem mensagem para o usuário
     * @param causa    exceção que originou o erro
     */
    public UrnaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
