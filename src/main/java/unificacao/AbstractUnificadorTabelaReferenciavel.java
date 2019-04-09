package br.com.pactosolucoes.atualizadb.processo.unificacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.MapaCodigoOrigemDestino;

/**
 * DOCME
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public abstract class AbstractUnificadorTabelaReferenciavel implements TabelaReferenciavel {

    final MapaCodigoOrigemDestino mapaCodigoOrigemDestino = new MapaCodigoOrigemDestino();

    @Override
    public MapaCodigoOrigemDestino getMapCodigoOrigemDestino() {
        return mapaCodigoOrigemDestino;
    }
}
