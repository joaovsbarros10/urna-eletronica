# 0001. Foto do candidato em coluna bytea

Data: 2026-11-07
Status: aceita

## Contexto
O projeto é só Java, com JDBC. O cadastro de candidato precisa de foto (RN06).

## Decisão
Guardar a foto em coluna `bytea`, com limite de 200 KB conferido em três lugares: no
`PainelCadastroCandidato` (que oferece reduzir a foto para 300 x 400 em JPEG), no
`ServicoCadastro` (tamanho e assinatura JPEG/PNG) e na restrição `check` do banco.

## Consequências
- Sem API REST nem chave de Storage no aplicativo.
- A abertura da urna baixa todas as fotos (cerca de 10 MB com 100 candidatos).
- O módulo administrativo relê o catálogo a cada troca de aba, o que também baixa as fotos.

## Alternativas consideradas
Supabase Storage com endereço da foto na tabela: melhor em escala, mais peças móveis
(chave de API, HTTP). Fica como evolução se o cadastro passar de uns 100 candidatos.
