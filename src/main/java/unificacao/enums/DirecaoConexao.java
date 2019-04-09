package br.com.pactosolucoes.atualizadb.processo.unificacao.enums;

import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.ConnectionUnificacao;

/**
 * Classifica uma {@link ConnectionUnificacao}.
 *
 * @author Bruno Cattany
 * @since 02/04/2019
 */
public enum DirecaoConexao {

    /**
     * Conceitualmente dizendo, representa a conex�o que servir� de base e que ter� os dados unificados para o {@link #DESTINO}.
     */
    ORIGEM,
    /**
     * Conceitualmente dizendo, representa a conex�o que ser� afetada pelos dados vindo da {@link #ORIGEM}.
     */
    DESTINO
}
