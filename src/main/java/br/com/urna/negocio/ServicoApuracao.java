/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ServicoApuracao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.negocio;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.voto.ApuracaoCargo;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.persistencia.RepositorioVoto;

/**
 * Apura os votos de um cargo (RF12). A apuração é feita em Java, com despacho
 * duplo: cada voto se contabiliza na {@link ApuracaoCargo}, sem {@code instanceof}
 * (decisão 0004). Tem acesso de pacote.
 *
 * @author João
 * @since 1.0
 */
final class ServicoApuracao {

    private final RepositorioVoto repositorioVoto;

    private final CatalogoEleitoral catalogoEleitoral;

    ServicoApuracao(RepositorioVoto repositorioVoto, CatalogoEleitoral catalogoEleitoral) {
        this.repositorioVoto = repositorioVoto;
        this.catalogoEleitoral = catalogoEleitoral;
    }

    // ---- Apuração ----

    ApuracaoCargo apurar(Cargo cargo) {
        ApuracaoCargo apuracao = new ApuracaoCargo(cargo);
        for (Candidato candidato : catalogoEleitoral.listarCandidatosDoCargo(cargo)) {
            apuracao.incluirCandidato(candidato);
        }
        for (Voto voto : repositorioVoto.listarPorCargo(cargo)) {
            voto.contabilizarEm(apuracao);
        }
        return apuracao;
    }
}
