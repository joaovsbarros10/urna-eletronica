/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioPartidoMemoria.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import br.com.urna.modelo.Partido;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Partidos guardados numa lista, para testes e para a demonstração sem internet.
 * Os identificadores são gerados em sequência a partir de 1. Os métodos são
 * sincronizados porque a carga roda fora da thread de eventos.
 *
 * @author João
 * @since 1.0
 */
public class RepositorioPartidoMemoria implements RepositorioPartido {

    private final List<Partido> partidos = new ArrayList<>();

    private long ultimoIdentificador;

    // ---- Leitura ----

    @Override
    public synchronized List<Partido> listarTodos() {
        List<Partido> copia = new ArrayList<>(partidos);
        copia.sort(Comparator.comparingInt(Partido::getNumero));
        return copia;
    }

    // ---- Gravação ----

    @Override
    public synchronized Partido salvar(Partido partido) {
        if (partido.getIdentificador() == 0) {
            Partido novo = new Partido(++ultimoIdentificador, partido.getNumero(), partido.getSigla(), partido.getNome());
            partidos.add(novo);
            return novo;
        }
        partidos.removeIf(existente -> existente.getIdentificador() == partido.getIdentificador());
        partidos.add(partido);
        ultimoIdentificador = Math.max(ultimoIdentificador, partido.getIdentificador());
        return partido;
    }

    @Override
    public synchronized void excluir(Partido partido) {
        partidos.removeIf(existente -> existente.getIdentificador() == partido.getIdentificador());
    }
}
