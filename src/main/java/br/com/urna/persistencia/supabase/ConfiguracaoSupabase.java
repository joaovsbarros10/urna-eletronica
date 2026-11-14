/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ConfiguracaoSupabase.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import br.com.urna.excecao.ConfiguracaoException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Endereço, usuário e senha do banco no Supabase.
 * <p>
 * Nenhuma credencial fica no código (RNF07). Os valores vêm de variáveis de
 * ambiente ou, na falta delas, do arquivo local {@code urna.propriedades}
 * (ignorado pelo Git), procurado na pasta atual e depois na pasta do usuário.
 * A urna usa as variáveis {@code URNA_BANCO_*}; o módulo administrativo usa
 * {@code URNA_ADMIN_BANCO_*}.
 *
 * @author João
 * @since 1.0
 */
public final class ConfiguracaoSupabase {

    /** Nome do arquivo local de configuração, procurado quando falta variável de ambiente. */
    public static final String ARQUIVOPROPRIEDADES = "urna.propriedades";

    private static final String PREFIXOVOTACAO = "URNA_BANCO_";

    private static final String PREFIXOADMINISTRACAO = "URNA_ADMIN_BANCO_";

    private final String enderecoConexao;

    private final String usuario;

    private final String senha;

    private ConfiguracaoSupabase(String enderecoConexao, String usuario, String senha) {
        this.enderecoConexao = enderecoConexao;
        this.usuario = usuario;
        this.senha = senha;
    }

    // ---- Carga da configuração ----

    /**
     * Lê a configuração da urna de votação ({@code URNA_BANCO_ENDERECO},
     * {@code URNA_BANCO_USUARIO} e {@code URNA_BANCO_SENHA}).
     *
     * @return configuração completa
     * @throws ConfiguracaoException se faltar alguma das três variáveis
     */
    public static ConfiguracaoSupabase carregar() {
        return carregar(PREFIXOVOTACAO, System.getenv(), listarArquivosPadrao());
    }

    /**
     * Lê a configuração do módulo administrativo ({@code URNA_ADMIN_BANCO_ENDERECO},
     * {@code URNA_ADMIN_BANCO_USUARIO} e {@code URNA_ADMIN_BANCO_SENHA}).
     *
     * @return configuração completa
     * @throws ConfiguracaoException se faltar alguma das três variáveis
     */
    public static ConfiguracaoSupabase carregarAdministracao() {
        return carregar(PREFIXOADMINISTRACAO, System.getenv(), listarArquivosPadrao());
    }

    static ConfiguracaoSupabase carregar(String prefixo, Map<String, String> variaveisDeAmbiente,
                                         List<Path> arquivosCandidatos) {
        Properties arquivo = lerPrimeiroArquivoExistente(arquivosCandidatos);
        String nomeEndereco = prefixo + "ENDERECO";
        String nomeUsuario = prefixo + "USUARIO";
        String nomeSenha = prefixo + "SENHA";
        String endereco = obterValor(nomeEndereco, variaveisDeAmbiente, arquivo);
        String usuario = obterValor(nomeUsuario, variaveisDeAmbiente, arquivo);
        String senha = obterValor(nomeSenha, variaveisDeAmbiente, arquivo);

        List<String> ausentes = new ArrayList<>();
        if (endereco == null) {
            ausentes.add(nomeEndereco);
        }
        if (usuario == null) {
            ausentes.add(nomeUsuario);
        }
        if (senha == null) {
            ausentes.add(nomeSenha);
        }
        if (!ausentes.isEmpty()) {
            throw new ConfiguracaoException("Configuração do banco incompleta. Defina " + String.join(", ", ausentes)
                    + " como variáveis de ambiente ou no arquivo " + ARQUIVOPROPRIEDADES + ".");
        }
        if (!endereco.startsWith("jdbc:postgresql://")) {
            throw new ConfiguracaoException(nomeEndereco + " deve começar com jdbc:postgresql:// (seção 8.1 do projeto).");
        }
        return new ConfiguracaoSupabase(endereco, usuario, senha);
    }

    private static List<Path> listarArquivosPadrao() {
        return List.of(Path.of(System.getProperty("user.dir"), ARQUIVOPROPRIEDADES),
                Path.of(System.getProperty("user.home"), ARQUIVOPROPRIEDADES));
    }

    private static Properties lerPrimeiroArquivoExistente(List<Path> arquivosCandidatos) {
        Properties propriedades = new Properties();
        for (Path caminho : arquivosCandidatos) {
            if (Files.isRegularFile(caminho)) {
                try (Reader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
                    propriedades.load(leitor);
                    return propriedades;
                } catch (IOException erro) {
                    throw new ConfiguracaoException("Não foi possível ler " + caminho + ".", erro);
                }
            }
        }
        return propriedades;
    }

    private static String obterValor(String nome, Map<String, String> variaveisDeAmbiente, Properties arquivo) {
        String valor = variaveisDeAmbiente.get(nome);
        if (valor == null || valor.isBlank()) {
            valor = arquivo.getProperty(nome);
        }
        return valor == null || valor.isBlank() ? null : valor.strip();
    }

    // ---- Dados da configuração ----

    public String getEnderecoConexao() {
        return enderecoConexao;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }

    // ---- Representação ----

    /** Descreve a configuração sem revelar a senha. */
    @Override
    public String toString() {
        return "ConfiguracaoSupabase[" + enderecoConexao + ", usuario=" + usuario + ", senha=***]";
    }
}
