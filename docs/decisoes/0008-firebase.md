# 0008. Banco no Firebase (Cloud Firestore)

Data: 2026-11-14
Status: aceita

## Contexto
O PDF do projeto pedia persistência no Supabase (PostgreSQL) via JDBC (RNF08). O grupo
decidiu levar o banco para o Firebase. O Firebase não tem driver JDBC; para Java desktop há a
API REST ou o Admin SDK.

## Decisão
- Usar o **Cloud Firestore pela API REST**, com `HttpURLConnection` e a biblioteca Gson para
  o JSON. As classes são `ConexaoFirebase` (requisições) e `RepositorioUrnaFirebase`
  (conversão entre objetos e documentos). A `FachadaUrna` e o resto do sistema não mudaram,
  porque já dependiam só da interface `RepositorioUrna`.
- Três coleções: `partido`, `candidato` e `voto`. Os cinco votos de um eleitor vão num único
  `commit`, que grava todos ou nenhum, como a transação do PostgreSQL.
- As regras que o PostgreSQL garantia (dígitos por cargo, prefixo do partido, foto de até
  200 KB, voto do mesmo cargo do candidato, voto que não pode ser alterado) passaram para
  `banco/firestore.rules`. Como o Firestore não tem chave estrangeira, o repositório confere
  se o candidato tem votos antes de excluí-lo; o partido com candidatos a `FachadaUrna` já
  recusava.
- O login no Firebase Authentication ficou **opcional** nesta fase. A versão com dois
  usuários (urna só cria votos; administração cadastra e apura) está pronta em
  `banco/firestore-com-login.rules`.

## Consequências
- Sai o JDBC e o driver do PostgreSQL; o jar caiu de 1,1 MB para cerca de 400 KB.
- O RNF08 do PDF deixa de ser atendido como escrito; esta decisão registra o motivo.
- Sem login, quem souber o código do projeto consegue ler os dados e mexer no cadastro.
  Votos continuam impossíveis de alterar ou apagar.

## Alternativas consideradas
- Admin SDK do Firebase: código mais curto, mas a chave de conta de serviço dá acesso total
  ao banco nas duas telas e o jar ganharia cerca de 40 MB.
- Realtime Database: regras de validação mais fracas e sem gravação atômica de vários
  caminhos tão simples quanto o `commit` do Firestore.
