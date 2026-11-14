/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: VotoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.voto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.CenarioDeTeste;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

class VotoTest {

    private final Candidato tiago = CenarioDeTeste.buscar(Cargo.DEPUTADOFEDERAL, "3105");

    // ---- O que o repositório pergunta ao voto ----

    @Test
    void deveInformarTipoEIdentificadorDoVotoEmCandidato() {
        Voto voto = new VotoCandidato(Cargo.DEPUTADOFEDERAL, tiago);
        assertEquals(TipoVoto.CANDIDATO, voto.getTipo());
        assertEquals(OptionalLong.of(7), voto.obterIdentificadorDoCandidato());
        assertEquals("3105 - Tiago Moura", voto.obterDescricao());
        assertEquals("Deputado Federal: 3105 - Tiago Moura", voto.toString());
    }

    @Test
    void deveInformarTipoSemCandidatoNosVotosBrancoENulo() {
        Voto branco = new VotoBranco(Cargo.SENADOR);
        Voto nulo = new VotoNulo(Cargo.SENADOR);
        assertEquals(TipoVoto.BRANCO, branco.getTipo());
        assertEquals(TipoVoto.NULO, nulo.getTipo());
        assertTrue(branco.obterIdentificadorDoCandidato().isEmpty());
        assertTrue(nulo.obterIdentificadorDoCandidato().isEmpty());
        assertEquals("Voto em branco", branco.obterDescricao());
        assertEquals("Voto nulo", nulo.obterDescricao());
    }

    // ---- Invariantes ----

    @Test
    void deveRecusarVotoSemCargo() {
        assertThrows(IllegalArgumentException.class, () -> new VotoBranco(null));
        assertThrows(IllegalArgumentException.class, () -> new VotoNulo(null));
    }

    @Test
    void deveRecusarVotoEmCandidatoDeOutroCargoOuSemCandidato() {
        assertThrows(IllegalArgumentException.class, () -> new VotoCandidato(Cargo.PRESIDENTE, tiago));
        assertThrows(IllegalArgumentException.class, () -> new VotoCandidato(Cargo.PRESIDENTE, null));
    }

    // ---- Despacho duplo ----

    @Test
    void deveRecusarContagemEmApuracaoDeOutroCargo() {
        ApuracaoCargo apuracaoDoSenado = new ApuracaoCargo(Cargo.SENADOR);
        assertThrows(IllegalArgumentException.class,
                () -> new VotoCandidato(Cargo.DEPUTADOFEDERAL, tiago).contabilizarEm(apuracaoDoSenado));
        assertThrows(IllegalArgumentException.class, () -> new VotoBranco(Cargo.PRESIDENTE).contabilizarEm(apuracaoDoSenado));
        assertThrows(IllegalArgumentException.class, () -> new VotoNulo(Cargo.PRESIDENTE).contabilizarEm(null));
    }
}
