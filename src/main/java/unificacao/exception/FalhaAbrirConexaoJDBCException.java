package br.com.pactosolucoes.atualizadb.processo.unificacao.exception;

import br.com.pactosolucoes.atualizadb.processo.unificacao.enums.DirecaoConexao;

/**
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public class FalhaAbrirConexaoJDBCException extends UnificadorGenericException {

    private static final String MESSAGE = "Não foi possível abrir conexão com o banco de (%s), dado a url informada: %s";

    public FalhaAbrirConexaoJDBCException(DirecaoConexao direcaoConexao, String url, Throwable cause) {
        super(String.format(direcaoConexao.name(), MESSAGE, url), cause);
    }

}
