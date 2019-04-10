package unificacao;

import unificacao.enums.DirecaoConexao;

/**
 * Representa um registro que seja da {@link DirecaoConexao#ORIGEM} e que � poss�vel retornar a {@link UnificadorConstantes#COLUNA_CODIGO}.
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public interface CodigoOrigemRetornavel {

    Integer getCodigo();
}
