package util.recuperador_enum;

import org.apache.commons.lang3.StringUtils;

/**
 * Implementações comuns que visa encapsular a lógica para se recuperar um {@link CampoRecuperavelEnum#VALOR} de um {@link ValorRecuperavel}.
 *
 * @author Bruno Cattany
 * @since 06/12/2018
 */
public final class RecuperadorEnumPadrao {

    /**
     * Implementação padrão de {@link DescricaoRecuperavel#getDescricaoMaiscula()} ()}}.
     */
    public static <T extends Enum & DescricaoRecuperavel> String getDescricaoMaiscula(T enumInstance) {
        return enumInstance.getDescricao().toUpperCase();
    }

    /**
     * Implementação padrão de {@link DescricaoRecuperavel#getValorComDescricao()}}.
     *
     * <p>
     * Exemplo:
     * <ul>
     * <li><b>Código: 5</b></li>
     * <li><b>Descrição: Pacto</b></li>
     * </ul>
     *
     * <h3>Resultado:</h3>
     * <h2>5 - Pacto</h2>
     * </p>
     */
    public static <T extends Enum & ValorRecuperavel & DescricaoRecuperavel> String retornarValorComDescricao(T enumInstance) {
        return String.format("%s - %s", enumInstance.getValor(), enumInstance.getDescricao());
    }

    /**
     * Implementação padrão de {@link DescricaoRecuperavel#getValorComDescricaoMaiscula()}}.
     *
     * <p>
     * Exemplo:
     * <ul>
     * <li><b>Código: 5</b></li>
     * <li><b>Descrição: Pacto</b></li>
     * </ul>
     *
     * <h3>Resultado:</h3>
     * <h2>5 - PACTO</h2>
     * </p>
     */
    public static <T extends Enum & ValorRecuperavel & DescricaoRecuperavel> String retornarValorComDescricaoMaiscula(T enumInstance) {
        return String.format("%s - %s", enumInstance.getValor(), enumInstance.getDescricaoMaiscula());
    }

    public static <T extends Enum & ValorRecuperavel> T fromValue(T[] enumInstances, Integer valor) throws EnumNaoEncontradaException {
        if (valor != null) {
            return fromValue(enumInstances, String.valueOf(valor));
        }

        return null;
    }

    public static <T extends Enum & ValorRecuperavel> T fromValue(T[] enumInstances, String value) throws EnumNaoEncontradaException {
        return _fromValue(enumInstances, value, true);
    }

    public static <T extends Enum & ValorRecuperavel> T fromValueOrNull(T[] enumInstances, String value) throws EnumNaoEncontradaException {
        return _fromValue(enumInstances, value, false);
    }

    private static <T extends Enum & ValorRecuperavel> T _fromValue(T[] enumInstances, String value, boolean shouldThrowException) throws EnumNaoEncontradaException {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        if (isArrayEnumNaoVazio(enumInstances)) {
            return retornarValorIterandoArray(enumInstances, value, shouldThrowException);
        }

        return null;
    }

    private static <T extends Enum & ValorRecuperavel> boolean isArrayEnumNaoVazio(T[] enumInstances) throws EnumNaoEncontradaException {
        if (enumInstances.length != 0) {
            return true;
        }

        throw new EnumNaoEncontradaException();
    }

    private static <T extends ValorRecuperavel> T retornarValorIterandoArray(T[] enumInstances, String value, boolean shouldThrowException) throws EnumNaoEncontradaException {
        for (T e : enumInstances) {
            if (value.equalsIgnoreCase(e.getValor())) {
                return e;
            }
        }

        if (shouldThrowException) {
            throw new EnumNaoEncontradaException(enumInstances[0].getClass(), value, CampoRecuperavelEnum.VALOR);
        }

        return null;
    }

}
