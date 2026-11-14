/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: Voto.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.voto;

import br.com.urna.modelo.Cargo;
import java.util.OptionalLong;

/**
 * Voto registrado para um cargo: em candidato, em branco ou nulo.
 * <p>
 * O voto não guarda eleitor, horário nem ordem de chegada (RN12). A apuração
 * usa despacho duplo: chama {@link #contabilizarEm(ApuracaoCargo)} e cada
 * subclasse chama o contador certo, sem {@code instanceof}. Os objetos são imutáveis.
 *
 * @author João
 * @since 1.0
 */
public abstract class Voto {

    private final Cargo cargo;

    /**
     * Cria o voto para o cargo informado.
     *
     * @param cargo cargo votado; não pode ser {@code null}
     * @throws IllegalArgumentException se o cargo for {@code null}
     */
    protected Voto(Cargo cargo) {
        if (cargo == null) {
            throw new IllegalArgumentException("O cargo do voto é obrigatório.");
        }
        this.cargo = cargo;
    }

    // ---- Dados do voto ----

    public Cargo getCargo() {
        return cargo;
    }

    /**
     * Informa o tipo do voto, que o repositório grava na coluna {@code tipo}.
     *
     * @return tipo do voto
     */
    public abstract TipoVoto getTipo();

    /**
     * Devolve o identificador do candidato votado, para a coluna {@code candidato_identificador}.
     *
     * @return identificador do candidato, ou vazio para voto em branco ou nulo
     */
    public abstract OptionalLong obterIdentificadorDoCandidato();

    /**
     * Descreve o voto em uma frase curta, para registro e mensagens.
     *
     * @return descrição legível do voto
     */
    public abstract String obterDescricao();

    // ---- Apuração (despacho duplo) ----

    /**
     * Soma este voto no contador certo da apuração.
     *
     * @param apuracao apuração do mesmo cargo do voto
     * @throws IllegalArgumentException se a apuração for de outro cargo
     */
    public abstract void contabilizarEm(ApuracaoCargo apuracao);

    /**
     * Confere se a apuração é do mesmo cargo deste voto.
     *
     * @param apuracao apuração que vai receber o voto
     * @throws IllegalArgumentException se a apuração for {@code null} ou de outro cargo
     */
    protected void exigirMesmoCargo(ApuracaoCargo apuracao) {
        if (apuracao == null || apuracao.getCargo() != cargo) {
            throw new IllegalArgumentException("Voto de " + cargo.getDescricao()
                    + " não pode ser contado em outra apuração.");
        }
    }

    // ---- Representação ----

    @Override
    public String toString() {
        return cargo.getDescricao() + ": " + obterDescricao();
    }
}
