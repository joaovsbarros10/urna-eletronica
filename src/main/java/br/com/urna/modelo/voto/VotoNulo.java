/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: VotoNulo.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.voto;

import br.com.urna.modelo.Cargo;
import java.util.OptionalLong;

/**
 * Voto nulo: número completo sem candidato no cargo (RN09).
 *
 * @author João
 * @since 1.0
 */
public class VotoNulo extends Voto {

    /**
     * Cria o voto nulo.
     *
     * @param cargo cargo votado
     * @throws IllegalArgumentException se o cargo for {@code null}
     */
    public VotoNulo(Cargo cargo) {
        super(cargo);
    }

    // ---- Dados do voto ----

    @Override
    public TipoVoto getTipo() {
        return TipoVoto.NULO;
    }

    @Override
    public OptionalLong obterIdentificadorDoCandidato() {
        return OptionalLong.empty();
    }

    @Override
    public String obterDescricao() {
        return "Voto nulo";
    }

    // ---- Apuração ----

    @Override
    public void contabilizarEm(ApuracaoCargo apuracao) {
        exigirMesmoCargo(apuracao);
        apuracao.registrarVotoNulo();
    }
}
