# Roteiro manual da interface (seção 13.2)

A interface Swing não entra na automação; por isso a visão não decide nada, e o que ela
mostra é conferido por este roteiro. Os passos 2 a 7 rodam no modo de demonstração
(`java -jar target/urna.jar demonstracao`); os passos 1 e 8 a 10 precisam do Supabase.

| # | Passos | Resultado esperado | Resultado obtido |
|---|---|---|---|
| 1 | Abrir a urna sem internet (com `URNA_BANCO_*` configurado) | Pergunta se tenta de novo; em seguida, se usa dados de demonstração | |
| 2 | Deputado Federal: digitar 3105 | Nome Tiago Moura, partido PAU e a foto | |
| 3 | Presidente: digitar 99 | NÚMERO ERRADO e VOTO NULO | |
| 4 | Presidente: digitar 31 e apertar CORRIGE | Caixas limpas, cargo continua Presidente | |
| 5 | Digitar um dígito e apertar BRANCO | Nada acontece | |
| 6 | Apertar CONFIRMA com número incompleto | Nada acontece e nenhum som de confirmação | |
| 7 | Votar nos cinco cargos | Tela FIM, som longo, e cinco linhas novas em `voto` (na demonstração: aparecem na aba Apuração) | |
| 8 | Desligar a rede antes do último CONFIRMA | Mensagem de falha e pergunta de nova tentativa; ao religar e aceitar (ou apertar CONFIRMA na tela FIM), os votos entram | |
| 9 | Na role administrativa (`administracao`), conferir a apuração | Totais batem com o que foi votado | |
| 10 | Com a role `urna_votacao`, executar `select * from voto` | Permissão negada | |

Passos extras sugeridos:

| # | Passos | Resultado esperado |
|---|---|---|
| 11 | Senador: digitar 312 | Clara Duarte, PAU, linhas "1º Suplente: Paulo Seixas" e "2º Suplente: Irene Lobo" |
| 12 | Deputado Estadual: digitar 31999 | CANDIDATO INEXISTENTE e VOTO NULO (o partido 31 existe) |
| 13 | Usar o teclado do computador: dígitos, Enter e Backspace | Mesmo efeito das teclas da tela |
| 14 | Cadastrar candidato com foto de mais de 200 KB | Pergunta se reduz para 300 x 400 em JPEG |
| 15 | Excluir partido que tem candidatos | Mensagem de erro, partido continua na lista |
