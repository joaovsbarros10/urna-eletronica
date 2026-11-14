/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: FachadaUrnaTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.sessao.SessaoVotacao;
import br.com.urna.modelo.voto.ApuracaoCargo;
import br.com.urna.persistencia.CargaDemonstracao;
import br.com.urna.persistencia.RepositorioCandidatoMemoria;
import br.com.urna.persistencia.RepositorioPartidoMemoria;
import br.com.urna.persistencia.RepositorioVotoMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Fase 2 do roteiro: uma votação completa roda por teste, sem tela e sem rede. */
class FachadaUrnaTest {

    private final RepositorioVotoMemoria repositorioVoto = new RepositorioVotoMemoria();

    private FachadaUrna fachada;

    @BeforeEach
    void prepararFachadaEmMemoria() {
        RepositorioPartidoMemoria repositorioPartido = new RepositorioPartidoMemoria();
        RepositorioCandidatoMemoria repositorioCandidato = new RepositorioCandidatoMemoria();
        CargaDemonstracao.preencher(repositorioPartido, repositorioCandidato);
        fachada = new FachadaUrna(repositorioPartido, repositorioCandidato, repositorioVoto);
        fachada.carregarDadosEleitorais();
    }

    @Test
    void deveVotarGravarEApurarDoisEleitores() {
        votar("3105", "31042", "312", "31", "31");
        votar("4721", "", "999", "47", "31");

        assertEquals(10, repositorioVoto.contarVotos());

        ApuracaoCargo presidente = fachada.apurarVotos(Cargo.PRESIDENTE);
        assertEquals(2, presidente.getVotosDoCandidato(buscar(Cargo.PRESIDENTE, "31")));
        assertEquals(0, presidente.getVotosDoCandidato(buscar(Cargo.PRESIDENTE, "47")));
        assertEquals(2, presidente.getTotalDeVotos());

        ApuracaoCargo senado = fachada.apurarVotos(Cargo.SENADOR);
        assertEquals(1, senado.getVotosDoCandidato(buscar(Cargo.SENADOR, "312")));
        assertEquals(1, senado.getVotosNulos());
        assertEquals(2, senado.listarCandidatosEmOrdemDeVotos().size());

        ApuracaoCargo estadual = fachada.apurarVotos(Cargo.DEPUTADOESTADUAL);
        assertEquals(1, estadual.getVotosBrancos());
    }

    @Test
    void deveListarPartidosECandidatosCarregados() {
        assertEquals(2, fachada.listarPartidos().size());
        assertEquals(10, fachada.listarCandidatos().size());
        assertEquals(Cargo.DEPUTADOFEDERAL, fachada.listarCandidatos().get(0).getCargo());
    }

    @Test
    void deveRecusarFachadaSemRepositorio() {
        assertThrows(IllegalArgumentException.class,
                () -> new FachadaUrna(null, new RepositorioCandidatoMemoria(), repositorioVoto));
    }

    private void votar(String... numeros) {
        SessaoVotacao sessao = fachada.iniciarSessaoVotacao();
        for (String numero : numeros) {
            if (numero.isEmpty()) {
                sessao.votarEmBranco();
            }
            for (char digito : numero.toCharArray()) {
                sessao.digitar(digito);
            }
            assertTrue(sessao.confirmar());
        }
        assertTrue(sessao.estaEncerrada());
        fachada.registrarVotos(sessao.getVotosRegistrados());
    }

    private Candidato buscar(Cargo cargo, String numero) {
        return fachada.listarCandidatos().stream()
                .filter(candidato -> candidato.getCargo() == cargo && candidato.getNumero().equals(numero))
                .findFirst()
                .orElseThrow();
    }
}
