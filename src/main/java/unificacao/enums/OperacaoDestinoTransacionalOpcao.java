package br.com.pactosolucoes.atualizadb.processo.unificacao.enums;

import negocio.comuns.utilitarias.RecuperadorEnumPadrao;
import negocio.comuns.utilitarias.ValorRecuperavel;

/**
 * Respons�vel por definir como ser� a transa��o na conex�o de {@link DirecaoConexao#DESTINO}.
 *
 * @author Bruno Cattany
 * @since 03/04/2019
 */
public enum OperacaoDestinoTransacionalOpcao implements ValorRecuperavel {

    /**
     * Significa que a transa��o na conex�o de {@link DirecaoConexao#DESTINO} ser� feita em uma transa��o � parte, e caso alguma exce��o aconte�a durante
     * a execu��o, ser� realizado um <b>rollback</b>, pelo contr�rio, no final do processo, a transa��o ser� <b>comitada</b>.
     */
    TRUE,
    /**
     * Significa que as opera��es na conex�o de {@link DirecaoConexao#DESTINO} n�o ser�o realizadas em uma transa��o controlada, mas sim na verdade,
     * cada opera��o j� ser� automaticamente comitada.
     */
    FALSE,
    /**
     * Significa o mesmo de {@link #TRUE}, entretanto ao final do processo, ser� executado um <b>rollback</b>. <br>
     * Pode ser usado para ter uma estimativa do tempo de dura��o da unifica��o, ou at� mesmo, testar as linhas afetadas.
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
