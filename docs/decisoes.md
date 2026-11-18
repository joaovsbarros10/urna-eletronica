# Decisões do projeto

## 1. Banco no Firebase (Cloud Firestore)

O PDF do projeto pedia Supabase (PostgreSQL) via JDBC (RNF08). O banco foi levado para o
**Cloud Firestore**, que não tem driver JDBC, então a urna usa a **API REST** com
`HttpURLConnection` e a biblioteca Gson. Tudo fica em `RepositorioUrnaFirebase`; a
`FachadaUrna` não mudou, porque só conhece a interface `RepositorioUrna`.

- Três coleções: `partido`, `candidato` e `voto`. Os cinco votos de um eleitor vão num único
  `commit`: entram todos ou nenhum.
- As regras que o banco garante (dígitos por cargo, prefixo do partido, foto de até 200 KB,
  voto do mesmo cargo do candidato, voto que não pode ser alterado nem apagado) ficam em
  `banco/firestore.rules`. Como o Firestore não tem chave estrangeira, o repositório confere
  se o candidato tem votos antes de excluí-lo.
- Sem login nesta fase: quem souber o código do projeto consegue ler os dados e mexer no
  cadastro. Para separar urna e administração, o próximo passo é o Firebase Authentication.
- Alternativa descartada: o Admin SDK do Firebase, que dá acesso total ao banco e aumentaria
  o jar em cerca de 40 MB.

## 2. Foto dentro do documento do candidato

A foto é o campo `foto` (bytes) do documento, com limite de 200 KB conferido na `FachadaUrna`
(tamanho e formato JPEG ou PNG) e nas regras do Firestore, que aceita documentos de até 1 MB.
Um serviço só, sem Firebase Storage. A abertura da urna baixa todas as fotos de uma vez.

## 3. Constantes sem sublinhado

O item 3.0 do checklist pede identificadores com "somente letras e dígitos", então as
constantes são `DEPUTADOFEDERAL`, `TAXAAMOSTRAGEM`. Se o avaliador preferir
`DEPUTADO_FEDERAL`, a troca é mecânica: renomear as constantes e ajustar a lista de cargos de
`banco/firestore.rules` e os dados gravados.

## 4. Apuração feita em Java

`FachadaUrna.apurar` lê os votos do cargo e chama `voto.contabilizarEm(apuracao)`; cada
subclasse de `Voto` soma no contador certo, sem `instanceof`. É o ponto real de polimorfismo
da apuração. A porcentagem do candidato é sobre os votos válidos, como faz a Justiça
Eleitoral; a de válidos, brancos e nulos é sobre o total.

## 5. Próximo eleitor 3 segundos depois da gravação

A tela FIM aparece assim que o último cargo é confirmado. Os votos são gravados em segundo
plano e, só depois de gravados, a urna espera 3 segundos e abre a próxima sessão. Se a
gravação falhar, os votos continuam na sessão, a urna pergunta se tenta de novo e CONFIRMA
na tela FIM também refaz a gravação. Nenhum voto se perde por falha de rede.

## 6. Urna, administração e demonstração no mesmo jar

| Argumento | Abre | Dados |
|---|---|---|
| (nenhum) | urna de votação | Firebase |
| `administracao` | cadastro e apuração | Firebase |
| `demonstracao` | urna e administração juntas | memória, dados fictícios |

Sem configuração, ou se o banco não responder, a urna oferece os dados de demonstração.

## 7. Simplificação

A versão 1.0 tinha 75 classes e cerca de 6.800 linhas. Para caber numa apresentação de 15
minutos, a estrutura foi reduzida mantendo todas as funcionalidades e todos os itens do
checklist: `Candidato` com 3 subclasses e o método fábrica `criar`; `SessaoVotacao` com um
`enum` interno no lugar do padrão State; só a `FachadaUrna` no pacote `negocio`; uma interface
`RepositorioUrna` com as implementações Firebase e memória; uma exceção, uma classe de som e
uma classe de tecla. Hoje são 32 classes. A versão completa está no commit `2704fc6` do Git.
