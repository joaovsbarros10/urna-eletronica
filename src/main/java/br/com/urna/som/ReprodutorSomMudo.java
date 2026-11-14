/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ReprodutorSomMudo.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.som;

/**
 * Reprodutor que não faz nada (padrão Null Object). Usado nos testes e quando
 * a máquina não tem saída de áudio, para a urna funcionar sem som em vez de falhar.
 *
 * @author João
 * @since 1.0
 */
public class ReprodutorSomMudo implements ReprodutorSom {

    // ---- Reprodução ----

    /** Não toca nada. */
    @Override
    public void reproduzir(EfeitoSonoro efeito) {
    }
}
