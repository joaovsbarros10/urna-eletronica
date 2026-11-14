/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: RepositorioMemoriaTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import static br.com.urna.CenarioDeTeste.AURORA;
import static br.com.urna.CenarioDeTeste.iniciarFicha;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.candidato.Candidato;
import br.com.urna.modelo.candidato.FabricaCandidato;
import br.com.urna.modelo.voto.VotoBranco;
import br.com.urna.modelo.voto.VotoNulo;
import java.util.List;
import org.junit.jupiter.api.Test;

class RepositorioMemoriaTest {

    @Test
    void deveGerarIdentificadorAlterarEExcluirPartido() {
        RepositorioPartidoMemoria repositorio = new RepositorioPartidoMemoria();
        Partido gravado = repositorio.salvar(new Partido(0, 47, "PCV", "Partido Cerrado Verde"));
        repositorio.salvar(new Partido(0, 31, "PAU", "Partido Aurora"));
        assertEquals(1, gravado.getIdentificador());

        repositorio.salvar(new Partido(gravado.getIdentificador(), 47, "PCV", "Cerrado Verde"));
        List<Partido> partidos = repositorio.listarTodos();
        assertEquals(31, partidos.get(0).getNumero());
        assertEquals("Cerrado Verde", partidos.get(1).getNome());

        repositorio.excluir(gravado);
        assertEquals(1, repositorio.listarTodos().size());
    }

    @Test
    void deveGerarIdentificadorAlterarEExcluirCandidato() {
        RepositorioCandidatoMemoria repositorio = new RepositorioCandidatoMemoria();
        Candidato gravado = repositorio.salvar(FabricaCandidato.criar(
                iniciarFicha(Cargo.DEPUTADOFEDERAL, "3105", "Tiago Moura", AURORA).montar()));
        assertEquals(1, gravado.getIdentificador());

        Candidato alterado = FabricaCandidato.criar(iniciarFicha(Cargo.DEPUTADOFEDERAL, "3106", "Tiago M. Moura", AURORA)
                .definirIdentificador(gravado.getIdentificador()).montar());
        repositorio.salvar(alterado);
        assertEquals("3106", repositorio.listarTodos().get(0).getNumero());

        repositorio.excluir(alterado);
        assertTrue(repositorio.listarTodos().isEmpty());
    }

    @Test
    void deveListarVotosPorCargo() {
        RepositorioVotoMemoria repositorio = new RepositorioVotoMemoria();
        repositorio.gravarTodos(List.of(new VotoBranco(Cargo.SENADOR), new VotoNulo(Cargo.PRESIDENTE)));
        assertEquals(1, repositorio.listarPorCargo(Cargo.SENADOR).size());
        assertEquals(2, repositorio.contarVotos());
    }
}
