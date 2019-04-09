package br.com.pactosolucoes.atualizadb.processo.unificacao.exception;

/**
 * Lançada quando algum problema durante a transação ocorrer.
 *
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public class FalhaTransacaoException extends UnificadorGenericException {

    public FalhaTransacaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
