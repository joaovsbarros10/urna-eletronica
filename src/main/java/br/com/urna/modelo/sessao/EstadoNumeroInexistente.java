/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: EstadoNumeroInexistente.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

import br.com.urna.modelo.voto.Voto;
import br.com.urna.modelo.voto.VotoNulo;

/**
 * Número completo sem candidato (RN09): mostra NÚMERO ERRADO, quando o partido
 * não existe, ou CANDIDATO INEXISTENTE, quando o partido existe mas não tem
 * candidato no cargo, seguido de VOTO NULO. CONFIRMA registra um {@link VotoNulo}.
 *
 * @author João
 * @since 1.0
 */
final class EstadoNumeroInexistente extends EstadoConfirmavel {

    static final String NUMEROERRADO = "NÚMERO ERRADO";

    static final String CANDIDATOINEXISTENTE = "CANDIDATO INEXISTENTE";

    static final String VOTONULO = "VOTO NULO";

    private final String aviso;

    EstadoNumeroInexistente(String aviso) {
        this.aviso = aviso;
    }

    // ---- Voto e exibição ----

    @Override
    protected Voto criarVoto(SessaoVotacao sessao) {
        return new VotoNulo(sessao.getCargoAtual());
    }

    @Override
    public String getAvisoPrincipal() {
        return aviso + "\n" + VOTONULO;
    }
}
