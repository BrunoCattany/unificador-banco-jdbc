package br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper;

/**
 * @author Bruno Cattany
 * @since 07/04/2019
 */
class CodigoUnificacaoOrigem extends CodigoUnificacao {

    CodigoUnificacaoOrigem(Integer value) {
        super(value);
    }

    public static CodigoUnificacaoOrigem as(Integer value) {
        return new CodigoUnificacaoOrigem(value);
    }
}

