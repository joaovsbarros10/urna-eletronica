# Checklist de inspeção: onde está cada item

| Item | Exigência | Como o projeto atende | Onde ver |
|---|---|---|---|
| 1.0 | MVC | Pacotes `modelo`, `visao` e `controle`. A tela chama o controlador; o controlador altera o modelo e manda a tela se redesenhar pela interface `VisaoVotacao` | `controle/ControladorVotacao.java`, `controle/VisaoVotacao.java`, `visao/JanelaUrna.java` |
| 1.1 | Desktop em Swing | Toda a interface usa `javax.swing`: `JFrame`, `JPanel`, `JButton`, `JLabel`, `JTable`, `JTabbedPane`, `JComboBox`, `JFileChooser`, `SwingWorker` | pacote `visao` |
| 2.0 | Interface uniforme da camada de negócio | `FachadaUrna` é a única classe do pacote `negocio`; os controladores só falam com ela | `negocio/FachadaUrna.java` |
| 3.0 | Sem notação húngara, sem palavras reservadas, só ASCII, palavras completas | `seletorCargo` (não `cboCargo`), `RepositorioUrna` (sem `I`), constantes sem sublinhado (`DEPUTADOFEDERAL`, `TAXAAMOSTRAGEM`), sem acento em identificador | todo o código |
| 3.1 | Classes em Pascal, no singular, nome significativo | `Candidato`, `ApuracaoCargo`, `SessaoVotacao`, `FachadaUrna` | todos os tipos |
| 3.2 | Ordem do arquivo | Cabeçalho, `package`, `import`, declaração da classe | todos os `.java` |
| 3.3 | Ordem dentro da classe | Javadoc, declaração, constantes estáticas, atributos, construtores e métodos agrupados por função | todos os `.java` |
| 3.4 | Métodos em Camel com verbo | `digitar`, `confirmar`, `votarEmBranco`, `procurarCandidato`, `contabilizarEm`, `estaEncerrada` | todos os tipos |
| 3.5 | Atributos e variáveis em Camel, palavras completas; uma letra só em laço | `candidatoEncontrado`, `votosPorCandidato`, `posicaoDoCargo`; `i` só em `for` | todos os tipos |
| 3.6 | Só métodos abstratos e constantes vira interface | `RepositorioUrna` e `VisaoVotacao` (só métodos abstratos), `TemaVisual` (só constantes). As classes abstratas `Candidato`, `Voto` e `PainelAdministrativo` têm atributos e métodos concretos | `persistencia/RepositorioUrna.java`, `controle/VisaoVotacao.java`, `visao/TemaVisual.java` |

## Orientação a objetos

| Conceito | Onde ver |
|---|---|
| Herança | `Candidato` → `CandidatoComVice`, `CandidatoSenador`, `CandidatoDeputado`; `Voto` → `VotoCandidato`, `VotoBranco`, `VotoNulo`; `PainelAdministrativo` → três abas |
| Polimorfismo | `candidato.obterLinhasAdicionais()` no `PainelDisplay`; `voto.contabilizarEm(apuracao)` na `FachadaUrna.apurar`; `painel.atualizar()` na `JanelaAdministracao`; `RepositorioUrna` com duas implementações |
| Encapsulamento | atributos `private final`; `Candidato.getFoto()` devolve cópia; `SessaoVotacao.getVotos()` devolve lista que não pode ser alterada; construtores de candidato com acesso de pacote |
| Abstração | interfaces `RepositorioUrna` e `VisaoVotacao`; classes abstratas `Candidato`, `Voto` e `PainelAdministrativo` |

## Observação para o avaliador

Constantes sem sublinhado (`DEPUTADOFEDERAL`): o item 3.0 pede só letras e dígitos. Se o
avaliador preferir `DEPUTADO_FEDERAL`, a troca é mecânica (decisão 0003).
