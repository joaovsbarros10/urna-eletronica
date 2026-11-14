/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ApuracaoCargoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.voto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.urna.CenarioDeTeste;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import java.util.List;
import org.junit.jupiter.api.Test;

class ApuracaoCargoTest {

    private final Candidato helena = CenarioDeTeste.buscar(Cargo.PRESIDENTE, "31");

    private final Candidato rui = CenarioDeTeste.buscar(Cargo.PRESIDENTE, "47");

    // ---- RF12: cada subclasse de Voto soma no contador certo ----

    @Test
    void deveContarCadaTipoDeVotoNoContadorCertoRf12() {
        ApuracaoCargo apuracao = new ApuracaoCargo(Cargo.PRESIDENTE);
        List<Voto> votos = List.of(
                new VotoCandidato(Cargo.PRESIDENTE, helena),
                new VotoCandidato(Cargo.PRESIDENTE, helena),
                new VotoCandidato(Cargo.PRESIDENTE, rui),
                new VotoBranco(Cargo.PRESIDENTE),
                new VotoNulo(Cargo.PRESIDENTE),
                new VotoNulo(Cargo.PRESIDENTE));

        for (Voto voto : votos) {
            voto.contabilizarEm(apuracao);
        }

        assertEquals(2, apuracao.getVotosDoCandidato(helena));
        assertEquals(1, apuracao.getVotosDoCandidato(rui));
        assertEquals(1, apuracao.getVotosBrancos());
        assertEquals(2, apuracao.getVotosNulos());
        assertEquals(3, apuracao.getVotosValidos());
        assertEquals(6, apuracao.getTotalDeVotos());
        assertEquals(Cargo.PRESIDENTE, apuracao.getCargo());
    }

    @Test
    void deveListarDoMaisVotadoParaOMenosVotado() {
        ApuracaoCargo apuracao = new ApuracaoCargo(Cargo.PRESIDENTE);
        apuracao.incluirCandidato(helena);
        apuracao.registrarVotoParaCandidato(rui);
        assertEquals(List.of(rui, helena), apuracao.listarCandidatosEmOrdemDeVotos());
        assertEquals(0, apuracao.getVotosDoCandidato(helena));
    }

    @Test
    void deveManterAContagemAoIncluirCandidatoQueJaTemVoto() {
        ApuracaoCargo apuracao = new ApuracaoCargo(Cargo.PRESIDENTE);
        apuracao.registrarVotoParaCandidato(helena);
        apuracao.incluirCandidato(helena);
        assertEquals(1, apuracao.getVotosDoCandidato(helena));
    }

    // ---- Invariantes ----

    @Test
    void deveRecusarCandidatoDeOutroCargoECargoNulo() {
        ApuracaoCargo apuracao = new ApuracaoCargo(Cargo.GOVERNADOR);
        assertThrows(IllegalArgumentException.class, () -> apuracao.registrarVotoParaCandidato(helena));
        assertThrows(IllegalArgumentException.class, () -> apuracao.incluirCandidato(null));
        assertThrows(IllegalArgumentException.class, () -> new ApuracaoCargo(null));
    }
}
