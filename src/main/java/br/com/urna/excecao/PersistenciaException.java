/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: PersistenciaException.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.excecao;

/**
 * Falha ao ler ou gravar no banco de dados, inclusive falha de rede.
 *
 * @author João
 * @since 1.0
 */
public class PersistenciaException extends UrnaException {

    /**
     * Cria o erro só com a mensagem.
     *
     * @param mensagem mensagem para o usuário
     */
    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria o erro com a mensagem e a causa original.
     *
     * @param mensagem mensagem para o usuário
     * @param causa    exceção do driver ou da rede
     */
    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
