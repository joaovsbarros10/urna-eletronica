/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioVotoSupabase.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import br.com.urna.excecao.PersistenciaException;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.voto.TipoVoto;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.modelo.voto.VotoBranco;
import br.com.urna.modelo.voto.VotoCandidato;
import br.com.urna.modelo.voto.VotoNulo;
import br.com.urna.persistencia.RepositorioCandidato;
import br.com.urna.persistencia.RepositorioVoto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Votos na tabela {@code voto} do Supabase.
 * <p>
 * A gravação dos votos de uma sessão é uma transação com inserção em lote:
 * entram todos ou nenhum (RN11). O repositório não olha a subclasse do voto;
 * pergunta tudo ao próprio voto. A tabela não tem eleitor nem horário, e o
 * identificador é aleatório (RN12).
 *
 * @author João
 * @since 1.0
 */
public class RepositorioVotoSupabase implements RepositorioVoto {

    private static final String COMANDOINCLUIR =
            "insert into voto (cargo, tipo, candidato_identificador) values (?, ?, ?)";

    private static final String CONSULTAPORCARGO =
            "select tipo, candidato_identificador from voto where cargo = ?";

    private final ConexaoBancoDados conexaoBancoDados;

    private final RepositorioCandidato repositorioCandidato;

    /**
     * Cria o repositório sobre a conexão informada.
     *
     * @param conexaoBancoDados    fábrica de conexões com o Supabase
     * @param repositorioCandidato usado na leitura, para ligar o voto ao seu candidato
     */
    public RepositorioVotoSupabase(ConexaoBancoDados conexaoBancoDados, RepositorioCandidato repositorioCandidato) {
        this.conexaoBancoDados = conexaoBancoDados;
        this.repositorioCandidato = repositorioCandidato;
    }

    // ---- Gravação atômica (RN11) ----

    @Override
    public void gravarTodos(List<Voto> votos) {
        try (Connection conexao = conexaoBancoDados.abrirConexao()) {
            conexao.setAutoCommit(false);
            try (PreparedStatement preparado = conexao.prepareStatement(COMANDOINCLUIR)) {
                for (Voto voto : votos) {
                    preencher(preparado, voto);
                    preparado.addBatch();
                }
                preparado.executeBatch();
                conexao.commit();
            } catch (SQLException erro) {
                conexao.rollback();
                throw TradutorErroBanco.traduzir(erro, "gravar os votos");
            }
        } catch (SQLException erro) {
            throw TradutorErroBanco.traduzir(erro, "gravar os votos");
        }
    }

    private void preencher(PreparedStatement comando, Voto voto) throws SQLException {
        OptionalLong identificadorDoCandidato = voto.obterIdentificadorDoCandidato();
        comando.setString(1, voto.getCargo().name());
        comando.setString(2, voto.getTipo().name());
        if (identificadorDoCandidato.isPresent()) {
            comando.setLong(3, identificadorDoCandidato.getAsLong());
        } else {
            comando.setNull(3, Types.BIGINT);
        }
    }

    // ---- Leitura para a apuração ----

    @Override
    public List<Voto> listarPorCargo(Cargo cargo) {
        Map<Long, Candidato> candidatosDoCargo = repositorioCandidato.listarTodos().stream()
                .filter(candidato -> candidato.getCargo() == cargo)
                .collect(Collectors.toMap(Candidato::getIdentificador, Function.identity()));
        try (Connection conexao = conexaoBancoDados.abrirConexao();
             PreparedStatement consulta = conexao.prepareStatement(CONSULTAPORCARGO)) {
            consulta.setString(1, cargo.name());
            try (ResultSet resultado = consulta.executeQuery()) {
                List<Voto> votos = new ArrayList<>();
                while (resultado.next()) {
                    votos.add(criarVoto(cargo, resultado, candidatosDoCargo));
                }
                return votos;
            }
        } catch (SQLException erro) {
            throw TradutorErroBanco.traduzir(erro, "ler os votos de " + cargo.getDescricao());
        }
    }

    /* Único switch sobre o tipo do voto no projeto: na fronteira com o banco ele é inevitável. */
    private Voto criarVoto(Cargo cargo, ResultSet resultado, Map<Long, Candidato> candidatosDoCargo)
            throws SQLException {
        return switch (TipoVoto.valueOf(resultado.getString("tipo"))) {
            case CANDIDATO -> {
                long identificador = resultado.getLong("candidato_identificador");
                Candidato candidato = candidatosDoCargo.get(identificador);
                if (candidato == null) {
                    throw new PersistenciaException("Voto aponta para o candidato " + identificador
                            + ", que não existe em " + cargo.getDescricao() + ".");
                }
                yield new VotoCandidato(cargo, candidato);
            }
            case BRANCO -> new VotoBranco(cargo);
            case NULO -> new VotoNulo(cargo);
        };
    }
}
