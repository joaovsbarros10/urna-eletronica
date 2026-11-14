/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: FabricaCandidato.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

/**
 * Cria o candidato da subclasse certa a partir da ficha (padrão Factory).
 * <p>
 * É o único lugar do projeto com {@code switch} sobre {@link br.com.urna.modelo.Cargo}:
 * daqui em diante o resto do sistema trata todos como {@link Candidato}.
 * A subclasse recusa dados sem o complemento do cargo (RN07) e o construtor de
 * {@code Candidato} recusa número fora das regras RN01 e RN04.
 *
 * @author João
 * @since 1.0
 */
public final class FabricaCandidato {

    private FabricaCandidato() {
    }

    // ---- Criação ----

    /**
     * Cria o candidato correspondente ao cargo da ficha.
     *
     * @param ficha dados do candidato
     * @return candidato da subclasse do cargo, já validado
     * @throws IllegalArgumentException se a ficha for {@code null} ou violar RN01, RN02, RN04, RN06 ou RN07
     */
    public static Candidato criar(FichaCandidato ficha) {
        if (ficha == null) {
            throw new IllegalArgumentException("A ficha do candidato é obrigatória.");
        }
        return switch (ficha.getCargo()) {
            case PRESIDENTE -> new CandidatoPresidente(ficha);
            case GOVERNADOR -> new CandidatoGovernador(ficha);
            case SENADOR -> new CandidatoSenador(ficha);
            case DEPUTADOFEDERAL -> new CandidatoDeputadoFederal(ficha);
            case DEPUTADOESTADUAL -> new CandidatoDeputadoEstadual(ficha);
        };
    }
}
