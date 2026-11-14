# Changelog

Todas as mudanças relevantes do projeto. O formato segue
[Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/).

## [1.0.0] - 2026-11-08

### Adicionado
- Urna de votação em Swing para os cinco cargos (Deputado Federal, Deputado Estadual,
  Senador, Governador e Presidente), com BRANCO, CORRIGE e CONFIRMA (RF01 a RF09).
- Sons de tecla, confirmação e fim gerados por código com `javax.sound.sampled` (RF10).
- Módulo administrativo com cadastro de partidos e candidatos, foto obrigatória e apuração
  por cargo (RF11 e RF12).
- Persistência no Supabase (PostgreSQL) via JDBC, com gravação atômica dos cinco votos (RN11)
  e duas roles de banco: `urna_votacao` (só insere votos) e `urna_administracao`.
- Repositórios em memória e modo `demonstracao`, que abre urna e administração juntas sem internet.
- Nova tentativa de gravação sem perder votos quando a rede falha (RNF09).
- Scripts `banco/01-esquema.sql`, `02-seguranca.sql` e `03-dados-ficticios.sql`.
- 95 testes JUnit 5, teste de integração opcional com o Supabase e meta de 80% de
  cobertura de linhas em `modelo` e `negocio` conferida pelo JaCoCo.
- Diagramas UML em Mermaid (`docs/uml/`), registros de decisão (`docs/decisoes/`),
  rastreabilidade do checklist e roteiro de testes manuais.
- JAR executável único (`target/urna.jar`) e Maven Wrapper (`mvnw`, `mvnw.cmd`).
