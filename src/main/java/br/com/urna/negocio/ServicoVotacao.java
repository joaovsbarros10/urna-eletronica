/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ServicoVotacao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.negocio;

import br.com.urna.excecao.RegraNegocioException;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.persistencia.RepositorioVoto;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Grava os votos de uma sessão encerrada. Tem acesso de pacote: a visão e o
 * controle só chegam aqui pela {@link FachadaUrna} (item 2.0 do checklist).
 *
 * @author João
 * @since 1.0
 */
final class ServicoVotacao {

    private final RepositorioVoto repositorioVoto;

    ServicoVotacao(RepositorioVoto repositorioVoto) {
        this.repositorioVoto = repositorioVoto;
    }

    // ---- Gravação dos votos ----

    void registrarVotos(List<Voto> votos) {
        int quantidadeDeCargos = Cargo.values().length;
        if (votos == null || votos.size() != quantidadeDeCargos) {
            throw new RegraNegocioException("A votação precisa de exatamente um voto para cada um dos "
                    + quantidadeDeCargos + " cargos (RN10).");
        }
        Set<Cargo> cargosVotados = EnumSet.noneOf(Cargo.class);
        for (Voto voto : votos) {
            if (voto == null || !cargosVotados.add(voto.getCargo())) {
                throw new RegraNegocioException("A votação tem voto ausente ou cargo repetido (RN10).");
            }
        }
        repositorioVoto.gravarTodos(List.copyOf(votos));
    }
}
