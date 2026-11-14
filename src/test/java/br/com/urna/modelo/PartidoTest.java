/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: PartidoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PartidoTest {

    // ---- RN03: número de 2 dígitos ----

    @Test
    void deveRecusarNumeroForaDaFaixaRn03() {
        assertThrows(IllegalArgumentException.class, () -> new Partido(0, 9, "PX", "Partido X"));
        assertThrows(IllegalArgumentException.class, () -> new Partido(0, 100, "PX", "Partido X"));
    }

    @Test
    void deveAceitarNumerosDosLimitesRn03() {
        assertEquals(10, new Partido(0, 10, "PX", "Partido X").getNumero());
        assertEquals(99, new Partido(0, 99, "PY", "Partido Y").getNumero());
    }

    @Test
    void deveRecusarSiglaOuNomeVazios() {
        assertThrows(IllegalArgumentException.class, () -> new Partido(0, 31, " ", "Partido Aurora"));
        assertThrows(IllegalArgumentException.class, () -> new Partido(0, 31, "PAU", null));
    }

    @Test
    void deveRemoverEspacosDasPontas() {
        Partido partido = new Partido(0, 31, "  PAU ", " Partido Aurora ");
        assertEquals("PAU", partido.getSigla());
        assertEquals("Partido Aurora", partido.getNome());
    }

    // ---- RN04: prefixo do candidato ----

    @Test
    void deveConferirPrefixoDoCandidatoRn04() {
        Partido partido = new Partido(1, 31, "PAU", "Partido Aurora");
        assertTrue(partido.correspondeAoPrefixo("3105"));
        assertTrue(partido.correspondeAoPrefixo("31"));
        assertFalse(partido.correspondeAoPrefixo("4721"));
        assertFalse(partido.correspondeAoPrefixo(null));
    }

    // ---- Identidade ----

    @Test
    void deveCompararPeloNumeroENaoPeloIdentificador() {
        Partido gravado = new Partido(7, 31, "PAU", "Partido Aurora");
        Partido novo = new Partido(0, 31, "PAU", "Partido Aurora");
        assertEquals(gravado, novo);
        assertEquals(gravado.hashCode(), novo.hashCode());
        assertNotEquals(gravado, new Partido(7, 47, "PCV", "Partido Cerrado Verde"));
        assertNotEquals(gravado, "PAU");
        assertEquals("PAU (31)", gravado.toString());
    }
}
