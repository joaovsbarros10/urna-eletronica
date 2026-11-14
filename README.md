# Urna Eletrônica em Java

Simulador de urna eletrônica **sem valor oficial**, para estudo. Aplicação desktop escrita
só em Java: interface em Swing, regras de negócio em camadas (MVC + Fachada) e votos
gravados no Supabase (PostgreSQL) via JDBC. Reproduz o fluxo de votação para cinco cargos
(Deputado Federal, Deputado Estadual, Senador, Governador e Presidente), com as teclas
BRANCO, CORRIGE e CONFIRMA e os sons de tecla, confirmação e fim.

Todos os partidos, candidatos e fotos são fictícios. O projeto completo (requisitos, UML,
checklist e roteiro) está no documento *Urna Eletrônica em Java — Projeto, UML e Documentação*.

## Experimente em 1 minuto (sem banco)

Requisito: Java 17 ou superior (recomendado 21 LTS).

```bash
./mvnw package
java -jar target/urna.jar demonstracao
```

No Windows, use `mvnw.cmd package`. O modo `demonstracao` abre a urna e o módulo
administrativo juntos, com dados fictícios em memória: vote na urna e veja o resultado na
aba **Apuração**. Números para testar: Deputado Federal `3105`, Deputado Estadual `31042`,
Senador `312`, Governador `31`, Presidente `47`. Número `99` (ou qualquer partido
inexistente) dá NÚMERO ERRADO; `3199` dá CANDIDATO INEXISTENTE.

Teclado do computador: dígitos, **Enter** = CONFIRMA, **Backspace** = CORRIGE.

![Urna com candidato encontrado](docs/telas/urna-candidato-encontrado.png)

Mais telas em `docs/telas/` (número errado, senador com suplentes, voto em branco, FIM,
cadastro de candidatos e apuração).

## Modos de abertura

No Windows, dê dois cliques em **`Abrir Urna.cmd`** ou **`Abrir Administracao.cmd`**, na
raiz do projeto. Eles usam o `urna.jar` e o `urna.propriedades` da mesma pasta. Depois de
recompilar (`mvnw.cmd package`), copie `target\urna.jar` para a raiz.

| Comando | Abre | Dados |
|---|---|---|
| `java -jar target/urna.jar` | urna de votação | Supabase, role `urna_votacao` |
| `java -jar target/urna.jar administracao` | cadastro e apuração | Supabase, role `urna_administracao` |
| `java -jar target/urna.jar demonstracao` | urna + administração | memória (fictícios) |

Sem configuração do banco, a urna e a administração avisam e abrem com os dados de
demonstração. Se o banco não responder na abertura, a urna pergunta se tenta de novo ou usa
a demonstração.

## Configurar o Supabase

