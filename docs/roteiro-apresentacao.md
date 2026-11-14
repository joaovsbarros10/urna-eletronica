# Roteiro da apresentação (15 minutos)

## Antes de começar

- Abra o projeto na IDE com a fonte grande (Ctrl + roda do mouse) e deixe abertos, nesta
  ordem, os arquivos das partes 3 a 6. Use **Ctrl+G** (IntelliJ) ou **Ctrl+G** (VS Code) para
  ir direto à linha indicada.
- Teste a internet: dê dois cliques em `Abrir Urna.cmd`. Se o banco não responder, use o
  plano B: `java -jar urna.jar demonstracao` (mesmas telas, dados em memória).
- Deixe a urna e a administração já abertas, minimizadas.

## Linha do tempo

| Tempo | Parte | O que mostrar |
|---|---|---|
| 0:00 – 1:30 | 1. O que é | Fala, sem código |
| 1:30 – 4:30 | 2. Demonstração | Urna e administração rodando |
| 4:30 – 6:00 | 3. Arquitetura | Diagrama de pacotes |
| 6:00 – 9:30 | 4. Herança, polimorfismo e encapsulamento | Hierarquia de `Candidato` |
| 9:30 – 11:30 | 5. Sessão de votação (State) | Estados e confirmação |
| 11:30 – 13:30 | 6. Banco de dados | Transação, roles e restrições |
| 13:30 – 14:30 | 7. Qualidade | Testes, cobertura, checklist |
| 14:30 – 15:00 | 8. Fechamento | Limitações e próximos passos |

---

## 1. O que é (1 min 30)

- Simulador de urna eletrônica **sem valor oficial**, para estudo.
- 100% Java: interface em **Swing**, regras em camadas (**MVC + Fachada**), banco
  **PostgreSQL no Supabase** acessado por **JDBC**, build **Maven**, testes **JUnit 5**.
- Cinco cargos, na ordem: Deputado Federal, Deputado Estadual, Senador, Governador,
  Presidente. Teclas BRANCO, CORRIGE e CONFIRMA, e sons de tecla, confirmação e fim.
- Regra central: **o número do candidato começa com o número do partido** (RN04).

## 2. Demonstração (3 min)

Na urna:

| Cargo | Digite | Mostra |
|---|---|---|
| Deputado Federal | `3105` | Tiago Moura, PAU, foto → CONFIRMA |
| Deputado Estadual | `99999` | NÚMERO ERRADO / VOTO NULO → CONFIRMA |
| Senador | `312` | Clara Duarte e os **dois suplentes** → CONFIRMA |
| Governador | BRANCO | VOTO EM BRANCO → CONFIRMA |
| Presidente | `31` e depois CORRIGE, `47` | Mostra que CORRIGE limpa; Rui Albuquerque e o **vice** → CONFIRMA |

Ao chegar ao **FIM**, troque para a administração, aba **Apuração**, e mostre que o voto
entrou (escolha o cargo e clique em Apurar). Se der tempo, mostre a aba **Candidatos**.

Frase de apoio: *"as teclas não vão ao banco: os candidatos ficam em memória; só no FIM
os cinco votos são gravados de uma vez, em segundo plano."*

## 3. Arquitetura (1 min 30)

Abra [docs/uml/02-pacotes.mmd](uml/02-pacotes.mmd) no mermaid.live ou mostre a figura
do PDF (seção 5.2).

- A dependência é de **mão única**: visão → controle → negócio → persistência; o
  **modelo não depende de ninguém** (só do JDK), por isso é testável sem tela e sem banco.
