/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CandidatoDeputadoFederal.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import br.com.urna.modelo.Cargo;

/**
 * Candidato a Deputado Federal: número de 4 dígitos, sem complemento.
 * Instâncias são criadas pela {@link FabricaCandidato}.
 *
 * @author João
 * @since 1.0
 */
public class CandidatoDeputadoFederal extends CandidatoDeputado {

    CandidatoDeputadoFederal(FichaCandidato ficha) {
        super(ficha);
    }

    // ---- Cargo ----

    @Override
    public Cargo getCargo() {
        return Cargo.DEPUTADOFEDERAL;
    }
}
