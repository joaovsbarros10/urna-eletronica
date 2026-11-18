/*
 * Urna Eletrônica
 * Arquivo: RepositorioUrnaFirebase.java
 * Autor: João
 */

package br.com.urna.persistencia;

import br.com.urna.excecao.UrnaException;
import br.com.urna.modelo.Candidato;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.Voto;
import br.com.urna.modelo.VotoBranco;
import br.com.urna.modelo.VotoCandidato;
import br.com.urna.modelo.VotoNulo;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Repositório no Cloud Firestore, acessado pela API REST. Há uma coleção para
 * cada tipo: {@code partido}, {@code candidato} e {@code voto}. O identificador
 * de partido e candidato é o nome do documento; o voto recebe um identificador
 * aleatório. As regras de segurança ({@code banco/firestore.rules}) conferem
 * cada gravação.
 *
 * @author João
 */
public class RepositorioUrnaFirebase implements RepositorioUrna {

    /** Arquivo local, fora do Git, com o código do projeto no Firebase. */
    public static final String ARQUIVOPROPRIEDADES = "urna.propriedades";

    private static final String ENDERECOFIRESTORE = "https://firestore.googleapis.com/v1/";

    private static final String TEXTO = "stringValue";

    private static final String INTEIRO = "integerValue";

    private static final String BYTES = "bytesValue";

    private static final int TEMPOLIMITE = 10000;

    static {
        // No Windows, usa os certificados do próprio sistema, onde antivírus que inspecionam HTTPS instalam o seu.
        if (System.getProperty("os.name").startsWith("Windows")
                && System.getProperty("javax.net.ssl.trustStoreType") == null) {
            System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
        }
    }

    private final String caminhoDosDocumentos;

    /**
     * Cria o repositório do projeto informado.
     *
     * @param projeto código do projeto no Firebase
     */
    public RepositorioUrnaFirebase(String projeto) {
        this.caminhoDosDocumentos = "projects/" + projeto + "/databases/(default)/documents";
    }

    /**
     * Cria o repositório com o projeto {@code URNA_FIREBASE_PROJETO} do arquivo
     * {@code urna.propriedades}.
     *
     * @return repositório configurado
     * @throws UrnaException se o arquivo ou a chave não existirem
     */
    public static RepositorioUrnaFirebase carregar() {
        Properties propriedades = new Properties();
        Path caminho = Path.of(System.getProperty("user.dir"), ARQUIVOPROPRIEDADES);
        if (Files.isRegularFile(caminho)) {
            try (Reader leitor = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
                propriedades.load(leitor);
            } catch (IOException erro) {
                throw new UrnaException("Não foi possível ler o arquivo " + ARQUIVOPROPRIEDADES + ".", erro);
            }
        }
        String projeto = propriedades.getProperty("URNA_FIREBASE_PROJETO", "").strip();
        if (projeto.isEmpty()) {
            throw new UrnaException("Defina URNA_FIREBASE_PROJETO no arquivo " + ARQUIVOPROPRIEDADES + ".");
        }
        return new RepositorioUrnaFirebase(projeto);
    }

    // ---- Partidos ----

    @Override
    public List<Partido> listarPartidos() {
        List<Partido> partidos = new ArrayList<>();
        for (JsonObject documento : listar("partido", "ler os partidos")) {
            JsonObject campos = documento.getAsJsonObject("fields");
            partidos.add(new Partido(lerIdentificador(documento), Integer.parseInt(ler(campos, "numero", INTEIRO)),
                    ler(campos, "sigla", TEXTO), ler(campos, "nome", TEXTO)));
        }
        partidos.sort(Comparator.comparingInt(Partido::getNumero));
        return partidos;
    }

    @Override
    public Partido salvarPartido(Partido partido) {
        boolean novo = partido.getIdentificador() == 0;
        Partido salvo = novo
                ? new Partido(System.currentTimeMillis(), partido.getNumero(), partido.getSigla(), partido.getNome())
                : partido;
        JsonObject campos = new JsonObject();
        campos.add("numero", criarValor(INTEIRO, String.valueOf(salvo.getNumero())));
        campos.add("sigla", criarValor(TEXTO, salvo.getSigla()));
        campos.add("nome", criarValor(TEXTO, salvo.getNome()));
        gravar("salvar o partido", criarGravacao("partido", String.valueOf(salvo.getIdentificador()), campos, novo));
        return salvo;
    }

    @Override
    public void excluirPartido(Partido partido) {
        gravar("excluir o partido", criarExclusao("partido", partido.getIdentificador()));
    }

    // ---- Candidatos ----

