/*
 * Urna Eletrônica
 * Arquivo: RepositorioUrnaSupabase.java
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositório no PostgreSQL do Supabase, acessado por JDBC. Todo comando usa
 * {@link PreparedStatement}, então os valores nunca entram no texto do SQL.
 *
 * @author João
 */
public class RepositorioUrnaSupabase implements RepositorioUrna {

    private static final String LISTARPARTIDOS = "select identificador, numero, sigla, nome from partido order by numero";

    private static final String INCLUIRPARTIDO =
            "insert into partido (numero, sigla, nome) values (?, ?, ?) returning identificador";

    private static final String ALTERARPARTIDO =
            "update partido set numero = ?, sigla = ?, nome = ? where identificador = ?";

    private static final String EXCLUIRPARTIDO = "delete from partido where identificador = ?";

    private static final String LISTARCANDIDATOS = """
            select c.identificador, c.cargo, c.numero, c.nome, c.foto, c.nome_vice,
                   c.nome_primeiro_suplente, c.nome_segundo_suplente,
                   p.identificador as partido_identificador, p.numero as partido_numero,
                   p.sigla as partido_sigla, p.nome as partido_nome
            from candidato c join partido p on p.identificador = c.partido_identificador
            order by c.cargo, c.numero""";

    private static final String INCLUIRCANDIDATO = """
            insert into candidato (cargo, numero, nome, partido_identificador, foto,
                                   nome_vice, nome_primeiro_suplente, nome_segundo_suplente)
            values (?, ?, ?, ?, ?, ?, ?, ?) returning identificador""";

    private static final String ALTERARCANDIDATO = """
            update candidato set cargo = ?, numero = ?, nome = ?, partido_identificador = ?, foto = ?,
                                 nome_vice = ?, nome_primeiro_suplente = ?, nome_segundo_suplente = ?
            where identificador = ?""";

    private static final String EXCLUIRCANDIDATO = "delete from candidato where identificador = ?";

    private static final String INCLUIRVOTO = "insert into voto (cargo, tipo, candidato_identificador) values (?, ?, ?)";

    private static final String LISTARVOTOS = "select tipo, candidato_identificador from voto where cargo = ?";

    private final ConexaoBancoDados conexaoBancoDados;

    /**
     * Cria o repositório sobre a conexão informada.
     *
     * @param conexaoBancoDados configuração de acesso ao banco
     */
    public RepositorioUrnaSupabase(ConexaoBancoDados conexaoBancoDados) {
        this.conexaoBancoDados = conexaoBancoDados;
    }

