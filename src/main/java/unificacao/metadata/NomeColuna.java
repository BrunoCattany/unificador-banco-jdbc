package unificacao.metadata;

import unificacao.AbstractUnificadorCodigoOrigemDestinoMapeavel;

import java.lang.annotation.*;

/**
 * @author Bruno Cattany
 * @since 09/04/2019
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NomeColuna {

    /**
     * @return Deve informar o nome da coluna que ser� lido e usado nas opera��es dentro de {@link AbstractUnificadorCodigoOrigemDestinoMapeavel}.
     */
    String value();
}
