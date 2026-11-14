/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ServicoCadastroTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.negocio;

import static br.com.urna.CenarioDeTeste.iniciarFicha;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.excecao.RegraNegocioException;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.persistencia.CargaDemonstracao;
import br.com.urna.persistencia.RepositorioCandidatoMemoria;
import br.com.urna.persistencia.RepositorioPartidoMemoria;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicoCadastroTest {

    private final CatalogoEleitoral catalogo = new CatalogoEleitoral();

    private ServicoCadastro servico;

    private Partido aurora;

    private Partido cerrado;

    @BeforeEach
    void prepararDadosFicticios() {
        RepositorioPartidoMemoria repositorioPartido = new RepositorioPartidoMemoria();
        RepositorioCandidatoMemoria repositorioCandidato = new RepositorioCandidatoMemoria();
        CargaDemonstracao.preencher(repositorioPartido, repositorioCandidato);
        servico = new ServicoCadastro(repositorioPartido, repositorioCandidato, catalogo);
        servico.recarregarCatalogo();
        aurora = buscarPartido(31);
        cerrado = buscarPartido(47);
    }

    // ---- Candidatos: RN04 e RN05 ----

    @Test
    void deveIncluirCandidatoNovoEColocaloNoCatalogo() {
        servico.salvarCandidato(iniciarFicha(Cargo.DEPUTADOFEDERAL, "3177", "Nova Pessoa", aurora).montar());
        assertTrue(catalogo.buscarCandidato(Cargo.DEPUTADOFEDERAL, "3177").isPresent());
        assertEquals(11, catalogo.listarCandidatos().size());
    }

    @Test
    void deveRecusarNumeroRepetidoNoCargoRn05() {
        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () -> servico.salvarCandidato(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Outra Pessoa", aurora).montar()));
        assertTrue(erro.getMessage().contains("RN05"));
    }

    @Test
    void deveAceitarOMesmoNumeroEmCargosDiferentesRn05() {
        Candidato presidente = catalogo.buscarCandidato(Cargo.PRESIDENTE, "31").orElseThrow();
        Candidato governador = catalogo.buscarCandidato(Cargo.GOVERNADOR, "31").orElseThrow();
        assertEquals(presidente.getNumero(), governador.getNumero());
    }

    @Test
    void devePermitirEditarCandidatoSemAcusarNumeroRepetido() {
        Candidato tiago = catalogo.buscarCandidato(Cargo.DEPUTADOFEDERAL, "3105").orElseThrow();
        servico.salvarCandidato(tiago.gerarFicha().copiarComIdentificador(tiago.getIdentificador()));
        assertEquals(10, catalogo.listarCandidatos().size());
    }

    @Test
    void deveRecusarPrefixoQueNaoEDoPartidoRn04() {
        assertThrows(RegraNegocioException.class, () -> servico.salvarCandidato(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "4777", "Nova Pessoa", aurora).montar()));
    }

    @Test
    void deveRecusarCandidatoDePartidoNaoCadastradoRn02() {
        Partido inexistente = new Partido(99, 55, "PIN", "Partido Inexistente");
        assertThrows(RegraNegocioException.class, () -> servico.salvarCandidato(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "5501", "Nova Pessoa", inexistente).montar()));
        assertThrows(RegraNegocioException.class, () -> servico.salvarCandidato(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "5501", "Nova Pessoa", null).montar()));
        assertThrows(RegraNegocioException.class, () -> servico.salvarCandidato(null));
    }

    // ---- Foto: RN06 ----

    @Test
    void deveRecusarFotoMaiorQueDuzentosKbRn06() {
        byte[] fotoGrande = new byte[ServicoCadastro.TAMANHOMAXIMOFOTO + 1];
        fotoGrande[0] = (byte) 0xFF;
        fotoGrande[1] = (byte) 0xD8;
        fotoGrande[2] = (byte) 0xFF;
        assertThrows(RegraNegocioException.class, () -> servico.salvarCandidato(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3177", "Nova Pessoa", aurora).definirFoto(fotoGrande).montar()));
    }

    @Test
    void deveRecusarFotoQueNaoEJpegNemPngRn06() {
        byte[] gif = "GIF89a-imagem".getBytes(StandardCharsets.US_ASCII);
        assertThrows(RegraNegocioException.class, () -> servico.salvarCandidato(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3177", "Nova Pessoa", aurora).definirFoto(gif).montar()));
        assertThrows(RegraNegocioException.class, () -> ServicoCadastro.validarFoto(null));
    }

    @Test
    void deveAceitarFotoJpegPequena() {
        byte[] jpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0};
        assertDoesNotThrow(() -> ServicoCadastro.validarFoto(jpeg));
    }

    @Test
    void deveExcluirCandidatoDoCatalogo() {
        Candidato tiago = catalogo.buscarCandidato(Cargo.DEPUTADOFEDERAL, "3105").orElseThrow();
        servico.excluirCandidato(tiago);
        assertFalse(catalogo.buscarCandidato(Cargo.DEPUTADOFEDERAL, "3105").isPresent());
        assertThrows(RegraNegocioException.class, () -> servico.excluirCandidato(null));
    }

    // ---- Partidos: RN03 ----

    @Test
    void deveRecusarPartidoComNumeroRepetidoRn03() {
        assertThrows(RegraNegocioException.class, () -> servico.salvarPartido(new Partido(0, 31, "PNV", "Partido Novo")));
    }

    @Test
    void deveRecusarPartidoComSiglaRepetida() {
        assertThrows(RegraNegocioException.class, () -> servico.salvarPartido(new Partido(0, 55, "pau", "Partido Novo")));
    }

    @Test
    void deveRecusarSiglaLongaDemais() {
        assertThrows(RegraNegocioException.class,
                () -> servico.salvarPartido(new Partido(0, 55, "SIGLAMUITOLONGA", "Partido Novo")));
        assertThrows(RegraNegocioException.class, () -> servico.salvarPartido(null));
    }

    @Test
    void deveIncluirEditarEExcluirPartidoSemCandidatos() {
        servico.salvarPartido(new Partido(0, 55, "PNV", "Partido Novo"));
        Partido novo = buscarPartido(55);
        servico.salvarPartido(new Partido(novo.getIdentificador(), 56, "PNV", "Partido Novo Renomeado"));
        assertEquals("Partido Novo Renomeado", buscarPartido(56).getNome());
        servico.excluirPartido(buscarPartido(56));
        assertFalse(catalogo.existePartidoComNumero(56));
    }

    @Test
    void deveRecusarExcluirPartidoQueTemCandidatos() {
        assertThrows(RegraNegocioException.class, () -> servico.excluirPartido(cerrado));
        assertTrue(catalogo.existePartidoComNumero(47));
        assertThrows(RegraNegocioException.class, () -> servico.excluirPartido(null));
    }

    @Test
    void deveRecusarTrocarONumeroDePartidoQueTemCandidatosRn04() {
        Partido trocado = new Partido(aurora.getIdentificador(), 32, aurora.getSigla(), aurora.getNome());
        assertThrows(RegraNegocioException.class, () -> servico.salvarPartido(trocado));
    }

    @Test
    void devePermitirRenomearPartidoQueTemCandidatos() {
        servico.salvarPartido(new Partido(aurora.getIdentificador(), 31, "PAU", "Partido Aurora Renovado"));
        assertEquals("Partido Aurora Renovado", buscarPartido(31).getNome());
    }

    private Partido buscarPartido(int numero) {
        return catalogo.listarPartidos().stream()
                .filter(partido -> partido.getNumero() == numero)
                .findFirst()
                .orElseThrow();
    }
}
