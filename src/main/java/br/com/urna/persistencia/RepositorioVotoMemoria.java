/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioVotoMemoria.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.voto.Voto;
import java.util.ArrayList;
import java.util.List;

/**
 * Votos guardados numa lista, para testes e para a demonstração sem internet.
 * A gravação de uma sessão acontece num único bloco sincronizado, então é atômica (RN11).
 *
 * @author João
 * @since 1.0
 */
public class RepositorioVotoMemoria implements RepositorioVoto {

    private final List<Voto> votos = new ArrayList<>();

    // ---- Gravação ----

    @Override
    public synchronized void gravarTodos(List<Voto> votosDaSessao) {
        votos.addAll(List.copyOf(votosDaSessao));
    }

    // ---- Leitura ----

    @Override
    public synchronized List<Voto> listarPorCargo(Cargo cargo) {
        return votos.stream().filter(voto -> voto.getCargo() == cargo).toList();
    }

    /**
     * Informa quantos votos foram gravados, somando todos os cargos.
     *
     * @return quantidade de votos gravados
     */
    public synchronized int contarVotos() {
        return votos.size();
    }
}
