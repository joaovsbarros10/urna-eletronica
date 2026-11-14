/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioPartidoSupabase.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import br.com.urna.modelo.Partido;
import br.com.urna.persistencia.RepositorioPartido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Partidos na tabela {@code partido} do Supabase, via JDBC com {@link PreparedStatement}.
 *
 * @author João
 * @since 1.0
 */
public class RepositorioPartidoSupabase implements RepositorioPartido {

    private static final String CONSULTATODOS =
            "select identificador, numero, sigla, nome from partido order by numero";

    private static final String COMANDOINCLUIR =
            "insert into partido (numero, sigla, nome) values (?, ?, ?) returning identificador";

    private static final String COMANDOALTERAR =
            "update partido set numero = ?, sigla = ?, nome = ? where identificador = ?";

    private static final String COMANDOEXCLUIR = "delete from partido where identificador = ?";

    private final ConexaoBancoDados conexaoBancoDados;

    /**
     * Cria o repositório sobre a conexão informada.
     *
     * @param conexaoBancoDados fábrica de conexões com o Supabase
     */
    public RepositorioPartidoSupabase(ConexaoBancoDados conexaoBancoDados) {
        this.conexaoBancoDados = conexaoBancoDados;
    }

    // ---- Leitura ----

    @Override
    public List<Partido> listarTodos() {
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement consulta = conexao.prepareStatement(CONSULTATODOS);
             ResultSet resultado = consulta.executeQuery()) {
            List<Partido> partidos = new ArrayList<>();
            while (resultado.next()) {
                partidos.add(new Partido(resultado.getLong("identificador"), resultado.getInt("numero"),
                        resultado.getString("sigla"), resultado.getString("nome")));
            }
            return partidos;
        } catch (SQLException erro) {
            throw TradutorErroBanco.traduzir(erro, "ler os partidos");
        }
    }

    // ---- Gravação ----

    @Override
    public Partido salvar(Partido partido) {
        try (Connection conexao = conexaoBancoDados.abrirConexao()) {
            return partido.getIdentificador() == 0 ? incluir(conexao, partido) : alterar(conexao, partido);
        } catch (SQLException erro) {
            throw TradutorErroBanco.traduzir(erro, "salvar o partido " + partido.getSigla());
        }
    }

    @Override
    public void excluir(Partido partido) {
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(COMANDOEXCLUIR)) {
            comando.setLong(1, partido.getIdentificador());
            comando.executeUpdate();
        } catch (SQLException erro) {
            throw TradutorErroBanco.traduzir(erro, "excluir o partido " + partido.getSigla());
        }
    }

    private Partido incluir(Connection conexao, Partido partido) throws SQLException {
        try (PreparedStatement comando = conexao.prepareStatement(COMANDOINCLUIR)) {
            comando.setInt(1, partido.getNumero());
            comando.setString(2, partido.getSigla());
            comando.setString(3, partido.getNome());
            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                return new Partido(resultado.getLong(1), partido.getNumero(), partido.getSigla(), partido.getNome());
            }
        }
    }

    private Partido alterar(Connection conexao, Partido partido) throws SQLException {
        try (PreparedStatement comando = conexao.prepareStatement(COMANDOALTERAR)) {
            comando.setInt(1, partido.getNumero());
            comando.setString(2, partido.getSigla());
            comando.setString(3, partido.getNome());
            comando.setLong(4, partido.getIdentificador());
            comando.executeUpdate();
            return partido;
        }
    }
}
