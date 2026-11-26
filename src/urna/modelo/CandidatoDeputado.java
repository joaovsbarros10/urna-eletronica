/*
 * Urna Eletrônica
 * Arquivo: CandidatoDeputado.java
 * Autor: João
 */

package urna.modelo;

import java.util.List;

/**
 * Candidato a Deputado Federal ou Estadual, sem vice nem suplentes.
 *
 * @author João
 */
public class CandidatoDeputado extends Candidato {

    CandidatoDeputado(long identificador, Cargo cargo, String numero, String nome, Partido partido, byte[] foto) {
        super(identificador, cargo, numero, nome, partido, foto);
    }

    @Override
    public List<String> obterLinhasAdicionais() {
        return List.of();
    }

    @Override
    public List<String> getComplementos() {
        return List.of();
    }
}
