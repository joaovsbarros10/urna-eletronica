# Rastreabilidade do checklist de inspeção (itens 1.0 a 3.6)

Onde está a evidência de cada item no código. A coluna "Check" do formulário original fica
para o inspetor.

| Item | Exigência | Como o projeto atende | Evidência no repositório |
|---|---|---|---|
| 1.0 | MVC | Pacotes `modelo`, `visao` e `controle`. A visão observa o modelo pela interface `OuvinteSessaoVotacao`; o controlador recebe os eventos da visão e altera o modelo; o controlador fala com a tela pela interface `VisaoVotacao`, não pela classe Swing | `modelo/sessao/OuvinteSessaoVotacao.java`, `controle/VisaoVotacao.java`, `controle/ControladorVotacao.java`, `visao/JanelaUrna.java` |
| 1.1 | Desktop em Swing | Toda a interface usa `javax.swing`: `JFrame`, `JPanel`, `JButton`, `JLabel`, `JTabbedPane`, `JTable`, `JComboBox`, `JFileChooser`, `SwingWorker`, `Timer`. Sem JavaFX, HTML ou JavaScript | pacote `visao` |
| 2.0 | Interface uniforme da camada de negócio | `FachadaUrna` é a única classe pública de `negocio`; `ServicoVotacao`, `ServicoCadastro`, `ServicoApuracao` e `CatalogoEleitoral` têm acesso de pacote | `negocio/*.java` (só `FachadaUrna` tem `public class`) |
| 3.0 | Nomes: sem notação húngara, sem palavras reservadas, só letras e dígitos ASCII, palavras completas | `botaoConfirma`, `RepositorioVoto` (sem `I`), `quantidadeDigitos`, constantes sem sublinhado (`DEPUTADOFEDERAL`, `TAXAAMOSTRAGEM`), sem acento em identificador, sem `DAO`/`DTO`/`Impl` | todo o código; decisão `docs/decisoes/0003-constantes-sem-sublinhado.md` |
| 3.1 | Classes e interfaces em Pascal, no singular | `Candidato`, `Partido`, `ApuracaoCargo`, `FachadaUrna`, `CargaDemonstracao`, `TradutorErroBanco`, `CenarioDeTeste` | todos os tipos |
| 3.2 | Ordem do arquivo: cabeçalho, pacote, imports, declarações | Todo arquivo começa com o comentário de cabeçalho, depois `package`, `import` e o tipo | todos os `.java` |
| 3.3 | Ordem dentro da classe | Javadoc, declaração, comentário de implementação, estáticas, instância, construtores e métodos agrupados por funcionalidade com comentário `// ---- Grupo ----` | todos os `.java`; exemplo: `modelo/Partido.java` |
| 3.4 | Métodos em Camel com verbo | `digitar`, `corrigir`, `confirmar`, `buscarCandidato`, `getNome`, `reproduzir`, `estaEncerrada`, `possuiVice`, `definirNome`; sem `record` (acessores sem verbo) | todos os tipos |
| 3.5 | Atributos, variáveis e argumentos em Camel, palavras completas; uma letra só em laço | `nomePrimeiroSuplente`, `digitosDigitados`, `candidatoEncontrado`; laços usam `i`; parâmetros de lambda com nome completo (`evento`, `candidato`, `erro`) | todos os tipos |
| 3.6 | Só abstratos e `static final` vira interface | Interfaces: `EstadoSessao`, `OuvinteSessaoVotacao`, `CatalogoCandidatos`, `VisaoVotacao`, `RepositorioPartido`, `RepositorioCandidato`, `RepositorioVoto`, `ReprodutorSom`, `TemaVisual` (só constantes). Classes `abstract` com membros concretos: `Candidato`, `CandidatoComVice`, `CandidatoDeputado`, `Voto`, `EstadoConfirmavel`, `BotaoTecla`, `PainelAdministrativo`, `UrnaException` | ver os arquivos citados |

## Polimorfismo, herança e encapsulamento (seção 7)

| Conceito | Onde verificar |
|---|---|
| `candidato.obterLinhasAdicionais()` sem `if` por cargo | `visao/PainelDisplay.java` (`exibirCandidato`), `visao/PainelCadastroCandidato.java` (tabela) |
| `voto.contabilizarEm(apuracao)` sem `instanceof` | `negocio/ServicoApuracao.java`, `modelo/voto/*.java` |
| Estados da sessão (State + Template Method) | `modelo/sessao/EstadoConfirmavel.java` |
| `botao.acionar(controlador)` sem `if` por tecla | `visao/PainelTeclado.java` (`montarBotoes`) |
| `painel.atualizar()` sem `if` por aba | `visao/JanelaAdministracao.java` |
| Repositório Supabase ou memória escolhido na composição | `Aplicacao.java` |
| Cópia defensiva da foto | `Candidato.getFoto()`, `FichaCandidato` |
| Lista somente leitura de votos | `SessaoVotacao.getVotosRegistrados()` |
| Único `switch` sobre `Cargo` | `modelo/candidato/FabricaCandidato.java` |
| Único `switch` sobre o tipo do voto | `persistencia/supabase/RepositorioVotoSupabase.java` (`criarVoto`) |

## Ressalvas para o avaliador

- **Sublinhado em constantes** (item 3.0): o projeto segue o texto do checklist ao pé da
  letra. Se o avaliador preferir `UPPER_SNAKE_CASE`, a troca é mecânica (decisão 0003).
- **`instanceof` em tratamento de erro**: `PainelAdministrativo.executarEmSegundoPlano`
  distingue exceções previstas (`UrnaException`, `IllegalArgumentException`) de erros
  inesperados; não é decisão de regra de negócio.
- **`equals` com `instanceof`**: `Partido.equals` e `Candidato.equals` seguem o contrato de
  `Object.equals`, como no exemplo da seção 11.3.
