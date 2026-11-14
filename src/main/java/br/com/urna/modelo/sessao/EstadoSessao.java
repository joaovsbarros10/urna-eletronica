/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: EstadoSessao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

/**
 * Estado da sessão de votação (padrão State). A {@link SessaoVotacao} repassa
 * as quatro teclas de ação ao estado atual, e cada estado diz, uma única vez,
 * o que a tecla faz naquela situação (tabela 5.6.2).
 * <p>
 * Os estados ficam no mesmo pacote da sessão porque só eles podem chamar os
 * métodos de transição, que têm acesso de pacote.
 *
 * @author João
 * @since 1.0
 */
interface EstadoSessao {

    /**
     * Trata uma tecla numérica.
     *
     * @param sessao sessão sobre a qual o estado age
     * @param digito dígito de '0' a '9'
     */
    void digitar(SessaoVotacao sessao, char digito);

    /**
     * Trata a tecla CORRIGE.
     *
     * @param sessao sessão sobre a qual o estado age
     */
    void corrigir(SessaoVotacao sessao);

    /**
     * Trata a tecla BRANCO.
     *
     * @param sessao sessão sobre a qual o estado age
     */
    void votarEmBranco(SessaoVotacao sessao);

    /**
     * Trata a tecla CONFIRMA.
     *
     * @param sessao sessão sobre a qual o estado age
     * @return {@code true} se um voto foi registrado
     */
    boolean confirmar(SessaoVotacao sessao);

    /**
     * Informa se a tecla CONFIRMA registra um voto neste estado.
     *
     * @return {@code true} se há número completo ou voto em branco para confirmar
     */
    boolean permiteConfirmar();

    /**
     * Devolve o aviso principal do display neste estado; as linhas são separadas por quebra de linha.
     *
     * @return aviso, ou texto vazio se não houver
     */
    String getAvisoPrincipal();
}
