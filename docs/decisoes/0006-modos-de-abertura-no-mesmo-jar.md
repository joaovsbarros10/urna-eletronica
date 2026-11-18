# 0006. Urna, administração e demonstração no mesmo JAR

Data: 2026-11-07
Status: aceita

## Contexto
A urna de votação e o módulo administrativo precisam de permissões diferentes no banco
(roles `urna_votacao` e `urna_administracao`). Também é útil demonstrar a urna sem internet.

## Decisão
Um único JAR, com o modo escolhido pelo argumento:

| Argumento | Abre | Dados |
|---|---|---|
| (nenhum) | urna de votação | Firebase, login opcional `URNA_BANCO_*` |
| `administracao` | módulo administrativo | Firebase, login opcional `URNA_ADMIN_BANCO_*` |
| `demonstracao` | urna e administração juntas | memória, dados fictícios |

Sem configuração do banco, a urna e a administração avisam e abrem com os dados de
demonstração. Se o banco não responder na abertura da urna, ela pergunta se tenta de novo
ou usa a demonstração (seção 9.2).

## Consequências
- Um só artefato para distribuir.
- No modo `demonstracao`, urna e administração compartilham a mesma `FachadaUrna` em
  memória: o voto dado aparece na apuração na hora.

## Alternativas consideradas
Dois JARs separados: separa melhor as permissões, mas duplica o build e a distribuição.
