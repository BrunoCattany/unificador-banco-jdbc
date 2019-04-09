package br.com.pactosolucoes.atualizadb.processo.unificacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.parametrizacao.ParametroObrigatorioUnificacaoEnum;
import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.ConnectionUnificacao;

/**
 * Representa a parte da execu��o de uma unifica��o completa, ou seja, parte de uma execu��o de uma inst�ncia de {@link AbstractOrquestradorUnificadorDadosJDBC}. <br>
 * Isto quer dizer que um ou v�rios {@link UnificadorFilho} precisam {@link #executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)},
 * para que uma unifica��o seja completa.
 *
 * @param <P> unificador orquestrador pai, ou seja, aquele ao qual o filho estar� inserido em meio a sua sequ�ncia de unifica��o.
 *
 * @author Bruno Cattany
 * @since 05/04/2019
 */
public interface UnificadorFilho<P extends AbstractOrquestradorUnificadorDadosJDBC> {

    /**
     * DOCME
     */
    String getNomeTabelaAlvo();

    /**
     * Deve conter e executar os scripts de unifica��o.
     *
     * @param unificadorOrquestrador representa o orquestrador da unifica��o em si e e define a ordem de excecu��o dos unificadores filhos, mediante {@link AbstractOrquestradorUnificadorDadosJDBC#criarSequenciaOrquestradaunificadoresFilhos()}.
     * @param conexaoOrigem        criado a partir dos par�metros informado nos argumentos:
     *                             <ul>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#HOST_AND_PORT_ORIGEM}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#DATABASE_NAME_ORIGEM}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#USER_DATABASE_ORIGEM}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#PASSWORD_DATABASE_ORIGEM}</li>
     *                             </ul>
     * @param conexaoDestino       criado a partir dos par�metros informado nos argumentos:
     *                             <ul>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#HOST_AND_PORT_DESTINO}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#DATABASE_NAME_DESTINO}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#USER_DATABASE_DESTINO}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#PASSWORD_DATABASE_DESTINO}</li>
     *                             </ul>
     *
     * @throws Exception quando
     */
    void executar(P unificadorOrquestrador, ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino) throws Exception;

}
