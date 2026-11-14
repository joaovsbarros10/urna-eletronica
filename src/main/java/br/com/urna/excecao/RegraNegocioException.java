/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RegraNegocioException.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.excecao;

/**
 * Violação de uma regra de negócio (RN01 a RN12), por exemplo número de
 * candidato repetido no cargo ou partido com candidatos sendo excluído.
 *
 * @author João
 * @since 1.0
 */
public class RegraNegocioException extends UrnaException {

    /**
     * Cria o erro só com a mensagem.
     *
     * @param mensagem regra violada, em linguagem do usuário
     */
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria o erro com a mensagem e a causa original.
     *
     * @param mensagem regra violada, em linguagem do usuário
     * @param causa    exceção que detectou a violação
     */
    public RegraNegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
