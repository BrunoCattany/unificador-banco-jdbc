package unificacao;

import unificacao.wrapper.MapaCodigoOrigemDestino;

/**
 * Representa uma tabela que � referenciada por outras, ou seja, outras possuem o seu c�digo, ou seja ainda, possuem uma <b>foreign key</b>.
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public interface TabelaReferenciavel {

    /**
     * Exemplo de implementa��o padr�o deste m�todo:
     * <pre>
     *     final MapaCodigoOrigemDestino mapaCodigoOrigemDestino = new MapaCodigoOrigemDestino();
     *
     *     {@literal @}Override
     *     public MapaCodigoOrigemDestino getMapCodigoOrigemDestino() {
     *         return mapaCodigoOrigemDestino;
     *     }
     * </pre>
     *
     * @return Deve retornar um {@link MapaCodigoOrigemDestino} para que se possa guardar o mapa que liga os c�digos de origem e destino.
     */
    MapaCodigoOrigemDestino getMapCodigoOrigemDestino();
}
