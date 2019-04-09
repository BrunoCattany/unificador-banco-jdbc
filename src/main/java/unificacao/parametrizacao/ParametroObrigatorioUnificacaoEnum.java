package br.com.pactosolucoes.atualizadb.processo.unificacao.parametrizacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.AbstractOrquestradorUnificadorDadosJDBC;
import br.com.pactosolucoes.atualizadb.processo.unificacao.enums.OperacaoDestinoTransacionalOpcao;
import negocio.comuns.utilitarias.RecuperadorEnumPadrao;
import negocio.comuns.utilitarias.ValorRecuperavel;

import static br.com.pactosolucoes.atualizadb.processo.unificacao.enums.OperacaoDestinoTransacionalOpcao.*;

/**
 * Responsável por conter os parâmetros obrigatórios para o funcionamento padrão da classe {@link AbstractOrquestradorUnificadorDadosJDBC}.
 *
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public enum ParametroObrigatorioUnificacaoEnum implements ValorRecuperavel {

    /**
     * Formato: <b>Alfanumérico</b> <br>
     * Exemplo: <b>localhost:5432</b>
     */
    HOST_AND_PORT_ORIGEM,
    /**
     * Formato: <b>Alfanumérico</b> <br>
     * Exemplo: <b>bdmuscevolve-2019-02-04</b>
     */
    DATABASE_NAME_ORIGEM,
    /**
     * Formato: <b>Alfanumérico</b> <br>
     * Exemplo: <b>postgres</b>
     */
    USER_DATABASE_ORIGEM,
    /**
     * Formato: <b>Alfanumérico</b> <br>
     * Exemplo: <b>pactodb</b>
     */
    PASSWORD_DATABASE_ORIGEM,
    /**
     * O mesmo de {@link #HOST_AND_PORT_ORIGEM}
     */
    HOST_AND_PORT_DESTINO,
    /**
     * O mesmo de {@link #DATABASE_NAME_ORIGEM}
     */
    DATABASE_NAME_DESTINO,
    /**
     * O mesmo de {@link #USER_DATABASE_ORIGEM}
     */
    USER_DATABASE_DESTINO,
    /**
     * O mesmo de {@link #PASSWORD_DATABASE_ORIGEM}
     */
    PASSWORD_DATABASE_DESTINO,
    /**
     * Veja os detalhes de cada opção em: {@link OperacaoDestinoTransacionalOpcao}.
     */
    OPERACAO_DESTINO_TRANSACIONAL(
            TRUE,
            FALSE,
            SIMULACAO
    );

    private final Object[] opcoesDisponiveis;

    public static ParametroObrigatorioUnificacaoEnum fromValueOrNull(String value) {
        return RecuperadorEnumPadrao.fromValueOrNull(values(), value);
    }

    ParametroObrigatorioUnificacaoEnum(Object... opcoesDisponiveis) {
        this.opcoesDisponiveis = opcoesDisponiveis;
    }

    @Override
    public String getValor() {
        return name();
    }

    public boolean is(ParametroObrigatorioUnificacaoEnum unificadorArgs) {
        return unificadorArgs == this;
    }

    public Object[] getOpcoesDisponiveis() {
        return opcoesDisponiveis;
    }
}
