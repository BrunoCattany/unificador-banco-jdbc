package unificacao.enums;

import unificacao.wrapper.ConnectionUnificacao;

/**
 * Classifica uma {@link ConnectionUnificacao}.
 *
 * @author Bruno Cattany
 * @since 02/04/2019
 */
public enum DirecaoConexao {

    /**
     * Conceitualmente dizendo, representa a conexão que servirá de base e que terá os dados unificados para o {@link #DESTINO}.
     */
    ORIGEM,
    /**
     * Conceitualmente dizendo, representa a conexão que será afetada pelos dados vindo da {@link #ORIGEM}.
     */
    DESTINO
}
