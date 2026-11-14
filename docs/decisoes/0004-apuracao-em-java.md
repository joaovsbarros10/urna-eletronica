# 0004. Apuração feita em Java, com despacho duplo

Data: 2026-11-07
Status: aceita

## Contexto
A apuração (RF12) soma votos por candidato, brancos e nulos. O banco faria isso com um
`count` agrupado; o projeto precisa mostrar polimorfismo em pontos reais.

## Decisão
O `ServicoApuracao` lê os votos do cargo e chama `voto.contabilizarEm(apuracao)`; cada
subclasse de `Voto` chama o contador certo de `ApuracaoCargo`, sem `instanceof`. O único
`switch` sobre o tipo do voto fica na leitura do banco (`RepositorioVotoSupabase`), que
precisa decidir qual subclasse criar.

## Consequências
- A apuração transfere todas as linhas de voto do cargo pela rede. Com o volume de um
  simulador, é desprezível.
- O boletim inclui candidatos sem voto (aparecem com zero).

## Alternativas consideradas
`select candidato_identificador, tipo, count(*) from voto where cargo = ? group by 1, 2`:
é a otimização natural se o volume crescer.
