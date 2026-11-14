/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ControladorVotacaoTest.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.controle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.urna.excecao.PersistenciaException;
import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import br.com.urna.modelo.sessao.SessaoVotacao;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.negocio.FachadaUrna;
import br.com.urna.persistencia.CargaDemonstracao;
import br.com.urna.persistencia.RepositorioCandidatoMemoria;
import br.com.urna.persistencia.RepositorioPartido;
import br.com.urna.persistencia.RepositorioPartidoMemoria;
import br.com.urna.persistencia.RepositorioVoto;
import br.com.urna.persistencia.RepositorioVotoMemoria;
import br.com.urna.som.EfeitoSonoro;
import br.com.urna.som.ReprodutorSom;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Controlador com um reprodutor de som e uma tela de teste, que registram as
 * chamadas. As teclas são acionadas na thread de eventos, como na urna real.
 */
class ControladorVotacaoTest {

    private static final long ESPERAMAXIMAEMSEGUNDOS = 10;

    private final ReprodutorSomDeTeste reprodutorSom = new ReprodutorSomDeTeste();

    private final VisaoDeTeste visao = new VisaoDeTeste();

    private final RepositorioVotoControlavel repositorioVoto = new RepositorioVotoControlavel();

    private RepositorioPartidoMemoria repositorioPartido;

    private RepositorioCandidatoMemoria repositorioCandidato;

    @BeforeEach
    void prepararRepositorios() {
        repositorioPartido = new RepositorioPartidoMemoria();
        repositorioCandidato = new RepositorioCandidatoMemoria();
        CargaDemonstracao.preencher(repositorioPartido, repositorioCandidato);
    }

    // ---- RF10: sons nos momentos certos ----

    @Test
    void deveTocarTeclaACadaTeclaEConfirmacaoAoConfirmarRf10() throws Exception {
        ControladorVotacao controlador = iniciarControlador(new FachadaUrna(repositorioPartido, repositorioCandidato,
                repositorioVoto));

        naThreadDeEventos(() -> {
            controlador.aoPressionarBranco();
            controlador.aoPressionarCorrige();
            for (char digito : "3105".toCharArray()) {
                controlador.aoPressionarDigito(digito);
            }
            controlador.aoPressionarConfirma();
        });

        assertEquals(List.of(EfeitoSonoro.TECLA, EfeitoSonoro.TECLA, EfeitoSonoro.TECLA, EfeitoSonoro.TECLA,
                EfeitoSonoro.TECLA, EfeitoSonoro.TECLA, EfeitoSonoro.CONFIRMACAO), reprodutorSom.listarEfeitos());
        naThreadDeEventos(() -> assertEquals(Cargo.DEPUTADOESTADUAL, controlador.getSessaoAtual().getCargoAtual()));
    }

    @Test
    void deveFicarEmSilencioNoConfirmaComNumeroIncompleto() throws Exception {
        ControladorVotacao controlador = iniciarControlador(new FachadaUrna(repositorioPartido, repositorioCandidato,
                repositorioVoto));
        naThreadDeEventos(() -> {
            controlador.aoPressionarDigito('3');
            controlador.aoPressionarConfirma();
        });
        assertEquals(List.of(EfeitoSonoro.TECLA), reprodutorSom.listarEfeitos());
    }

    @Test
    void deveTocarFimGravarOsVotosEChamarOProximoEleitorRf09() throws Exception {
        ControladorVotacao controlador = iniciarControlador(new FachadaUrna(repositorioPartido, repositorioCandidato,
                repositorioVoto));
        List<SessaoVotacao> primeiraSessao = new ArrayList<>();
        naThreadDeEventos(() -> primeiraSessao.add(controlador.getSessaoAtual()));

        naThreadDeEventos(() -> votarNosCincoCargos(controlador));

        SessaoVotacao telaFim = visao.esperarSessao();
        assertTrue(telaFim.estaEncerrada());
        SessaoVotacao proximoEleitor = visao.esperarSessao();
        assertNotSame(primeiraSessao.get(0), proximoEleitor);
        assertFalse(proximoEleitor.estaEncerrada());
        assertEquals(5, repositorioVoto.contarVotos());
        assertEquals(EfeitoSonoro.FIM, reprodutorSom.listarEfeitos().get(reprodutorSom.listarEfeitos().size() - 1));
        naThreadDeEventos(() -> assertFalse(controlador.possuiVotosNaoGravados()));
    }

