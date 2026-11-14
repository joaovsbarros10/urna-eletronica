# 0001. Foto do candidato em coluna bytea

Data: 2026-11-07
Status: aceita

## Contexto
O projeto é só Java, com JDBC. O cadastro de candidato precisa de foto.

## Decisão
Guardar a foto na coluna `bytea` da tabela `candidato`, com limite de 200 KB conferido no
cadastro (`PainelCadastroCandidato`), na `FachadaUrna` (tamanho e formato JPEG ou PNG) e na
restrição `check` do banco.

## Consequências
- Sem API REST nem chave de Storage no aplicativo.
- A abertura da urna baixa todas as fotos de uma vez.

## Alternativas consideradas
Supabase Storage com o endereço da foto na tabela: melhor se houver muitos candidatos, mas
exige chave de API e HTTP.
