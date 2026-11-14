-- =====================================================================
-- Urna Eletrônica - 01-esquema.sql
-- Cria as três tabelas (partido, candidato, voto) e as regras de negócio
-- que o banco garante sozinho. Rode no SQL Editor do Supabase, antes de
-- 02-seguranca.sql e 03-dados-ficticios.sql.
-- =====================================================================

-- Partido: número de 2 dígitos, único (RN03).
create table partido (
    identificador bigint generated always as identity primary key,
    numero        smallint     not null unique check (numero between 10 and 99),
    sigla         varchar(10)  not null unique,
    nome          varchar(120) not null
);

-- Candidato: tabela única para os cinco cargos; 'cargo' é o discriminador.
create table candidato (
    identificador          bigint generated always as identity primary key,
    cargo                  text         not null
        check (cargo in ('PRESIDENTE', 'GOVERNADOR', 'SENADOR', 'DEPUTADOFEDERAL', 'DEPUTADOESTADUAL')),
    numero                 varchar(5)   not null check (numero ~ '^[0-9]{2,5}$'),
    nome                   varchar(120) not null,
    -- RN02: todo candidato pertence a exatamente um partido.
    partido_identificador  bigint       not null references partido (identificador) on delete restrict,
    -- RN06: foto obrigatória, até 200 KB.
    foto                   bytea        not null check (octet_length(foto) between 1 and 204800),
    nome_vice              varchar(120),
    nome_primeiro_suplente varchar(120),
    nome_segundo_suplente  varchar(120),

    -- RN05: número único dentro do cargo.
    constraint candidato_numero_unico_no_cargo unique (cargo, numero),
    -- Necessária para a chave estrangeira composta de voto.
    constraint candidato_identificador_cargo_unico unique (identificador, cargo),
    -- RN01: quantidade de dígitos por cargo.
    constraint candidato_quantidade_digitos check (
        length(numero) = case cargo
            when 'PRESIDENTE'      then 2
            when 'GOVERNADOR'      then 2
            when 'SENADOR'         then 3
            when 'DEPUTADOFEDERAL' then 4
            else 5
        end),
    -- RN07: complemento por cargo.
    constraint candidato_complemento_por_cargo check (
        (cargo in ('PRESIDENTE', 'GOVERNADOR')
            and nome_vice is not null and nome_primeiro_suplente is null and nome_segundo_suplente is null)
        or (cargo = 'SENADOR'
            and nome_vice is null and nome_primeiro_suplente is not null and nome_segundo_suplente is not null)
        or (cargo in ('DEPUTADOFEDERAL', 'DEPUTADOESTADUAL')
            and nome_vice is null and nome_primeiro_suplente is null and nome_segundo_suplente is null))
);

-- Voto: sem eleitor e sem horário (RN12). O identificador é aleatório para
-- não revelar a ordem de chegada.
create table voto (
    identificador           uuid primary key default gen_random_uuid(),
    cargo                   text not null
        check (cargo in ('PRESIDENTE', 'GOVERNADOR', 'SENADOR', 'DEPUTADOFEDERAL', 'DEPUTADOESTADUAL')),
    tipo                    text not null check (tipo in ('CANDIDATO', 'BRANCO', 'NULO')),
    candidato_identificador bigint,

    -- O candidato votado tem de ser do mesmo cargo do voto.
    constraint voto_candidato_do_mesmo_cargo
        foreign key (candidato_identificador, cargo)
        references candidato (identificador, cargo) on delete restrict,
    -- Só voto de candidato aponta para candidato.
    constraint voto_tipo_e_candidato check ((tipo = 'CANDIDATO') = (candidato_identificador is not null))
);

create index voto_por_cargo_e_candidato on voto (cargo, candidato_identificador);

-- RN04: o número do candidato começa com o número do partido.
create function validar_prefixo_do_candidato() returns trigger
language plpgsql as $$
declare
    numero_do_partido smallint;
begin
    select numero into numero_do_partido from partido where identificador = new.partido_identificador;
    if left(new.numero, 2) <> numero_do_partido::text then
        raise exception 'O número do candidato (%) deve começar com o número do partido (%).',
            new.numero, numero_do_partido;
    end if;
    return new;
end;
$$;

create trigger candidato_prefixo_do_partido
    before insert or update on candidato
    for each row execute function validar_prefixo_do_candidato();

-- O número de um partido com candidatos não pode mudar (quebraria RN04).
create function impedir_troca_de_numero_do_partido() returns trigger
language plpgsql as $$
begin
    if new.numero <> old.numero
       and exists (select 1 from candidato where partido_identificador = old.identificador) then
        raise exception 'Não é possível trocar o número de um partido que já tem candidatos.';
    end if;
    return new;
end;
$$;

create trigger partido_numero_estavel
    before update on partido
    for each row execute function impedir_troca_de_numero_do_partido();
