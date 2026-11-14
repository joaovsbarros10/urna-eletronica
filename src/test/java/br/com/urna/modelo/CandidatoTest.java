/*
 * Urna Eletrônica
 * Arquivo: CandidatoTest.java
 * Autor: João
 */

package br.com.urna.modelo;

import static br.com.urna.modelo.Dados.AURORA;
import static br.com.urna.modelo.Dados.FOTO;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CandidatoTest {

    @Test
    void deveCriarASubclasseDeCadaCargo() {
        assertInstanceOf(CandidatoComVice.class,
                Candidato.criar(0, Cargo.PRESIDENTE, "31", "Helena", AURORA, FOTO, List.of("Otávio")));
        assertInstanceOf(CandidatoSenador.class,
                Candidato.criar(0, Cargo.SENADOR, "312", "Clara", AURORA, FOTO, List.of("Paulo", "Irene")));
        assertInstanceOf(CandidatoDeputado.class,
                Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3105", "Tiago", AURORA, FOTO, List.of()));
    }

    @Test
    void deveRecusarQuantidadeErradaDeDigitos() {
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "310", "Tiago", AURORA, FOTO, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "31A5", "Tiago", AURORA, FOTO, List.of()));
    }

    @Test
    void deveRecusarNumeroQueNaoComecaComOPartido() {
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "4705", "Tiago", AURORA, FOTO, List.of()));
    }

    @Test
    void deveExigirViceESuplentes() {
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, Cargo.GOVERNADOR, "31", "Marcos", AURORA, FOTO, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, Cargo.SENADOR, "312", "Clara", AURORA, FOTO, List.of("Paulo")));
    }

    @Test
    void deveExigirFotoNomePartidoECargo() {
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3105", "Tiago", AURORA, null, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3105", " ", AURORA, FOTO, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3105", "Tiago", null, FOTO, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> Candidato.criar(0, null, "3105", "Tiago", AURORA, FOTO, List.of()));
    }

    @Test
    void deveMostrarAsLinhasDeCadaCargo() {
        assertEquals(List.of("Vice-Presidente: Otávio"),
                Candidato.criar(0, Cargo.PRESIDENTE, "31", "Helena", AURORA, FOTO, List.of("Otávio"))
                        .obterLinhasAdicionais());
        assertEquals(List.of("1º Suplente: Paulo", "2º Suplente: Irene"),
                Candidato.criar(0, Cargo.SENADOR, "312", "Clara", AURORA, FOTO, List.of("Paulo", "Irene"))
                        .obterLinhasAdicionais());
        assertTrue(Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3105", "Tiago", AURORA, FOTO, List.of())
                .obterLinhasAdicionais().isEmpty());
    }

    @Test
    void deveProtegerAFotoECopiarComNovoIdentificador() {
        Candidato candidato = Candidato.criar(0, Cargo.SENADOR, "312", "Clara", AURORA, FOTO, List.of("Paulo", "Irene"));
        candidato.getFoto()[0] = 0;
        assertArrayEquals(FOTO, candidato.getFoto());

        Candidato copia = candidato.copiarComIdentificador(9);
        assertEquals(9, copia.getIdentificador());
        assertEquals(candidato.getComplementos(), copia.getComplementos());
        assertEquals(copia, Candidato.criar(9, Cargo.DEPUTADOFEDERAL, "3105", "Outro", AURORA, FOTO, List.of()));
        assertFalse(copia.equals(candidato));
        assertEquals("312 - Clara (PAU)", candidato.toString());
    }

    @Test
    void deveValidarOPartido() {
        assertThrows(IllegalArgumentException.class, () -> new Partido(0, 9, "PX", "Partido X"));
        assertThrows(IllegalArgumentException.class, () -> new Partido(0, 31, "", "Partido X"));
        assertTrue(AURORA.correspondeAoPrefixo("3105"));
        assertFalse(AURORA.correspondeAoPrefixo("4721"));
        assertEquals(AURORA, new Partido(0, 31, "PAU", "Outro nome"));
        assertEquals("PAU (31)", AURORA.toString());
    }
}
