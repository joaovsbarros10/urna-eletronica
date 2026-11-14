/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: TradutorErroBanco.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia.supabase;

import br.com.urna.excecao.PersistenciaException;
import java.sql.SQLException;

/**
 * Converte o erro do driver JDBC numa {@link PersistenciaException} com
 * mensagem que o usuário entende, a partir do código SQLSTATE do PostgreSQL.
 *
 * @author João
 * @since 1.0
 */
final class TradutorErroBanco {

    private static final String VIOLACAOUNICIDADE = "23505";

    private static final String VIOLACAOCHAVEESTRANGEIRA = "23503";

    private static final String VIOLACAOCHECAGEM = "23514";

    private static final String ERROLEVANTADOPELOBANCO = "P0001";

    private static final String PERMISSAONEGADA = "42501";

    private static final String SENHAINVALIDA = "28P01";

    private static final String PREFIXOFALHACONEXAO = "08";

    private TradutorErroBanco() {
    }

    // ---- Tradução ----

    static PersistenciaException traduzir(SQLException erro, String acao) {
        String estado = erro.getSQLState() == null ? "" : erro.getSQLState();
        String motivo;
        if (VIOLACAOUNICIDADE.equals(estado)) {
            motivo = "já existe um registro com esse número ou sigla.";
        } else if (VIOLACAOCHAVEESTRANGEIRA.equals(estado)) {
            motivo = "o registro está em uso (partido com candidatos ou candidato com votos).";
        } else if (VIOLACAOCHECAGEM.equals(estado)) {
            motivo = "os dados não obedecem às regras do banco (dígitos, complemento ou tamanho da foto).";
        } else if (ERROLEVANTADOPELOBANCO.equals(estado)) {
            motivo = extrairMensagemDoServidor(erro);
        } else if (PERMISSAONEGADA.equals(estado)) {
            motivo = "o usuário do banco não tem permissão para esta operação.";
        } else if (SENHAINVALIDA.equals(estado)) {
            motivo = "usuário ou senha do banco recusados.";
        } else if (estado.startsWith(PREFIXOFALHACONEXAO) || erro.getSQLState() == null) {
            motivo = "sem conexão com o banco de dados. Verifique a internet.";
        } else {
            motivo = extrairMensagemDoServidor(erro);
        }
        return new PersistenciaException("Não foi possível " + acao + ": " + motivo, erro);
    }

    private static String extrairMensagemDoServidor(SQLException erro) {
        String mensagem = erro.getMessage() == null ? "erro desconhecido do banco." : erro.getMessage();
        int quebraDeLinha = mensagem.indexOf('\n');
        if (quebraDeLinha >= 0) {
            mensagem = mensagem.substring(0, quebraDeLinha);
        }
        int doisPontos = mensagem.indexOf(": ");
        return doisPontos >= 0 ? mensagem.substring(doisPontos + 2) : mensagem;
    }
}
