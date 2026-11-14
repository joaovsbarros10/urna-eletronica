/*
 * Urna Eletrônica
 * Arquivo: VisaoVotacao.java
 * Autor: João
 */

package br.com.urna.controle;

import br.com.urna.modelo.SessaoVotacao;

/**
 * O que o controlador precisa da tela da urna. O controlador depende desta
 * interface e não da janela Swing, o que mantém o MVC desacoplado.
 *
 * @author João
 */
public interface VisaoVotacao {

    /**
     * Redesenha o display a partir da sessão.
     *
     * @param sessao sessão do eleitor atual
     */
    void atualizar(SessaoVotacao sessao);

    /** Mostra que os candidatos estão sendo carregados. */
    void exibirCarregando();

    /**
     * Mostra uma mensagem de erro.
     *
     * @param mensagem texto do erro
     */
    void exibirErro(String mensagem);

    /**
     * Pergunta se deve tentar de novo a operação que falhou.
     *
     * @param mensagem descrição da falha
     * @return {@code true} para tentar de novo
     */
    boolean perguntarSeTentaNovamente(String mensagem);

    /**
     * Pergunta se a urna deve abrir com os dados de demonstração.
     *
     * @return {@code true} para usar a demonstração
     */
    boolean perguntarSeUsaDemonstracao();

    /** Fecha a urna. */
    void encerrar();
}
