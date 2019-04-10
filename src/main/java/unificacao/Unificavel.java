package unificacao;

import unificacao.metadata.NomeColuna;

/**
 * Representa uma classe que ter�o os seus campos lidos e processados, sendo assim, � necess�rio que estes mesmos campos:
 *
 * <ul>
 * <li>Informe a {@link NomeColuna}</li>
 * <li>Tenha Get/Set para que se possa manipular seus valores via <b>Reflection</b></li>
 * </ul>
 *
 * � necess�rio que a classe de implementa��o:
 *
 * <ul>
 * <li>Seja <b>Public</b> e <b>static</b></li>
 * <li>Possua um construtor p�blico sem argumentos, ou seja o <b>Construtor Default</b></li>
 * </ul>
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public interface Unificavel {

}
