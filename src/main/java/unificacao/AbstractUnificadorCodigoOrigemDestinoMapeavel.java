package unificacao;

import unificacao.wrapper.MapaCodigoOrigemDestino;

/**
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public abstract class AbstractUnificadorCodigoOrigemDestinoMapeavel implements TabelaReferenciavel {

    final MapaCodigoOrigemDestino mapaCodigoOrigemDestino = new MapaCodigoOrigemDestino();

    @Override
    public MapaCodigoOrigemDestino getMapCodigoOrigemDestino() {
        return mapaCodigoOrigemDestino;
    }
}
