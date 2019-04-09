package br.com.pactosolucoes.atualizadb.processo.unificacao.parametrizacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.exception.UnificadorGenericException;

/**
 * @author Bruno Cattany
 * @since 01/04/2019
 */
class AtribuicaoSeparadorEntreChaveValorException extends UnificadorGenericException {

    private static final String MESSAGE_DEFAULT = ""
            + "O primeiro argumento do método main deve representar o SEPARADOR entre chave e valor!"
            + "\nque por sua vez deve ter o tamanho de 1 CARACTERE."
            + "\nPor exemplo, caso você queira usar o símbolo =, isso representaria no array de parâmetros algo do tipo: \n"
            + "{\"CHAVE_ZW=1234\", \"USER_ZW=PACTOBR\"}";

    private static final String MESSAGE_PARAMETERIZED = MESSAGE_DEFAULT + "\nO primeiro argumento informado está inválido: %s";

    AtribuicaoSeparadorEntreChaveValorException() {
        super(MESSAGE_DEFAULT);
    }

    AtribuicaoSeparadorEntreChaveValorException(String wrongArgument) {
        super(String.format(MESSAGE_PARAMETERIZED, wrongArgument));
    }

}
