package unificacao.wrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public class MapaCodigoOrigemDestino {

    private Map<CodigoUnificacaoOrigem, CodigoUnificacaoDestino> map = new HashMap<CodigoUnificacaoOrigem, CodigoUnificacaoDestino>();

    public Integer getPorCodigoOrigem(Integer codigoOrigem) {
        return map.get(CodigoUnificacaoOrigem.as(codigoOrigem)).value;
    }

    public void putCodigoDestinoChaveadoPorCodigoOrigem(Integer codigoOrigemChave, Integer codigoDestinoValor) {
        map.put(CodigoUnificacaoOrigem.as(codigoOrigemChave), CodigoUnificacaoDestino.as(codigoDestinoValor));
    }

}
