/*
 * Urna Eletrônica
 * Arquivo: VotoNulo.java
 * Autor: João
 */

package urna.modelo;

/**
 * Voto nulo: número completo que não pertence a nenhum candidato do cargo.
 *
 * @author João
 */
public class VotoNulo extends Voto {

    /**
     * Cria o voto nulo.
     *
     * @param cargo cargo votado
     */
    public VotoNulo(Cargo cargo) {
        super(cargo);
    }

    @Override
    public String getTipo() {
        return "NULO";
    }

    @Override
    public void contabilizarEm(ApuracaoCargo apuracao) {
        apuracao.registrarVotoNulo();
    }
}
