/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CandidatoPresidente.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import br.com.urna.modelo.Cargo;

/**
 * Candidato a Presidente: número de 2 dígitos e vice-presidente.
 * Instâncias são criadas pela {@link FabricaCandidato}.
 *
 * @author João
 * @since 1.0
 */
public class CandidatoPresidente extends CandidatoComVice {

    CandidatoPresidente(FichaCandidato ficha) {
        super(ficha);
    }

    // ---- Cargo e complemento ----

    @Override
    public Cargo getCargo() {
        return Cargo.PRESIDENTE;
    }

    @Override
    protected String obterRotuloDoVice() {
        return "Vice-Presidente";
    }
}
