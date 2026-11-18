# Urna Eletrônica em Java

Simulador de urna eletrônica **sem valor oficial**, feito para estudo. Aplicação desktop
escrita só em Java: interface em **Swing**, arquitetura **MVC com Fachada** e votos gravados
no **Firebase (Cloud Firestore)** pela API REST. A votação segue a ordem Deputado Federal,
Deputado Estadual, Senador, Governador e Presidente, com as teclas BRANCO, CORRIGE e
CONFIRMA e os sons de tecla, confirmação e fim. Partidos, candidatos e fotos são fictícios.

![Urna com candidato encontrado](docs/urna.png)

## Como abrir

Requisito: Java 17 ou superior.

| No Windows | Comando | Abre | Dados |
|---|---|---|---|
| `Abrir Urna.cmd` | `java -jar urna.jar` | urna de votação | Firebase |
| `Abrir Administracao.cmd` | `java -jar urna.jar administracao` | cadastro e apuração | Firebase |
| — | `java -jar urna.jar demonstracao` | urna e administração juntas | memória, sem internet |

Se o Firebase não estiver configurado ou não responder, a urna oferece os dados de
demonstração. Teclado do computador: dígitos, **Enter** = CONFIRMA, **Backspace** = CORRIGE.

No modo demonstração: Deputado Federal `3105`, Deputado Estadual `31042`, Senador `312`,
Governador `31`, Presidente `47`. Partido inexistente (`99`) dá NÚMERO ERRADO; partido
existente sem candidato (`3199`) dá CANDIDATO INEXISTENTE.

## Configurar o Firebase

1. No [console do Firebase](https://console.firebase.google.com), crie um projeto e um banco
   **Cloud Firestore**.
2. Em **Firestore Database > Regras**, cole `banco/firestore.rules` e publique.
3. Crie o arquivo `urna.propriedades` na pasta do projeto (fica fora do Git) com uma linha:
   `URNA_FIREBASE_PROJETO=<código do projeto>`.
4. Cadastre partidos e candidatos pela administração.

## Compilar e testar

```bash
mvnw.cmd verify
```

Compila, roda os testes JUnit 5, confere a cobertura mínima de 80% no modelo e no negócio e
gera `target/urna.jar`; copie-o para a raiz para usar com os atalhos. Se o Maven falhar com
`PKIX path building failed` (antivírus inspecionando HTTPS), rode antes
`$env:MAVEN_OPTS = "-Djavax.net.ssl.trustStoreType=Windows-ROOT"`.

## Estrutura

```
src/main/java/br/com/urna/
├── Aplicacao.java    ponto de entrada: monta e liga as peças
├── modelo/           Cargo, Partido, Candidato (+3 subclasses), Voto (+3 subclasses),
│                     ApuracaoCargo e SessaoVotacao
├── negocio/          FachadaUrna: única porta de entrada das regras
├── persistencia/     RepositorioUrna e as implementações Firebase e memória
├── controle/         ControladorVotacao, ControladorAdministracao, VisaoVotacao
├── visao/            janelas, painéis e teclas Swing
├── som/              ReprodutorSom
└── excecao/          UrnaException
```

A visão chama o controle, o controle chama a `FachadaUrna`, a Fachada usa o repositório, e o
modelo não depende de ninguém.

## Documentação

- [docs/Documentacao-Tecnica-Urna.pdf](docs/Documentacao-Tecnica-Urna.pdf): todas as classes,
  métodos, fluxos e o banco.
- [docs/roteiro-apresentacao.md](docs/roteiro-apresentacao.md): roteiro de 15 minutos e testes manuais.
- [docs/checklist-inspecao.md](docs/checklist-inspecao.md): evidência de cada item do checklist.
- [docs/decisoes.md](docs/decisoes.md): decisões do projeto, como a troca do Supabase pelo
  Firebase (o PDF pedia Supabase via JDBC no RNF08).
- [docs/uml/](docs/uml/): diagramas em Mermaid (abra em [mermaid.live](https://mermaid.live)).

## Limitações

- Sem login: quem souber o código do projeto consegue ler os dados e mexer no cadastro.
  Ninguém consegue alterar ou apagar votos.
- Fora do escopo: identificação do eleitor, voto de legenda, segundo turno.
