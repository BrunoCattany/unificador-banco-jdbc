package br.com.pactosolucoes.atualizadb.processo.unificacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.OperacaoSQL;

/**
 * Representa uma classe que terão os seus atributos transformado em valores passíveis de serem inseridos em uma operação {@link OperacaoSQL#INSERT}.
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public interface ColunasInseriveisSQL {

    /**
     * Exemplo de uma implementação:
     * <pre>
     * class Pessoa {
     *     String codigo;
     *     String nome;
     *     Integer idade;
     *
     *     {@literal @}Override
     *     public String colunasValorString() {
     *         return '\'' + nome + "', + " + idade;
     *     }
     * }
     * </pre>
     *
     * Veja que é necessário informar na sintaxe correta, respeitando os tipos (VARCHAR, NUMBER...)
     *
     * @return Deve retornar os valores das colunas que devem ser inseridos via SQL.
     */
    String colunasValorString();
}
