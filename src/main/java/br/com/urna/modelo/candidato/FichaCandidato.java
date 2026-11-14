/*
 * Urna Eletrônica - projeto acadêmico de simulação de votação.
 * Arquivo: FichaCandidato.java
 * Autor: João
 * Criado em: 2026-11-07
 */

package br.com.urna.modelo.candidato;

import br.com.urna.modelo.Cargo;
import br.com.urna.modelo.Partido;

/**
 * Reúne os dados para criar um candidato, sem validá-los.
 * <p>
 * A ficha existe para que {@link Candidato} não precise de um construtor com
 * nove argumentos: quem cria candidatos (o cadastro, os repositórios e os
 * testes) preenche uma ficha com o {@link Montador} e entrega à
 * {@link FabricaCandidato}, que escolhe a subclasse e aplica as regras.
 * A ficha é imutável; a foto é copiada na entrada e na saída.
 *
 * @author João
 * @since 1.0
 */
public final class FichaCandidato {

    private final Cargo cargo;

    private final long identificador;

    private final String nome;

    private final String numero;

    private final Partido partido;

    private final byte[] foto;

    private final String nomeVice;

    private final String nomePrimeiroSuplente;

    private final String nomeSegundoSuplente;

    private FichaCandidato(Montador montador) {
        this.cargo = montador.cargo;
        this.identificador = montador.identificador;
        this.nome = montador.nome;
        this.numero = montador.numero;
        this.partido = montador.partido;
        this.foto = copiar(montador.foto);
        this.nomeVice = montador.nomeVice;
        this.nomePrimeiroSuplente = montador.nomePrimeiroSuplente;
        this.nomeSegundoSuplente = montador.nomeSegundoSuplente;
    }

    // ---- Dados da ficha ----

    public Cargo getCargo() {
        return cargo;
    }

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
     * Devolve uma cópia da foto, para que quem a recebe não altere a ficha.
     *
     * @return bytes da imagem JPEG ou PNG, ou {@code null} se a ficha está sem foto
     */
    public byte[] getFoto() {
        return copiar(foto);
    }

    /**
     * Devolve o nome do vice, preenchido só para Presidente e Governador (RN07).
     *
     * @return nome do vice, ou {@code null}
     */
    public String getNomeVice() {
        return nomeVice;
    }

    /**
     * Devolve o nome do 1º suplente, preenchido só para Senador (RN07).
     *
     * @return nome do 1º suplente, ou {@code null}
     */
    public String getNomePrimeiroSuplente() {
        return nomePrimeiroSuplente;
    }

    /**
     * Devolve o nome do 2º suplente, preenchido só para Senador (RN07).
     *
     * @return nome do 2º suplente, ou {@code null}
     */
    public String getNomeSegundoSuplente() {
        return nomeSegundoSuplente;
    }

    // ---- Cópia ----

    /**
     * Cria uma ficha igual a esta, com outro identificador. Os repositórios usam
     * este método para devolver o candidato com o identificador gerado na gravação.
     *
     * @param novoIdentificador identificador da cópia
     * @return nova ficha com os mesmos dados e o identificador informado
     */
    public FichaCandidato copiarComIdentificador(long novoIdentificador) {
        return new Montador(this).definirIdentificador(novoIdentificador).montar();
    }

    private static byte[] copiar(byte[] bytes) {
        return bytes == null ? null : bytes.clone();
    }

    /**
     * Preenche uma {@link FichaCandidato} campo a campo.
     * <p>
     * Exemplo:
     * <pre>{@code
     * FichaCandidato ficha = new FichaCandidato.Montador(Cargo.SENADOR)
     *         .definirNome("Clara Duarte")
     *         .definirNumero("312")
     *         .definirPartido(partido)
     *         .definirFoto(foto)
     *         .definirNomesDosSuplentes("Paulo Seixas", "Irene Lobo")
     *         .montar();
     * }</pre>
     *
     * @since 1.0
     */
    public static final class Montador {

        private final Cargo cargo;

        private long identificador;

        private String nome;

        private String numero;

        private Partido partido;

        private byte[] foto;

        private String nomeVice;

        private String nomePrimeiroSuplente;

        private String nomeSegundoSuplente;

        /**
         * Começa uma ficha para o cargo informado.
         *
         * @param cargo cargo do candidato; não pode ser {@code null}
         * @throws IllegalArgumentException se o cargo for {@code null}
         */
        public Montador(Cargo cargo) {
            if (cargo == null) {
                throw new IllegalArgumentException("O cargo do candidato é obrigatório.");
            }
            this.cargo = cargo;
        }

        private Montador(FichaCandidato ficha) {
            this(ficha.cargo);
            this.identificador = ficha.identificador;
            this.nome = ficha.nome;
            this.numero = ficha.numero;
            this.partido = ficha.partido;
            this.foto = ficha.foto;
            this.nomeVice = ficha.nomeVice;
            this.nomePrimeiroSuplente = ficha.nomePrimeiroSuplente;
            this.nomeSegundoSuplente = ficha.nomeSegundoSuplente;
        }

        // ---- Preenchimento ----

        /**
         * Define o identificador gravado no banco.
         *
         * @param identificador identificador, ou 0 para candidato novo
         * @return este montador
         */
        public Montador definirIdentificador(long identificador) {
            this.identificador = identificador;
            return this;
        }

        /**
         * Define o nome do candidato como aparece na urna.
         *
         * @param nome nome do candidato
         * @return este montador
         */
        public Montador definirNome(String nome) {
            this.nome = nome;
            return this;
        }

        /**
         * Define o número do candidato, só com dígitos e com o prefixo do partido (RN04).
         *
         * @param numero número do candidato
         * @return este montador
         */
        public Montador definirNumero(String numero) {
            this.numero = numero;
            return this;
        }

        /**
         * Define o partido do candidato (RN02).
         *
         * @param partido partido do candidato
         * @return este montador
         */
        public Montador definirPartido(Partido partido) {
            this.partido = partido;
            return this;
        }

        /**
         * Define a foto do candidato; os bytes são copiados (RN06).
         *
         * @param foto bytes de uma imagem JPEG ou PNG
         * @return este montador
         */
        public Montador definirFoto(byte[] foto) {
            this.foto = copiar(foto);
            return this;
        }

        /**
         * Define o nome do vice; só é usado para Presidente e Governador (RN07).
         *
         * @param nomeVice nome do vice
         * @return este montador
         */
        public Montador definirNomeVice(String nomeVice) {
            this.nomeVice = nomeVice;
            return this;
        }

        /**
         * Define os nomes dos suplentes; só são usados para Senador (RN07).
         *
         * @param nomePrimeiroSuplente nome do 1º suplente
         * @param nomeSegundoSuplente  nome do 2º suplente
         * @return este montador
         */
        public Montador definirNomesDosSuplentes(String nomePrimeiroSuplente, String nomeSegundoSuplente) {
            this.nomePrimeiroSuplente = nomePrimeiroSuplente;
            this.nomeSegundoSuplente = nomeSegundoSuplente;
            return this;
        }

        // ---- Conclusão ----

        /**
         * Cria a ficha com os dados preenchidos até aqui.
         *
         * @return ficha imutável
         */
        public FichaCandidato montar() {
            return new FichaCandidato(this);
        }
    }
}
