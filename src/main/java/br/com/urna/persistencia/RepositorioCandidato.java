/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioCandidato.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import br.com.urna.modelo.candidato.Candidato;
import java.util.List;

/**
 * Guarda e lê candidatos, com a foto. Há uma implementação no Supabase (JDBC)
 * e uma em memória, para testes e demonstração sem internet.
 *
 * @author João
 * @since 1.0
 */
public interface RepositorioCandidato {

    /**
     * Lista todos os candidatos, de todos os cargos, já com partido e foto.
     *
     * @return lista de candidatos
     * @throws br.com.urna.excecao.PersistenciaException se a leitura falhar
     */
    List<Candidato> listarTodos();

    /**
     * Inclui o candidato, se o identificador for 0, ou altera o candidato de mesmo identificador.
     *
     * @param candidato candidato a gravar
     * @return o candidato gravado, com o identificador definitivo
     * @throws br.com.urna.excecao.PersistenciaException se a gravação falhar ou violar regra do banco
     */
    Candidato salvar(Candidato candidato);

    /**
     * Exclui o candidato de mesmo identificador.
     *
     * @param candidato candidato a excluir
     * @throws br.com.urna.excecao.PersistenciaException se a exclusão falhar, por exemplo se ele já tiver votos
     */
    void excluir(Candidato candidato);
}