    @Override
    public List<Candidato> listarCandidatos() {
        Map<Long, Partido> partidos = new HashMap<>();
        for (Partido partido : listarPartidos()) {
            partidos.put(partido.getIdentificador(), partido);
        }
        List<Candidato> candidatos = new ArrayList<>();
        for (JsonObject documento : listar("candidato", "ler os candidatos")) {
            JsonObject campos = documento.getAsJsonObject("fields");
            List<String> complementos = new ArrayList<>();
            JsonArray valores = campos.getAsJsonObject("complementos").getAsJsonObject("arrayValue").getAsJsonArray("values");
            if (valores != null) {
                valores.forEach(valor -> complementos.add(valor.getAsJsonObject().get(TEXTO).getAsString()));
            }
            candidatos.add(Candidato.criar(lerIdentificador(documento), Cargo.valueOf(ler(campos, "cargo", TEXTO)),
                    ler(campos, "numero", TEXTO), ler(campos, "nome", TEXTO),
                    partidos.get(Long.parseLong(ler(campos, "partido", INTEIRO))),
                    Base64.getDecoder().decode(ler(campos, "foto", BYTES)), complementos));
        }
        candidatos.sort(Comparator.comparing(Candidato::getCargo).thenComparing(Candidato::getNumero));
        return candidatos;
    }

    @Override
    public Candidato salvarCandidato(Candidato candidato) {
        boolean novo = candidato.getIdentificador() == 0;
        Candidato salvo = novo ? candidato.copiarComIdentificador(System.currentTimeMillis()) : candidato;
        JsonArray nomes = new JsonArray();
        salvo.getComplementos().forEach(nome -> nomes.add(criarValor(TEXTO, nome)));
        JsonObject complementos = new JsonObject();
        complementos.add("arrayValue", new JsonObject());
        complementos.getAsJsonObject("arrayValue").add("values", nomes);
        JsonObject campos = new JsonObject();
        campos.add("cargo", criarValor(TEXTO, salvo.getCargo().name()));
        campos.add("numero", criarValor(TEXTO, salvo.getNumero()));
        campos.add("nome", criarValor(TEXTO, salvo.getNome()));
        campos.add("partido", criarValor(INTEIRO, String.valueOf(salvo.getPartido().getIdentificador())));
        campos.add("foto", criarValor(BYTES, Base64.getEncoder().encodeToString(salvo.getFoto())));
        campos.add("complementos", complementos);
        gravar("salvar o candidato", criarGravacao("candidato", String.valueOf(salvo.getIdentificador()), campos, novo));
        return salvo;
    }

    /** O Firestore não tem chave estrangeira, então a exclusão confere antes se há votos. */
    @Override
    public void excluirCandidato(Candidato candidato) {
        String identificador = String.valueOf(candidato.getIdentificador());
        if (!consultarVotos("candidato", criarValor(INTEIRO, identificador), 1).isEmpty()) {
            throw new UrnaException("Não foi possível excluir o candidato: ele já recebeu votos.");
        }
        gravar("excluir o candidato", criarExclusao("candidato", candidato.getIdentificador()));
    }

    // ---- Votos ----

    /**
     * Grava os votos num único commit: se um deles for recusado, nenhum fica
     * gravado. O voto não guarda eleitor nem horário.
     */
    @Override
    public void gravarVotos(List<Voto> votos) {
        List<JsonObject> gravacoes = new ArrayList<>();
        for (Voto voto : votos) {
            JsonObject campos = new JsonObject();
            campos.add("cargo", criarValor(TEXTO, voto.getCargo().name()));
            campos.add("tipo", criarValor(TEXTO, voto.getTipo()));
            if (voto.getCandidato() != null) {
                campos.add("candidato", criarValor(INTEIRO, String.valueOf(voto.getCandidato().getIdentificador())));
            }
            gravacoes.add(criarGravacao("voto", UUID.randomUUID().toString(), campos, true));
        }
        gravar("gravar os votos", gravacoes.toArray(new JsonObject[0]));
    }

    @Override
    public List<Voto> listarVotos(Cargo cargo, List<Candidato> candidatos) {
        Map<Long, Candidato> candidatosPorIdentificador = new HashMap<>();
        for (Candidato candidato : candidatos) {
            candidatosPorIdentificador.put(candidato.getIdentificador(), candidato);
        }
        List<Voto> votos = new ArrayList<>();
        for (JsonObject campos : consultarVotos("cargo", criarValor(TEXTO, cargo.name()), 0)) {
            String tipo = ler(campos, "tipo", TEXTO);
            if (tipo.equals("CANDIDATO")) {
                long identificador = Long.parseLong(ler(campos, "candidato", INTEIRO));
                votos.add(new VotoCandidato(candidatosPorIdentificador.get(identificador)));
            } else if (tipo.equals("BRANCO")) {
                votos.add(new VotoBranco(cargo));
            } else {
                votos.add(new VotoNulo(cargo));
            }
        }
        return votos;
    }

    // ---- Requisições ao Firestore ----

    /** Lê todos os documentos de uma coleção, página por página. */
    private List<JsonObject> listar(String colecao, String acao) {
        List<JsonObject> documentos = new ArrayList<>();
        String pagina = "";
        do {
            JsonObject resposta = enviar("GET", "/" + colecao + "?pageSize=300" + pagina, null, acao).getAsJsonObject();
            if (resposta.has("documents")) {
                resposta.getAsJsonArray("documents").forEach(documento -> documentos.add(documento.getAsJsonObject()));
            }
            pagina = resposta.has("nextPageToken")
                    ? "&pageToken=" + URLEncoder.encode(resposta.get("nextPageToken").getAsString(), StandardCharsets.UTF_8)
                    : "";
        } while (!pagina.isEmpty());
        return documentos;
    }

