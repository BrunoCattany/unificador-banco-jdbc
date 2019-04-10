package unificacao.wrapper;

/**
 * @author Bruno Cattany
 * @since 07/04/2019
 */
class CodigoUnificacaoDestino extends CodigoUnificacao {

    CodigoUnificacaoDestino(Integer value) {
        super(value);
    }

    public static CodigoUnificacaoDestino as(Integer value) {
        return new CodigoUnificacaoDestino(value);
    }
}