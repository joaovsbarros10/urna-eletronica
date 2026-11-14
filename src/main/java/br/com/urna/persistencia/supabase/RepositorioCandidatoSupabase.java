/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioCandidatoSupabase.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import br.com.urna.excecao.PersistenciaException;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FabricaCandidato;
import br.com.urna.modelo.candidato.FichaCandidato;
import br.com.urna.persistencia.RepositorioCandidato;
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
 * Candidatos na tabela {@code candidato} do Supabase. A tabela é única para os
 * cinco cargos, com a coluna {@code cargo} como discriminador: ao ler, o
 * repositório monta uma {@link FichaCandidato} e a {@link FabricaCandidato}
 * escolhe a subclasse.
 *
 * @author João
 * @since 1.0
 */
public class RepositorioCandidatoSupabase implements RepositorioCandidato {

    private static final String CONSULTATODOS = """
            select c.identificador, c.cargo, c.numero, c.nome, c.foto,
                   c.nome_vice, c.nome_primeiro_suplente, c.nome_segundo_suplente,
                   p.identificador as partido_identificador, p.numero as partido_numero,
                   p.sigla as partido_sigla, p.nome as partido_nome
            from candidato c
            join partido p on p.identificador = c.partido_identificador
            order by c.cargo, c.numero""";

    private static final String COMANDOINCLUIR = """
            insert into candidato (cargo, numero, nome, partido_identificador, foto,
                                   nome_vice, nome_primeiro_suplente, nome_segundo_suplente)
            values (?, ?, ?, ?, ?, ?, ?, ?) returning identificador""";

    private static final String COMANDOALTERAR = """
            update candidato set cargo = ?, numero = ?, nome = ?, partido_identificador = ?, foto = ?,
                                 nome_vice = ?, nome_primeiro_suplente = ?, nome_segundo_suplente = ?
            where identificador = ?""";

    private static final String COMANDOEXCLUIR = "delete from candidato where identificador = ?";

    private final ConexaoBancoDados conexaoBancoDados;

    /**
     * Cria o repositório sobre a conexão informada.
     *
     * @param conexaoBancoDados fábrica de conexões com o Supabase
     */
    public RepositorioCandidatoSupabase(ConexaoBancoDados conexaoBancoDados) {
        this.conexaoBancoDados = conexaoBancoDados;
    }

    // ---- Leitura ----

    @Override
    public List<Candidato> listarTodos() {
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement consulta = conexao.prepareStatement(CONSULTATODOS);
             ResultSet resultado = consulta.executeQuery()) {
            Map<Long, Partido> partidosLidos = new HashMap<>();
            List<Candidato> candidatos = new ArrayList<>();
            while (resultado.next()) {
                candidatos.add(lerCandidato(resultado, partidosLidos));
            }
            return candidatos;
        } catch (SQLException erro) {
            throw TradutorErroBanco.traduzir(erro, "ler os candidatos");
        }
    }

    private Candidato lerCandidato(ResultSet resultado, Map<Long, Partido> partidosLidos) throws SQLException {
        long identificadorDoPartido = resultado.getLong("partido_identificador");
        Partido partido = partidosLidos.get(identificadorDoPartido);
        if (partido == null) {
            partido = new Partido(identificadorDoPartido, resultado.getInt("partido_numero"),
                    resultado.getString("partido_sigla"), resultado.getString("partido_nome"));
            partidosLidos.put(identificadorDoPartido, partido);
        }
        FichaCandidato ficha = new FichaCandidato.Montador(Cargo.valueOf(resultado.getString("cargo")))
                .definirIdentificador(resultado.getLong("identificador"))
                .definirNumero(resultado.getString("numero"))
                .definirNome(resultado.getString("nome"))
                .definirPartido(partido)
                .definirFoto(resultado.getBytes("foto"))
                .definirNomeVice(resultado.getString("nome_vice"))
                .definirNomesDosSuplentes(resultado.getString("nome_primeiro_suplente"),
                        resultado.getString("nome_segundo_suplente"))
                .montar();
        try {
            return FabricaCandidato.criar(ficha);
        } catch (IllegalArgumentException erro) {
            throw new PersistenciaException("O candidato " + ficha.getNumero() + " gravado no banco é inválido: "
                    + erro.getMessage(), erro);
        }
    }

    // ---- Gravação ----

    @Override
    public Candidato salvar(Candidato candidato) {
        FichaCandidato ficha = candidato.gerarFicha();
        boolean inclusao = ficha.getIdentificador() == 0;
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(inclusao ? COMANDOINCLUIR : COMANDOALTERAR)) {
            preencher(comando, ficha);
            if (inclusao) {
                try (ResultSet resultado = comando.executeQuery()) {
                    resultado.next();
                    return FabricaCandidato.criar(ficha.copiarComIdentificador(resultado.getLong(1)));
                }
            }
            comando.setLong(9, ficha.getIdentificador());
            comando.executeUpdate();
            return candidato;
        } catch (SQLException erro) {
            throw TradutorErroBanco.traduzir(erro, "salvar o candidato " + candidato.getNumero());
        }
    }

    @Override
    public void excluir(Candidato candidato) {
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement comando = conexao.prepareStatement(COMANDOEXCLUIR)) {
            comando.setLong(1, candidato.getIdentificador());
            comando.executeUpdate();
        } catch (SQLException erro) {
            throw TradutorErroBanco.traduzir(erro, "excluir o candidato " + candidato.getNumero());
        }
    }

    private void preencher(PreparedStatement comando, FichaCandidato ficha) throws SQLException {
        comando.setString(1, ficha.getCargo().name());
        comando.setString(2, ficha.getNumero());
        comando.setString(3, ficha.getNome());
        comando.setLong(4, ficha.getPartido().getIdentificador());
        comando.setBytes(5, ficha.getFoto());
        preencherTextoOpcional(comando, 6, ficha.getNomeVice());
        preencherTextoOpcional(comando, 7, ficha.getNomePrimeiroSuplente());
        preencherTextoOpcional(comando, 8, ficha.getNomeSegundoSuplente());
    }

    private void preencherTextoOpcional(PreparedStatement comando, int posicao, String valor) throws SQLException {
        if (valor == null) {
            comando.setNull(posicao, Types.VARCHAR);
        } else {
            comando.setString(posicao, valor);
        }
    }
}
