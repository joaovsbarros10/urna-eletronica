# Urna Eletrônica em Java

Simulador de urna eletrônica **sem valor oficial**, feito para estudo. Aplicação desktop
escrita só em Java: interface em **Swing**, arquitetura **MVC com Fachada** e votos gravados
no **Firebase (Cloud Firestore)** pela API REST. A votação segue a ordem Deputado Federal,
Deputado Estadual, Senador, Governador e Presidente, com as teclas BRANCO, CORRIGE e
CONFIRMA e os sons de tecla, confirmação e fim. A apuração mostra votos e porcentagens.

## Como usar em qualquer computador (Windows 10 ou 11)

Na página de [Releases](../../releases/latest), baixe um dos arquivos:

| Arquivo | O que faz |
|---|---|
| `Urna.cmd` | Abre a urna de votação |
| `Urna-Administracao.cmd` | Abre o painel administrativo (cadastro de partidos e candidatos e apuração) |
| `Urna-Windows.zip` | O programa completo: descompacte e abra `Urna.exe` ou `Administracao.exe` |

Os dois `.cmd` funcionam sozinhos, sem a pasta do projeto e sem Java instalado: na primeira
vez baixam a urna (com o Java incluído, cerca de 27 MB) para `%LOCALAPPDATA%\UrnaEletronica`
e, nas próximas, só baixam de novo quando sai uma versão nova. Se o Windows avisar que o
arquivo veio da internet, clique em **Mais informações > Executar assim mesmo**.

Teclado do computador: dígitos, **Enter** = CONFIRMA, **Backspace** = CORRIGE. Se o Firebase
não responder, a urna oferece os dados de demonstração.

Quem já tem Java 17 ou superior também pode usar o `urna.jar`:

| Comando | Abre |
|---|---|
| `java -jar urna.jar` | urna de votação |
| `java -jar urna.jar administracao` | cadastro e apuração |
| `java -jar urna.jar demonstracao` | urna e administração juntas, com dados em memória, sem internet |

## Banco de dados

A urna usa o projeto Firebase `urna-13db0`, já configurado dentro do programa. As regras de
segurança do Firestore ficam em `banco/firestore.rules`: só aceitam dados válidos e nenhum voto
pode ser alterado ou apagado. Para usar outro projeto, crie um banco Cloud Firestore, publique
essas regras e coloque um arquivo `urna.propriedades` na pasta de onde a urna é aberta, com a
linha `URNA_FIREBASE_PROJETO=<código do projeto>`.

## Compilar e testar

```bash
mvnw.cmd verify
```

Compila, roda os testes JUnit 5, confere a cobertura mínima de 80% no modelo e no negócio e
gera `target/urna.jar`. Se o Maven falhar com `PKIX path building failed` (antivírus
inspecionando HTTPS), rode antes `$env:MAVEN_OPTS = "-Djavax.net.ssl.trustStoreType=Windows-ROOT"`.

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

## Limitações

- Sem login: quem tiver o programa consegue abrir a administração e mexer no cadastro.
  Ninguém consegue alterar ou apagar votos.
- Fora do escopo: identificação do eleitor, voto de legenda, segundo turno.
