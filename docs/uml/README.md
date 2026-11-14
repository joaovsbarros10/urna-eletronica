# Diagramas UML

Fontes Mermaid dos diagramas do projeto (seção 5 do documento de projeto). O texto de cada
arquivo **é** o diagrama: cole em [mermaid.live](https://mermaid.live) para exportar PNG ou
SVG para o relatório. Os diagramas de classes refletem o código atual, inclusive os métodos
acrescentados durante a implementação (por exemplo `Candidato.gerarFicha()` e
`ControladorVotacao.possuiVotosNaoGravados()`).

| Arquivo | Diagrama | Seção |
|---|---|---|
| `01-casos-de-uso.mmd` | Casos de uso (fluxograma com elipses, já que o Mermaid não tem esse tipo nativo) | 5.1 |
| `02-pacotes.mmd` | Pacotes e dependências de mão única | 5.2 |
| `03-classes-candidato.mmd` | `Partido`, `Cargo` e a hierarquia de `Candidato` | 5.3.1 |
| `04-classes-voto.mmd` | Hierarquia de `Voto` e `ApuracaoCargo` | 5.3.2 |
| `05-classes-visao-votacao.mmd` | Visão e controle da votação | 5.4.1 |
| `06-classes-visao-administracao.mmd` | Visão e controle da administração | 5.4.2 |
| `07-classes-negocio.mmd` | Fachada, serviços e catálogo | 5.4.3 |
| `08-classes-persistencia-som-excecao.mmd` | Repositórios, som e exceções | 5.4.4 |
| `09-sequencia-digitar.mmd` | Sequência: digitar um dígito | 5.5.1 |
| `10-sequencia-confirmar-gravar.mmd` | Sequência: confirmar o último cargo e gravar | 5.5.2 |
| `11-estados-sessao.mmd` | Máquina de estados da sessão | 5.6.1 |
| `12-classes-estado.mmd` | Classes do padrão State | 5.6.3 |
| `13-entidade-relacionamento.mmd` | Modelo de dados (Supabase) | 5.7 |

Regra (seção 12.4): um commit que muda uma assinatura pública, um pacote ou uma tabela
atualiza o diagrama correspondente **no mesmo commit**.
