# 0002. Padrão State na sessão de votação

Data: 2026-11-07
Status: aceita

## Contexto
As quatro teclas de ação (dígito, BRANCO, CORRIGE, CONFIRMA) fazem coisas diferentes
conforme a situação do cargo: digitando, candidato encontrado, número inexistente, voto em
branco ou fim. Com `if` e `switch`, essa tabela ficaria espalhada pelo controlador e pela sessão.

## Decisão
Cada situação é uma classe que implementa `EstadoSessao`. A `SessaoVotacao` só repassa a
tecla ao estado atual. Os três estados em que CONFIRMA registra voto herdam de
`EstadoConfirmavel` (Template Method) e só dizem qual voto criar.

Os estados têm acesso de pacote (`modelo.sessao`): só eles chamam os métodos de transição
da sessão, que também têm acesso de pacote.

## Consequências
- A tabela 5.6.2 do projeto está escrita uma vez, nas classes de estado.
- Seis tipos a mais no projeto, todos pequenos.
- `SessaoVotacaoTest` testa o fluxo inteiro sem tela, sem banco e sem som.

## Alternativas consideradas
Um `enum` de estado com `switch` em cada tecla: menos classes, mas o comportamento de uma
situação fica dividido em quatro métodos diferentes.
