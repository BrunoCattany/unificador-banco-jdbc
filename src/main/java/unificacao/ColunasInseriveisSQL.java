package br.com.pactosolucoes.atualizadb.processo.unificacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.OperacaoSQL;

/**
 * Representa uma classe que ter�o os seus atributos transformado em valores pass�veis de serem inseridos em uma opera��o {@link OperacaoSQL#INSERT}.
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public interface ColunasInseriveisSQL {

    /**
     * Exemplo de uma implementa��o:
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
     * Veja que � necess�rio informar na sintaxe correta, respeitando os tipos (VARCHAR, NUMBER...)
     *
     * @return Deve retornar os valores das colunas que devem ser inseridos via SQL.
     */
    String colunasValorString();
}
