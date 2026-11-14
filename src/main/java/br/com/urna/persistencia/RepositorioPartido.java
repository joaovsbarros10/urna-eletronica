/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioPartido.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import br.com.urna.modelo.Partido;
import java.util.List;

/**
 * Guarda e lê partidos. Há uma implementação no Supabase (JDBC) e uma em
 * memória, para testes e demonstração sem internet.
 *
 * @author João
 * @since 1.0
 */
public interface RepositorioPartido {

    /**
     * Lista todos os partidos cadastrados.
     *
     * @return lista de partidos, ordenada pelo número
     * @throws br.com.urna.excecao.PersistenciaException se a leitura falhar
     */
    List<Partido> listarTodos();

    /**
     * Inclui o partido, se o identificador for 0, ou altera o partido de mesmo identificador.
     *
     * @param partido partido a gravar
     * @return o partido gravado, com o identificador definitivo
     * @throws br.com.urna.excecao.PersistenciaException se a gravação falhar ou violar regra do banco
     */
    Partido salvar(Partido partido);

    /**
     * Exclui o partido de mesmo identificador.
     *
     * @param partido partido a excluir
     * @throws br.com.urna.excecao.PersistenciaException se a exclusão falhar, por exemplo se o partido tiver candidatos
     */
    void excluir(Partido partido);
}