    @Override
    public List<Partido> listarPartidos() {
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(LISTARPARTIDOS);
             ResultSet resultado = comando.executeQuery()) {
            List<Partido> partidos = new ArrayList<>();
            while (resultado.next()) {
                partidos.add(new Partido(resultado.getLong("identificador"), resultado.getInt("numero"),
                        resultado.getString("sigla"), resultado.getString("nome")));
            }
            return partidos;
        } catch (SQLException erro) {
            throw traduzir(erro, "ler os partidos");
        }
    }

    @Override
    public Partido salvarPartido(Partido partido) {
        boolean novo = partido.getIdentificador() == 0;
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(novo ? INCLUIRPARTIDO : ALTERARPARTIDO)) {
            comando.setInt(1, partido.getNumero());
            comando.setString(2, partido.getSigla());
            comando.setString(3, partido.getNome());
            if (!novo) {
                comando.setLong(4, partido.getIdentificador());
                comando.executeUpdate();
                return partido;
            }
            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                return new Partido(resultado.getLong(1), partido.getNumero(), partido.getSigla(), partido.getNome());
            }
        } catch (SQLException erro) {
            throw traduzir(erro, "salvar o partido");
        }
    }

    @Override
    public void excluirPartido(Partido partido) {
        executarExclusao(EXCLUIRPARTIDO, partido.getIdentificador(), "excluir o partido");
    }

    @Override
    public List<Candidato> listarCandidatos() {
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(LISTARCANDIDATOS);
             ResultSet resultado = comando.executeQuery()) {
            Map<Long, Partido> partidos = new HashMap<>();
            List<Candidato> candidatos = new ArrayList<>();
            while (resultado.next()) {
                Partido partido = partidos.computeIfAbsent(resultado.getLong("partido_identificador"),
                        identificador -> lerPartido(resultado, identificador));
                List<String> complementos = new ArrayList<>();
                for (String coluna : List.of("nome_vice", "nome_primeiro_suplente", "nome_segundo_suplente")) {
                    if (resultado.getString(coluna) != null) {
                        complementos.add(resultado.getString(coluna));
                    }
                }
                candidatos.add(Candidato.criar(resultado.getLong("identificador"),
                        Cargo.valueOf(resultado.getString("cargo")), resultado.getString("numero"),
                        resultado.getString("nome"), partido, resultado.getBytes("foto"), complementos));
            }
            return candidatos;
        } catch (SQLException erro) {
            throw traduzir(erro, "ler os candidatos");
        }
    }

    private static Partido lerPartido(ResultSet resultado, long identificador) {
        try {
            return new Partido(identificador, resultado.getInt("partido_numero"),
                    resultado.getString("partido_sigla"), resultado.getString("partido_nome"));
        } catch (SQLException erro) {
            throw traduzir(erro, "ler o partido do candidato");
        }
    }

    @Override
    public Candidato salvarCandidato(Candidato candidato) {
        boolean novo = candidato.getIdentificador() == 0;
        Cargo cargo = candidato.getCargo();
        List<String> complementos = candidato.getComplementos();
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(novo ? INCLUIRCANDIDATO : ALTERARCANDIDATO)) {
            comando.setString(1, cargo.name());
            comando.setString(2, candidato.getNumero());
            comando.setString(3, candidato.getNome());
            comando.setLong(4, candidato.getPartido().getIdentificador());
            comando.setBytes(5, candidato.getFoto());
            preencherTexto(comando, 6, cargo.possuiVice() ? complementos.get(0) : null);
            preencherTexto(comando, 7, cargo.possuiSuplentes() ? complementos.get(0) : null);
            preencherTexto(comando, 8, cargo.possuiSuplentes() ? complementos.get(1) : null);
            if (!novo) {
                comando.setLong(9, candidato.getIdentificador());
                comando.executeUpdate();
                return candidato;
            }
            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                return candidato.copiarComIdentificador(resultado.getLong(1));
            }
        } catch (SQLException erro) {
            throw traduzir(erro, "salvar o candidato");
        }
    }

    private static void preencherTexto(PreparedStatement comando, int posicao, String valor) throws SQLException {
        if (valor == null) {
            comando.setNull(posicao, Types.VARCHAR);
        } else {
            comando.setString(posicao, valor);
        }
    }

    @Override
    public void excluirCandidato(Candidato candidato) {
        executarExclusao(EXCLUIRCANDIDATO, candidato.getIdentificador(), "excluir o candidato");
    }

    /**
     * Grava os votos numa única transação: se um INSERT falhar, o rollback
     * desfaz os outros e nenhum voto fica gravado pela metade.
     */
    @Override
    public void gravarVotos(List<Voto> votos) {
        try (Connection conexao = conexaoBancoDados.abrirConexao()) {
            conexao.setAutoCommit(false);
            try (PreparedStatement comando = conexao.prepareStatement(INCLUIRVOTO)) {
                for (Voto voto : votos) {
                    comando.setString(1, voto.getCargo().name());
                    comando.setString(2, voto.getTipo());
                    if (voto.getCandidato() == null) {
                        comando.setNull(3, Types.BIGINT);
                    } else {
                        comando.setLong(3, voto.getCandidato().getIdentificador());
                    }
                    comando.addBatch();
                }
                comando.executeBatch();
                conexao.commit();
            } catch (SQLException erro) {
                conexao.rollback();
                throw erro;
            }
        } catch (SQLException erro) {
            throw traduzir(erro, "gravar os votos");
        }
    }

    @Override
    public List<Voto> listarVotos(Cargo cargo, List<Candidato> candidatos) {
        Map<Long, Candidato> candidatosPorIdentificador = new HashMap<>();
        for (Candidato candidato : candidatos) {
            candidatosPorIdentificador.put(candidato.getIdentificador(), candidato);
        }
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(LISTARVOTOS)) {
            comando.setString(1, cargo.name());
            try (ResultSet resultado = comando.executeQuery()) {
                List<Voto> votos = new ArrayList<>();
                while (resultado.next()) {
                    String tipo = resultado.getString("tipo");
                    if (tipo.equals("CANDIDATO")) {
                        votos.add(new VotoCandidato(candidatosPorIdentificador.get(resultado.getLong("candidato_identificador"))));
                    } else if (tipo.equals("BRANCO")) {
                        votos.add(new VotoBranco(cargo));
                    } else {
                        votos.add(new VotoNulo(cargo));
                    }
                }
                return votos;
            }
        } catch (SQLException erro) {
            throw traduzir(erro, "ler os votos");
        }
    }

    private void executarExclusao(String sql, long identificador, String acao) {
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setLong(1, identificador);
            comando.executeUpdate();
        } catch (SQLException erro) {
            throw traduzir(erro, acao);
        }
    }

    private static UrnaException traduzir(SQLException erro, String acao) {
        String estado = erro.getSQLState() == null ? "08" : erro.getSQLState();
        String motivo;
        if (estado.startsWith("08")) {
            motivo = "sem conexão com o banco de dados. Verifique a internet.";
        } else if (estado.equals("23505")) {
            motivo = "já existe um registro com esse número ou sigla.";
        } else if (estado.equals("23503")) {
            motivo = "o registro está em uso (partido com candidatos ou candidato com votos).";
        } else if (estado.equals("42501")) {
            motivo = "o usuário do banco não tem permissão para esta operação.";
        } else {
            motivo = erro.getMessage().split("\n")[0];
        }
        return new UrnaException("Não foi possível " + acao + ": " + motivo, erro);
    }
}
