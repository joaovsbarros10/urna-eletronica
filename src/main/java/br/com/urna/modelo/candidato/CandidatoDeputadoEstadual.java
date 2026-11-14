/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CandidatoDeputadoEstadual.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import br.com.urna.modelo.Cargo;

/**
 * Candidato a Deputado Estadual: número de 5 dígitos, sem complemento.
 * Instâncias são criadas pela {@link FabricaCandidato}.
 *
 * @author João
 * @since 1.0
 */
public class CandidatoDeputadoEstadual extends CandidatoDeputado {

    CandidatoDeputadoEstadual(FichaCandidato ficha) {
        super(ficha);
    }

    // ---- Cargo ----

    @Override
    public Cargo getCargo() {
        return Cargo.DEPUTADOESTADUAL;
    }
}
