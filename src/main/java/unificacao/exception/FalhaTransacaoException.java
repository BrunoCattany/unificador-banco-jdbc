package br.com.pactosolucoes.atualizadb.processo.unificacao.exception;

/**
 * Lan�ada quando algum problema durante a transa��o ocorrer.
 *
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public class FalhaTransacaoException extends UnificadorGenericException {

    public FalhaTransacaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
