/*
 * Urna Eletrônica
 * Arquivo: Candidato.java
 * Autor: João
 */

package urna.modelo;

import java.util.List;

/**
 * Candidato a um cargo. Guarda os dados comuns a todos os cargos e confere o
 * número: quantidade de dígitos do cargo e prefixo do partido.
 * <p>
 * Cada tipo de candidato é uma subclasse: com vice (Presidente e Governador),
 * senador (dois suplentes) e deputado (sem complemento). Use {@link #criar} para
 * obter a subclasse certa a partir do cargo.
 *
 * @author João
 */
public abstract class Candidato {

    private final long identificador;

    private final Cargo cargo;

    private final String numero;

    private final String nome;

    private final Partido partido;

    private final byte[] foto;

    /**
     * Cria o candidato e valida os dados comuns.
     *
     * @param identificador identificador no banco, ou 0 se ainda não foi gravado
     * @param cargo         cargo disputado
     * @param numero        número com os dígitos do cargo, começando pelo número do partido
     * @param nome          nome que aparece na urna
     * @param partido       partido do candidato
     * @param foto          bytes de uma imagem JPEG ou PNG
     * @throws IllegalArgumentException se algum dado for inválido
     */
    protected Candidato(long identificador, Cargo cargo, String numero, String nome, Partido partido, byte[] foto) {
        if (cargo == null || partido == null) {
            throw new IllegalArgumentException("Informe o cargo e o partido do candidato.");
        }
        if (foto == null || foto.length == 0) {
            throw new IllegalArgumentException("A foto do candidato é obrigatória.");
        }
        this.identificador = identificador;
        this.cargo = cargo;
        this.numero = exigirTexto(numero, "o número do candidato");
        this.nome = exigirTexto(nome, "o nome do candidato");
        this.partido = partido;
        this.foto = foto.clone();
        validarNumero();
    }

    /**
     * Cria o candidato da subclasse que corresponde ao cargo.
     *
     * @param identificador identificador no banco, ou 0 se ainda não foi gravado
     * @param cargo         cargo disputado
     * @param numero        número do candidato
     * @param nome          nome que aparece na urna
     * @param partido       partido do candidato
     * @param foto          bytes da foto
     * @param complementos  nome do vice, ou nomes dos dois suplentes, ou lista vazia
     * @return candidato com vice, senador ou deputado
     * @throws IllegalArgumentException se algum dado for inválido ou faltar complemento
     */
    public static Candidato criar(long identificador, Cargo cargo, String numero, String nome, Partido partido,
                                  byte[] foto, List<String> complementos) {
        if (cargo == null) {
            throw new IllegalArgumentException("Informe o cargo do candidato.");
        }
        if (cargo.possuiVice()) {
            return new CandidatoComVice(identificador, cargo, numero, nome, partido, foto, obter(complementos, 0));
        }
        if (cargo.possuiSuplentes()) {
            return new CandidatoSenador(identificador, numero, nome, partido, foto,
                    obter(complementos, 0), obter(complementos, 1));
        }
        return new CandidatoDeputado(identificador, cargo, numero, nome, partido, foto);
    }

    /**
     * Devolve um candidato igual a este com outro identificador, usado depois de gravar no banco.
     *
     * @param novoIdentificador identificador gerado na gravação
     * @return cópia do candidato
     */
    public Candidato copiarComIdentificador(long novoIdentificador) {
        return criar(novoIdentificador, cargo, numero, nome, partido, foto, getComplementos());
    }

    public long getIdentificador() {
        return identificador;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public String getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public Partido getPartido() {
        return partido;
    }

    /**
     * Devolve uma cópia da foto, para que ninguém altere a foto do candidato por fora.
     *
     * @return bytes da imagem
     */
    public byte[] getFoto() {
        return foto.clone();
    }

    /**
     * Devolve as linhas que o display mostra abaixo do partido, como "Vice-Presidente: nome".
     *
     * @return linhas do vice ou dos suplentes, ou lista vazia
     */
    public abstract List<String> obterLinhasAdicionais();

    /**
     * Devolve os nomes do complemento do cargo: o vice, ou os dois suplentes.
     *
     * @return nomes do complemento, ou lista vazia
     */
    public abstract List<String> getComplementos();

    private void validarNumero() {
        int quantidade = cargo.getQuantidadeDigitos();
        if (!numero.matches("[0-9]+") || numero.length() != quantidade) {
            throw new IllegalArgumentException("O número de " + cargo.getDescricao() + " deve ter "
                    + quantidade + " dígitos.");
        }
        if (!partido.correspondeAoPrefixo(numero)) {
            throw new IllegalArgumentException("O número do candidato deve começar com "
                    + partido.getNumero() + ", o número do partido.");
        }
    }

    /**
     * Confere um texto obrigatório e tira os espaços das pontas.
     *
     * @param valor     texto informado
     * @param descricao o que o texto representa, para a mensagem de erro
     * @return o texto sem espaços nas pontas
     * @throws IllegalArgumentException se o texto estiver vazio
     */
    protected static String exigirTexto(String valor, String descricao) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe " + descricao + ".");
        }
        return valor.strip();
    }

    private static String obter(List<String> complementos, int posicao) {
        return complementos != null && complementos.size() > posicao ? complementos.get(posicao) : null;
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Candidato candidato && candidato.identificador == identificador;
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
