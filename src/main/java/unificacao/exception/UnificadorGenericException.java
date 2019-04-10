package unificacao.exception;

import unificacao.AbstractOrquestradorUnificadorDadosJDBC;

/**
 * Exceção genérica do módulo de unificação de dados usando JPA, sob o controle da classe {@link AbstractOrquestradorUnificadorDadosJDBC}.
 *
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public class UnificadorGenericException extends RuntimeException {

    public UnificadorGenericException(String message) {
        super(message);
    }

    public UnificadorGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}
