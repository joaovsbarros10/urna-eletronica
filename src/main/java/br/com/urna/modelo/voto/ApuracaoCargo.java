/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ApuracaoCargo.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.voto;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Boletim simplificado de um cargo (RF12): votos por candidato, brancos,
 * nulos e o total. É calculado na leitura dos votos e não é gravado.
 * <p>
 * Os votos chegam por {@link Voto#contabilizarEm(ApuracaoCargo)}, que chama o
 * método de registro certo para cada subclasse.
 *
 * @author João
 * @since 1.0
 */
public class ApuracaoCargo {

    private final Cargo cargo;

    private final Map<Candidato, Integer> votosPorCandidato = new LinkedHashMap<>();

    private int votosBrancos;

    private int votosNulos;

    /**
     * Cria uma apuração zerada.
     *
     * @param cargo cargo apurado
     * @throws IllegalArgumentException se o cargo for {@code null}
     */
    public ApuracaoCargo(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("O cargo da apuração é obrigatório.");
        }
        this.cargo = cargo;
    }

    // ---- Candidatos da apuração ----

    /**
     * Inclui o candidato com zero votos, para que ele apareça no boletim mesmo sem voto.
     * Incluir um candidato que já está na apuração não muda a contagem dele.
     *
     * @param candidato candidato do mesmo cargo
     * @throws IllegalArgumentException se o candidato for de outro cargo
     */
    public void incluirCandidato(Candidato candidato) {
        exigirCandidatoDoCargo(candidato);
        votosPorCandidato.putIfAbsent(candidato, 0);
    }

    // ---- Registro de votos ----

    /**
     * Soma um voto para o candidato.
     *
     * @param candidato candidato do mesmo cargo
     * @throws IllegalArgumentException se o candidato for de outro cargo
     */
    public void registrarVotoParaCandidato(Candidato candidato) {
        exigirCandidatoDoCargo(candidato);
        votosPorCandidato.merge(candidato, 1, Integer::sum);
    }

    /** Soma um voto em branco. */
    public void registrarVotoBranco() {
        votosBrancos++;
    }

    /** Soma um voto nulo. */
    public void registrarVotoNulo() {
        votosNulos++;
    }

    // ---- Resultado ----

    public Cargo getCargo() {
        return cargo;
    }

    /**
     * Informa quantos votos o candidato recebeu.
     *
     * @param candidato candidato consultado
     * @return quantidade de votos; 0 se o candidato não recebeu voto
     */
    public int getVotosDoCandidato(Candidato candidato) {
        return votosPorCandidato.getOrDefault(candidato, 0);
    }

    public int getVotosBrancos() {
        return votosBrancos;
    }

    public int getVotosNulos() {
        return votosNulos;
    }

    /**
     * Soma os votos dados a candidatos (votos válidos).
     *
     * @return total de votos em candidatos
     */
    public int getVotosValidos() {
        return votosPorCandidato.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Soma todos os votos do cargo: em candidatos, brancos e nulos.
     *
     * @return total de votos
     */
    public int getTotalDeVotos() {
        return getVotosValidos() + votosBrancos + votosNulos;
    }

    /**
     * Lista os candidatos da apuração do mais votado para o menos votado;
     * no empate, pelo número.
     *
     * @return nova lista ordenada
     */
    public List<Candidato> listarCandidatosEmOrdemDeVotos() {
        List<Candidato> candidatos = new ArrayList<>(votosPorCandidato.keySet());
        candidatos.sort(Comparator.comparingInt(this::getVotosDoCandidato).reversed()
                .thenComparing(Candidato::getNumero));
        return candidatos;
    }

    // ---- Validação interna ----

    private void exigirCandidatoDoCargo(Candidato candidato) {
        if (candidato == null || candidato.getCargo() != cargo) {
            throw new IllegalArgumentException("Só candidatos a " + cargo.getDescricao() + " entram nesta apuração.");
        }
    }
}
