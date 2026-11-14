/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioCandidatoMemoria.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FabricaCandidato;
import java.util.ArrayList;
import java.util.List;

/**
 * Candidatos guardados numa lista, para testes e para a demonstração sem internet.
 * Os identificadores são gerados em sequência a partir de 1.
 *
 * @author João
 * @since 1.0
 */
public class RepositorioCandidatoMemoria implements RepositorioCandidato {

    private final List<Candidato> candidatos = new ArrayList<>();

    private long ultimoIdentificador;

    // ---- Leitura ----

    @Override
    public synchronized List<Candidato> listarTodos() {
        return new ArrayList<>(candidatos);
    }

    // ---- Gravação ----

    @Override
    public synchronized Candidato salvar(Candidato candidato) {
        if (candidato.getIdentificador() == 0) {
            Candidato novo = FabricaCandidato.criar(candidato.gerarFicha().copiarComIdentificador(++ultimoIdentificador));
            candidatos.add(novo);
            return novo;
        }
        candidatos.removeIf(existente -> existente.getIdentificador() == candidato.getIdentificador());
        candidatos.add(candidato);
        ultimoIdentificador = Math.max(ultimoIdentificador, candidato.getIdentificador());
        return candidato;
    }

    @Override
    public synchronized void excluir(Candidato candidato) {
        candidatos.removeIf(existente -> existente.getIdentificador() == candidato.getIdentificador());
    }
}
