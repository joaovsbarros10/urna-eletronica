/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: VotoCandidato.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.voto;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import java.util.OptionalLong;

/**
 * Voto em um candidato do mesmo cargo.
 *
 * @author João
 * @since 1.0
 */
public class VotoCandidato extends Voto {

    private final Candidato candidato;

    /**
     * Cria o voto no candidato.
     *
     * @param cargo     cargo votado
     * @param candidato candidato escolhido; tem de ser do mesmo cargo
     * @throws IllegalArgumentException se o cargo ou o candidato forem {@code null},
     *                                  ou se o candidato for de outro cargo
     */
    public VotoCandidato(Cargo cargo, Candidato candidato) {
        super(cargo);
        if (candidato == null) {
            throw new IllegalArgumentException("O voto em candidato precisa do candidato.");
        }
        if (candidato.getCargo() != cargo) {
            throw new IllegalArgumentException("O candidato " + candidato.getNumero() + " concorre a "
                    + candidato.getCargo().getDescricao() + ", não a " + cargo.getDescricao() + ".");
        }
        this.candidato = candidato;
    }

    // ---- Dados do voto ----

    public Candidato getCandidato() {
        return candidato;
    }

    @Override
    public TipoVoto getTipo() {
        return TipoVoto.CANDIDATO;
    }

    @Override
    public OptionalLong obterIdentificadorDoCandidato() {
        return OptionalLong.of(candidato.getIdentificador());
    }

    @Override
    public String obterDescricao() {
        return candidato.getNumero() + " - " + candidato.getNome();
    }

    // ---- Apuração ----

    @Override
    public void contabilizarEm(ApuracaoCargo apuracao) {
        exigirMesmoCargo(apuracao);
        apuracao.registrarVotoParaCandidato(candidato);
    }
}
