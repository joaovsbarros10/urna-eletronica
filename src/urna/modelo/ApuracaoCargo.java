/*
 * Urna Eletrônica
 * Arquivo: ApuracaoCargo.java
 * Autor: João
 */

package urna.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado de um cargo: votos de cada candidato, brancos, nulos e total.
 * É calculado a partir dos votos lidos do banco e não é gravado.
 *
 * @author João
 */
public class ApuracaoCargo {

    private final Cargo cargo;

    private final Map<Candidato, Integer> votosPorCandidato = new LinkedHashMap<>();

    private int votosBrancos;

    private int votosNulos;

    /**
     * Cria a apuração zerada, já com todos os candidatos do cargo.
     *
     * @param cargo      cargo apurado
     * @param candidatos candidatos do cargo, que aparecem mesmo sem votos
     */
    public ApuracaoCargo(Cargo cargo, List<Candidato> candidatos) {
        this.cargo = cargo;
        for (Candidato candidato : candidatos) {
            votosPorCandidato.put(candidato, 0);
        }
    }

    /**
     * Soma um voto para o candidato.
     *
     * @param candidato candidato votado
     */
    public void registrarVotoParaCandidato(Candidato candidato) {
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

    public Cargo getCargo() {
        return cargo;
    }

    /**
     * Informa quantos votos o candidato recebeu.
     *
     * @param candidato candidato consultado
     * @return quantidade de votos
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
     * Soma os votos dados a candidatos.
     *
     * @return total de votos válidos
     */
    public int getVotosValidos() {
        return votosPorCandidato.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Soma todos os votos do cargo.
     *
     * @return válidos, brancos e nulos
     */
    public int getTotalDeVotos() {
        return getVotosValidos() + votosBrancos + votosNulos;
    }

    /**
     * Calcula a porcentagem do candidato sobre os votos válidos, como faz a
     * Justiça Eleitoral: brancos e nulos não entram na conta.
     *
     * @param candidato candidato consultado
     * @return porcentagem de 0 a 100; 0 se ainda não há votos válidos
     */
    public double calcularPercentualDoCandidato(Candidato candidato) {
        return calcularPercentual(getVotosDoCandidato(candidato), getVotosValidos());
    }

    /**
     * Calcula a porcentagem de uma quantidade de votos sobre o total do cargo;
     * usada para válidos, brancos e nulos.
     *
     * @param votos quantidade de votos
     * @return porcentagem de 0 a 100; 0 se o cargo ainda não tem votos
     */
    public double calcularPercentualDoTotal(int votos) {
        return calcularPercentual(votos, getTotalDeVotos());
    }

    private static double calcularPercentual(int votos, int base) {
        return base == 0 ? 0 : votos * 100.0 / base;
    }

    /**
     * Lista os candidatos do mais votado para o menos votado.
     *
     * @return candidatos ordenados por votos
     */
    public List<Candidato> listarCandidatosPorVotos() {
        List<Candidato> candidatos = new ArrayList<>(votosPorCandidato.keySet());
        candidatos.sort(Comparator.comparingInt(this::getVotosDoCandidato).reversed());
        return candidatos;
    }
}
