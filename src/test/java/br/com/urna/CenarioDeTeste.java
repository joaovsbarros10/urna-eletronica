/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CenarioDeTeste.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FabricaCandidato;
import br.com.urna.modelo.candidato.FichaCandidato;
import br.com.urna.modelo.sessao.CatalogoCandidatos;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Dados fictícios para os testes, os mesmos de {@code banco/03-dados-ficticios.sql},
 * com identificadores fixos.
 */
public final class CenarioDeTeste {

    /** PNG de 1 pixel, o mesmo do script de dados fictícios. */
    public static final byte[] FOTOPNG = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

    public static final Partido AURORA = new Partido(1, 31, "PAU", "Partido Aurora");

    public static final Partido CERRADO = new Partido(2, 47, "PCV", "Partido Cerrado Verde");

    private CenarioDeTeste() {
    }

    // ---- Fichas e candidatos ----

    public static FichaCandidato.Montador iniciarFicha(Cargo cargo, String numero, String nome, Partido partido) {
        return new FichaCandidato.Montador(cargo)
                .definirNumero(numero)
                .definirNome(nome)
                .definirPartido(partido)
                .definirFoto(FOTOPNG);
    }

    public static List<Candidato> criarCandidatos() {
        return List.of(
                criar(1, iniciarFicha(Cargo.PRESIDENTE, "31", "Helena Marques", AURORA).definirNomeVice("Otávio Rangel")),
                criar(2, iniciarFicha(Cargo.PRESIDENTE, "47", "Rui Albuquerque", CERRADO).definirNomeVice("Sônia Tavares")),
                criar(3, iniciarFicha(Cargo.GOVERNADOR, "31", "Marcos Vilela", AURORA).definirNomeVice("Beatriz Nunes")),
                criar(4, iniciarFicha(Cargo.GOVERNADOR, "47", "Elisa Prado", CERRADO).definirNomeVice("Hugo Matos")),
                criar(5, iniciarFicha(Cargo.SENADOR, "312", "Clara Duarte", AURORA)
                        .definirNomesDosSuplentes("Paulo Seixas", "Irene Lobo")),
                criar(6, iniciarFicha(Cargo.SENADOR, "475", "Davi Sampaio", CERRADO)
                        .definirNomesDosSuplentes("Nair Costa", "Jorge Leal")),
                criar(7, iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA)),
                criar(8, iniciarFicha(Cargo.DEPUTADOFEDERAL, "4721", "Lívia Castro", CERRADO)),
                criar(9, iniciarFicha(Cargo.DEPUTADOESTADUAL, "31042", "Renata Bastos", AURORA)),
                criar(10, iniciarFicha(Cargo.DEPUTADOESTADUAL, "47388", "Caio Pimentel", CERRADO)));
    }

    public static Candidato buscar(Cargo cargo, String numero) {
        return criarCandidatos().stream()
                .filter(candidato -> candidato.getCargo() == cargo && candidato.getNumero().equals(numero))
                .findFirst()
                .orElseThrow();
    }

    private static Candidato criar(long identificador, FichaCandidato.Montador montador) {
        return FabricaCandidato.criar(montador.definirIdentificador(identificador).montar());
    }

    // ---- Catálogo simples para a sessão ----

    public static CatalogoCandidatos criarCatalogo() {
        List<Candidato> candidatos = criarCandidatos();
        List<Partido> partidos = List.of(AURORA, CERRADO);
        return new CatalogoCandidatos() {
            @Override
            public Optional<Candidato> buscarCandidato(Cargo cargo, String numero) {
                return candidatos.stream()
                        .filter(candidato -> candidato.getCargo() == cargo && candidato.getNumero().equals(numero))
                        .findFirst();
            }

            @Override
            public boolean existePartidoComNumero(int numero) {
                return partidos.stream().anyMatch(partido -> partido.getNumero() == numero);
            }
        };
    }
}
