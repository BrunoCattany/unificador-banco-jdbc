package unificacao;

import unificacao.metadata.NomeColuna;

/**
 * Representa uma classe que terão os seus campos lidos e processados, sendo assim, é necessário que estes mesmos campos:
 *
 * <ul>
 * <li>Informe a {@link NomeColuna}</li>
 * <li>Tenha Get/Set para que se possa manipular seus valores via <b>Reflection</b></li>
 * </ul>
 *
 * É necessário que a classe de implementação:
 *
 * <ul>
 * <li>Seja <b>Public</b> e <b>static</b></li>
 * <li>Possua um construtor público sem argumentos, ou seja o <b>Construtor Default</b></li>
 * </ul>
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public interface Unificavel {

}
