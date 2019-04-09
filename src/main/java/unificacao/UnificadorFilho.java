package br.com.pactosolucoes.atualizadb.processo.unificacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.parametrizacao.ParametroObrigatorioUnificacaoEnum;
import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.ConnectionUnificacao;

/**
 * Representa a parte da execução de uma unificação completa, ou seja, parte de uma execução de uma instância de {@link AbstractOrquestradorUnificadorDadosJDBC}. <br>
 * Isto quer dizer que um ou vários {@link UnificadorFilho} precisam {@link #executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)},
 * para que uma unificação seja completa.
 *
 * @param <P> unificador orquestrador pai, ou seja, aquele ao qual o filho estará inserido em meio a sua sequência de unificação.
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
     * Deve conter e executar os scripts de unificação.
     *
     * @param unificadorOrquestrador representa o orquestrador da unificação em si e e define a ordem de excecução dos unificadores filhos, mediante {@link AbstractOrquestradorUnificadorDadosJDBC#criarSequenciaOrquestradaunificadoresFilhos()}.
     * @param conexaoOrigem        criado a partir dos parâmetros informado nos argumentos:
     *                             <ul>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#HOST_AND_PORT_ORIGEM}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#DATABASE_NAME_ORIGEM}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#USER_DATABASE_ORIGEM}</li>
     *                             <li>{@link ParametroObrigatorioUnificacaoEnum#PASSWORD_DATABASE_ORIGEM}</li>
     *                             </ul>
     * @param conexaoDestino       criado a partir dos parâmetros informado nos argumentos:
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
