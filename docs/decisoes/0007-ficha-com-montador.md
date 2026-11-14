# 0007. FichaCandidato preenchida por um Montador

Data: 2026-11-07
Status: aceita

## Contexto
A `FichaCandidato` existe para que `Candidato` não tenha um construtor de nove argumentos
(seção 6.2). Mas a própria ficha precisaria desse construtor. Além disso, o cadastro e os
repositórios precisam transformar um candidato existente de volta em ficha, sem olhar a
subclasse (vice e suplentes só existem em algumas).

## Decisão
- A ficha é imutável e é preenchida por `FichaCandidato.Montador`, com métodos
  `definirNome`, `definirNumero` etc. (nomes com verbo, item 3.4).
- `Candidato.gerarFicha()` monta a ficha com os dados comuns e chama o gancho protegido
  `completarFicha`, que `CandidatoComVice` e `CandidatoSenador` sobrescrevem.
- `Cargo` informa `possuiVice()` e `possuiSuplentes()`, para a tela habilitar os campos sem
  `switch` por cargo.

## Consequências
- Nenhum `instanceof` para ler vice ou suplentes.
- A ficha gerada por um deputado nunca leva vice nem suplentes, o que respeita a restrição
  `candidato_complemento_por_cargo` do banco.

## Alternativas consideradas
Construtor de nove argumentos na ficha: mais curto, mas ilegível e propenso a trocar a ordem
dos nomes de vice e suplentes.
