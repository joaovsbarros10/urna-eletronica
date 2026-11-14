/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: SessaoVotacao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.voto.Voto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Votação de um eleitor: cargo atual, dígitos digitados, candidato encontrado
 * e votos já registrados.
 * <p>
 * A sessão percorre os cargos na ordem da tabela 2.1 (RF02) e registra um voto
 * por cargo (RN10). Cada tecla é repassada ao estado atual (padrão State) e, em
 * seguida, todos os ouvintes são avisados uma vez (padrão Observer). Os métodos
 * de transição têm acesso de pacote: só as classes de estado mexem na sessão.
 * <p>
 * A classe não é segura para várias threads; na urna ela só é usada na thread
 * de eventos do Swing.
 *
 * @author João
 * @since 1.0
 */
public class SessaoVotacao {

    private final List<Cargo> cargosNaOrdem;

    private final CatalogoCandidatos catalogo;

    private final StringBuilder digitosDigitados = new StringBuilder();

    private final List<Voto> votosRegistrados = new ArrayList<>();

    private final List<OuvinteSessaoVotacao> ouvintes = new ArrayList<>();

    private int posicaoDoCargo;

    private Candidato candidatoEncontrado;

    private EstadoSessao estadoAtual = new EstadoDigitando();

    /**
     * Começa a sessão no primeiro cargo da ordem de votação, sem dígitos.
     *
     * @param catalogo catálogo em memória usado para achar o candidato ao completar o número
     * @throws IllegalArgumentException se o catálogo for {@code null}
     */
    public SessaoVotacao(CatalogoCandidatos catalogo) {
        if (catalogo == null) {
            throw new IllegalArgumentException("A sessão precisa de um catálogo de candidatos.");
        }
        this.catalogo = catalogo;
        this.cargosNaOrdem = Cargo.listarEmOrdemDeVotacao();
    }

    // ---- Ouvintes (Observer) ----

    /**
     * Passa a avisar o ouvinte a cada mudança da sessão.
     *
     * @param ouvinte quem recebe os avisos; não pode ser {@code null}
     * @throws IllegalArgumentException se o ouvinte for {@code null}
     */
    public void adicionarOuvinte(OuvinteSessaoVotacao ouvinte) {
        if (ouvinte == null) {
            throw new IllegalArgumentException("O ouvinte não pode ser nulo.");
        }
        ouvintes.add(ouvinte);
    }

    /**
     * Para de avisar o ouvinte. Não faz nada se ele não estiver registrado.
     *
     * @param ouvinte ouvinte a remover
     */
    public void removerOuvinte(OuvinteSessaoVotacao ouvinte) {
        ouvintes.remove(ouvinte);
    }

    // ---- Teclas do eleitor ----

    /**
     * Trata uma tecla numérica (RF03 e RF04).
     *
     * @param digito dígito de '0' a '9'
     * @throws IllegalArgumentException se o caractere não for um dígito
     */
    public void digitar(char digito) {
        if (digito < '0' || digito > '9') {
            throw new IllegalArgumentException("Tecla numérica inválida: " + digito);
        }
        estadoAtual.digitar(this, digito);
        notificarOuvintes();
    }

    /** Trata a tecla CORRIGE: apaga o voto do cargo atual e reinicia o cargo (RF07). */
    public void corrigir() {
        estadoAtual.corrigir(this);
        notificarOuvintes();
    }

    /** Trata a tecla BRANCO, que só vale com o display vazio (RF06 e RN08). */
    public void votarEmBranco() {
        estadoAtual.votarEmBranco(this);
        notificarOuvintes();
    }

    /**
     * Trata a tecla CONFIRMA: registra o voto do cargo e avança, ou encerra no
     * último cargo (RF08 e RF09). Com número incompleto não faz nada (RN08).
     *
     * @return {@code true} se um voto foi registrado
     */
    public boolean confirmar() {
        boolean votoRegistrado = estadoAtual.confirmar(this);
        notificarOuvintes();
        return votoRegistrado;
    }

    // ---- Leitura do estado (usada pela tela e pelo controlador) ----

    /**
     * Devolve o cargo em votação; depois do fim, continua sendo o último cargo.
     *
     * @return cargo atual
     */
    public Cargo getCargoAtual() {
        return cargosNaOrdem.get(posicaoDoCargo);
    }

    /**
     * Devolve os dígitos já digitados no cargo atual.
     *
     * @return dígitos digitados, possivelmente vazio
     */
    public String getDigitosDigitados() {
        return digitosDigitados.toString();
    }

    /**
     * Devolve o candidato do número completo, se houver.
     *
     * @return candidato encontrado, ou vazio enquanto o número estiver incompleto ou sem candidato
     */
    public Optional<Candidato> getCandidatoEncontrado() {
        return Optional.ofNullable(candidatoEncontrado);
    }

    /**
     * Devolve o aviso do display: VOTO EM BRANCO, NÚMERO ERRADO, CANDIDATO
     * INEXISTENTE seguido de VOTO NULO, ou FIM. As linhas vêm separadas por quebra de linha.
     *
     * @return aviso principal, ou texto vazio se não houver
     */
    public String getAvisoPrincipal() {
        return estadoAtual.getAvisoPrincipal();
    }

    /**
     * Informa se a tecla CONFIRMA registra um voto agora.
     *
     * @return {@code true} com número completo ou voto em branco no cargo atual
     */
    public boolean permiteConfirmar() {
        return estadoAtual.permiteConfirmar();
    }

    /**
     * Devolve os votos já registrados, em lista somente leitura: quem lê não
     * consegue incluir um voto falso.
     *
     * @return cópia imutável dos votos, na ordem dos cargos votados
     */
    public List<Voto> getVotosRegistrados() {
        return List.copyOf(votosRegistrados);
    }

    /**
     * Informa se a sessão terminou, isto é, se já há um voto para cada cargo (RN10).
     *
     * @return {@code true} depois da confirmação do último cargo
     */
    public boolean estaEncerrada() {
        return votosRegistrados.size() == cargosNaOrdem.size();
    }

    // ---- Transições usadas pelos estados (acesso de pacote) ----

    void acrescentarDigito(char digito) {
        digitosDigitados.append(digito);
    }

    void limparDigitos() {
        digitosDigitados.setLength(0);
        candidatoEncontrado = null;
    }

    boolean estaComNumeroCompleto() {
        return digitosDigitados.length() == getCargoAtual().getQuantidadeDigitos();
    }

    boolean estaSemDigitos() {
        return digitosDigitados.length() == 0;
    }

    void definirEstado(EstadoSessao estado) {
        this.estadoAtual = estado;
    }

    void definirCandidatoEncontrado(Candidato candidato) {
        this.candidatoEncontrado = candidato;
    }

    CatalogoCandidatos getCatalogo() {
        return catalogo;
    }

    void registrarVoto(Voto voto) {
        votosRegistrados.add(voto);
    }

    boolean existeProximoCargo() {
        return posicaoDoCargo < cargosNaOrdem.size() - 1;
    }

    void avancarParaProximoCargo() {
        posicaoDoCargo++;
        limparDigitos();
    }

    // ---- Aviso aos ouvintes ----

    private void notificarOuvintes() {
        for (OuvinteSessaoVotacao ouvinte : List.copyOf(ouvintes)) {
            ouvinte.aoAtualizarSessao(this);
        }
    }
}
