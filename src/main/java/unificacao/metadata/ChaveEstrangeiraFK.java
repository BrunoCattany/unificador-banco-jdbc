package unificacao.metadata;

import unificacao.TabelaReferenciavel;

import java.lang.annotation.*;

/**
 * @author Bruno Cattany
 * @since 10/04/2019
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChaveEstrangeiraFK {

    /**
     * @return Deve informar o nome da classe de uma {@link TabelaReferenciavel}.
     */
    Class<? extends TabelaReferenciavel> tabelaReferenciavelName();
}
