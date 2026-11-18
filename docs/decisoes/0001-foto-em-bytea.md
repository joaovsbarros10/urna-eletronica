# 0001. Foto do candidato dentro do documento

Data: 2026-11-07
Status: aceita

## Contexto
O cadastro de candidato precisa de foto. Na versão com Supabase a foto ficava numa coluna
`bytea`; com o Firestore (decisão 0008) ela passou a ser um campo `bytes` do documento.

## Decisão
Guardar a foto no campo `foto` do documento do candidato, com limite de 200 KB conferido no
cadastro (`PainelCadastroCandidato`), na `FachadaUrna` (tamanho e formato JPEG ou PNG) e nas
regras do Firestore. O documento do Firestore aceita até 1 MB, então a foto cabe com folga.

## Consequências
- Um serviço só (Firestore), sem Firebase Storage nem segundo endereço por candidato.
- A abertura da urna baixa todas as fotos de uma vez.

## Alternativas consideradas
Firebase Storage com o endereço da foto no documento: melhor se houver muitos candidatos,
mas exige outro serviço e outra regra de segurança.