1. Crie um projeto no [Supabase](https://supabase.com).
2. No **SQL Editor**, rode os scripts **nesta ordem**:
   1. `banco/01-esquema.sql` — tabelas, restrições e gatilhos (RN01 a RN07).
   2. `banco/02-seguranca.sql` — **antes de rodar, troque as duas senhas `TROQUE-ESTA-SENHA`**
      (e não salve a versão com senha no Git). Liga RLS e cria as roles
      `urna_votacao` e `urna_administracao`.
   3. `banco/03-dados-ficticios.sql` — dois partidos e dez candidatos fictícios.
3. No botão **Connect** do projeto, copie a string do **Session pooler** (porta 5432): ela
   funciona em redes só IPv4. O host tem o formato `aws-[INDEX]-[REGION].pooler.supabase.com`.
4. Configure as credenciais (RNF07: nunca no código) por **variáveis de ambiente** ou pelo
   arquivo local `urna.propriedades` (veja o modelo em
   `src/main/resources/urna.propriedades.exemplo`), na pasta de onde a urna é aberta ou na
   sua pasta de usuário. O arquivo está no `.gitignore`.

| Variável | Exemplo |
|---|---|
| `URNA_BANCO_ENDERECO` | `jdbc:postgresql://<host-do-pooler>:5432/postgres?sslmode=require` |
| `URNA_BANCO_USUARIO` | `urna_votacao.<referencia-do-projeto>` |
| `URNA_BANCO_SENHA` | senha da role `urna_votacao` |
| `URNA_ADMIN_BANCO_ENDERECO` | mesmo endereço |
| `URNA_ADMIN_BANCO_USUARIO` | `urna_administracao.<referencia-do-projeto>` |
| `URNA_ADMIN_BANCO_SENHA` | senha da role `urna_administracao` |

As variáveis de ambiente têm prioridade sobre o arquivo. A conexão sempre exige
`sslmode=require`.

## Compilar, testar e gerar o JAR

```bash
./mvnw verify            # compila, roda os testes, confere a cobertura e gera target/urna.jar
./mvnw javadoc:javadoc   # gera a documentação em target/reports/apidocs (aviso vira erro)
```

- **Testes de unidade** (JUnit 5): rodam sem tela, sem som e sem internet, com os
  repositórios em memória. Relatório de cobertura em `target/site/jacoco/index.html`; o build
  falha se `modelo` ou `negocio` ficarem abaixo de 80% de linhas cobertas.
- **Teste de integração** (`RepositorioVotoSupabaseIT`): só roda quando as variáveis
  `URNA_BANCO_*` e `URNA_ADMIN_BANCO_*` estão definidas. **Aponte para um projeto Supabase de
  teste**: o teste grava votos.
- **Interface**: conferida pelo roteiro manual em `docs/roteiro-testes-manuais.md`.

## Estrutura

```
urna/
├── pom.xml, mvnw, mvnw.cmd       build Maven (com wrapper)
├── README.md, CHANGELOG.md
├── banco/                        scripts SQL do Supabase, em ordem
├── docs/
│   ├── uml/                      diagramas em Mermaid (cole em mermaid.live)
│   ├── decisoes/                 registros de decisão de arquitetura
│   ├── checklist-inspecao.md     onde está a evidência de cada item 1.0 a 3.6
│   └── roteiro-testes-manuais.md
└── src/
    ├── main/java/br/com/urna/
    │   ├── Aplicacao.java        raiz de composição e main
    │   ├── modelo/               Cargo, Partido (núcleo: só depende do JDK)
    │   │   ├── candidato/        hierarquia de Candidato, FichaCandidato, FabricaCandidato
    │   │   ├── voto/             hierarquia de Voto e ApuracaoCargo
    │   │   └── sessao/           SessaoVotacao e os estados (padrão State)
    │   ├── negocio/              FachadaUrna (única classe pública) e serviços
    │   ├── persistencia/         interfaces de repositório e implementações em memória
    │   │   └── supabase/         implementações JDBC
    │   ├── controle/             ControladorVotacao, ControladorAdministracao
    │   ├── visao/                janelas, painéis e teclas Swing
    │   ├── som/                  efeitos sonoros
    │   └── excecao/              UrnaException e subclasses
    ├── main/resources/           urna.propriedades.exemplo
    └── test/java/br/com/urna/    testes JUnit 5
```

## Arquitetura em uma frase

A visão chama o controle, o controle chama a `FachadaUrna`, a Fachada chama os serviços, e o
modelo não conhece ninguém. Padrões usados: MVC, Fachada, Observer (sessão → tela), State
(sessão de votação), Template Method (`EstadoConfirmavel`), Factory (`FabricaCandidato`),
Repositório com duas implementações, Null Object (`ReprodutorSomMudo`) e despacho duplo
(`Voto.contabilizarEm`). Veja `docs/uml/` e `docs/decisoes/`.

## Limitações conhecidas (é um simulador)

- **Sigilo**: a tabela `voto` não tem eleitor nem horário e o identificador é aleatório, mas
  as cinco linhas de uma sessão entram juntas; em tese, quem tiver acesso ao banco pode
  ligá-las pela ordem física das linhas.
- **Senha da urna**: quem tiver a senha de `urna_votacao` consegue inserir votos falsos
  (não consegue ler nem apagar). Aceitável para estudo; numa eleição real exigiria um
  servidor com autenticação.
- **Fidelidade visual**: layout, textos do display, ordem dos cargos e sons vêm da memória
  da urna real, não foram conferidos no simulador do TSE. Ajuste em `Cargo`, `TemaVisual`,
  nas constantes de texto de `PainelDisplay` e em `EfeitoSonoro`.
- **Fora da versão 1**: identificação do eleitor, voto de legenda, segundo turno, várias UFs,
  assinatura digital e zerésima.

## Problemas comuns

- **`PKIX path building failed` ao rodar o Maven no Windows**: um antivírus ou proxy está
  inspecionando HTTPS. Use o repositório de certificados do Windows:
  `set MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT` (PowerShell:
  `$env:MAVEN_OPTS = "-Djavax.net.ssl.trustStoreType=Windows-ROOT"`).
- **Teclas sem cor no macOS**: a aplicação já força o visual Metal; se mudar isso, o
  `JButton` pode ignorar a cor de fundo.
- **Sem som**: em máquina sem saída de áudio, a urna usa o reprodutor mudo e continua funcionando.
