/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CargoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CargoTest {

    @Test
    void deveListarOsCargosNaOrdemDaTabelaDoisPontoUm() {
        assertEquals(List.of(Cargo.DEPUTADOFEDERAL, Cargo.DEPUTADOESTADUAL, Cargo.SENADOR, Cargo.GOVERNADOR,
                Cargo.PRESIDENTE), Cargo.listarEmOrdemDeVotacao());
    }

    @Test
    void deveTerAQuantidadeDeDigitosDeCadaCargoRn01() {
        assertEquals(2, Cargo.PRESIDENTE.getQuantidadeDigitos());
        assertEquals(2, Cargo.GOVERNADOR.getQuantidadeDigitos());
        assertEquals(3, Cargo.SENADOR.getQuantidadeDigitos());
        assertEquals(4, Cargo.DEPUTADOFEDERAL.getQuantidadeDigitos());
        assertEquals(5, Cargo.DEPUTADOESTADUAL.getQuantidadeDigitos());
    }

    @Test
    void deveInformarOComplementoDeCadaCargoRn07() {
        assertTrue(Cargo.PRESIDENTE.possuiVice());
        assertTrue(Cargo.GOVERNADOR.possuiVice());
        assertTrue(Cargo.SENADOR.possuiSuplentes());
        assertFalse(Cargo.SENADOR.possuiVice());
        assertFalse(Cargo.DEPUTADOFEDERAL.possuiVice());
        assertFalse(Cargo.DEPUTADOESTADUAL.possuiSuplentes());
    }

    @Test
    void deveExibirADescricaoDoCargo() {
        assertEquals("Deputado Federal", Cargo.DEPUTADOFEDERAL.getDescricao());
        assertEquals("Deputado Federal", Cargo.DEPUTADOFEDERAL.toString());
    }
}
