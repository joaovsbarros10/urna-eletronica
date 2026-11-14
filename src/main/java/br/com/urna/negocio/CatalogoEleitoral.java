/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CatalogoEleitoral.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.negocio;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.sessao.CatalogoCandidatos;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Índice em memória de partidos e candidatos. A urna carrega tudo uma vez na
 * abertura, e nenhuma tecla vai ao banco (RNF05).
 * <p>
 * A carga roda fora da thread de eventos e a consulta roda nela, por isso os
 * métodos são sincronizados.
 *
 * @author João
 * @since 1.0
 */
final class CatalogoEleitoral implements CatalogoCandidatos {

    private Map<String, Candidato> candidatosPorChave = Map.of();

    private Set<Integer> numerosDePartido = Set.of();

    private List<Partido> partidos = List.of();

    private List<Candidato> candidatos = List.of();

    // ---- Carga ----

    synchronized void carregar(List<Partido> novosPartidos, List<Candidato> novosCandidatos) {
        Map<String, Candidato> indice = new HashMap<>();
        for (Candidato candidato : novosCandidatos) {
            indice.put(montarChave(candidato.getCargo(), candidato.getNumero()), candidato);
        }
        Set<Integer> numeros = new HashSet<>();
        for (Partido partido : novosPartidos) {
            numeros.add(partido.getNumero());
        }
        this.candidatosPorChave = Map.copyOf(indice);
        this.numerosDePartido = Set.copyOf(numeros);
        this.partidos = novosPartidos.stream()
                .sorted(Comparator.comparingInt(Partido::getNumero))
                .toList();
        this.candidatos = novosCandidatos.stream()
                .sorted(Comparator.comparingInt((Candidato candidato) -> candidato.getCargo().getOrdemDeVotacao())
                        .thenComparing(Candidato::getNumero))
                .toList();
    }

    // ---- Consulta usada pela sessão de votação ----

    @Override
    public synchronized Optional<Candidato> buscarCandidato(Cargo cargo, String numero) {
        return Optional.ofNullable(candidatosPorChave.get(montarChave(cargo, numero)));
    }

    @Override
    public synchronized boolean existePartidoComNumero(int numero) {
        return numerosDePartido.contains(numero);
    }

    // ---- Consulta usada pelo cadastro e pela apuração ----

    synchronized List<Partido> listarPartidos() {
        return partidos;
    }

    synchronized List<Candidato> listarCandidatos() {
        return candidatos;
    }

    synchronized List<Candidato> listarCandidatosDoCargo(Cargo cargo) {
        return candidatos.stream().filter(candidato -> candidato.getCargo() == cargo).toList();
    }

    synchronized boolean possuiCandidatos(Partido partido) {
        return candidatos.stream().anyMatch(candidato -> candidato.getPartido().getIdentificador()
                == partido.getIdentificador());
    }

    // ---- Chave do índice ----

    private static String montarChave(Cargo cargo, String numero) {
        return cargo.name() + ":" + numero;
    }
}
