# 0004. Apuração feita em Java

Data: 2026-11-07
Status: aceita

## Contexto
A apuração soma votos por candidato, brancos e nulos. O banco faria isso com um `count`
agrupado, mas o projeto precisa mostrar polimorfismo em um ponto real.

## Decisão
`FachadaUrna.apurar` lê os votos do cargo e chama `voto.contabilizarEm(apuracao)`. Cada
subclasse de `Voto` chama o contador certo de `ApuracaoCargo`, sem `instanceof`.

## Consequências
- A apuração lê todos os votos do cargo pela rede; no volume de um simulador isso é desprezível.
- Candidatos sem voto aparecem com zero.

## Alternativas consideradas
`select candidato_identificador, tipo, count(*) from voto where cargo = ? group by 1, 2`,
se o volume crescer.
