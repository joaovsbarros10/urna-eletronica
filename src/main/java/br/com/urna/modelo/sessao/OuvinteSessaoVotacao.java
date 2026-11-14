/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: OuvinteSessaoVotacao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

/**
 * Quem quer ser avisado quando a sessão de votação muda (padrão Observer).
 * A tela da urna implementa esta interface e se redesenha a cada aviso.
 *
 * @author João
 * @since 1.0
 */
public interface OuvinteSessaoVotacao {

    /**
     * Recebe o aviso de que a sessão mudou. É chamado uma vez por tecla, na
     * mesma thread que acionou a tecla.
     *
     * @param sessao sessão que mudou, para ser lida
     */
    void aoAtualizarSessao(SessaoVotacao sessao);
}
