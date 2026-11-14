/*
 * Urna Eletrônica
 * Arquivo: FachadaUrnaTest.java
 * Autor: João
 */

package br.com.urna.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.excecao.UrnaException;
import br.com.urna.modelo.ApuracaoCargo;
import br.com.urna.modelo.Candidato;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.SessaoVotacao;
import br.com.urna.modelo.VotoBranco;
import br.com.urna.persistencia.RepositorioUrnaMemoria;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FachadaUrnaTest {

    private FachadaUrna fachada;

    @BeforeEach
    void carregarDadosDeDemonstracao() {
        fachada = new FachadaUrna(RepositorioUrnaMemoria.criarComDadosDeDemonstracao());
        fachada.carregarDados();
    }

    @Test
    void deveVotarGravarEApurar() {
        votar("3105", "31042", "312", "31", "31");
        votar("4721", "", "999", "47", "31");

        ApuracaoCargo presidente = fachada.apurar(Cargo.PRESIDENTE);
        assertEquals(2, presidente.getVotosDoCandidato(buscar(Cargo.PRESIDENTE, "31")));
        assertEquals(0, presidente.getVotosDoCandidato(buscar(Cargo.PRESIDENTE, "47")));
        assertEquals("31", presidente.listarCandidatosPorVotos().get(0).getNumero());

        ApuracaoCargo senado = fachada.apurar(Cargo.SENADOR);
        assertEquals(1, senado.getVotosNulos());
        assertEquals(1, senado.getVotosValidos());
        assertEquals(1, fachada.apurar(Cargo.DEPUTADOESTADUAL).getVotosBrancos());
        assertEquals(2, senado.getTotalDeVotos());
    }

    @Test
    void deveRecusarVotacaoIncompleta() {
        assertThrows(UrnaException.class, () -> fachada.registrarVotos(List.of(new VotoBranco(Cargo.SENADOR))));
    }

    @Test
    void deveIncluirEditarEExcluirPartido() {
        fachada.salvarPartido(new Partido(0, 55, "PNV", "Partido Novo"));
        Partido novo = buscarPartido(55);
        fachada.salvarPartido(new Partido(novo.getIdentificador(), 56, "PNV", "Partido Renomeado"));
        assertEquals("Partido Renomeado", buscarPartido(56).getNome());
        fachada.excluirPartido(buscarPartido(56));
        assertEquals(2, fachada.listarPartidos().size());
    }

    @Test
    void deveRecusarPartidoRepetidoOuComCandidatos() {
        Partido aurora = buscarPartido(31);
        assertThrows(UrnaException.class, () -> fachada.salvarPartido(new Partido(0, 31, "PNV", "Partido Novo")));
        assertThrows(UrnaException.class, () -> fachada.salvarPartido(new Partido(0, 55, "pau", "Partido Novo")));
        assertThrows(UrnaException.class, () -> fachada.excluirPartido(aurora));
        assertThrows(UrnaException.class,
                () -> fachada.salvarPartido(new Partido(aurora.getIdentificador(), 32, "PAU", "Partido Aurora")));
    }

    @Test
    void deveIncluirEExcluirCandidato() {
        Candidato novo = Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3177", "Nova Pessoa", buscarPartido(31),
                buscar(Cargo.DEPUTADOFEDERAL, "3105").getFoto(), List.of());
        fachada.salvarCandidato(novo);
        Candidato gravado = buscar(Cargo.DEPUTADOFEDERAL, "3177");
        assertTrue(gravado.getIdentificador() > 0);
        fachada.excluirCandidato(gravado);
        assertEquals(10, fachada.listarCandidatos().size());
    }

    @Test
    void deveRecusarNumeroRepetidoEFotoInvalida() {
        byte[] foto = buscar(Cargo.DEPUTADOFEDERAL, "3105").getFoto();
        Partido aurora = buscarPartido(31);
        assertThrows(UrnaException.class, () -> fachada.salvarCandidato(
                Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3105", "Outra Pessoa", aurora, foto, List.of())));
        assertThrows(UrnaException.class, () -> fachada.salvarCandidato(
                Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3177", "Outra Pessoa", aurora, new byte[] {1, 2, 3},
                        List.of())));
        assertThrows(UrnaException.class, () -> fachada.salvarCandidato(
                Candidato.criar(0, Cargo.DEPUTADOFEDERAL, "3177", "Outra Pessoa", aurora, new byte[300 * 1024],
                        List.of())));
    }

    private void votar(String... numeros) {
        SessaoVotacao sessao = fachada.iniciarSessao();
        for (String numero : numeros) {
            if (numero.isEmpty()) {
                sessao.votarEmBranco();
            }
            for (char digito : numero.toCharArray()) {
                sessao.digitar(digito);
            }
            sessao.confirmar();
        }
        fachada.registrarVotos(sessao.getVotos());
    }

    private Candidato buscar(Cargo cargo, String numero) {
        return fachada.listarCandidatos().stream()
                .filter(candidato -> candidato.getCargo() == cargo && candidato.getNumero().equals(numero))
                .findFirst()
                .orElseThrow();
    }

    private Partido buscarPartido(int numero) {
        return fachada.listarPartidos().stream()
                .filter(partido -> partido.getNumero() == numero)
                .findFirst()
                .orElseThrow();
    }
}
