/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: FabricaCandidatoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import static br.com.urna.CenarioDeTeste.AURORA;
import static br.com.urna.CenarioDeTeste.CERRADO;
import static br.com.urna.CenarioDeTeste.FOTOPNG;
import static br.com.urna.CenarioDeTeste.iniciarFicha;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.modelo.Cargo;
import java.util.List;
import org.junit.jupiter.api.Test;

class FabricaCandidatoTest {

    // ---- Subclasse por cargo ----

    @Test
    void deveCriarASubclasseDeCadaCargo() {
        assertInstanceOf(CandidatoPresidente.class, FabricaCandidato.criar(
                iniciarFicha(Cargo.PRESIDENTE, "31", "Helena Marques", AURORA).definirNomeVice("Otávio Rangel").montar()));
        assertInstanceOf(CandidatoGovernador.class, FabricaCandidato.criar(
                iniciarFicha(Cargo.GOVERNADOR, "47", "Elisa Prado", CERRADO).definirNomeVice("Hugo Matos").montar()));
        assertInstanceOf(CandidatoSenador.class, FabricaCandidato.criar(
                iniciarFicha(Cargo.SENADOR, "312", "Clara Duarte", AURORA)
                        .definirNomesDosSuplentes("Paulo Seixas", "Irene Lobo").montar()));
        assertInstanceOf(CandidatoDeputadoFederal.class, FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA).montar()));
        assertInstanceOf(CandidatoDeputadoEstadual.class, FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOESTADUAL, "47388", "Caio Pimentel", CERRADO).montar()));
    }

    // ---- RN01 e RN04: regra do número ----

    @Test
    void deveRecusarQuantidadeErradaDeDigitosRn01() {
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "31050", "Tiago Moura", AURORA).montar()));
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.SENADOR, "31", "Clara Duarte", AURORA)
                        .definirNomesDosSuplentes("Paulo Seixas", "Irene Lobo").montar()));
    }

    @Test
    void deveRecusarNumeroComCaractereQueNaoEDigitoRn01() {
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "31A5", "Tiago Moura", AURORA).montar()));
    }

    @Test
    void deveRecusarNumeroQueNaoComecaComOPartidoRn04() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "4705", "Tiago Moura", AURORA).montar()));
        assertTrue(erro.getMessage().contains("RN04"));
    }

    // ---- RN07: complemento por cargo ----

    @Test
    void deveRecusarPresidenteEGovernadorSemViceRn07() {
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.PRESIDENTE, "31", "Helena Marques", AURORA).montar()));
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.GOVERNADOR, "31", "Marcos Vilela", AURORA).definirNomeVice("  ").montar()));
    }

    @Test
    void deveRecusarSenadorSemOsDoisSuplentesRn07() {
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.SENADOR, "312", "Clara Duarte", AURORA)
                        .definirNomesDosSuplentes("Paulo Seixas", null).montar()));
    }

    // ---- Dados obrigatórios (RN02 e RN06) ----

    @Test
    void deveRecusarCandidatoSemFotoRn06() {
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA).definirFoto(null).montar()));
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA).definirFoto(new byte[0]).montar()));
    }

    @Test
    void deveRecusarCandidatoSemPartidoSemNomeOuSemFichaRn02() {
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", null).montar()));
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", " ", AURORA).montar()));
        assertThrows(IllegalArgumentException.class, () -> FabricaCandidato.criar(null));
        assertThrows(IllegalArgumentException.class, () -> new FichaCandidato.Montador(null));
    }

    // ---- Polimorfismo: linhas adicionais ----

    @Test
    void deveMostrarALinhaDoViceDoPresidente() {
        Candidato candidato = FabricaCandidato.criar(
                iniciarFicha(Cargo.PRESIDENTE, "31", "Helena Marques", AURORA).definirNomeVice("Otávio Rangel").montar());
        assertEquals(List.of("Vice-Presidente: Otávio Rangel"), candidato.obterLinhasAdicionais());
    }

    @Test
    void deveMostrarALinhaDoViceDoGovernador() {
        Candidato candidato = FabricaCandidato.criar(
                iniciarFicha(Cargo.GOVERNADOR, "47", "Elisa Prado", CERRADO).definirNomeVice("Hugo Matos").montar());
        assertEquals(List.of("Vice-Governador: Hugo Matos"), candidato.obterLinhasAdicionais());
    }

    @Test
    void deveMostrarAsDuasLinhasDeSuplentesDoSenador() {
        Candidato candidato = FabricaCandidato.criar(iniciarFicha(Cargo.SENADOR, "312", "Clara Duarte", AURORA)
                .definirNomesDosSuplentes("Paulo Seixas", "Irene Lobo").montar());
        assertEquals(List.of("1º Suplente: Paulo Seixas", "2º Suplente: Irene Lobo"), candidato.obterLinhasAdicionais());
    }

    @Test
    void deveNaoMostrarLinhasParaDeputado() {
        Candidato candidato = FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOESTADUAL, "31042", "Renata Bastos", AURORA).montar());
        assertTrue(candidato.obterLinhasAdicionais().isEmpty());
    }

    // ---- Encapsulamento e ficha ----

    @Test
    void deveDevolverCopiaDaFoto() {
        Candidato candidato = FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA).montar());
        byte[] foto = candidato.getFoto();
        foto[0] = 0;
        assertArrayEquals(FOTOPNG, candidato.getFoto());
    }

    @Test
    void deveGerarFichaComOComplementoDoCargo() {
        Candidato senador = FabricaCandidato.criar(iniciarFicha(Cargo.SENADOR, "312", "Clara Duarte", AURORA)
                .definirIdentificador(5)
                .definirNomeVice("ignorado")
                .definirNomesDosSuplentes("Paulo Seixas", "Irene Lobo").montar());
        FichaCandidato ficha = senador.gerarFicha();
        assertEquals(5, ficha.getIdentificador());
        assertEquals("Paulo Seixas", ficha.getNomePrimeiroSuplente());
        assertEquals("Irene Lobo", ficha.getNomeSegundoSuplente());
        assertNull(ficha.getNomeVice());
        assertEquals(senador, FabricaCandidato.criar(ficha));
    }

    @Test
    void deveCopiarAFichaComOutroIdentificador() {
        FichaCandidato ficha = iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA).montar();
        FichaCandidato copia = ficha.copiarComIdentificador(42);
        assertEquals(0, ficha.getIdentificador());
        assertEquals(42, copia.getIdentificador());
        assertEquals("3105", copia.getNumero());
        assertArrayEquals(FOTOPNG, copia.getFoto());
    }

    @Test
    void deveCompararCandidatosPeloIdentificador() {
        Candidato primeiro = FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA).definirIdentificador(7).montar());
        Candidato mesmoIdentificador = FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3106", "Outro Nome", AURORA).definirIdentificador(7).montar());
        Candidato outro = FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA).definirIdentificador(8).montar());
        assertEquals(primeiro, mesmoIdentificador);
        assertEquals(primeiro.hashCode(), mesmoIdentificador.hashCode());
        assertNotEquals(primeiro, outro);
        assertEquals("3105 - Tiago Moura (PAU)", primeiro.toString());
    }
}
