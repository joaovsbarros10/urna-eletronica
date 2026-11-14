/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: SessaoVotacaoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.sessao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.CenarioDeTeste;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.voto.TipoVoto;
import br.com.urna.modelo.voto.Voto;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SessaoVotacaoTest {

    // ---- RF02 e RF03: início da sessão ----

    @Test
    void deveComecarNoPrimeiroCargoSemDigitos() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        assertEquals(Cargo.DEPUTADOFEDERAL, sessao.getCargoAtual());
        assertEquals("", sessao.getDigitosDigitados());
        assertEquals("", sessao.getAvisoPrincipal());
        assertFalse(sessao.permiteConfirmar());
        assertFalse(sessao.estaEncerrada());
    }

    @Test
    void deveRecusarSessaoSemCatalogo() {
        assertThrows(IllegalArgumentException.class, () -> new SessaoVotacao(null));
    }

    // ---- RF04: número completo com candidato ----

    @Test
    void deveEncontrarCandidatoAoCompletarOsDigitosDoPrimeiroCargo() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());

        digitar(sessao, "3105");

        assertTrue(sessao.getCandidatoEncontrado().isPresent());
        assertEquals("Tiago Moura", sessao.getCandidatoEncontrado().get().getNome());
        assertTrue(sessao.confirmar());
        assertEquals(Cargo.DEPUTADOESTADUAL, sessao.getCargoAtual());
        assertEquals("", sessao.getDigitosDigitados());
    }

    @Test
    void deveIgnorarDigitosAlemDaQuantidadeDoCargoRn01() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        digitar(sessao, "310599");
        assertEquals("3105", sessao.getDigitosDigitados());
    }

    @Test
    void deveRecusarCaractereQueNaoEDigito() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        assertThrows(IllegalArgumentException.class, () -> sessao.digitar('x'));
    }

    // ---- RF05 e RN09: número completo sem candidato vira voto nulo ----

    @Test
    void deveAvisarNumeroErradoQuandoOPartidoNaoExisteRn09() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        digitar(sessao, "9999");
        assertEquals("NÚMERO ERRADO\nVOTO NULO", sessao.getAvisoPrincipal());
        assertTrue(sessao.getCandidatoEncontrado().isEmpty());
        assertTrue(sessao.permiteConfirmar());
    }

    @Test
    void deveAvisarCandidatoInexistenteQuandoOPartidoExisteRn09() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        digitar(sessao, "3199");
        assertEquals("CANDIDATO INEXISTENTE\nVOTO NULO", sessao.getAvisoPrincipal());
    }

    @Test
    void deveRegistrarVotoNuloAoConfirmarNumeroSemCandidatoRn09() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        digitar(sessao, "9999");
        assertTrue(sessao.confirmar());
        assertEquals(TipoVoto.NULO, sessao.getVotosRegistrados().get(0).getTipo());
    }

    // ---- RF06 e RN08: voto em branco ----

    @Test
    void deveIgnorarVotoEmBrancoQuandoJaHaDigitosDigitados() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());

        digitar(sessao, "31");
        sessao.votarEmBranco();

        assertFalse(sessao.confirmar());
    }

    @Test
    void deveRegistrarVotoEmBrancoComODisplayVazioRn08() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        sessao.votarEmBranco();
        assertEquals("VOTO EM BRANCO", sessao.getAvisoPrincipal());
        assertTrue(sessao.permiteConfirmar());

        sessao.digitar('3');
        assertEquals("", sessao.getDigitosDigitados());

        assertTrue(sessao.confirmar());
        assertEquals(TipoVoto.BRANCO, sessao.getVotosRegistrados().get(0).getTipo());
    }

    // ---- RF07: CORRIGE ----

    @Test
    void deveReiniciarOCargoAoCorrigirOCandidatoEncontradoRf07() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        digitar(sessao, "3105");
        sessao.corrigir();
        assertEquals("", sessao.getDigitosDigitados());
        assertTrue(sessao.getCandidatoEncontrado().isEmpty());
        assertEquals(Cargo.DEPUTADOFEDERAL, sessao.getCargoAtual());
        assertFalse(sessao.confirmar());
    }

    @Test
    void deveApagarDigitosIncompletosEDesfazerVotoEmBrancoAoCorrigirRf07() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        digitar(sessao, "31");
        sessao.corrigir();
        assertEquals("", sessao.getDigitosDigitados());

        sessao.votarEmBranco();
        sessao.corrigir();
        assertEquals("", sessao.getAvisoPrincipal());
        assertFalse(sessao.permiteConfirmar());
    }

    // ---- RF08: CONFIRMA ----

    @Test
    void deveIgnorarConfirmaComNumeroIncompletoRn08() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        digitar(sessao, "310");
        assertFalse(sessao.confirmar());
        assertTrue(sessao.getVotosRegistrados().isEmpty());
    }

    // ---- RF09 e RN10: fim da sessão ----

    @Test
    void deveEncerrarAposOsCincoCargosComUmVotoPorCargoRn10() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        votarNosCincoCargos(sessao);

        assertTrue(sessao.estaEncerrada());
        assertEquals("FIM", sessao.getAvisoPrincipal());
        List<Voto> votos = sessao.getVotosRegistrados();
        assertEquals(Cargo.listarEmOrdemDeVotacao(), votos.stream().map(Voto::getCargo).toList());
        assertEquals(List.of(TipoVoto.CANDIDATO, TipoVoto.CANDIDATO, TipoVoto.BRANCO, TipoVoto.NULO,
                TipoVoto.CANDIDATO), votos.stream().map(Voto::getTipo).toList());
    }

    @Test
    void deveIgnorarTodasAsTeclasDepoisDoFim() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        votarNosCincoCargos(sessao);

        sessao.digitar('3');
        sessao.votarEmBranco();
        sessao.corrigir();

        assertFalse(sessao.confirmar());
        assertFalse(sessao.permiteConfirmar());
        assertEquals(5, sessao.getVotosRegistrados().size());
        assertEquals(Cargo.PRESIDENTE, sessao.getCargoAtual());
    }

    // ---- Encapsulamento e Observer ----

    @Test
    void deveDevolverVotosSomenteLeitura() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        sessao.votarEmBranco();
        sessao.confirmar();
        List<Voto> votos = sessao.getVotosRegistrados();
        assertThrows(UnsupportedOperationException.class, () -> votos.add(votos.get(0)));
    }

    @Test
    void deveAvisarOsOuvintesUmaVezPorTecla() {
        SessaoVotacao sessao = new SessaoVotacao(CenarioDeTeste.criarCatalogo());
        AtomicInteger avisos = new AtomicInteger();
        OuvinteSessaoVotacao ouvinte = sessaoAvisada -> avisos.incrementAndGet();
        sessao.adicionarOuvinte(ouvinte);

        digitar(sessao, "31");
        sessao.corrigir();
        sessao.votarEmBranco();
        sessao.confirmar();
        assertEquals(5, avisos.get());

        sessao.removerOuvinte(ouvinte);
        sessao.votarEmBranco();
        assertEquals(5, avisos.get());
        assertThrows(IllegalArgumentException.class, () -> sessao.adicionarOuvinte(null));
    }

    // ---- Apoio ----

    private void digitar(SessaoVotacao sessao, String numero) {
        for (char digito : numero.toCharArray()) {
            sessao.digitar(digito);
        }
    }

    private void votarNosCincoCargos(SessaoVotacao sessao) {
        digitar(sessao, "3105");
        assertTrue(sessao.confirmar());
        digitar(sessao, "47388");
        assertTrue(sessao.confirmar());
        sessao.votarEmBranco();
        assertTrue(sessao.confirmar());
        digitar(sessao, "99");
        assertTrue(sessao.confirmar());
        digitar(sessao, "31");
        assertTrue(sessao.confirmar());
    }
}
