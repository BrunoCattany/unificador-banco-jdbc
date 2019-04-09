package util.recuperador_enum;

/**
 * Respons�vel por fornecer o {@link CampoRecuperavelEnum#DESCRICAO} de uma {@link Enum} em quest�o.
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
