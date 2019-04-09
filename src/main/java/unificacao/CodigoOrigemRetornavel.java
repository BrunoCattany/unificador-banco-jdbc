package br.com.pactosolucoes.atualizadb.processo.unificacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.enums.DirecaoConexao;

/**
 * Representa um registro que seja da {@link DirecaoConexao#ORIGEM} e que é possível retornar a {@link UnificadorConstantes#COLUNA_CODIGO}.
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public interface CodigoOrigemRetornavel {

    Integer getCodigoOrigem();
}
