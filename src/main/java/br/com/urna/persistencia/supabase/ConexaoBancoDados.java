/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ConexaoBancoDados.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexões JDBC com o PostgreSQL do Supabase pelo Session pooler (porta 5432).
 * <p>
 * Usuário e senha vão separados do endereço, então a senha não precisa de
 * codificação especial. A conexão exige criptografia ({@code sslmode=require})
 * e tem tempo limite curto, para a urna perceber logo a falta de rede.
 *
 * @author João
 * @since 1.0
 */
public class ConexaoBancoDados {

    private static final String TEMPOLIMITECONEXAOEMSEGUNDOS = "10";

    private static final String TEMPOLIMITELEITURAEMSEGUNDOS = "30";

    private final ConfiguracaoSupabase configuracao;

    /**
     * Prepara a abertura de conexões com a configuração informada.
     *
     * @param configuracao endereço, usuário e senha do banco
     * @throws IllegalArgumentException se a configuração for {@code null}
     */
    public ConexaoBancoDados(ConfiguracaoSupabase configuracao) {
        if (configuracao == null) {
            throw new IllegalArgumentException("A conexão precisa da configuração do banco.");
        }
        this.configuracao = configuracao;
    }

    // ---- Conexão ----

    /**
     * Abre uma nova conexão. Quem chama deve fechá-la (use try-with-resources).
     *
     * @return conexão aberta, com confirmação automática ligada
     * @throws SQLException se o banco não responder ou recusar o usuário
     */
    public Connection abrirConexao() throws SQLException {
        Properties propriedades = new Properties();
        propriedades.setProperty("user", configuracao.getUsuario());
        propriedades.setProperty("password", configuracao.getSenha());
        propriedades.setProperty("sslmode", "require");
        propriedades.setProperty("connectTimeout", TEMPOLIMITECONEXAOEMSEGUNDOS);
        propriedades.setProperty("loginTimeout", TEMPOLIMITECONEXAOEMSEGUNDOS);
        propriedades.setProperty("socketTimeout", TEMPOLIMITELEITURAEMSEGUNDOS);
        propriedades.setProperty("ApplicationName", "urna-eletronica");
        return DriverManager.getConnection(configuracao.getEnderecoConexao(), propriedades);
    }
}
