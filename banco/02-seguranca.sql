-- =====================================================================
-- Urna Eletrônica - 02-seguranca.sql
-- Liga RLS nas três tabelas, fecha a Data API (REST) e cria duas roles:
--   urna_votacao       lê o cadastro e INSERE votos (não lê, não altera, não apaga votos)
--   urna_administracao cadastro completo e leitura dos votos para a apuração
--
-- ATENÇÃO (RNF07): troque as duas senhas abaixo ANTES de rodar e NUNCA
-- versione este arquivo com a senha real. O repositório guarda só o modelo.
-- =====================================================================

-- Ninguém usa a Data API (REST) com estas tabelas: RLS ligado e sem política para anon e authenticated.
alter table partido   enable row level security;
alter table candidato enable row level security;
alter table voto      enable row level security;
revoke all on partido, candidato, voto from anon, authenticated;

-- Role da urna: lê o cadastro e INSERE votos. Não lê, não altera e não apaga votos (RN12, RNF08).
create role urna_votacao login password 'TROQUE-ESTA-SENHA';
grant usage on schema public to urna_votacao;
grant select on partido, candidato to urna_votacao;
grant insert on voto to urna_votacao;

create policy urna_le_partido   on partido   for select to urna_votacao using (true);
create policy urna_le_candidato on candidato for select to urna_votacao using (true);
create policy urna_insere_voto  on voto      for insert to urna_votacao with check (true);

-- Role administrativa: cadastro completo e leitura dos votos para a apuração. Não grava votos.
create role urna_administracao login password 'TROQUE-ESTA-SENHA';
grant usage on schema public to urna_administracao;
grant select, insert, update, delete on partido, candidato to urna_administracao;
grant select on voto to urna_administracao;

create policy admin_partido   on partido   for all    to urna_administracao using (true) with check (true);
create policy admin_candidato on candidato for all    to urna_administracao using (true) with check (true);
create policy admin_le_voto   on voto      for select to urna_administracao using (true);

-- Se o INSERT do administrador reclamar de permissão na sequência de identidade,
-- descomente as duas linhas abaixo (o nome da sequência pode variar; confira no seu projeto).
-- grant usage on sequence partido_identificador_seq   to urna_administracao;
-- grant usage on sequence candidato_identificador_seq to urna_administracao;
