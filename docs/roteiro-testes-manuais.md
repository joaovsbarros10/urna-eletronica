# Roteiro de testes manuais da interface

Os passos 2 a 7 também funcionam no modo demonstração (`java -jar urna.jar demonstracao`).

| # | Passos | Resultado esperado |
|---|---|---|
| 1 | Abrir a urna sem internet | Pergunta se tenta de novo e depois se usa os dados de demonstração |
| 2 | Deputado Federal: digitar 3105 | Tiago Moura, partido PAU e a foto |
| 3 | Presidente: digitar 99 | NÚMERO ERRADO e VOTO NULO |
| 4 | Presidente: digitar 31 e apertar CORRIGE | Caixas limpas, continua em Presidente |
| 5 | Digitar um dígito e apertar BRANCO | Nada acontece |
| 6 | Apertar CONFIRMA com número incompleto | Nada acontece e nenhum som de confirmação |
| 7 | Votar nos cinco cargos | Tela FIM, som longo e os votos aparecem na aba Apuração |
| 8 | Desligar a internet antes do último CONFIRMA | Mensagem de falha e pergunta de nova tentativa; ao religar, os votos entram |
| 9 | Senador: digitar 312 | Clara Duarte com os dois suplentes |
| 10 | Usar o teclado do computador | Dígitos, Enter e Backspace funcionam como as teclas da tela |
| 11 | Cadastrar candidato com foto maior que 200 KB | Mensagem pedindo foto menor |
| 12 | Excluir partido que tem candidatos | Mensagem de erro; o partido continua na lista |
