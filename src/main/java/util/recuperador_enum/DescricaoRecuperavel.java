package util.recuperador_enum;

/**
 * Responsável por fornecer o {@link CampoRecuperavelEnum#DESCRICAO} de uma {@link Enum} em questão.
 *
 * @author Bruno Cattany
 * @since 06/12/2018
 */
public interface DescricaoRecuperavel {

    String getDescricao();

    String getDescricaoMaiscula();

    String getValorComDescricao();

    String getValorComDescricaoMaiscula();

}
