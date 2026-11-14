-- =====================================================================
-- Urna Eletrônica - 03-dados-ficticios.sql
-- Partidos, candidatos e fotos inventados, com números que obedecem à
-- regra de prefixo (RN04). Nada aqui se refere a político real.
-- A foto é um PNG de 1 pixel só para satisfazer o campo obrigatório (RN06);
-- troque pelas fotos reais no módulo administrativo.
-- =====================================================================

insert into partido (numero, sigla, nome) values
    (31, 'PAU', 'Partido Aurora'),
    (47, 'PCV', 'Partido Cerrado Verde');

insert into candidato (cargo, numero, nome, partido_identificador, foto,
                       nome_vice, nome_primeiro_suplente, nome_segundo_suplente)
select v.cargo, v.numero, v.nome, p.identificador, f.foto_padrao,
       v.nome_vice, v.suplente_um, v.suplente_dois
from (values
    ('PRESIDENTE',       '31',    'Helena Marques',  31, 'Otávio Rangel', null,           null),
    ('PRESIDENTE',       '47',    'Rui Albuquerque', 47, 'Sônia Tavares', null,           null),
    ('GOVERNADOR',       '31',    'Marcos Vilela',   31, 'Beatriz Nunes', null,           null),
    ('GOVERNADOR',       '47',    'Elisa Prado',     47, 'Hugo Matos',    null,           null),
    ('SENADOR',          '312',   'Clara Duarte',    31, null,            'Paulo Seixas', 'Irene Lobo'),
    ('SENADOR',          '475',   'Davi Sampaio',    47, null,            'Nair Costa',   'Jorge Leal'),
    ('DEPUTADOFEDERAL',  '3105',  'Tiago Moura',     31, null,            null,           null),
    ('DEPUTADOFEDERAL',  '4721',  'Lívia Castro',    47, null,            null,           null),
    ('DEPUTADOESTADUAL', '31042', 'Renata Bastos',   31, null,            null,           null),
    ('DEPUTADOESTADUAL', '47388', 'Caio Pimentel',   47, null,            null,           null)
) as v (cargo, numero, nome, numero_do_partido, nome_vice, suplente_um, suplente_dois)
join partido p on p.numero = v.numero_do_partido
cross join (
    select decode('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==', 'base64') as foto_padrao
) f;
