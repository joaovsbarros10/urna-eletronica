/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: TradutorErroBancoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.excecao.PersistenciaException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class TradutorErroBancoTest {

    @Test
    void deveExplicarFalhaDeConexao() {
        SQLException erro = new SQLException("Connection refused", "08001");
        PersistenciaException traduzido = TradutorErroBanco.traduzir(erro, "gravar os votos");
        assertTrue(traduzido.getMessage().startsWith("Não foi possível gravar os votos: sem conexão"));
        assertSame(erro, traduzido.getCause());
    }

    @Test
    void deveUsarAMensagemDoGatilhoDoBanco() {
        SQLException erro = new SQLException(
                "ERRO: O número do candidato (4705) deve começar com o número do partido (31).\n  Onde: função",
                "P0001");
        assertEquals("Não foi possível salvar o candidato 4705: O número do candidato (4705) deve começar com o "
                + "número do partido (31).", TradutorErroBanco.traduzir(erro, "salvar o candidato 4705").getMessage());
    }

    @Test
    void deveExplicarPermissaoNegadaEUnicidade() {
        assertTrue(TradutorErroBanco.traduzir(new SQLException("permission denied", "42501"), "ler os votos")
                .getMessage().contains("permissão"));
        assertTrue(TradutorErroBanco.traduzir(new SQLException("duplicate key", "23505"), "salvar o partido")
                .getMessage().contains("já existe"));
    }
}
