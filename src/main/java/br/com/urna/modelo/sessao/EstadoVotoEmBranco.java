/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: EstadoVotoEmBranco.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

import br.com.urna.modelo.voto.Voto;
import br.com.urna.modelo.voto.VotoBranco;

/**
 * BRANCO aceito com o display vazio: mostra VOTO EM BRANCO e CONFIRMA
 * registra um {@link VotoBranco} (RF06).
 *
 * @author João
 * @since 1.0
 */
final class EstadoVotoEmBranco extends EstadoConfirmavel {

    static final String VOTOEMBRANCO = "VOTO EM BRANCO";

    // ---- Voto e exibição ----

    @Override
    protected Voto criarVoto(SessaoVotacao sessao) {
        return new VotoBranco(sessao.getCargoAtual());
    }

    @Override
    public String getAvisoPrincipal() {
        return VOTOEMBRANCO;
    }
}
