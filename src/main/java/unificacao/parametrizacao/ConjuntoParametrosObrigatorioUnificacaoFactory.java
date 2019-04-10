package unificacao.parametrizacao;

import unificacao.AbstractOrquestradorUnificadorDadosJDBC;
import unificacao.enums.OperacaoDestinoTransacionalOpcao;
import unificacao.exception.FalhaProcuraArgumentoException;
import org.apache.commons.lang3.StringUtils;

import static unificacao.parametrizacao.ParametroObrigatorioUnificacaoEnum.*;

/**
 * Representa o conjunto de parâmetros obrigatórios para o funcionado da classe {@link AbstractOrquestradorUnificadorDadosJDBC}. <br>
 * Segue um exemplo de um conjunto de argumentos passados via <b>Método Main</b>:
 * <pre>
 *    =
 *    HOST_AND_PORT_ORIGEM=localhost:5432
 *    DATABASE_NAME_ORIGEM=bdmuscevolve
 *    USER_DATABASE_ORIGEM=postgres
 *    PASSWORD_DATABASE_ORIGEM=pactodb
 *    HOST_AND_PORT_DESTINO=localhost:5432
 *    DATABASE_NAME_DESTINO=bdmuscevolve
 *    USER_DATABASE_DESTINO=postgres
 *    PASSWORD_DATABASE_DESTINO=pactodb
 *    OPERACAO_DESTINO_TRANSACIONAL=true
 * </pre>
 *
 * Perceba que em um método <b>Main</b>, os argumentos são separados por um simples espaço, sendo assim, no exemplo anterior
 * contém um total de 10 argumentos, sendo o primeiro o {@link ConjuntoParametrosObrigatorioUnificacao#getSeparadorEntreChaveValor}.
 *
 * @author Bruno Cattany
 * @since 06/04/2019
 */
public class ConjuntoParametrosObrigatorioUnificacaoFactory {

    private final static ConjuntoParametrosObrigatorioUnificacaoFactory INSTANCE = new ConjuntoParametrosObrigatorioUnificacaoFactory();

    private ConjuntoParametrosObrigatorioUnificacaoFactory() {
    }

    public static ConjuntoParametrosObrigatorioUnificacaoFactory getInstance() {
        return INSTANCE;
    }

    public ConjuntoParametrosObrigatorioUnificacao toConjuntoParametrosObrigatoriounificacao(String... args) {
        validarSeparadorEntreChaveValor(getPrimeiroArgumento(args));

        final Character separadorEntreChaveValor = getPrimeiroArgumento(args).charAt(0);

        // Parâmetros da Origem

        final String hostAndPortOrigem = recuperarValorArgumentosPorChaveObrigatorios(HOST_AND_PORT_ORIGEM, args);
        final String dataBaseNameOrigem = recuperarValorArgumentosPorChaveObrigatorios(DATABASE_NAME_ORIGEM, args);
        final String userDatabaseOrigem = recuperarValorArgumentosPorChaveObrigatorios(USER_DATABASE_ORIGEM, args);
        final String passwordDatabaseOrigem = recuperarValorArgumentosPorChaveObrigatorios(PASSWORD_DATABASE_ORIGEM, args);

        // Parâmetros do Destino

        final String hostAndPortDestino = recuperarValorArgumentosPorChaveObrigatorios(HOST_AND_PORT_DESTINO, args);
        final String dataBaseNameDestino = recuperarValorArgumentosPorChaveObrigatorios(DATABASE_NAME_DESTINO, args);
        final String userDatabaseDestino = recuperarValorArgumentosPorChaveObrigatorios(USER_DATABASE_DESTINO, args);
        final String passwordDatabaseDestino = recuperarValorArgumentosPorChaveObrigatorios(PASSWORD_DATABASE_DESTINO, args);

        // Referente a transação

        final OperacaoDestinoTransacionalOpcao operacaoDestinoTransacionalOpcao = OperacaoDestinoTransacionalOpcao.fromValueOrNull(
                recuperarValorArgumentosPorChaveObrigatorios(OPERACAO_DESTINO_TRANSACIONAL, args)
        );

        return new ConjuntoParametrosObrigatorioUnificacao(
                separadorEntreChaveValor,
                hostAndPortOrigem,
                dataBaseNameOrigem,
                userDatabaseOrigem,
                passwordDatabaseOrigem,
                hostAndPortDestino,
                dataBaseNameDestino,
                userDatabaseDestino,
                passwordDatabaseDestino,
                operacaoDestinoTransacionalOpcao
        );
    }

    private void validarSeparadorEntreChaveValor(String primeiroArgumento) {
        if (StringUtils.isBlank(primeiroArgumento)) {
            throw new AtribuicaoSeparadorEntreChaveValorException();
        } else if (primeiroArgumento.length() != 1) {
            throw new AtribuicaoSeparadorEntreChaveValorException(primeiroArgumento);
        }
    }

    private String recuperarValorArgumentosPorChaveObrigatorios(ParametroObrigatorioUnificacaoEnum unificadorArgsTarget, String... args) {
        final String primeiroArgumento = getPrimeiroArgumento(args);

        for (String arg : args) {
            if (argumentoIsNotSeparador(arg, primeiroArgumento)) {
                String chave = recuperarChaveDoArgumento(arg, primeiroArgumento);

                if (unificadorArgsTarget.is(fromValueOrNull(chave))) {
                    return recuperarValorDoArgumento(arg, primeiroArgumento);
                }
            }
        }

        throw new FalhaProcuraArgumentoException(unificadorArgsTarget, args);
    }

    private boolean argumentoIsNotSeparador(String arg, String separadorEntreChaveValor) {
        return !StringUtils.equals(arg, separadorEntreChaveValor);
    }

    private String recuperarChaveDoArgumento(String arg, String separadorEntreChaveValor) {
        return arg.substring(0, arg.indexOf(separadorEntreChaveValor));
    }

    private String recuperarValorDoArgumento(String arg, String separadorEntreChaveValor) {
        return arg.substring(arg.indexOf(separadorEntreChaveValor) + 1);
    }

    private String getPrimeiroArgumento(String... args) {
        return args[0];
    }
}
