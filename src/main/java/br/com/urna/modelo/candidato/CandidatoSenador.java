/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CandidatoSenador.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import br.com.urna.modelo.Cargo;
import java.util.List;

/**
 * Candidato a Senador: número de 3 dígitos, 1º e 2º suplentes (RN07).
 * Os suplentes ficam como texto, não como entidade própria (escopo da versão 1).
 * Instâncias são criadas pela {@link FabricaCandidato}.
 *
 * @author João
 * @since 1.0
 */
public class CandidatoSenador extends Candidato {

    private final String nomePrimeiroSuplente;

    private final String nomeSegundoSuplente;

    CandidatoSenador(FichaCandidato ficha) {
        super(ficha);
        this.nomePrimeiroSuplente = exigirTexto(ficha.getNomePrimeiroSuplente(), "o nome do 1º suplente (RN07)");
        this.nomeSegundoSuplente = exigirTexto(ficha.getNomeSegundoSuplente(), "o nome do 2º suplente (RN07)");
    }

    // ---- Cargo e exibição ----

    @Override
    public Cargo getCargo() {
        return Cargo.SENADOR;
    }

    /**
     * Devolve duas linhas, uma para cada suplente.
     *
     * @return lista com a linha do 1º e a do 2º suplente
     */
    @Override
    public List<String> obterLinhasAdicionais() {
        return List.of("1º Suplente: " + nomePrimeiroSuplente, "2º Suplente: " + nomeSegundoSuplente);
    }

    // ---- Ficha ----

    @Override
    protected void completarFicha(FichaCandidato.Montador montador) {
        montador.definirNomesDosSuplentes(nomePrimeiroSuplente, nomeSegundoSuplente);
    }
}
