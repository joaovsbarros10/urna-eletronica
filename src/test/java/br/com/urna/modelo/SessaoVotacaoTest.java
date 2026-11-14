/*
 * Urna Eletrônica
 * Arquivo: SessaoVotacaoTest.java
 * Autor: João
 */

package br.com.urna.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SessaoVotacaoTest {

    private final SessaoVotacao sessao = new SessaoVotacao(Dados.partidos(), Dados.candidatos());

    @Test
    void deveComecarNoDeputadoFederal() {
        assertEquals(Cargo.DEPUTADOFEDERAL, sessao.getCargoAtual());
        assertEquals("", sessao.getDigitos());
        assertFalse(sessao.permiteConfirmar());
    }

    @Test
    void deveEncontrarOCandidatoAoCompletarONumero() {
        digitar("3105");
        assertEquals("Tiago Moura", sessao.getCandidatoEncontrado().orElseThrow().getNome());
        assertTrue(sessao.confirmar());
        assertEquals(Cargo.DEPUTADOESTADUAL, sessao.getCargoAtual());
    }

    @Test
    void deveIgnorarDigitosAlemDoTamanhoDoCargo() {
        digitar("310599");
        assertEquals("3105", sessao.getDigitos());
    }

    @Test
    void deveAvisarNumeroErradoQuandoOPartidoNaoExiste() {
        digitar("9999");
        assertEquals("NÚMERO ERRADO\nVOTO NULO", sessao.getAviso());
        assertTrue(sessao.confirmar());
        assertEquals("NULO", sessao.getVotos().get(0).getTipo());
    }

    @Test
    void deveAvisarCandidatoInexistenteQuandoOPartidoExiste() {
        digitar("3199");
        assertEquals("CANDIDATO INEXISTENTE\nVOTO NULO", sessao.getAviso());
    }

    @Test
    void deveAceitarBrancoSoComODisplayVazio() {
        digitar("31");
        sessao.votarEmBranco();
        assertFalse(sessao.confirmar());

        sessao.corrigir();
        sessao.votarEmBranco();
        assertEquals("VOTO EM BRANCO", sessao.getAviso());
        assertTrue(sessao.confirmar());
        assertEquals("BRANCO", sessao.getVotos().get(0).getTipo());
    }

    @Test
    void deveApagarONumeroAoCorrigir() {
        digitar("3105");
        sessao.corrigir();
        assertEquals("", sessao.getDigitos());
        assertTrue(sessao.getCandidatoEncontrado().isEmpty());
        assertFalse(sessao.confirmar());
    }

    @Test
    void deveEncerrarDepoisDosCincoCargos() {
        digitar("3105");
        sessao.confirmar();
        digitar("47388");
        sessao.confirmar();
        sessao.votarEmBranco();
        sessao.confirmar();
        digitar("99");
        sessao.confirmar();
        digitar("31");
        sessao.confirmar();

        assertTrue(sessao.estaEncerrada());
        assertEquals("FIM", sessao.getAviso());
        List<Voto> votos = sessao.getVotos();
        assertEquals(Cargo.listarEmOrdemDeVotacao(), votos.stream().map(Voto::getCargo).toList());
        assertEquals(List.of("CANDIDATO", "CANDIDATO", "BRANCO", "NULO", "CANDIDATO"),
                votos.stream().map(Voto::getTipo).toList());

        digitar("1");
        sessao.corrigir();
        assertFalse(sessao.confirmar());
        assertThrows(UnsupportedOperationException.class, () -> votos.add(votos.get(0)));
    }

    private void digitar(String numero) {
        for (char digito : numero.toCharArray()) {
            sessao.digitar(digito);
        }
    }
}
