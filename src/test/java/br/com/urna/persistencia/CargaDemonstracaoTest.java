/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: CargaDemonstracaoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.candidato.Candidato;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class CargaDemonstracaoTest {

    @Test
    void deveCarregarDoisPartidosEDoisCandidatosPorCargo() {
        RepositorioPartidoMemoria repositorioPartido = new RepositorioPartidoMemoria();
        RepositorioCandidatoMemoria repositorioCandidato = new RepositorioCandidatoMemoria();

        CargaDemonstracao.preencher(repositorioPartido, repositorioCandidato);

        assertEquals(2, repositorioPartido.listarTodos().size());
        List<Candidato> candidatos = repositorioCandidato.listarTodos();
        assertEquals(10, candidatos.size());
        for (Cargo cargo : Cargo.values()) {
            assertEquals(2, candidatos.stream().filter(candidato -> candidato.getCargo() == cargo).count());
        }
        assertTrue(candidatos.stream().allMatch(candidato -> candidato.getIdentificador() > 0));
    }

    @Test
    void deveDesenharFotoPngTresPorQuatroDentroDoLimiteRn06() throws Exception {
        byte[] foto = CargaDemonstracao.desenharFoto("Tiago Moura", Color.ORANGE);
        assertTrue(foto.length <= 200 * 1024);
        BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(foto));
        assertNotNull(imagem);
        assertEquals(300, imagem.getWidth());
        assertEquals(400, imagem.getHeight());
    }
}
