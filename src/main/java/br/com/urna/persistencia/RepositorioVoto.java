/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioVoto.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.voto.Voto;
import java.util.List;

/**
 * Grava e lê votos. O voto gravado não tem vínculo com eleitor, horário ou
 * ordem de chegada (RN12 e RNF08).
 *
 * @author João
 * @since 1.0
 */
public interface RepositorioVoto {

    /**
     * Grava os votos de uma sessão de forma atômica: entram todos ou nenhum (RN11).
     *
     * @param votos votos de um eleitor, um por cargo
     * @throws br.com.urna.excecao.PersistenciaException se a gravação falhar; nesse caso nada foi gravado
     */
    void gravarTodos(List<Voto> votos);

    /**
     * Lista os votos de um cargo, para a apuração.
     *
     * @param cargo cargo apurado
     * @return votos do cargo, em ordem indefinida
     * @throws br.com.urna.excecao.PersistenciaException se a leitura falhar ou não for permitida
     */
    List<Voto> listarPorCargo(Cargo cargo);
}
