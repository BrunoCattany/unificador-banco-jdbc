package br.com.pactosolucoes.atualizadb.processo.unificacao.enums;

import negocio.comuns.utilitarias.RecuperadorEnumPadrao;
import negocio.comuns.utilitarias.ValorRecuperavel;

/**
 * Responsável por definir como será a transação na conexão de {@link DirecaoConexao#DESTINO}.
 *
 * @author Bruno Cattany
 * @since 03/04/2019
 */
public enum OperacaoDestinoTransacionalOpcao implements ValorRecuperavel {

    /**
     * Significa que a transação na conexão de {@link DirecaoConexao#DESTINO} será feita em uma transação à parte, e caso alguma exceção aconteça durante
     * a execução, será realizado um <b>rollback</b>, pelo contrário, no final do processo, a transação será <b>comitada</b>.
     */
    TRUE,
    /**
     * Significa que as operações na conexão de {@link DirecaoConexao#DESTINO} não serão realizadas em uma transação controlada, mas sim na verdade,
     * cada operação já será automaticamente comitada.
     */
    FALSE,
    /**
     * Significa o mesmo de {@link #TRUE}, entretanto ao final do processo, será executado um <b>rollback</b>. <br>
     * Pode ser usado para ter uma estimativa do tempo de duração da unificação, ou até mesmo, testar as linhas afetadas.
     */
    SIMULACAO;

    @Override
    public String getValor() {
        return name();
    }

    public static OperacaoDestinoTransacionalOpcao fromValueOrNull(String value) {
        return RecuperadorEnumPadrao.fromValue(values(), value);
    }

    public boolean isSimulacao() {
        return this == SIMULACAO;
    }
}
