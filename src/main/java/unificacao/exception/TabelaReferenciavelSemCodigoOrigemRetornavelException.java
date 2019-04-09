package br.com.pactosolucoes.atualizadb.processo.unificacao.exception;

import br.com.pactosolucoes.atualizadb.processo.unificacao.CodigoOrigemRetornavel;
import br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorFilho;
import br.com.pactosolucoes.atualizadb.processo.unificacao.TabelaReferenciavel;

import static java.lang.String.format;

/**
 * DOCME
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public class TabelaReferenciavelSemCodigoOrigemRetornavelException extends UnificadorGenericException {

    private final static String MESSAGE = ""
            + "O unificador (%s) � um " + TabelaReferenciavel.class.getSimpleName() + ", "
            + "por�m n�o possui associa��o com alguma classe do tipo " + CodigoOrigemRetornavel.class.getSimpleName() + "."
            + "\nEste �ltimo � necess�rio para preencher o c�digo de origem.";

    public TabelaReferenciavelSemCodigoOrigemRetornavelException(UnificadorFilho unificadorFilho) {
        super(format(MESSAGE, unificadorFilho.getClass().getName()));
    }
}
