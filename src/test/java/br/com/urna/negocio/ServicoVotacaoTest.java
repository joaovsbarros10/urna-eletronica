/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ServicoVotacaoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.urna.excecao.RegraNegocioException;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.modelo.voto.VotoBranco;
import br.com.urna.persistencia.RepositorioVotoMemoria;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicoVotacaoTest {

    private final RepositorioVotoMemoria repositorioVoto = new RepositorioVotoMemoria();

    private final ServicoVotacao servico = new ServicoVotacao(repositorioVoto);

    // ---- RN10: um voto por cargo ----

    @Test
    void deveGravarOsCincoVotosDeUmaSessaoRn10() {
        servico.registrarVotos(criarVotosEmBranco(Cargo.listarEmOrdemDeVotacao()));
        assertEquals(5, repositorioVoto.contarVotos());
    }

    @Test
    void deveRecusarListaSemOsCincoCargosRn10() {
        List<Voto> votos = criarVotosEmBranco(List.of(Cargo.PRESIDENTE, Cargo.GOVERNADOR));
        assertThrows(RegraNegocioException.class, () -> servico.registrarVotos(votos));
        assertThrows(RegraNegocioException.class, () -> servico.registrarVotos(null));
        assertEquals(0, repositorioVoto.contarVotos());
    }

    @Test
    void deveRecusarCargoRepetidoRn10() {
        List<Voto> votos = criarVotosEmBranco(List.of(Cargo.PRESIDENTE, Cargo.PRESIDENTE, Cargo.SENADOR,
                Cargo.DEPUTADOFEDERAL, Cargo.DEPUTADOESTADUAL));
        assertThrows(RegraNegocioException.class, () -> servico.registrarVotos(votos));
        assertEquals(0, repositorioVoto.contarVotos());
    }

    @Test
    void deveRecusarListaComVotoAusente() {
        List<Voto> votos = new ArrayList<>(criarVotosEmBranco(Cargo.listarEmOrdemDeVotacao().subList(0, 4)));
        votos.add(null);
        assertThrows(RegraNegocioException.class, () -> servico.registrarVotos(votos));
    }

    private static List<Voto> criarVotosEmBranco(List<Cargo> cargos) {
        return Arrays.asList(cargos.stream().map(VotoBranco::new).toArray(Voto[]::new));
    }
}
