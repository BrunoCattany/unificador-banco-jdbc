package unificacao.exception;

import unificacao.Unificavel;
import unificacao.metadata.NomeColuna;

import java.lang.reflect.Field;

import static java.lang.String.format;

/**
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public class CampoSemNomeColunaException extends UnificadorGenericException {

    private static final String MESSAGE = "A classe %s � uma %s, por�m o campo %s n�o possui a anota��o %s";

    public CampoSemNomeColunaException(Class<? extends Unificavel> clazz, Field declaredField) {
        super(
                format(
                        MESSAGE,
                        clazz.getSimpleName(),
                        Unificavel.class.getSimpleName(),
                        declaredField.getName(),
                        NomeColuna.class.getSimpleName()
                )
        );
    }
}
