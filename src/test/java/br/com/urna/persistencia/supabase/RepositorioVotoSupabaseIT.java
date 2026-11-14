/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioVotoSupabaseIT.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import static br.com.urna.CenarioDeTeste.iniciarFicha;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.urna.excecao.PersistenciaException;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FabricaCandidato;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.modelo.voto.VotoBranco;
import br.com.urna.modelo.voto.VotoCandidato;
import br.com.urna.modelo.voto.VotoNulo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Teste de integração com um projeto Supabase DE TESTE (RN11 e RN12). Só roda
 * quando as variáveis de ambiente da seção 8.1 estão definidas, para a role da
 * urna e para a role administrativa. Nunca aponte para o banco da demonstração:
 * o teste grava votos.
 */
@EnabledIfEnvironmentVariable(named = "URNA_BANCO_ENDERECO", matches = ".+")
@EnabledIfEnvironmentVariable(named = "URNA_ADMIN_BANCO_ENDERECO", matches = ".+")
class RepositorioVotoSupabaseIT {

    private final ConexaoBancoDados conexaoUrna = new ConexaoBancoDados(ConfiguracaoSupabase.carregar());

    private final ConexaoBancoDados conexaoAdministracao =
            new ConexaoBancoDados(ConfiguracaoSupabase.carregarAdministracao());

    private final RepositorioVotoSupabase repositorio =
            new RepositorioVotoSupabase(conexaoUrna, new RepositorioCandidatoSupabase(conexaoUrna));

    @Test
    void deveNaoGravarNenhumVotoQuandoOTerceiroInsertFalhaRn11() throws SQLException {
        long antes = contarVotos();
        Partido partido = new RepositorioPartidoSupabase(conexaoUrna).listarTodos().get(0);
        Candidato inexistente = FabricaCandidato.criar(iniciarFicha(Cargo.SENADOR, partido.getNumero() + "9",
                "Candidato Inexistente", partido)
                .definirIdentificador(Long.MAX_VALUE)
                .definirNomesDosSuplentes("Suplente Um", "Suplente Dois")
                .montar());
        List<Voto> votos = List.of(new VotoBranco(Cargo.DEPUTADOFEDERAL), new VotoNulo(Cargo.DEPUTADOESTADUAL),
                new VotoCandidato(Cargo.SENADOR, inexistente), new VotoBranco(Cargo.GOVERNADOR),
                new VotoBranco(Cargo.PRESIDENTE));

        assertThrows(PersistenciaException.class, () -> repositorio.gravarTodos(votos));

        assertEquals(antes, contarVotos());
    }

    @Test
    void deveGravarOsCincoVotosJuntosRn11() throws SQLException {
        long antes = contarVotos();
        repositorio.gravarTodos(List.of(new VotoBranco(Cargo.DEPUTADOFEDERAL), new VotoBranco(Cargo.DEPUTADOESTADUAL),
                new VotoNulo(Cargo.SENADOR), new VotoNulo(Cargo.GOVERNADOR), new VotoBranco(Cargo.PRESIDENTE)));
        assertEquals(antes + 5, contarVotos());
    }

    @Test
    void deveImpedirQueARoleDaUrnaLeiaOsVotosRn12() {
        PersistenciaException erro = assertThrows(PersistenciaException.class,
                () -> repositorio.listarPorCargo(Cargo.PRESIDENTE));
        assertEquals("42501", ((SQLException) erro.getCause()).getSQLState());
    }

    private long contarVotos() throws SQLException {
        try (Connection conexao = conexaoAdministracao.abrirConexao();
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery("select count(*) from voto")) {
            resultado.next();
            return resultado.getLong(1);
        }
    }
}
