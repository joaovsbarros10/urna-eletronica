/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ConfiguracaoSupabaseTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.excecao.ConfiguracaoException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** RNF07: credenciais vêm de variáveis de ambiente ou de arquivo local, nunca do código. */
class ConfiguracaoSupabaseTest {

    private static final String ENDERECO = "jdbc:postgresql://exemplo.pooler.supabase.com:5432/postgres";

    @TempDir
    Path pastaTemporaria;

    @Test
    void deveLerAsTresVariaveisDeAmbiente() {
        ConfiguracaoSupabase configuracao = ConfiguracaoSupabase.carregar("URNA_BANCO_", Map.of(
                "URNA_BANCO_ENDERECO", ENDERECO,
                "URNA_BANCO_USUARIO", "urna_votacao.referencia",
                "URNA_BANCO_SENHA", "segredo"), List.of());
        assertEquals(ENDERECO, configuracao.getEnderecoConexao());
        assertEquals("urna_votacao.referencia", configuracao.getUsuario());
        assertEquals("segredo", configuracao.getSenha());
    }

    @Test
    void deveListarAsVariaveisQueFaltam() {
        ConfiguracaoException erro = assertThrows(ConfiguracaoException.class, () -> ConfiguracaoSupabase.carregar(
                "URNA_BANCO_", Map.of("URNA_BANCO_ENDERECO", ENDERECO), List.of()));
        assertTrue(erro.getMessage().contains("URNA_BANCO_USUARIO"));
        assertTrue(erro.getMessage().contains("URNA_BANCO_SENHA"));
        assertFalse(erro.getMessage().contains("URNA_BANCO_ENDERECO,"));
    }

    @Test
    void deveUsarOArquivoLocalQuandoFaltaVariavelDeAmbiente() throws Exception {
        Path arquivo = pastaTemporaria.resolve(ConfiguracaoSupabase.ARQUIVOPROPRIEDADES);
        Files.writeString(arquivo, "URNA_ADMIN_BANCO_ENDERECO=" + ENDERECO + "\n"
                + "URNA_ADMIN_BANCO_USUARIO=urna_administracao.referencia\n"
                + "URNA_ADMIN_BANCO_SENHA=do-arquivo\n", StandardCharsets.UTF_8);

        ConfiguracaoSupabase configuracao = ConfiguracaoSupabase.carregar("URNA_ADMIN_BANCO_",
                Map.of("URNA_ADMIN_BANCO_SENHA", "do-ambiente"),
                List.of(pastaTemporaria.resolve("inexistente.propriedades"), arquivo));

        assertEquals("urna_administracao.referencia", configuracao.getUsuario());
        assertEquals("do-ambiente", configuracao.getSenha());
    }

    @Test
    void deveRecusarEnderecoQueNaoEJdbcPostgresql() {
        assertThrows(ConfiguracaoException.class, () -> ConfiguracaoSupabase.carregar("URNA_BANCO_", Map.of(
                "URNA_BANCO_ENDERECO", "https://exemplo.supabase.co",
                "URNA_BANCO_USUARIO", "usuario",
                "URNA_BANCO_SENHA", "senha"), List.of()));
    }

    @Test
    void deveEsconderASenhaNaDescricao() {
        ConfiguracaoSupabase configuracao = ConfiguracaoSupabase.carregar("URNA_BANCO_", Map.of(
                "URNA_BANCO_ENDERECO", ENDERECO,
                "URNA_BANCO_USUARIO", "usuario",
                "URNA_BANCO_SENHA", "segredo"), List.of());
        assertFalse(configuracao.toString().contains("segredo"));
    }
}