    // ---- RNF09: falha de rede não perde votos ----

    @Test
    void deveManterOsVotosQuandoAGravacaoFalhaETentarDeNovoComConfirmaRnf09() throws Exception {
        ControladorVotacao controlador = iniciarControlador(new FachadaUrna(repositorioPartido, repositorioCandidato,
                repositorioVoto));
        repositorioVoto.definirFalha(true);
        visao.definirRespostaTentarNovamente(false);

        naThreadDeEventos(() -> votarNosCincoCargos(controlador));

        assertTrue(visao.esperarPergunta().contains("ainda não foram gravados"));
        visao.esperarErro();
        naThreadDeEventos(() -> {
            assertTrue(controlador.possuiVotosNaoGravados());
            List<Voto> votosGuardados = controlador.getSessaoAtual().getVotosRegistrados();
            assertEquals(5, votosGuardados.size());
        });
        assertEquals(0, repositorioVoto.contarVotos());

        repositorioVoto.definirFalha(false);
        naThreadDeEventos(controlador::aoPressionarConfirma);

        SessaoVotacao proximoEleitor = esperarSessaoNaoEncerrada();
        assertFalse(proximoEleitor.estaEncerrada());
        assertEquals(5, repositorioVoto.contarVotos());
    }

    @Test
    void deveGravarNaNovaTentativaQuandoOEleitorAceitaTentarDeNovoRnf09() throws Exception {
        ControladorVotacao controlador = iniciarControlador(new FachadaUrna(repositorioPartido, repositorioCandidato,
                repositorioVoto));
        repositorioVoto.definirFalhasRestantes(1);
        visao.definirRespostaTentarNovamente(true);

        naThreadDeEventos(() -> votarNosCincoCargos(controlador));

        visao.esperarPergunta();
        esperarSessaoNaoEncerrada();
        assertEquals(5, repositorioVoto.contarVotos());
    }

    // ---- Abertura: banco fora do ar ----

    @Test
    void deveUsarDemonstracaoQuandoOBancoNaoRespondeNaAbertura() throws Exception {
        RepositorioPartido partidoForaDoAr = new RepositorioPartido() {
            @Override
            public List<Partido> listarTodos() {
                throw new PersistenciaException("sem conexão com o banco de dados");
            }

            @Override
            public Partido salvar(Partido partido) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void excluir(Partido partido) {
                throw new UnsupportedOperationException();
            }
        };
        FachadaUrna fachadaForaDoAr = new FachadaUrna(partidoForaDoAr, repositorioCandidato, repositorioVoto);
        FachadaUrna fachadaDemonstracao = new FachadaUrna(repositorioPartido, repositorioCandidato,
                new RepositorioVotoMemoria());
        visao.definirRespostaTentarNovamente(false);
        visao.definirRespostaUsarDemonstracao(true);

        ControladorVotacao controlador = new ControladorVotacao(fachadaForaDoAr, reprodutorSom);
        controlador.definirFachadaDeDemonstracao(fachadaDemonstracao);
        controlador.definirVisao(visao);
        naThreadDeEventos(controlador::iniciar);

        assertTrue(visao.esperarPergunta().contains("sem conexão"));
        SessaoVotacao sessao = visao.esperarSessao();
        assertEquals(Cargo.DEPUTADOFEDERAL, sessao.getCargoAtual());
    }

    // ---- Apoio ----

    private ControladorVotacao iniciarControlador(FachadaUrna fachada) throws Exception {
        ControladorVotacao controlador = new ControladorVotacao(fachada, reprodutorSom);
        controlador.definirEsperaAntesDeReiniciar(0);
        controlador.definirVisao(visao);
        naThreadDeEventos(controlador::iniciar);
        visao.esperarCarregando();
        visao.esperarSessao();
        return controlador;
    }

    private SessaoVotacao esperarSessaoNaoEncerrada() throws InterruptedException {
        SessaoVotacao sessao = visao.esperarSessao();
        while (sessao.estaEncerrada()) {
            sessao = visao.esperarSessao();
        }
        return sessao;
    }

    private static void votarNosCincoCargos(ControladorVotacao controlador) {
        for (String numero : List.of("3105", "31042", "312", "31", "31")) {
            for (char digito : numero.toCharArray()) {
                controlador.aoPressionarDigito(digito);
            }
            controlador.aoPressionarConfirma();
        }
    }

