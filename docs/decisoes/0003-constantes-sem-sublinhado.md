# 0003. Constantes em maiúsculas coladas, sem sublinhado

Data: 2026-11-07
Status: aceita (pendente de confirmação do avaliador)

## Contexto
O item 3.0 do checklist diz que identificadores usam "somente letras e dígitos". A
convenção Java para constantes usa sublinhado (`DEPUTADO_FEDERAL`).

## Decisão
Seguir o texto do checklist ao pé da letra: `DEPUTADOFEDERAL`, `TAXAAMOSTRAGEM`,
`QUANTIDADEDIGITOS`. Os valores das constantes (textos, nomes de variáveis de ambiente
como `URNA_BANCO_ENDERECO`) podem ter sublinhado, porque não são identificadores Java.

## Consequências
- O código fica fora da convenção Java mais comum; a leitura de nomes longos piora.
- O nome da constante de enum é gravado no banco (`Cargo.name()`), então trocar para
  `DEPUTADO_FEDERAL` exige atualizar também `banco/firestore.rules` e os dados.

## Alternativas consideradas
`UPPER_SNAKE_CASE`, se o avaliador aceitar. A troca é mecânica: renomear as constantes e
ajustar as listas de cargos das regras do Firestore.