- [FachadaUrna.java:34](../src/main/java/br/com/urna/negocio/FachadaUrna.java#L34) é a
  **única classe pública** do pacote `negocio`;
  [ServicoVotacao.java:25](../src/main/java/br/com/urna/negocio/ServicoVotacao.java#L25)
  não tem `public`, então a tela não consegue chamá-lo direto (item 2.0 do checklist).
- [Aplicacao.java:143](../src/main/java/br/com/urna/Aplicacao.java#L143) e
  [:150](../src/main/java/br/com/urna/Aplicacao.java#L150): a mesma Fachada roda sobre o
  Supabase ou sobre memória; só a composição muda.

## 4. Herança, polimorfismo e encapsulamento (3 min 30)

**Herança.** Abra [docs/uml/03-classes-candidato.mmd](uml/03-classes-candidato.mmd) ou fale
a árvore: `Candidato` → `CandidatoComVice` (Presidente, Governador), `CandidatoSenador`,
`CandidatoDeputado` (Federal, Estadual).

- [Cargo.java:28](../src/main/java/br/com/urna/modelo/Cargo.java#L28): cada cargo guarda
  dígitos, descrição, ordem de votação e complemento.
- [Candidato.java:128](../src/main/java/br/com/urna/modelo/candidato/Candidato.java#L128):
  `validarNumero()` aplica RN01 (quantidade de dígitos) e RN04 (prefixo do partido) no
  construtor: **não existe candidato inválido em memória**.

**Polimorfismo** (o ponto mais forte da apresentação):

- [Candidato.java:118](../src/main/java/br/com/urna/modelo/candidato/Candidato.java#L118):
  `obterLinhasAdicionais()` é abstrato.
- [CandidatoComVice.java:57](../src/main/java/br/com/urna/modelo/candidato/CandidatoComVice.java#L57)
  devolve a linha do vice;
  [CandidatoSenador.java:46](../src/main/java/br/com/urna/modelo/candidato/CandidatoSenador.java#L46)
  devolve os dois suplentes; deputado devolve lista vazia.
- [PainelDisplay.java:288](../src/main/java/br/com/urna/visao/PainelDisplay.java#L288): a
  tela só faz `for` no que vier — **nenhum `if` por cargo**.
- [FabricaCandidato.java:39](../src/main/java/br/com/urna/modelo/candidato/FabricaCandidato.java#L39):
  o **único `switch` sobre cargo** do projeto (padrão Factory).
- [PainelTeclado.java:58](../src/main/java/br/com/urna/visao/PainelTeclado.java#L58): um único
  tratador para as 13 teclas; cada tecla sabe sua ação.

**Encapsulamento:** atributos `private final`, objetos imutáveis, `getFoto()` devolve
**cópia** do array e `getVotosRegistrados()` devolve lista **somente leitura** — o
controlador não consegue inserir um voto falso.

## 5. Sessão de votação: padrão State (2 min)

Abra [docs/uml/11-estados-sessao.mmd](uml/11-estados-sessao.mmd) ou fale os cinco estados:
Digitando, CandidatoEncontrado, NumeroInexistente, VotoEmBranco, Encerrado.

- [SessaoVotacao.java:96](../src/main/java/br/com/urna/modelo/sessao/SessaoVotacao.java#L96):
  a sessão só repassa a tecla ao estado atual e avisa a tela (Observer).
- [EstadoDigitando.java:69](../src/main/java/br/com/urna/modelo/sessao/EstadoDigitando.java#L69):
  ao completar o número, decide entre candidato, CANDIDATO INEXISTENTE e NÚMERO ERRADO (RN09).
- [EstadoConfirmavel.java:44](../src/main/java/br/com/urna/modelo/sessao/EstadoConfirmavel.java#L44):
  **Template Method** — `confirmar` é escrito uma vez; cada estado só diz qual voto criar
  ([EstadoCandidatoEncontrado.java:32](../src/main/java/br/com/urna/modelo/sessao/EstadoCandidatoEncontrado.java#L32)).
- Apuração sem `instanceof`:
  [ServicoApuracao.java:37](../src/main/java/br/com/urna/negocio/ServicoApuracao.java#L37)
  chama `voto.contabilizarEm(apuracao)` e cada tipo de voto soma no contador certo
  ([VotoCandidato.java:68](../src/main/java/br/com/urna/modelo/voto/VotoCandidato.java#L68)).

## 6. Banco de dados (2 min)

- [RepositorioVotoSupabase.java:69](../src/main/java/br/com/urna/persistencia/supabase/RepositorioVotoSupabase.java#L69):
  os cinco votos numa **transação** (`setAutoCommit(false)`, lote, `commit` ou
  `rollback`): entram todos ou nenhum (RN11).
- [ControladorVotacao.java:245](../src/main/java/br/com/urna/controle/ControladorVotacao.java#L245):
  se a rede cair, os votos **ficam na memória** e a urna oferece nova tentativa (RNF09).
- [banco/01-esquema.sql:56](../banco/01-esquema.sql#L56): a tabela `voto` **não tem
  eleitor nem horário** e o identificador é aleatório — sigilo do voto (RN12).
- [banco/01-esquema.sql:36](../banco/01-esquema.sql#L36) e
  [:74](../banco/01-esquema.sql#L74): o banco também garante dígitos por cargo e prefixo
  do partido.
- [banco/02-seguranca.sql:18](../banco/02-seguranca.sql#L18): a role da urna **só insere**
  votos; não lê nem apaga. A senha fica fora do código, em `urna.propriedades`.

## 7. Qualidade (1 min)

- **95 testes JUnit 5**, rodando sem tela, sem som e sem internet (repositórios em memória).
- Cobertura medida pelo JaCoCo: **94 a 99%** no modelo e **95%** no negócio (meta: 80%).
- Javadoc sem nenhum aviso; nomes seguem o checklist 3.0 a 3.6 (sem abreviação, sem
  sublinhado, métodos com verbo). Evidência item a item em
  [docs/checklist-inspecao.md](checklist-inspecao.md).

## 8. Fechamento (30 s)

- **Limitações**: é simulador — sem identificação do eleitor, sem voto de legenda, sem
  segundo turno; quem tiver a senha da role da urna consegue inserir votos.
- **Próximos passos**: voto de legenda, fotos no Supabase Storage, apuração por SQL se o
  volume crescer.

---

## Perguntas prováveis

| Pergunta | Resposta curta |
|---|---|
| Por que não usou Spring? | O enunciado pede aplicação desktop em Swing com MVC e Fachada; a injeção de dependência é feita à mão na `Aplicacao`, sem framework. |
| Por que tantas classes de candidato? | O que muda por cargo (vice, suplentes, linhas na tela) fica em cada subclasse, e a tela não precisa de `if` por cargo. |
| E se a internet cair na hora de gravar? | Os votos ficam em memória; a urna pergunta se tenta de novo e CONFIRMA na tela FIM refaz a gravação. Nada é perdido. |
| Como garante o sigilo? | A tabela de votos não tem eleitor, horário nem sequência; o identificador é um UUID aleatório. |
| Por que constantes sem sublinhado (`DEPUTADOFEDERAL`)? | O item 3.0 do checklist pede só letras e dígitos; a troca para `DEPUTADO_FEDERAL` é mecânica se o avaliador preferir. |
| As teclas consultam o banco? | Não. Partidos e candidatos são carregados uma vez na abertura; a busca é em memória (RNF05). |
