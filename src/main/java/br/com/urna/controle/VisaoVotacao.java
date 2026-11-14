/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: VisaoVotacao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.controle;

import br.com.urna.modelo.sessao.OuvinteSessaoVotacao;

/**
 * O que o {@link ControladorVotacao} precisa da tela da urna.
 * <p>
 * O controlador depende desta interface, e não da janela Swing: é isso que
 * mantém a dependência de mão única da visão para o controle (MVC, item 1.0).
 * Todos os métodos são chamados na thread de eventos do Swing.
 *
 * @author João
 * @since 1.0
 */
public interface VisaoVotacao extends OuvinteSessaoVotacao {

    /** Mostra que a urna está carregando os candidatos. */
    void exibirCarregando();

    /**
     * Mostra uma mensagem de erro ao eleitor ou ao mesário.
     *
     * @param mensagem texto do erro
     */
    void exibirErro(String mensagem);

    /**
     * Pergunta se a urna deve tentar de novo a operação que falhou.
     *
     * @param mensagem descrição da falha
     * @return {@code true} se a resposta for tentar de novo
     */
    boolean perguntarSeTentaNovamente(String mensagem);

    /**
     * Pergunta se a urna deve abrir com os dados de demonstração em memória,
     * depois que o banco não respondeu.
     *
     * @return {@code true} se a resposta for usar a demonstração
     */
    boolean perguntarSeUsaDemonstracao();

    /** Fecha a urna, quando não é possível continuar. */
    void encerrar();
}
