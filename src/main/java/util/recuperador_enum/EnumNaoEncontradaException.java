package util.recuperador_enum;

/**
 * Exce��o especializada para quando n�o encontrar uma enum a partir de um m�todo de busca.
 *
 * @author Bruno Cattany
 * @since 06/12/2018
 */
public class EnumNaoEncontradaException extends RuntimeException {

    private static final String ERROR_MESSAGE = "N�o foi poss�vel recuperar a enum {%s} por {%s}, dado o valor: {%s}";
    private final static String MENSAGEM_ERROR_ARRAY_VAZIO = "Nenhum valor foi passado para o m�todo.";

    EnumNaoEncontradaException(Class<? extends ValorRecuperavel> clazz, String valorInformado, CampoRecuperavelEnum campoRecuperavelEnum) {
        super(String.format(ERROR_MESSAGE, clazz, campoRecuperavelEnum, valorInformado));
    }

    public EnumNaoEncontradaException(Class<? extends Enum> clazz, CampoRecuperavelEnum campoRecuperavelEnum, String valorInformado) {
        super(String.format(ERROR_MESSAGE, clazz, campoRecuperavelEnum, valorInformado));
    }

    /**
     * Retorna a exception imprimindo {@link #MENSAGEM_ERROR_ARRAY_VAZIO}.
     */
    EnumNaoEncontradaException() {
        super(MENSAGEM_ERROR_ARRAY_VAZIO);
    }

    /**
     * @param nomeValorBusca representa uma alternativa ao valores contidos em {@link CampoRecuperavelEnum}, ou seja,
     *                       permite especificar atrav�s de qual campo que houve a falha na busca.
     */
    public EnumNaoEncontradaException(Class<? extends ValorRecuperavel> clazz, String nomeValorBusca, Object valorInformado) {
        super(String.format(ERROR_MESSAGE, clazz, nomeValorBusca, valorInformado));
    }

}
