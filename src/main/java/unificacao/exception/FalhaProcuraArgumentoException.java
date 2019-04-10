package unificacao.exception;

import unificacao.parametrizacao.ParametroObrigatorioUnificacaoEnum;

import java.util.Arrays;

/**
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public class FalhaProcuraArgumentoException extends UnificadorGenericException {

    private static final String MESSAGE = "Se espera o argumento (%s) mas não foi possível encontrar nos argumentos passados: \n(%s)";

    public FalhaProcuraArgumentoException(ParametroObrigatorioUnificacaoEnum unificadorArgsMissing, String... args) {
        super(String.format(MESSAGE, unificadorArgsMissing, Arrays.toString(args)));
    }
}
