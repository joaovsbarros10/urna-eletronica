/*
 * Urna Eletrônica
 * Arquivo: SessaoVotacao.java
 * Autor: João
 */

package urna.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Votação de um eleitor. Percorre os cargos na ordem de votação, recebe as
 * teclas e registra um voto por cargo. A busca do candidato é feita nas listas
 * em memória, sem consultar o banco a cada tecla.
 *
 * @author João
 */
public class SessaoVotacao {

    private final List<Cargo> cargos = Cargo.listarEmOrdemDeVotacao();

    private final List<Partido> partidos;

    private final List<Candidato> candidatos;

    private final StringBuilder digitos = new StringBuilder();

    private final List<Voto> votos = new ArrayList<>();

    private int posicaoDoCargo;

    private Candidato candidatoEncontrado;

    private Situacao situacao = Situacao.DIGITANDO;

    /**
     * Começa a sessão no primeiro cargo.
     *
     * @param partidos   partidos cadastrados
     * @param candidatos candidatos cadastrados
     */
    public SessaoVotacao(List<Partido> partidos, List<Candidato> candidatos) {
        this.partidos = List.copyOf(partidos);
        this.candidatos = List.copyOf(candidatos);
    }

    /**
     * Trata uma tecla numérica. Ao completar o número, procura o candidato.
     *
     * @param digito dígito de '0' a '9'
     */
    public void digitar(char digito) {
        if (situacao != Situacao.DIGITANDO || digito < '0' || digito > '9') {
            return;
        }
        digitos.append(digito);
        if (digitos.length() == getCargoAtual().getQuantidadeDigitos()) {
            procurarCandidato();
        }
    }

    /** Trata a tecla CORRIGE: apaga o voto do cargo atual. */
    public void corrigir() {
        if (situacao != Situacao.ENCERRADA) {
            limparCargoAtual();
        }
    }

    /** Trata a tecla BRANCO, que só vale com o display vazio. */
    public void votarEmBranco() {
        if (situacao == Situacao.DIGITANDO && digitos.length() == 0) {
            situacao = Situacao.BRANCO;
        }
    }

    /**
     * Trata a tecla CONFIRMA: registra o voto do cargo e passa para o próximo.
     * Com o número incompleto, não faz nada.
     *
     * @return {@code true} se um voto foi registrado
     */
    public boolean confirmar() {
        if (!permiteConfirmar()) {
            return false;
        }
        votos.add(criarVoto());
        if (posicaoDoCargo < cargos.size() - 1) {
            posicaoDoCargo++;
            limparCargoAtual();
        } else {
            situacao = Situacao.ENCERRADA;
        }
        return true;
    }

    private void procurarCandidato() {
        String numero = digitos.toString();
        for (Candidato candidato : candidatos) {
            if (candidato.getCargo() == getCargoAtual() && candidato.getNumero().equals(numero)) {
                candidatoEncontrado = candidato;
                situacao = Situacao.CANDIDATO;
                return;
            }
        }
        boolean partidoExiste = partidos.stream().anyMatch(partido -> partido.correspondeAoPrefixo(numero));
        situacao = partidoExiste ? Situacao.CANDIDATOINEXISTENTE : Situacao.NUMEROERRADO;
    }

    private Voto criarVoto() {
        if (situacao == Situacao.CANDIDATO) {
            return new VotoCandidato(candidatoEncontrado);
        }
        if (situacao == Situacao.BRANCO) {
            return new VotoBranco(getCargoAtual());
        }
        return new VotoNulo(getCargoAtual());
    }

    private void limparCargoAtual() {
        digitos.setLength(0);
        candidatoEncontrado = null;
        situacao = Situacao.DIGITANDO;
    }

    public Cargo getCargoAtual() {
        return cargos.get(posicaoDoCargo);
    }

    public String getDigitos() {
        return digitos.toString();
    }

    public Optional<Candidato> getCandidatoEncontrado() {
        return Optional.ofNullable(candidatoEncontrado);
    }

    /**
     * Devolve o aviso do display; as linhas vêm separadas por quebra de linha.
     *
     * @return o aviso da situação atual, ou texto vazio
     */
    public String getAviso() {
        return situacao.getAviso();
    }

    /**
     * Informa se CONFIRMA registra voto agora.
     *
     * @return {@code true} com número completo ou voto em branco
     */
    public boolean permiteConfirmar() {
        return situacao != Situacao.DIGITANDO && situacao != Situacao.ENCERRADA;
    }

    public boolean estaEncerrada() {
        return situacao == Situacao.ENCERRADA;
    }

    /**
     * Devolve os votos registrados numa lista que não pode ser alterada.
     *
     * @return votos da sessão
     */
    public List<Voto> getVotos() {
        return List.copyOf(votos);
    }

    private enum Situacao {
        DIGITANDO(""),
        CANDIDATO(""),
        BRANCO("VOTO EM BRANCO"),
        NUMEROERRADO("NÚMERO ERRADO\nVOTO NULO"),
        CANDIDATOINEXISTENTE("CANDIDATO INEXISTENTE\nVOTO NULO"),
        ENCERRADA("FIM");

        private final String aviso;

        Situacao(String aviso) {
            this.aviso = aviso;
        }

        String getAviso() {
            return aviso;
        }
    }
}
