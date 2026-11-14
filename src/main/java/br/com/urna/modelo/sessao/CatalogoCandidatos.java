/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CatalogoCandidatos.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import java.util.Optional;

/**
 * Consulta de candidatos e partidos que a sessão de votação usa ao completar um número.
 * <p>
 * A interface fica no modelo para que ele não dependa do pacote {@code negocio},
 * que fornece a implementação em memória. As consultas não vão ao banco (RNF05).
 *
 * @author João
 * @since 1.0
 */
public interface CatalogoCandidatos {

    /**
     * Busca o candidato pelo cargo e número (RN05: o número é único dentro do cargo).
     *
     * @param cargo  cargo da votação atual
     * @param numero número completo digitado
     * @return o candidato, ou vazio se não houver candidato com esse número no cargo
     */
    Optional<Candidato> buscarCandidato(Cargo cargo, String numero);

    /**
     * Informa se existe partido com o número. Distingue NÚMERO ERRADO de
     * CANDIDATO INEXISTENTE no aviso do voto nulo (RN09).
     *
     * @param numero número do partido, de 2 dígitos
     * @return {@code true} se o partido existe
     */
    boolean existePartidoComNumero(int numero);
}