    /** Devolve os campos dos votos em que {@code campo} é igual ao valor; limite 0 traz todos. */
    private List<JsonObject> consultarVotos(String campo, JsonObject valor, int limite) {
        String consulta = """
                {"structuredQuery": {"from": [{"collectionId": "voto"}],
                  "where": {"fieldFilter": {"field": {"fieldPath": "%s"}, "op": "EQUAL", "value": %s}}%s}}"""
                .formatted(campo, valor, limite > 0 ? ", \"limit\": " + limite : "");
        List<JsonObject> votos = new ArrayList<>();
        for (JsonElement resultado : enviar("POST", ":runQuery", consulta, "ler os votos").getAsJsonArray()) {
            if (resultado.getAsJsonObject().has("document")) {
                votos.add(resultado.getAsJsonObject().getAsJsonObject("document").getAsJsonObject("fields"));
            }
        }
        return votos;
    }

    /** Aplica as gravações num único {@code commit}: ou todas valem, ou nenhuma. */
    private void gravar(String acao, JsonObject... gravacoes) {
        JsonArray lista = new JsonArray();
        for (JsonObject gravacao : gravacoes) {
            lista.add(gravacao);
        }
        JsonObject corpo = new JsonObject();
        corpo.add("writes", lista);
        enviar("POST", ":commit", corpo.toString(), acao);
    }

    /** O documento novo exige que ainda não exista; o alterado exige que já exista. */
    private JsonObject criarGravacao(String colecao, String identificador, JsonObject campos, boolean novo) {
        JsonObject documento = new JsonObject();
        documento.addProperty("name", caminhoDosDocumentos + "/" + colecao + "/" + identificador);
        documento.add("fields", campos);
        JsonObject condicao = new JsonObject();
        condicao.addProperty("exists", !novo);
        JsonObject gravacao = new JsonObject();
        gravacao.add("update", documento);
        gravacao.add("currentDocument", condicao);
        return gravacao;
    }

    private JsonObject criarExclusao(String colecao, long identificador) {
        JsonObject gravacao = new JsonObject();
        gravacao.addProperty("delete", caminhoDosDocumentos + "/" + colecao + "/" + identificador);
        return gravacao;
    }

    private JsonElement enviar(String metodo, String caminho, String corpo, String acao) {
        try {
            HttpURLConnection conexao = (HttpURLConnection) URI.create(ENDERECOFIRESTORE + caminhoDosDocumentos + caminho)
                    .toURL().openConnection();
            conexao.setRequestMethod(metodo);
            conexao.setConnectTimeout(TEMPOLIMITE);
            conexao.setReadTimeout(TEMPOLIMITE);
            if (corpo != null) {
                conexao.setDoOutput(true);
                conexao.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (OutputStream saida = conexao.getOutputStream()) {
                    saida.write(corpo.getBytes(StandardCharsets.UTF_8));
                }
            }
            int situacao = conexao.getResponseCode();
            try (InputStream entrada = situacao < 400 ? conexao.getInputStream() : conexao.getErrorStream()) {
                String resposta = entrada == null ? "{}" : new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
                if (situacao >= 400) {
                    throw new UrnaException("Não foi possível " + acao + ": " + traduzir(situacao, resposta));
                }
                return JsonParser.parseString(resposta);
            }
        } catch (IOException erro) {
            throw new UrnaException("Não foi possível " + acao + ": sem conexão com o Firebase. Verifique a internet.",
                    erro);
        }
    }

    private static String traduzir(int situacao, String resposta) {
        if (situacao == 403) {
            return "as regras de segurança do Firestore recusaram a operação.";
        }
        if (situacao == 409) {
            return "já existe um registro com esse identificador.";
        }
        if (situacao == 404) {
            return "projeto ou banco do Firebase não encontrado. Confira URNA_FIREBASE_PROJETO.";
        }
        try {
            return JsonParser.parseString(resposta).getAsJsonObject().getAsJsonObject("error").get("message").getAsString();
        } catch (RuntimeException formatoDesconhecido) {
            return "o Firebase respondeu com o código " + situacao + ".";
        }
    }

    // ---- Valores do Firestore ----

    /** Monta um valor tipado do Firestore, por exemplo {@code {"stringValue": "PT"}}. */
    private static JsonObject criarValor(String tipo, String conteudo) {
        JsonObject valor = new JsonObject();
        valor.addProperty(tipo, conteudo);
        return valor;
    }

    /** Lê um campo do documento; o Firestore manda inteiros e bytes como texto. */
    private static String ler(JsonObject campos, String nome, String tipo) {
        return campos.getAsJsonObject(nome).get(tipo).getAsString();
    }

    private static long lerIdentificador(JsonObject documento) {
        String nome = documento.get("name").getAsString();
        return Long.parseLong(nome.substring(nome.lastIndexOf('/') + 1));
    }
}
