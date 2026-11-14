/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: EstadoDigitando.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import java.util.Optional;

/**
 * Estado inicial de cada cargo: recebe os dígitos e, ao completar o número,
 * consulta o catálogo e troca de estado (RF04 e RF05).
 *
 * @author João
 * @since 1.0
 */
final class EstadoDigitando implements EstadoSessao {

    // ---- Teclas ----

    @Override
    public void digitar(SessaoVotacao sessao, char digito) {
        if (sessao.estaComNumeroCompleto()) {
            return;
        }
        sessao.acrescentarDigito(digito);
        if (sessao.estaComNumeroCompleto()) {
            decidirVoto(sessao);
        }
    }

    @Override
    public void corrigir(SessaoVotacao sessao) {
        sessao.limparDigitos();
    }

    /** BRANCO só vale com o display vazio (RN08). */
    @Override
    public void votarEmBranco(SessaoVotacao sessao) {
        if (sessao.estaSemDigitos()) {
            sessao.definirEstado(new EstadoVotoEmBranco());
        }
    }

    /** Número incompleto não confirma (RN08). */
    @Override
    public boolean confirmar(SessaoVotacao sessao) {
        return false;
    }

    // ---- Exibição ----

    @Override
    public boolean permiteConfirmar() {
        return false;
    }

    @Override
    public String getAvisoPrincipal() {
        return "";
    }

    // ---- Decisão do voto ao completar o número (RN09) ----

    private void decidirVoto(SessaoVotacao sessao) {
        String numero = sessao.getDigitosDigitados();
        Optional<Candidato> candidato = sessao.getCatalogo().buscarCandidato(sessao.getCargoAtual(), numero);
        if (candidato.isPresent()) {
            sessao.definirCandidatoEncontrado(candidato.get());
            sessao.definirEstado(new EstadoCandidatoEncontrado(candidato.get()));
            return;
        }
        int numeroDoPartido = Integer.parseInt(numero.substring(0, Partido.QUANTIDADEDIGITOS));
        String aviso = sessao.getCatalogo().existePartidoComNumero(numeroDoPartido)
                ? EstadoNumeroInexistente.CANDIDATOINEXISTENTE
                : EstadoNumeroInexistente.NUMEROERRADO;
        sessao.definirEstado(new EstadoNumeroInexistente(aviso));
    }
}
