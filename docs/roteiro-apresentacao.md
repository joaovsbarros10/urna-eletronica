# Roteiro da apresentação (15 minutos)

## Antes de começar

- Abra o projeto na IDE com a fonte grande e deixe abertos os arquivos das partes 3 a 6.
- Dê dois cliques em `Abrir Urna.cmd` para testar a internet. Se o banco não responder, use
  `java -jar urna.jar demonstracao` (mesmas telas, dados em memória).

| Tempo | Parte |
|---|---|
| 0:00 – 1:30 | 1. O que é |
| 1:30 – 4:30 | 2. Demonstração |
| 4:30 – 6:00 | 3. Arquitetura |
| 6:00 – 9:00 | 4. Herança e polimorfismo |
| 9:00 – 11:00 | 5. A sessão de votação |
| 11:00 – 13:30 | 6. Banco de dados |
| 13:30 – 15:00 | 7. Testes e fechamento |

## 1. O que é (1 min 30)

Simulador de urna sem valor oficial, 100% Java: interface em **Swing**, **MVC com Fachada**,
banco **Cloud Firestore no Firebase** pela **API REST**, testes **JUnit 5**. São 33 classes. A regra
central: **o número do candidato começa com o número do partido**.

## 2. Demonstração (3 min)

Os números abaixo são os do modo demonstração (`java -jar urna.jar demonstracao`, que abre a
urna e a administração juntas). Com o Firebase, use os candidatos cadastrados.

| Cargo | Digite | Mostra |
|---|---|---|
| Deputado Federal | `3105` | Tiago Moura, PAU e foto → CONFIRMA |
| Deputado Estadual | `99999` | NÚMERO ERRADO / VOTO NULO → CONFIRMA |
| Senador | `312` | Clara Duarte e os dois suplentes → CONFIRMA |
| Governador | BRANCO | VOTO EM BRANCO → CONFIRMA |
| Presidente | `31`, CORRIGE, `47` | CORRIGE limpa; Rui Albuquerque e o vice → CONFIRMA |

No FIM, vá à administração, aba **Apuração**, e mostre o voto contado e a porcentagem.

## 3. Arquitetura (1 min 30)

- A visão chama o controle, o controle chama a Fachada, a Fachada usa o repositório. O
  **modelo não depende de ninguém**.
- [FachadaUrna.java:31](../src/main/java/br/com/urna/negocio/FachadaUrna.java#L31): única porta
  de entrada das regras (item 2.0 do checklist).
- [Aplicacao.java:69](../src/main/java/br/com/urna/Aplicacao.java#L69): a mesma Fachada roda sobre
  o Firebase ou sobre a memória; só muda o repositório. Trocar o Supabase pelo Firebase
  mexeu só no pacote `persistencia`.

## 4. Herança e polimorfismo (3 min)

- `Candidato` é abstrata, com três subclasses: com vice (Presidente e Governador), senador e deputado.
- [Candidato.java:75](../src/main/java/br/com/urna/modelo/Candidato.java#L75): `criar` escolhe a
  subclasse pelo cargo.
- [Candidato.java:143](../src/main/java/br/com/urna/modelo/Candidato.java#L143): `validarNumero`
  confere dígitos e prefixo do partido no construtor: não existe candidato inválido.
- [Candidato.java:134](../src/main/java/br/com/urna/modelo/Candidato.java#L134): `obterLinhasAdicionais`
  é abstrato; [CandidatoComVice.java:31](../src/main/java/br/com/urna/modelo/CandidatoComVice.java#L31)
  devolve o vice, [CandidatoSenador.java:30](../src/main/java/br/com/urna/modelo/CandidatoSenador.java#L30)
  os suplentes.
- [PainelDisplay.java:211](../src/main/java/br/com/urna/visao/PainelDisplay.java#L211): a tela
  só faz `for` no que vier, **sem `if` por cargo**.
- [VotoCandidato.java:40](../src/main/java/br/com/urna/modelo/VotoCandidato.java#L40) e
  [FachadaUrna.java:166](../src/main/java/br/com/urna/negocio/FachadaUrna.java#L166): cada voto
  se soma no contador certo da apuração, sem `instanceof`.

## 5. A sessão de votação (2 min)

- [SessaoVotacao.java:170](../src/main/java/br/com/urna/modelo/SessaoVotacao.java#L170): o `enum`
  `Situacao` (digitando, candidato, branco, número errado, candidato inexistente, encerrada)
  já guarda o aviso de cada situação.
- [SessaoVotacao.java:54](../src/main/java/br/com/urna/modelo/SessaoVotacao.java#L54) `digitar` e
  [:98](../src/main/java/br/com/urna/modelo/SessaoVotacao.java#L98) `procurarCandidato`: busca em
  memória, sem ir ao banco a cada tecla.
- [SessaoVotacao.java:84](../src/main/java/br/com/urna/modelo/SessaoVotacao.java#L84) `confirmar`:
  registra o voto e passa ao próximo cargo ou encerra.

## 6. Banco de dados (2 min 30)

- [ConexaoFirebase.java:89](../src/main/java/br/com/urna/persistencia/ConexaoFirebase.java#L89):
  o código do projeto vem do `urna.propriedades`, fora do Git; as requisições vão por HTTPS.
- [RepositorioUrnaFirebase.java:143](../src/main/java/br/com/urna/persistencia/RepositorioUrnaFirebase.java#L143):
  os cinco votos num único **commit**: entram todos ou nenhum.
- [RepositorioUrnaFirebase.java:127](../src/main/java/br/com/urna/persistencia/RepositorioUrnaFirebase.java#L127):
  o Firestore não tem chave estrangeira, então o repositório confere os votos antes de excluir.
- [ControladorVotacao.java:173](../src/main/java/br/com/urna/controle/ControladorVotacao.java#L173):
  se a internet cair, os votos ficam na memória e a urna tenta de novo.
- [banco/firestore.rules](../banco/firestore.rules): o banco só aceita dados válidos, o voto não
  tem eleitor nem horário (sigilo) e nenhum voto pode ser alterado ou apagado.

## 7. Testes e fechamento (1 min 30)

- 23 testes JUnit 5 sem tela e sem internet; cobertura acima de 80% no modelo e no negócio.
- Limitações: é simulador, sem identificação do eleitor e sem voto de legenda.

## Perguntas prováveis

| Pergunta | Resposta |
|---|---|
| Por que não usou Spring? | O enunciado pede desktop em Swing com MVC e Fachada; as peças são ligadas à mão na `Aplicacao`. |
| E se a internet cair? | Os votos ficam na memória e a urna oferece nova tentativa. |
| Como garante o sigilo? | O voto não tem eleitor, horário nem sequência; o identificador é aleatório. |
| As teclas consultam o banco? | Não. Os candidatos são carregados uma vez na abertura. |
| Por que Firebase, se o PDF pedia Supabase? | Decisão 0008. Como tudo passa pela interface `RepositorioUrna`, a troca mexeu só em duas classes. |
| Por que não usou o SDK do Firebase? | A API REST mantém o jar pequeno e permite regras de segurança diferentes para a urna e a administração (versão com login). |
| Por que `DEPUTADOFEDERAL` sem sublinhado? | O item 3.0 pede só letras e dígitos. |
