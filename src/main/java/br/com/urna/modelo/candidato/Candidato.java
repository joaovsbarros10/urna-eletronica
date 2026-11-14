/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: Candidato.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;
import java.util.List;

/**
 * Candidato a um cargo. Guarda os dados comuns a todos os cargos
 * (identificador, nome, número, partido e foto) e a regra do número:
 * quantidade de dígitos do cargo (RN01) e prefixo do partido (RN04).
 * <p>
 * Cada cargo tem uma subclasse, criada só pela {@link FabricaCandidato}. O que
 * muda por cargo (vice, suplentes e as linhas mostradas no display) fica nas
 * subclasses, e o resto do sistema trata todos como {@code Candidato}.
 * Os objetos são imutáveis e a igualdade é pelo identificador.
 *
 * @author João
 * @since 1.0
 */
public abstract class Candidato {

    private final long identificador;

    private final String nome;

    private final String numero;

    private final Partido partido;

    private final byte[] foto;

    /**
     * Cria um candidato a partir da ficha e valida o número.
     * <p>
     * O construtor chama {@link #validarNumero()}, que usa {@link #getCargo()},
     * método que a subclasse sobrescreve. Isso só é seguro porque toda
     * implementação de {@code getCargo()} devolve uma constante e não lê campo
     * da subclasse, que ainda não foi inicializado neste ponto. Mantenha assim.
     *
     * @param ficha dados do candidato
     * @throws IllegalArgumentException se faltar nome, número, partido ou foto, se a ficha
     *                                  for de outro cargo ou se o número violar RN01 ou RN04
     */
    protected Candidato(FichaCandidato ficha) {
        if (ficha == null) {
            throw new IllegalArgumentException("A ficha do candidato é obrigatória.");
        }
        if (ficha.getCargo() != getCargo()) {
            throw new IllegalArgumentException("A ficha é de " + ficha.getCargo().getDescricao()
                    + ", mas o candidato é de " + getCargo().getDescricao() + ".");
        }
        if (ficha.getPartido() == null) {
            throw new IllegalArgumentException("O candidato precisa de um partido (RN02).");
        }
        this.identificador = ficha.getIdentificador();
        this.nome = exigirTexto(ficha.getNome(), "o nome do candidato");
        this.numero = exigirTexto(ficha.getNumero(), "o número do candidato");
        this.partido = ficha.getPartido();
        this.foto = exigirFoto(ficha.getFoto());
        validarNumero();
    }

    // ---- Dados do candidato ----

    /**
     * Devolve o identificador gravado no banco.
     *
     * @return identificador, ou 0 para candidato ainda não gravado
     */
    public long getIdentificador() {
        return identificador;
    }

    public String getNome() {
        return nome;
    }

    public String getNumero() {
        return numero;
    }

    public Partido getPartido() {
        return partido;
    }

    /**
     * Devolve uma cópia da foto, para que quem a recebe não altere a do candidato.
     *
     * @return bytes da imagem JPEG ou PNG, nunca vazios
     */
    public byte[] getFoto() {
        return foto.clone();
    }

    // ---- O que muda por cargo ----

    /**
     * Informa o cargo do candidato. Toda implementação deve devolver uma constante,
     * porque o método é chamado pelo construtor desta classe.
     *
     * @return cargo do candidato
     */
    public abstract Cargo getCargo();

    /**
     * Devolve as linhas que o display mostra abaixo do partido: a do vice, as dos
     * suplentes ou nenhuma (RN07). A tela desenha o que vier, sem saber o cargo.
     *
     * @return lista imutável de linhas prontas para exibir
     */
    public abstract List<String> obterLinhasAdicionais();

    // ---- Regra do número (RN01 e RN04) ----

    /**
     * Confere o número do candidato: só dígitos, a quantidade de dígitos do cargo
     * (RN01) e o número do partido como prefixo (RN04).
     *
     * @throws IllegalArgumentException se o número violar alguma das regras
     */
    public final void validarNumero() {
        int quantidadeEsperada = getCargo().getQuantidadeDigitos();
        boolean somenteDigitos = numero.chars().allMatch(caractere -> caractere >= '0' && caractere <= '9');
        if (!somenteDigitos || numero.length() != quantidadeEsperada) {
            throw new IllegalArgumentException("O número de candidato a " + getCargo().getDescricao()
                    + " deve ter " + quantidadeEsperada + " dígitos (RN01): " + numero);
        }
        if (!partido.correspondeAoPrefixo(numero)) {
            throw new IllegalArgumentException("O número do candidato (" + numero
                    + ") deve começar com o número do partido (" + partido.getNumero() + ") (RN04).");
        }
    }

    // ---- Ficha ----

    /**
     * Gera a ficha com todos os dados deste candidato, inclusive vice ou suplentes.
     * O cadastro usa a ficha para editar o candidato e os repositórios para gravá-lo.
     *
     * @return ficha com os dados atuais
     */
    public FichaCandidato gerarFicha() {
        FichaCandidato.Montador montador = new FichaCandidato.Montador(getCargo())
                .definirIdentificador(identificador)
                .definirNome(nome)
                .definirNumero(numero)
                .definirPartido(partido)
                .definirFoto(foto);
        completarFicha(montador);
        return montador.montar();
    }

    /**
     * Acrescenta à ficha os dados próprios do cargo. O padrão é não acrescentar
     * nada; candidatos com vice ou suplentes sobrescrevem.
     *
     * @param montador montador já preenchido com os dados comuns
     */
    protected void completarFicha(FichaCandidato.Montador montador) {
    }

    // ---- Validação comum às subclasses ----

    /**
     * Devolve o texto sem espaços nas pontas, ou recusa texto vazio.
     *
     * @param valor     texto informado
     * @param descricao o que o texto representa, para a mensagem de erro
     * @return texto sem espaços nas pontas
     * @throws IllegalArgumentException se o texto for {@code null} ou vazio
     */
    protected static String exigirTexto(String valor, String descricao) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe " + descricao + ".");
        }
        return valor.strip();
    }

    private static byte[] exigirFoto(byte[] foto) {
        if (foto == null || foto.length == 0) {
            throw new IllegalArgumentException("A foto do candidato é obrigatória (RN06).");
        }
        return foto;
    }

    // ---- Identidade do objeto ----

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Candidato)) {
            return false;
        }
        return identificador == ((Candidato) outro).identificador;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(identificador);
    }

    @Override
    public String toString() {
        return numero + " - " + nome + " (" + partido.getSigla() + ")";
    }
}
