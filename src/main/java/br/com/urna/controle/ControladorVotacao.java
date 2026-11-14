/*
 * Urna Eletrônica
 * Arquivo: ControladorVotacao.java
 * Autor: João
 */

package br.com.urna.controle;

import br.com.urna.modelo.SessaoVotacao;
import br.com.urna.modelo.Voto;
import br.com.urna.negocio.FachadaUrna;
import br.com.urna.som.ReprodutorSom;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * Controlador da urna: recebe as teclas, toca o som, repassa a tecla à sessão
 * e grava os votos ao fim de cada eleitor.
 * <p>
 * Carregar os candidatos e gravar os votos rodam em {@link SwingWorker}, para a
 * janela não travar. Se a gravação falhar, os votos continuam na sessão e a
 * tecla CONFIRMA na tela FIM tenta gravar de novo.
 *
 * @author João
 */
public class ControladorVotacao {

    private static final int ESPERAANTESDOPROXIMOELEITOR = 3000;

    private final ReprodutorSom som;

    private FachadaUrna fachada;

    private FachadaUrna fachadaDemonstracao;

    private VisaoVotacao visao;

    private SessaoVotacao sessao;

    private boolean gravacaoPendente;

    private boolean gravando;

    /**
     * Cria o controlador.
     *
     * @param fachada porta de entrada da camada de negócio
     * @param som     reprodutor dos sons da urna
     */
    public ControladorVotacao(FachadaUrna fachada, ReprodutorSom som) {
        this.fachada = fachada;
        this.som = som;
    }

    public void definirVisao(VisaoVotacao visao) {
        this.visao = visao;
    }

    /**
     * Define a fachada com dados em memória, oferecida se o banco não responder.
     *
     * @param fachadaDemonstracao fachada sobre o repositório em memória
     */
    public void definirFachadaDemonstracao(FachadaUrna fachadaDemonstracao) {
        this.fachadaDemonstracao = fachadaDemonstracao;
    }

    /** Carrega os candidatos em segundo plano e abre a sessão do primeiro eleitor. */
    public void iniciar() {
        visao.exibirCarregando();
        executarEmSegundoPlano(fachada::carregarDados, this::iniciarNovaSessao, this::tratarFalhaDeCarga);
    }

    /**
     * Trata uma tecla numérica.
     *
     * @param digito dígito de '0' a '9'
     */
    public void aoPressionarDigito(char digito) {
        if (estaRecebendoVotos()) {
            som.tocarTecla();
            sessao.digitar(digito);
            visao.atualizar(sessao);
        }
    }

    /** Trata a tecla BRANCO. */
    public void aoPressionarBranco() {
        if (estaRecebendoVotos()) {
            som.tocarTecla();
            sessao.votarEmBranco();
            visao.atualizar(sessao);
        }
    }

    /** Trata a tecla CORRIGE. */
    public void aoPressionarCorrige() {
        if (estaRecebendoVotos()) {
            som.tocarTecla();
            sessao.corrigir();
            visao.atualizar(sessao);
        }
    }

    /**
     * Trata a tecla CONFIRMA. No último cargo, toca o som de fim e grava os votos.
     * Na tela FIM com votos não gravados, tenta gravar de novo.
     */
    public void aoPressionarConfirma() {
        if (sessao == null || gravando) {
            return;
        }
        if (gravacaoPendente) {
            gravarVotos();
            return;
        }
        if (!sessao.confirmar()) {
            return;
        }
        visao.atualizar(sessao);
        if (sessao.estaEncerrada()) {
            som.tocarFim();
            gravacaoPendente = true;
            gravarVotos();
        } else {
            som.tocarConfirmacao();
        }
    }

    public boolean possuiVotosNaoGravados() {
        return gravacaoPendente;
    }

    private boolean estaRecebendoVotos() {
        return sessao != null && !sessao.estaEncerrada();
    }

    private void gravarVotos() {
        gravando = true;
        FachadaUrna fachadaEmUso = fachada;
        List<Voto> votos = sessao.getVotos();
        executarEmSegundoPlano(() -> fachadaEmUso.registrarVotos(votos), this::agendarNovaSessao,
                this::tratarFalhaDeGravacao);
    }

    private void agendarNovaSessao() {
        gravando = false;
        gravacaoPendente = false;
        Timer temporizador = new Timer(ESPERAANTESDOPROXIMOELEITOR, evento -> iniciarNovaSessao());
        temporizador.setRepeats(false);
        temporizador.start();
    }

    private void iniciarNovaSessao() {
        sessao = fachada.iniciarSessao();
        visao.atualizar(sessao);
    }

    private void tratarFalhaDeCarga(Throwable causa) {
        if (visao.perguntarSeTentaNovamente("Não foi possível carregar os candidatos.\n" + causa.getMessage())) {
            iniciar();
        } else if (fachadaDemonstracao != null && fachada != fachadaDemonstracao && visao.perguntarSeUsaDemonstracao()) {
            fachada = fachadaDemonstracao;
            iniciar();
        } else {
            visao.encerrar();
        }
    }

    private void tratarFalhaDeGravacao(Throwable causa) {
        gravando = false;
        if (visao.perguntarSeTentaNovamente("Os votos ainda não foram gravados.\n" + causa.getMessage())) {
            gravarVotos();
        } else {
            visao.exibirErro("Os votos continuam guardados na urna. Aperte CONFIRMA para tentar gravar de novo.");
        }
    }

    private static void executarEmSegundoPlano(Runnable tarefa, Runnable aoConcluir, Consumer<Throwable> aoFalhar) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                tarefa.run();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    aoConcluir.run();
                } catch (ExecutionException erro) {
                    aoFalhar.accept(erro.getCause());
                } catch (InterruptedException erro) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