    private static void naThreadDeEventos(Runnable tarefa) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(tarefa);
    }

    /** Reprodutor que só anota os efeitos pedidos. */
    private static final class ReprodutorSomDeTeste implements ReprodutorSom {

        private final List<EfeitoSonoro> efeitos = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void reproduzir(EfeitoSonoro efeito) {
            efeitos.add(efeito);
        }

        List<EfeitoSonoro> listarEfeitos() {
            synchronized (efeitos) {
                return List.copyOf(efeitos);
            }
        }
    }

    /** Repositório de votos em memória que pode simular queda de rede. */
    private static final class RepositorioVotoControlavel implements RepositorioVoto {

        private final RepositorioVotoMemoria memoria = new RepositorioVotoMemoria();

        private volatile boolean falhaPermanente;

        private volatile int falhasRestantes;

        void definirFalha(boolean falha) {
            this.falhaPermanente = falha;
        }

        void definirFalhasRestantes(int quantidade) {
            this.falhasRestantes = quantidade;
        }

        int contarVotos() {
            return memoria.contarVotos();
        }

        @Override
        public void gravarTodos(List<Voto> votos) {
            if (falhaPermanente || falhasRestantes-- > 0) {
                throw new PersistenciaException("sem conexão com o banco de dados");
            }
            memoria.gravarTodos(votos);
        }

        @Override
        public List<Voto> listarPorCargo(Cargo cargo) {
            return memoria.listarPorCargo(cargo);
        }
    }

    /** Tela de teste: responde às perguntas com valores definidos e avisa o teste pelas filas. */
    private static final class VisaoDeTeste implements VisaoVotacao {

        private final BlockingQueue<SessaoVotacao> sessoes = new LinkedBlockingQueue<>();

        private final BlockingQueue<String> perguntas = new LinkedBlockingQueue<>();

        private final BlockingQueue<String> erros = new LinkedBlockingQueue<>();

        private final BlockingQueue<Boolean> carregamentos = new LinkedBlockingQueue<>();

        private volatile boolean respostaTentarNovamente;

        private volatile boolean respostaUsarDemonstracao;

        private SessaoVotacao ultimaSessao;

        private boolean ultimaEstavaEncerrada;

        void definirRespostaTentarNovamente(boolean resposta) {
            this.respostaTentarNovamente = resposta;
        }

        void definirRespostaUsarDemonstracao(boolean resposta) {
            this.respostaUsarDemonstracao = resposta;
        }

        @Override
        public void aoAtualizarSessao(SessaoVotacao sessao) {
            boolean sessaoNova = sessao != ultimaSessao;
            boolean acabouDeEncerrar = !sessaoNova && sessao.estaEncerrada() && !ultimaEstavaEncerrada;
            if (sessaoNova || acabouDeEncerrar) {
                sessoes.add(sessao);
            }
            ultimaSessao = sessao;
            ultimaEstavaEncerrada = sessao.estaEncerrada();
        }

        @Override
        public void exibirCarregando() {
            carregamentos.add(Boolean.TRUE);
        }

        @Override
        public void exibirErro(String mensagem) {
            erros.add(mensagem);
        }

        @Override
        public boolean perguntarSeTentaNovamente(String mensagem) {
            perguntas.add(mensagem);
            boolean resposta = respostaTentarNovamente;
            respostaTentarNovamente = false;
            return resposta;
        }

        @Override
        public boolean perguntarSeUsaDemonstracao() {
            return respostaUsarDemonstracao;
        }

        @Override
        public void encerrar() {
            erros.add("encerrada");
        }

        SessaoVotacao esperarSessao() throws InterruptedException {
            return esperar(sessoes, "sessão");
        }

        String esperarPergunta() throws InterruptedException {
            return esperar(perguntas, "pergunta");
        }

        String esperarErro() throws InterruptedException {
            return esperar(erros, "mensagem de erro");
        }

        void esperarCarregando() throws InterruptedException {
            esperar(carregamentos, "carregando");
        }

        private static <Elemento> Elemento esperar(BlockingQueue<Elemento> fila, String descricao)
                throws InterruptedException {
            Elemento elemento = fila.poll(ESPERAMAXIMAEMSEGUNDOS, TimeUnit.SECONDS);
            if (elemento == null) {
                throw new AssertionError("A tela não recebeu " + descricao + " a tempo.");
            }
            return elemento;
        }
    }
}
