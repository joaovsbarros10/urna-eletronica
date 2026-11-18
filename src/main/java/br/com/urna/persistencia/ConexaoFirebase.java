/*
 * Urna Eletrônica
 * Arquivo: ConexaoFirebase.java
 * Autor: João
 */

package br.com.urna.persistencia;

import br.com.urna.excecao.UrnaException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Fala com o Cloud Firestore pela API REST; as regras de segurança do
 * Firestore decidem o que cada requisição pode fazer. O login no Firebase
 * Authentication é opcional: só acontece se e-mail e senha estiverem
 * configurados. Os valores vêm de variáveis de ambiente ou do arquivo
 * {@code urna.propriedades}, que fica fora do Git.
 *
 * @author João
 */
public class ConexaoFirebase {

    /** Nome do arquivo local com as credenciais. */
    public static final String ARQUIVOPROPRIEDADES = "urna.propriedades";

    private static final String ENDERECOLOGIN =
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

    private static final String ENDERECOFIRESTORE = "https://firestore.googleapis.com/v1/";

    private static final int TEMPOLIMITE = 10000;

    static {
        // No Windows, usa os certificados do próprio sistema, onde antivírus que inspecionam HTTPS instalam o seu.
        if (System.getProperty("os.name").startsWith("Windows")
                && System.getProperty("javax.net.ssl.trustStoreType") == null) {
            System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
        }
    }

    private final String caminhoDosDocumentos;

    private final String chave;

    private final String email;

    private final String senha;

    private String token;

    private Instant validadeDoToken = Instant.MIN;

    private ConexaoFirebase(String projeto, String chave, String email, String senha) {
        this.caminhoDosDocumentos = "projects/" + projeto + "/databases/(default)/documents";
        this.chave = chave;
        this.email = email;
        this.senha = senha;
    }

    // ---- Configuração ----

