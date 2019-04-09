package br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper;

import java.util.Objects;

/**
 * DOCME
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
abstract class CodigoUnificacao {

    CodigoUnificacao(Integer value) {
        this.value = value;
    }

    Integer value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodigoUnificacao codigo = (CodigoUnificacao) o;
        return Objects.equals(value, codigo.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
