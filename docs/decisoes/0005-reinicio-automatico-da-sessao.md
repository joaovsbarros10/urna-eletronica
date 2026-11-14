# 0005. Reinício automático 3 segundos depois da gravação

Data: 2026-11-07
Status: aceita (conferir no simulador do TSE)

## Contexto
Depois do FIM, a urna precisa ficar pronta para o próximo eleitor. Não foi possível conferir
no simulador do TSE se ele reinicia sozinho ou espera um clique.

## Decisão
A tela FIM aparece assim que o último cargo é confirmado (o eleitor não espera o banco). O
controlador grava em segundo plano e, **só depois de gravar com sucesso**, espera 3 segundos
e abre a nova sessão. O tempo é a constante `ControladorVotacao.ESPERAPADRAOEMMILISSEGUNDOS`
e pode ser trocado com `definirEsperaAntesDeReiniciar`.

Se a gravação falhar, a sessão não reinicia: os votos continuam em `sessaoAtual` (RNF09), a
urna pergunta se tenta de novo e, se a resposta for não, CONFIRMA na tela FIM refaz a
gravação. Fechar a janela com votos pendentes pede confirmação.

## Consequências
- Nenhum voto é descartado por falha de rede.
- Enquanto o banco estiver fora, a fila de eleitores para naquela urna.

## Alternativas consideradas
Guardar sessões pendentes numa fila e liberar o próximo eleitor: mais complexo e com risco
de perder votos se a máquina for desligada.
