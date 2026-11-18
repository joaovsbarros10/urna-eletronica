# 0007. Simplificação da estrutura (versão 2.0)

Data: 2026-11-14
Status: aceita

## Contexto
A versão 1.0 tinha 75 classes e cerca de 6.800 linhas. Atendia ao checklist, mas era grande
demais para ser explicada numa apresentação de 15 minutos.

## Decisão
Manter todas as funcionalidades (urna, cadastro, apuração, demonstração em memória e teclado
do computador) e todos os itens do checklist, reduzindo a estrutura:

| Antes | Depois |
|---|---|
| 8 classes de candidato, `FichaCandidato`, `Montador` e `FabricaCandidato` | `Candidato` e 3 subclasses; `Candidato.criar` escolhe a subclasse |
| 7 classes do padrão State para a sessão | `SessaoVotacao` com um `enum` interno de situação |
| 3 serviços e o catálogo no pacote `negocio` | só a `FachadaUrna` |
| 3 interfaces e 9 classes de persistência | `RepositorioUrna` com uma implementação no banco (hoje Firebase, decisão 0008) e uma em memória |
| 4 exceções | `UrnaException` |
| 4 classes de som | `ReprodutorSom` |
| 5 classes de tecla | `BotaoTecla` recebendo a ação |

## Consequências
- 33 classes e cerca de 3.000 linhas.
- O polimorfismo continua nas hierarquias de `Candidato`, `Voto` e `PainelAdministrativo`.
- A versão anterior está no commit `2704fc6` do Git.