    /**
     * Lê a configuração. Projeto e chave são comuns às duas telas; e-mail e senha,
     * opcionais, usam o prefixo informado: {@code URNA_BANCO_} para a urna ou
     * {@code URNA_ADMIN_BANCO_} para a administração. A variável de ambiente tem
     * prioridade sobre o arquivo.
     *
     * @param prefixo prefixo das chaves EMAIL e SENHA
     * @return conexão configurada, ainda sem login
     * @throws UrnaException se faltar o projeto, ou a chave quando há login
     */
    public static ConexaoFirebase carregar(String prefixo) {
        Properties arquivo = lerArquivo();
        String projeto = obterValor("URNA_FIREBASE_PROJETO", arquivo);
        String chave = obterValor("URNA_FIREBASE_CHAVE", arquivo);
        String email = obterValor(prefixo + "EMAIL", arquivo);
        String senha = obterValor(prefixo + "SENHA", arquivo);
        if (projeto == null) {
            throw new UrnaException("Configuração do Firebase incompleta: defina URNA_FIREBASE_PROJETO no arquivo "
                    + ARQUIVOPROPRIEDADES + ".");
        }
        if (email != null && senha != null && chave == null) {
            throw new UrnaException("Para entrar com " + prefixo + "EMAIL, defina também URNA_FIREBASE_CHAVE no arquivo "
                    + ARQUIVOPROPRIEDADES + ".");
        }
        return new ConexaoFirebase(projeto, chave, email, senha);
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

    // ---- Operações no Firestore ----

    /**
     * Devolve o nome completo de um documento, usado nas gravações.
     *
     * @param colecao coleção, por exemplo {@code partido}
     * @param identificador identificador do documento
     * @return nome no formato {@code projects/.../documents/colecao/identificador}
     */
    public String obterNomeDoDocumento(String colecao, String identificador) {
        return caminhoDosDocumentos + "/" + colecao + "/" + identificador;
    }

    /**
     * Lê todos os documentos de uma coleção, página por página.
     *
     * @param colecao coleção a ler
     * @param campos campos que devem vir; vazio traz o documento inteiro
     * @param acao o que se tentava fazer, para a mensagem de erro
     * @return documentos lidos, no formato do Firestore
     */
    public List<JsonObject> listar(String colecao, List<String> campos, String acao) {
        StringBuilder filtro = new StringBuilder("?pageSize=300");
        for (String campo : campos) {
            filtro.append("&mask.fieldPaths=").append(campo);
        }
        List<JsonObject> documentos = new ArrayList<>();
        String pagina = "";
        do {
            JsonObject resposta = enviar("GET", ENDERECOFIRESTORE + caminhoDosDocumentos + "/" + colecao + filtro + pagina,
                    null, acao).getAsJsonObject();
            if (resposta.has("documents")) {
                for (JsonElement documento : resposta.getAsJsonArray("documents")) {
                    documentos.add(documento.getAsJsonObject());
                }
            }
            pagina = resposta.has("nextPageToken")
                    ? "&pageToken=" + URLEncoder.encode(resposta.get("nextPageToken").getAsString(), StandardCharsets.UTF_8)
                    : "";
        } while (!pagina.isEmpty());
        return documentos;
    }

    /**
     * Executa uma consulta estruturada (filtro por campo) no Firestore.
     *
     * @param consulta consulta no formato {@code structuredQuery} do Firestore
     * @param acao o que se tentava fazer, para a mensagem de erro
     * @return documentos encontrados
     */
    public List<JsonObject> consultar(JsonObject consulta, String acao) {
        JsonObject corpo = new JsonObject();
        corpo.add("structuredQuery", consulta);
        List<JsonObject> documentos = new ArrayList<>();
        for (JsonElement resultado : enviar("POST", ENDERECOFIRESTORE + caminhoDosDocumentos + ":runQuery", corpo, acao)
                .getAsJsonArray()) {
            if (resultado.getAsJsonObject().has("document")) {
                documentos.add(resultado.getAsJsonObject().getAsJsonObject("document"));
            }
        }
        return documentos;
    }

    /**
     * Aplica as gravações num único {@code commit}: ou todas valem, ou nenhuma.
     *
     * @param gravacoes lista de gravações no formato {@code writes} do Firestore
     * @param acao o que se tentava fazer, para a mensagem de erro
     */
    public void gravar(JsonArray gravacoes, String acao) {
        JsonObject corpo = new JsonObject();
        corpo.add("writes", gravacoes);
        enviar("POST", ENDERECOFIRESTORE + caminhoDosDocumentos + ":commit", corpo, acao);
    }

    // ---- HTTP ----

    private JsonElement enviar(String metodo, String endereco, JsonObject corpo, String acao) {
        if (email != null && senha != null && Instant.now().isAfter(validadeDoToken)) {
            entrar(acao);
        }
        return executar(metodo, endereco, corpo, token, acao);
    }

    /** Faz o login; o token vale uma hora e é renovado alguns minutos antes. */
    private void entrar(String acao) {
        JsonObject corpo = new JsonObject();
        corpo.addProperty("email", email);
        corpo.addProperty("password", senha);
        corpo.addProperty("returnSecureToken", true);
        JsonObject resposta = executar("POST", ENDERECOLOGIN + chave, corpo, null, acao).getAsJsonObject();
        token = resposta.get("idToken").getAsString();
        validadeDoToken = Instant.now().plusSeconds(resposta.get("expiresIn").getAsLong() - 300);
    }

    private static JsonElement executar(String metodo, String endereco, JsonObject corpo, String token, String acao) {
        try {
            HttpURLConnection conexao = (HttpURLConnection) URI.create(endereco).toURL().openConnection();
            conexao.setRequestMethod(metodo);
            conexao.setConnectTimeout(TEMPOLIMITE);
            conexao.setReadTimeout(TEMPOLIMITE);
            if (token != null) {
                conexao.setRequestProperty("Authorization", "Bearer " + token);
            }
            if (corpo != null) {
                conexao.setDoOutput(true);
                conexao.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (OutputStream saida = conexao.getOutputStream()) {
                    saida.write(corpo.toString().getBytes(StandardCharsets.UTF_8));
                }
            }
            int situacao = conexao.getResponseCode();
            try (InputStream entrada = situacao < 400 ? conexao.getInputStream() : conexao.getErrorStream()) {
                String texto = entrada == null ? "{}" : new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
                if (situacao >= 400) {
                    throw new UrnaException("Não foi possível " + acao + ": " + traduzir(situacao, texto));
                }
                return JsonParser.parseString(texto);
            }
        } catch (IOException erro) {
            throw new UrnaException("Não foi possível " + acao + ": sem conexão com o Firebase. Verifique a internet.",
                    erro);
        }
    }

    private static String traduzir(int situacao, String texto) {
        String mensagem = "";
        try {
            JsonObject erro = JsonParser.parseString(texto).getAsJsonObject().getAsJsonObject("error");
            mensagem = erro.get("message").getAsString();
        } catch (RuntimeException formatoDesconhecido) {
            mensagem = "o Firebase respondeu com o código " + situacao + ".";
        }
        if (mensagem.startsWith("INVALID_LOGIN_CREDENTIALS") || mensagem.startsWith("INVALID_PASSWORD")
                || mensagem.startsWith("EMAIL_NOT_FOUND") || mensagem.startsWith("INVALID_EMAIL")) {
            return "e-mail ou senha do Firebase incorretos.";
        }
        if (mensagem.startsWith("API key not valid") || mensagem.startsWith("API_KEY_INVALID")) {
            return "a chave do Firebase (URNA_FIREBASE_CHAVE) não é válida.";
        }
        if (situacao == 403) {
            return "as regras de segurança do Firestore recusaram a operação.";
        }
        if (situacao == 409) {
            return "já existe um registro com esse identificador.";
        }
        if (situacao == 404) {
            return "projeto ou banco do Firebase não encontrado. Confira URNA_FIREBASE_PROJETO.";
        }
        return mensagem;
    }
}
