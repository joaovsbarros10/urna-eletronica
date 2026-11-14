/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: ControladorVotacao.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.controle;

import br.com.urna.modelo.sessao.SessaoVotacao;
import br.com.urna.modelo.voto.Voto;
import br.com.urna.negocio.FachadaUrna;
import br.com.urna.som.EfeitoSonoro;
import br.com.urna.som.ReprodutorSom;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * Controlador da urna (MVC): recebe as teclas da tela, toca o som, repassa a
 * tecla à sessão de votação e grava os votos em segundo plano ao fim de cada eleitor.
 * <p>
 * Carregar o catálogo e gravar os votos rodam em {@link SwingWorker}, para a
 * janela não congelar (RNF06). Se a gravação falhar, os votos continuam na
 * sessão atual e nada é descartado (RNF09): a urna pergunta se tenta de novo e,
 * se a resposta for não, a tecla CONFIRMA na tela FIM faz nova tentativa.
 * Todos os métodos públicos devem ser chamados na thread de eventos do Swing.
 *
 * @author João
 * @since 1.0
 */
public class ControladorVotacao {

    /** Tempo, em milissegundos, que a tela FIM fica visível antes do próximo eleitor (decisão 0005). */
    public static final int ESPERAPADRAOEMMILISSEGUNDOS = 3000;

    private final ReprodutorSom reprodutorSom;

    private FachadaUrna fachada;

    private FachadaUrna fachadaDemonstracao;

    private VisaoVotacao visao;

    private SessaoVotacao sessaoAtual;

    private int esperaAntesDeReiniciar = ESPERAPADRAOEMMILISSEGUNDOS;

    private boolean gravacaoPendente;

    private boolean gravando;

    /**
     * Cria o controlador.
     *
     * @param fachada       porta de entrada da camada de negócio
     * @param reprodutorSom reprodutor dos efeitos sonoros
     * @throws IllegalArgumentException se algum argumento for {@code null}
     */
    public ControladorVotacao(FachadaUrna fachada, ReprodutorSom reprodutorSom) {
        if (fachada == null || reprodutorSom == null) {
            throw new IllegalArgumentException("O controlador precisa da fachada e do reprodutor de som.");
        }
        this.fachada = fachada;
        this.reprodutorSom = reprodutorSom;
    }

    // ---- Configuração ----

    /**
     * Liga o controlador à tela. Precisa ser chamado antes de {@link #iniciar()}.
     *
     * @param visao tela da urna
     */
    public void definirVisao(VisaoVotacao visao) {
        this.visao = visao;
    }

    /**
     * Define a fachada com dados em memória oferecida quando o banco não responde
     * na abertura (seção 9.2). Sem ela, a urna só oferece tentar de novo.
     *
     * @param fachadaDemonstracao fachada sobre repositórios em memória
     */
    public void definirFachadaDeDemonstracao(FachadaUrna fachadaDemonstracao) {
        this.fachadaDemonstracao = fachadaDemonstracao;
    }

    /**
     * Define quanto tempo a tela FIM fica visível antes do próximo eleitor.
     *
     * @param milissegundos espera em milissegundos; 0 reinicia logo após a gravação
     * @throws IllegalArgumentException se o valor for negativo
     */
    public void definirEsperaAntesDeReiniciar(int milissegundos) {
        if (milissegundos < 0) {
            throw new IllegalArgumentException("A espera não pode ser negativa.");
        }
        this.esperaAntesDeReiniciar = milissegundos;
    }

    // ---- Abertura da urna ----

    /**
     * Mostra "carregando", lê partidos, candidatos e fotos em segundo plano e
     * abre a sessão do primeiro eleitor.
     *
     * @throws IllegalStateException se a visão não tiver sido definida
     */
    public void iniciar() {
        if (visao == null) {
            throw new IllegalStateException("Defina a visão antes de iniciar a urna.");
        }
        carregarDados();
    }

    private void carregarDados() {
        visao.exibirCarregando();
        FachadaUrna fachadaEmUso = fachada;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                fachadaEmUso.carregarDadosEleitorais();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    iniciarNovaSessao();
                } catch (ExecutionException erro) {
                    tratarFalhaDeCarga(erro.getCause());
                } catch (InterruptedException erro) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void tratarFalhaDeCarga(Throwable causa) {
        String mensagem = "Não foi possível carregar os candidatos.\n" + causa.getMessage();
        if (visao.perguntarSeTentaNovamente(mensagem)) {
            carregarDados();
        } else if (fachadaDemonstracao != null && fachadaDemonstracao != fachada
                && visao.perguntarSeUsaDemonstracao()) {
            fachada = fachadaDemonstracao;
            carregarDados();
        } else {
            visao.encerrar();
        }
    }

