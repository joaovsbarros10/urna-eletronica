/*
 * Urna Eletrônica
 * Arquivo: Dados.java
 * Autor: João
 */

package br.com.urna.modelo;

import java.util.Base64;
import java.util.List;

/**
 * Partidos e candidatos fictícios usados nos testes.
 */
public final class Dados {

    public static final byte[] FOTO = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

    public static final Partido AURORA = new Partido(1, 31, "PAU", "Partido Aurora");

    public static final Partido CERRADO = new Partido(2, 47, "PCV", "Partido Cerrado Verde");

    private Dados() {
    }

    public static List<Partido> partidos() {
        return List.of(AURORA, CERRADO);
    }

    public static List<Candidato> candidatos() {
        return List.of(
                Candidato.criar(1, Cargo.PRESIDENTE, "31", "Helena Marques", AURORA, FOTO, List.of("Otávio Rangel")),
                Candidato.criar(2, Cargo.GOVERNADOR, "47", "Elisa Prado", CERRADO, FOTO, List.of("Hugo Matos")),
                Candidato.criar(3, Cargo.SENADOR, "312", "Clara Duarte", AURORA, FOTO,
                        List.of("Paulo Seixas", "Irene Lobo")),
                Candidato.criar(4, Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA, FOTO, List.of()),
                Candidato.criar(5, Cargo.DEPUTADOESTADUAL, "47388", "Caio Pimentel", CERRADO, FOTO, List.of()));
    }
}
