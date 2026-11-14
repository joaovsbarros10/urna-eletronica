/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ConfiguracaoException.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.excecao;

/**
 * Configuração ausente ou inválida, por exemplo variável de ambiente do banco não definida.
 *
 * @author João
 * @since 1.0
 */
public class ConfiguracaoException extends UrnaException {

    /**
     * Cria o erro só com a mensagem.
     *
     * @param mensagem o que falta configurar
     */
    public ConfiguracaoException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria o erro com a mensagem e a causa original.
     *
     * @param mensagem o que falta configurar
     * @param causa    exceção ao ler a configuração
     */
    public ConfiguracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
