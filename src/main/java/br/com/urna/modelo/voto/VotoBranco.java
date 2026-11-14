/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: VotoBranco.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.voto;

import br.com.urna.modelo.Cargo;
import java.util.OptionalLong;

/**
 * Voto em branco: a tecla BRANCO foi aceita com o display vazio (RN08).
 *
 * @author João
 * @since 1.0
 */
public class VotoBranco extends Voto {

    /**
     * Cria o voto em branco.
     *
     * @param cargo cargo votado
     * @throws IllegalArgumentException se o cargo for {@code null}
     */
    public VotoBranco(Cargo cargo) {
        super(cargo);
    }

    // ---- Dados do voto ----

    @Override
    public TipoVoto getTipo() {
        return TipoVoto.BRANCO;
    }

    @Override
    public OptionalLong obterIdentificadorDoCandidato() {
        return OptionalLong.empty();
    }

    @Override
    public String obterDescricao() {
        return "Voto em branco";
    }

    // ---- Apuração ----

    @Override
    public void contabilizarEm(ApuracaoCargo apuracao) {
        exigirMesmoCargo(apuracao);
        apuracao.registrarVotoBranco();
    }
}
