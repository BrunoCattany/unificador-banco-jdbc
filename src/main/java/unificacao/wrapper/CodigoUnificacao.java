package unificacao.wrapper;

/**
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

        CodigoUnificacao that = (CodigoUnificacao) o;

        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
