# Changelog

## [2.0.0] - 2026-11-14

### Alterado
- Estrutura simplificada para facilitar a apresentação: de 75 para 33 classes e de cerca de
  6.800 para 3.000 linhas, com as mesmas funcionalidades (decisão 0007).
- A sessão de votação passou a ser uma classe só, com um `enum` interno de situação, no lugar
  das classes do padrão State.
- Os serviços de negócio foram reunidos na `FachadaUrna`, e a persistência numa interface
  `RepositorioUrna` com implementações Supabase e memória.
- Janelas com os títulos "Urna" e "Urna Eletrônica - Administração".

### Corrigido
- O som da tecla não fica mais apitando sem parar quando o eleitor não confirma.

## [1.0.0] - 2026-11-08

### Adicionado
- Urna em Swing para os cinco cargos, com BRANCO, CORRIGE, CONFIRMA e sons.
- Módulo administrativo com cadastro de partidos e candidatos (com foto) e apuração.
- Gravação dos votos no Supabase via JDBC, numa transação, com duas roles de banco.
- Modo demonstração em memória, testes JUnit 5, diagramas UML e documentação técnica.
