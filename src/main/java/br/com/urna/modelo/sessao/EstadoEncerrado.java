/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: EstadoEncerrado.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

/**
 * Fim da sessão (RN10): todas as teclas são ignoradas e o display mostra FIM.
 *
 * @author João
 * @since 1.0
 */
final class EstadoEncerrado implements EstadoSessao {

    static final String FIM = "FIM";

    // ---- Teclas (todas ignoradas) ----

    @Override
    public void digitar(SessaoVotacao sessao, char digito) {
    }

    @Override
    public void corrigir(SessaoVotacao sessao) {
    }

    @Override
    public void votarEmBranco(SessaoVotacao sessao) {
    }

    @Override
    public boolean confirmar(SessaoVotacao sessao) {
        return false;
    }

    // ---- Exibição ----

    @Override
    public boolean permiteConfirmar() {
        return false;
    }

    @Override
    public String getAvisoPrincipal() {
        return FIM;
    }
}