    // ---- Teclas do eleitor ----

    /**
     * Trata uma tecla numérica: toca o som de tecla e repassa o dígito à sessão.
     *
     * @param digito dígito de '0' a '9'
     */
    public void aoPressionarDigito(char digito) {
        if (estaRecebendoVotos()) {
            reprodutorSom.reproduzir(EfeitoSonoro.TECLA);
            sessaoAtual.digitar(digito);
        }
    }

    /** Trata a tecla BRANCO: toca o som de tecla e repassa à sessão. */
    public void aoPressionarBranco() {
        if (estaRecebendoVotos()) {
            reprodutorSom.reproduzir(EfeitoSonoro.TECLA);
            sessaoAtual.votarEmBranco();
        }
    }

    /** Trata a tecla CORRIGE: toca o som de tecla e repassa à sessão. */
    public void aoPressionarCorrige() {
        if (estaRecebendoVotos()) {
            reprodutorSom.reproduzir(EfeitoSonoro.TECLA);
            sessaoAtual.corrigir();
        }
    }

    /**
     * Trata a tecla CONFIRMA. Com voto aceito, toca o som de confirmação ou, no
     * último cargo, o som de fim e grava os votos. Um CONFIRMA recusado (número
     * incompleto) não toca nada. Na tela FIM com gravação pendente, tenta gravar de novo.
     */
    public void aoPressionarConfirma() {
        if (sessaoAtual == null) {
            return;
        }
        if (gravacaoPendente) {
            if (!gravando) {
                gravarVotos();
            }
            return;
        }
        if (!sessaoAtual.confirmar()) {
            return;
        }
        if (sessaoAtual.estaEncerrada()) {
            reprodutorSom.reproduzir(EfeitoSonoro.FIM);
            gravacaoPendente = true;
            gravarVotos();
        } else {
            reprodutorSom.reproduzir(EfeitoSonoro.CONFIRMACAO);
        }
    }

    private boolean estaRecebendoVotos() {
        return sessaoAtual != null && !sessaoAtual.estaEncerrada();
    }

    // ---- Gravação dos votos (RN11 e RNF09) ----

    private void gravarVotos() {
        gravando = true;
        List<Voto> votos = sessaoAtual.getVotosRegistrados();
        FachadaUrna fachadaEmUso = fachada;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                fachadaEmUso.registrarVotos(votos);
                return null;
            }

            @Override
            protected void done() {
                gravando = false;
                try {
                    get();
                    gravacaoPendente = false;
                    agendarNovaSessao();
                } catch (ExecutionException erro) {
                    tratarFalhaDeGravacao(erro.getCause());
                } catch (InterruptedException erro) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void tratarFalhaDeGravacao(Throwable causa) {
        String mensagem = "Os votos deste eleitor ainda não foram gravados.\n" + causa.getMessage();
        if (visao.perguntarSeTentaNovamente(mensagem)) {
            gravarVotos();
        } else {
            visao.exibirErro("Os votos continuam guardados na memória da urna.\n"
                    + "Aperte CONFIRMA para tentar gravar de novo.");
        }
    }

    // ---- Próximo eleitor ----

    private void agendarNovaSessao() {
        if (esperaAntesDeReiniciar == 0) {
            iniciarNovaSessao();
            return;
        }
        Timer temporizador = new Timer(esperaAntesDeReiniciar, evento -> iniciarNovaSessao());
        temporizador.setRepeats(false);
        temporizador.start();
    }

    private void iniciarNovaSessao() {
        if (sessaoAtual != null) {
            sessaoAtual.removerOuvinte(visao);
        }
        sessaoAtual = fachada.iniciarSessaoVotacao();
        sessaoAtual.adicionarOuvinte(visao);
        visao.aoAtualizarSessao(sessaoAtual);
    }

    // ---- Consulta de estado ----

    /**
     * Informa se há votos de um eleitor que ainda não foram gravados no banco.
     * A tela usa esta consulta para avisar antes de fechar a urna.
     *
     * @return {@code true} enquanto a gravação da última sessão não terminou com sucesso
     */
    public boolean possuiVotosNaoGravados() {
        return gravacaoPendente;
    }

    SessaoVotacao getSessaoAtual() {
        return sessaoAtual;
    }
}
