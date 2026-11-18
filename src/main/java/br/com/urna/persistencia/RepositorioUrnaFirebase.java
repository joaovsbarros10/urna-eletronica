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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repositório no Cloud Firestore, com uma coleção para cada tipo:
 * {@code partido}, {@code candidato} e {@code voto}. O identificador de partido
 * e candidato é o nome do documento; o voto recebe um identificador aleatório.
 *
 * @author João
 */
public class RepositorioUrnaFirebase implements RepositorioUrna {

    private static final String PARTIDOS = "partido";

    private static final String CANDIDATOS = "candidato";

    private static final String VOTOS = "voto";

    private final ConexaoFirebase conexao;

    /**
     * Cria o repositório sobre a conexão informada.
     *
     * @param conexao acesso ao Firebase
     */
    public RepositorioUrnaFirebase(ConexaoFirebase conexao) {
        this.conexao = conexao;
    }

    // ---- Partidos ----

    @Override
    public List<Partido> listarPartidos() {
        List<Partido> partidos = new ArrayList<>();
        for (JsonObject documento : conexao.listar(PARTIDOS, List.of(), "ler os partidos")) {
            JsonObject campos = documento.getAsJsonObject("fields");
            partidos.add(new Partido(lerIdentificador(documento), (int) lerInteiro(campos, "numero"),
                    lerTexto(campos, "sigla"), lerTexto(campos, "nome")));
        }
        partidos.sort(Comparator.comparingInt(Partido::getNumero));
        return partidos;
    }

    @Override
    public Partido salvarPartido(Partido partido) {
        boolean novo = partido.getIdentificador() == 0;
        Partido salvo = novo
                ? new Partido(gerarIdentificador(PARTIDOS), partido.getNumero(), partido.getSigla(), partido.getNome())
                : partido;
        JsonObject campos = new JsonObject();
        campos.add("numero", criarInteiro(salvo.getNumero()));
        campos.add("sigla", criarTexto(salvo.getSigla()));
        campos.add("nome", criarTexto(salvo.getNome()));
        JsonArray gravacoes = new JsonArray();
        gravacoes.add(criarGravacao(PARTIDOS, String.valueOf(salvo.getIdentificador()), campos, novo));
        conexao.gravar(gravacoes, "salvar o partido");
        return salvo;
    }

    @Override
    public void excluirPartido(Partido partido) {
        excluirDocumento(PARTIDOS, partido.getIdentificador(), "excluir o partido");
    }

    // ---- Candidatos ----

    @Override
    public List<Candidato> listarCandidatos() {
        Map<Long, Partido> partidos = new HashMap<>();
        for (Partido partido : listarPartidos()) {
            partidos.put(partido.getIdentificador(), partido);
        }
        List<Candidato> candidatos = new ArrayList<>();
        for (JsonObject documento : conexao.listar(CANDIDATOS, List.of(), "ler os candidatos")) {
            JsonObject campos = documento.getAsJsonObject("fields");
            candidatos.add(Candidato.criar(lerIdentificador(documento), Cargo.valueOf(lerTexto(campos, "cargo")),
                    lerTexto(campos, "numero"), lerTexto(campos, "nome"), partidos.get(lerInteiro(campos, "partido")),
                    lerBytes(campos, "foto"), lerLista(campos, "complementos")));
        }
        candidatos.sort(Comparator.comparing(Candidato::getCargo).thenComparing(Candidato::getNumero));
        return candidatos;
    }

    @Override
    public Candidato salvarCandidato(Candidato candidato) {
        boolean novo = candidato.getIdentificador() == 0;
        Candidato salvo = novo ? candidato.copiarComIdentificador(gerarIdentificador(CANDIDATOS)) : candidato;
        JsonObject campos = new JsonObject();
        campos.add("cargo", criarTexto(salvo.getCargo().name()));
        campos.add("numero", criarTexto(salvo.getNumero()));
        campos.add("nome", criarTexto(salvo.getNome()));
        campos.add("partido", criarInteiro(salvo.getPartido().getIdentificador()));
        campos.add("foto", criarBytes(salvo.getFoto()));
        campos.add("complementos", criarLista(salvo.getComplementos()));
        JsonArray gravacoes = new JsonArray();
        gravacoes.add(criarGravacao(CANDIDATOS, String.valueOf(salvo.getIdentificador()), campos, novo));
        conexao.gravar(gravacoes, "salvar o candidato");
        return salvo;
    }

    /** O Firestore não tem chave estrangeira, então a exclusão confere antes se há votos. */
    @Override
    public void excluirCandidato(Candidato candidato) {
        JsonObject consulta = criarConsulta("candidato", criarInteiro(candidato.getIdentificador()));
        consulta.addProperty("limit", 1);
        if (!conexao.consultar(consulta, "conferir os votos do candidato").isEmpty()) {
            throw new UrnaException("Não foi possível excluir o candidato: ele já recebeu votos.");
        }
        excluirDocumento(CANDIDATOS, candidato.getIdentificador(), "excluir o candidato");
    }

    // ---- Votos ----

    /**
     * Grava os votos num único commit: se um deles for recusado, nenhum fica
     * gravado. O voto não guarda eleitor nem horário.
     */
    @Override
    public void gravarVotos(List<Voto> votos) {
        JsonArray gravacoes = new JsonArray();
        for (Voto voto : votos) {
            JsonObject campos = new JsonObject();
            campos.add("cargo", criarTexto(voto.getCargo().name()));
            campos.add("tipo", criarTexto(voto.getTipo()));
            if (voto.getCandidato() != null) {
                campos.add("candidato", criarInteiro(voto.getCandidato().getIdentificador()));
            }
            gravacoes.add(criarGravacao(VOTOS, UUID.randomUUID().toString(), campos, true));
        }
        conexao.gravar(gravacoes, "gravar os votos");
    }

