/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: EstadoCandidatoEncontrado.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.modelo.voto.VotoCandidato;

/**
 * Número completo com candidato: a tela mostra os dados do candidato e
 * CONFIRMA registra um {@link VotoCandidato}.
 *
 * @author João
 * @since 1.0
 */
final class EstadoCandidatoEncontrado extends EstadoConfirmavel {

    private final Candidato candidato;

    EstadoCandidatoEncontrado(Candidato candidato) {
        this.candidato = candidato;
    }

    // ---- Voto e exibição ----

    @Override
    protected Voto criarVoto(SessaoVotacao sessao) {
        return new VotoCandidato(sessao.getCargoAtual(), candidato);
    }

    /** Sem aviso: a tela mostra nome, partido, foto e complemento do candidato. */
    @Override
    public String getAvisoPrincipal() {
        return "";
    }
}
