# 0005. Próximo eleitor 3 segundos depois da gravação

Data: 2026-11-07
Status: aceita

## Contexto
Depois do FIM, a urna precisa ficar pronta para o próximo eleitor.

## Decisão
A tela FIM aparece assim que o último cargo é confirmado. O `ControladorVotacao` grava os
votos em segundo plano e, só depois de gravar, espera 3 segundos e abre a nova sessão.

Se a gravação falhar, os votos continuam na sessão, a urna pergunta se tenta de novo e a
tecla CONFIRMA na tela FIM também refaz a gravação. Fechar a janela com votos pendentes pede
confirmação.

## Consequências
- Nenhum voto é descartado por falha de rede.
- Enquanto o banco estiver fora do ar, a fila de eleitores para naquela urna.
