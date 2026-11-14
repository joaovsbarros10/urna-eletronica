/*
 * Urna Eletrônica
 * Arquivo: ConexaoBancoDados.java
 * Autor: João
 */

package br.com.urna.persistencia;

import br.com.urna.excecao.UrnaException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexões JDBC com o PostgreSQL do Supabase. Endereço, usuário e senha
 * vêm de variáveis de ambiente ou do arquivo {@code urna.propriedades}, que fica
 * fora do Git; nenhuma senha fica no código.
 *
 * @author João
 */
public class ConexaoBancoDados {

    /** Nome do arquivo local com as credenciais do banco. */
    public static final String ARQUIVOPROPRIEDADES = "urna.propriedades";

    private final String endereco;

    private final String usuario;

    private final String senha;

    private ConexaoBancoDados(String endereco, String usuario, String senha) {
        this.endereco = endereco;
        this.usuario = usuario;
        this.senha = senha;
    }

    /**
     * Lê a configuração com o prefixo informado, por exemplo {@code URNA_BANCO_}
     * para a urna ou {@code URNA_ADMIN_BANCO_} para a administração. A variável
     * de ambiente tem prioridade sobre o arquivo.
     *
     * @param prefixo prefixo das chaves ENDERECO, USUARIO e SENHA
     * @return conexão configurada
     * @throws UrnaException se faltar alguma das três chaves
     */
    public static ConexaoBancoDados carregar(String prefixo) {
        Properties arquivo = lerArquivo();
        String endereco = obterValor(prefixo + "ENDERECO", arquivo);
        String usuario = obterValor(prefixo + "USUARIO", arquivo);
        String senha = obterValor(prefixo + "SENHA", arquivo);
        if (endereco == null || usuario == null || senha == null) {
            throw new UrnaException("Configuração do banco incompleta: defina " + prefixo + "ENDERECO, "
                    + prefixo + "USUARIO e " + prefixo + "SENHA no arquivo " + ARQUIVOPROPRIEDADES + ".");
        }
        return new ConexaoBancoDados(endereco, usuario, senha);
    }

    private static Properties lerArquivo() {
        Properties propriedades = new Properties();
        Path caminho = Path.of(System.getProperty("user.dir"), ARQUIVOPROPRIEDADES);
        if (Files.isRegularFile(caminho)) {
            try (Reader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
                propriedades.load(leitor);
            } catch (IOException erro) {
                throw new UrnaException("Não foi possível ler o arquivo " + ARQUIVOPROPRIEDADES + ".", erro);
            }
        }
        return propriedades;
    }

    private static String obterValor(String chave, Properties arquivo) {
        String valor = System.getenv(chave);
        if (valor == null || valor.isBlank()) {
            valor = arquivo.getProperty(chave);
        }
        return valor == null || valor.isBlank() ? null : valor.strip();
    }

    /**
     * Abre uma nova conexão criptografada. Quem chama deve fechá-la.
     *
     * @return conexão aberta
     * @throws SQLException se o banco não responder ou recusar o usuário
     */
    public Connection abrirConexao() throws SQLException {
        Properties propriedades = new Properties();
        propriedades.setProperty("user", usuario);
        propriedades.setProperty("password", senha);
        propriedades.setProperty("sslmode", "require");
        propriedades.setProperty("connectTimeout", "10");
        return DriverManager.getConnection(endereco, propriedades);
    }
}
