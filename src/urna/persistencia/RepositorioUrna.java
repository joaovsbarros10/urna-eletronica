/*
 * Urna Eletrônica
 * Arquivo: RepositorioUrna.java
 * Autor: João
 */

package urna.persistencia;

import java.util.List;
import urna.modelo.Candidato;
import urna.modelo.Cargo;
import urna.modelo.Partido;
import urna.modelo.Voto;

/**
 * Onde a urna guarda partidos, candidatos e votos. Há uma implementação no
 * Firebase (Cloud Firestore) e outra em memória, para a demonstração e os testes.
 * Os erros chegam como {@link urna.excecao.UrnaException}.
 *
 * @author João
 */
public interface RepositorioUrna {

    /**
     * Lista os partidos cadastrados.
     *
     * @return partidos ordenados pelo número
     */
    List<Partido> listarPartidos();

    /**
     * Inclui o partido (identificador 0) ou altera o partido de mesmo identificador.
     *
     * @param partido partido a gravar
     * @return o partido gravado, com o identificador definitivo
     */
    Partido salvarPartido(Partido partido);

    /**
     * Exclui o partido.
     *
     * @param partido partido a excluir
     */
    void excluirPartido(Partido partido);

    /**
     * Lista os candidatos de todos os cargos, com partido e foto.
     *
     * @return candidatos cadastrados
     */
    List<Candidato> listarCandidatos();

    /**
     * Inclui o candidato (identificador 0) ou altera o candidato de mesmo identificador.
     *
     * @param candidato candidato a gravar
     * @return o candidato gravado, com o identificador definitivo
     */
    Candidato salvarCandidato(Candidato candidato);

    /**
     * Exclui o candidato.
     *
     * @param candidato candidato a excluir
     */
    void excluirCandidato(Candidato candidato);

    /**
     * Grava os votos de um eleitor de uma vez: entram todos ou nenhum.
     *
     * @param votos um voto para cada cargo
     */
    void gravarVotos(List<Voto> votos);

    /**
     * Lista os votos de um cargo, para a apuração.
     *
     * @param cargo      cargo apurado
     * @param candidatos candidatos do cargo, para montar os votos em candidato
     * @return votos do cargo
     */
    List<Voto> listarVotos(Cargo cargo, List<Candidato> candidatos);
}
