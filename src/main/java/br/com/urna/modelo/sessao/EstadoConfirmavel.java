/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: EstadoConfirmavel.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

import br.com.urna.modelo.voto.Voto;

/**
 * Base dos estados em que a tecla CONFIRMA registra um voto: candidato
 * encontrado, número inexistente e voto em branco.
 * <p>
 * Aplica Template Method: {@link #confirmar(SessaoVotacao)} é escrito uma vez
 * (registra o voto, avança o cargo ou encerra) e cada subclasse só diz qual
 * voto criar em {@link #criarVoto(SessaoVotacao)}. Assim a regra "voto de
 * candidato, nulo ou branco" não precisa de {@code switch} sobre tipo.
 *
 * @author João
 * @since 1.0
 */
abstract class EstadoConfirmavel implements EstadoSessao {

    // ---- Teclas ----

    /** Com o voto decidido, novos dígitos são ignorados. */
    @Override
    public void digitar(SessaoVotacao sessao, char digito) {
    }

    @Override
    public void corrigir(SessaoVotacao sessao) {
        sessao.limparDigitos();
        sessao.definirEstado(new EstadoDigitando());
    }

    @Override
    public void votarEmBranco(SessaoVotacao sessao) {
    }

    @Override
    public boolean confirmar(SessaoVotacao sessao) {
        sessao.registrarVoto(criarVoto(sessao));
        if (sessao.existeProximoCargo()) {
            sessao.avancarParaProximoCargo();
            sessao.definirEstado(new EstadoDigitando());
        } else {
            sessao.definirEstado(new EstadoEncerrado());
        }
        return true;
    }

    @Override
    public boolean permiteConfirmar() {
        return true;
    }

    // ---- Passo variável do Template Method ----

    /**
     * Cria o voto que a confirmação registra neste estado.
     *
     * @param sessao sessão em votação, para ler o cargo atual
     * @return voto do cargo atual
     */
    protected abstract Voto criarVoto(SessaoVotacao sessao);
}