    @Override
    public List<Voto> listarVotos(Cargo cargo, List<Candidato> candidatos) {
        Map<Long, Candidato> candidatosPorIdentificador = new HashMap<>();
        for (Candidato candidato : candidatos) {
            candidatosPorIdentificador.put(candidato.getIdentificador(), candidato);
        }
        List<Voto> votos = new ArrayList<>();
        for (JsonObject documento : conexao.consultar(criarConsulta("cargo", criarTexto(cargo.name())), "ler os votos")) {
            JsonObject campos = documento.getAsJsonObject("fields");
            String tipo = lerTexto(campos, "tipo");
            if (tipo.equals("CANDIDATO")) {
                votos.add(new VotoCandidato(candidatosPorIdentificador.get(lerInteiro(campos, "candidato"))));
            } else if (tipo.equals("BRANCO")) {
                votos.add(new VotoBranco(cargo));
            } else {
                votos.add(new VotoNulo(cargo));
            }
        }
        return votos;
    }

    // ---- Montagem das requisições ----

    /** Próximo identificador livre da coleção: o maior que existe mais um. */
    private long gerarIdentificador(String colecao) {
        long maior = 0;
        for (JsonObject documento : conexao.listar(colecao, List.of("numero"), "gerar o identificador")) {
            maior = Math.max(maior, lerIdentificador(documento));
        }
        return maior + 1;
    }

    /** O documento novo exige que ainda não exista; o alterado exige que já exista. */
    private JsonObject criarGravacao(String colecao, String identificador, JsonObject campos, boolean novo) {
        JsonObject documento = new JsonObject();
        documento.addProperty("name", conexao.obterNomeDoDocumento(colecao, identificador));
        documento.add("fields", campos);
        JsonObject condicao = new JsonObject();
        condicao.addProperty("exists", !novo);
        JsonObject gravacao = new JsonObject();
        gravacao.add("update", documento);
        gravacao.add("currentDocument", condicao);
        return gravacao;
    }

    private void excluirDocumento(String colecao, long identificador, String acao) {
        JsonObject gravacao = new JsonObject();
        gravacao.addProperty("delete", conexao.obterNomeDoDocumento(colecao, String.valueOf(identificador)));
        JsonArray gravacoes = new JsonArray();
        gravacoes.add(gravacao);
        conexao.gravar(gravacoes, acao);
    }

    /** Consulta os votos em que o campo informado é igual ao valor. */
    private static JsonObject criarConsulta(String campo, JsonObject valor) {
        JsonObject caminho = new JsonObject();
        caminho.addProperty("fieldPath", campo);
        JsonObject filtroDeCampo = new JsonObject();
        filtroDeCampo.add("field", caminho);
        filtroDeCampo.addProperty("op", "EQUAL");
        filtroDeCampo.add("value", valor);
        JsonObject filtro = new JsonObject();
        filtro.add("fieldFilter", filtroDeCampo);
        JsonObject colecao = new JsonObject();
        colecao.addProperty("collectionId", VOTOS);
        JsonArray origem = new JsonArray();
        origem.add(colecao);
        JsonObject consulta = new JsonObject();
        consulta.add("from", origem);
        consulta.add("where", filtro);
        return consulta;
    }

    // ---- Conversão de valores do Firestore ----

    private static JsonObject criarTexto(String valor) {
        JsonObject texto = new JsonObject();
        texto.addProperty("stringValue", valor);
        return texto;
    }

    /** O Firestore manda e recebe inteiros como texto. */
    private static JsonObject criarInteiro(long valor) {
        JsonObject inteiro = new JsonObject();
        inteiro.addProperty("integerValue", String.valueOf(valor));
        return inteiro;
    }

    private static JsonObject criarBytes(byte[] valor) {
        JsonObject bytes = new JsonObject();
        bytes.addProperty("bytesValue", Base64.getEncoder().encodeToString(valor));
        return bytes;
    }

    private static JsonObject criarLista(List<String> valores) {
        JsonArray textos = new JsonArray();
        for (String valor : valores) {
            textos.add(criarTexto(valor));
        }
        JsonObject conteudo = new JsonObject();
        conteudo.add("values", textos);
        JsonObject lista = new JsonObject();
        lista.add("arrayValue", conteudo);
        return lista;
    }

    private static long lerIdentificador(JsonObject documento) {
        String nome = documento.get("name").getAsString();
        return Long.parseLong(nome.substring(nome.lastIndexOf('/') + 1));
    }

    private static String lerTexto(JsonObject campos, String nome) {
        return campos.getAsJsonObject(nome).get("stringValue").getAsString();
    }

    private static long lerInteiro(JsonObject campos, String nome) {
        return campos.getAsJsonObject(nome).get("integerValue").getAsLong();
    }

    private static byte[] lerBytes(JsonObject campos, String nome) {
        return Base64.getDecoder().decode(campos.getAsJsonObject(nome).get("bytesValue").getAsString());
    }

    /** Uma lista vazia chega sem o campo {@code values}. */
    private static List<String> lerLista(JsonObject campos, String nome) {
        List<String> lista = new ArrayList<>();
        JsonArray valores = campos.getAsJsonObject(nome).getAsJsonObject("arrayValue").getAsJsonArray("values");
        if (valores != null) {
            for (JsonElement valor : valores) {
                lista.add(valor.getAsJsonObject().get("stringValue").getAsString());
            }
        }
        return lista;
    }
}
