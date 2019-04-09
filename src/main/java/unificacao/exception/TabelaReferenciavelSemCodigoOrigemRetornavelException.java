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
            + "O unificador (%s) é um " + TabelaReferenciavel.class.getSimpleName() + ", "
            + "porém não possui associação com alguma classe do tipo " + CodigoOrigemRetornavel.class.getSimpleName() + "."
            + "\nEste último é necessário para preencher o código de origem.";

    public TabelaReferenciavelSemCodigoOrigemRetornavelException(UnificadorFilho unificadorFilho) {
        super(format(MESSAGE, unificadorFilho.getClass().getName()));
    }
}
