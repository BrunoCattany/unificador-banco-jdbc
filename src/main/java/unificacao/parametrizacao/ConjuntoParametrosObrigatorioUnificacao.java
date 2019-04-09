package br.com.pactosolucoes.atualizadb.processo.unificacao.parametrizacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.enums.OperacaoDestinoTransacionalOpcao;

/**
 * Respons�vel por guardar o valor que representa um conjunto de {@link ParametroObrigatorioUnificacaoEnum}.
 *
 * @author Bruno Cattany
 * @since 06/04/2019
 */
public class ConjuntoParametrosObrigatorioUnificacao {

    /**
     * <h2>Este par�metro define como os demais ser�o lidos a partir dos argumentos de um m�todo Main.</h2>
     * Representa o separador entre <b>chave e valor</b> de um argumento.
     * <br>
     * Por exemplo, caso voc� queira usar o s�mbolo <b>=</b>, isso representaria no array de par�metros algo do tipo:
     * <pre>
     * HOST_AND_PORT_ORIGEM=localhost:
     * DATABASE_NAME_ORIGEM=bdzillyonfitpark
     * </pre>
     */
    private final Character separadorEntreChaveValor;

    // ==== Origem

    /**
     * {@link ParametroObrigatorioUnificacaoEnum#HOST_AND_PORT_ORIGEM}
     */
    private final String hostAndPortOrigem;
    /**
     * {@link ParametroObrigatorioUnificacaoEnum#DATABASE_NAME_ORIGEM}
     */
    private final String databaseNameOrigem;
    /**
     * {@link ParametroObrigatorioUnificacaoEnum#USER_DATABASE_ORIGEM}
     */
    private final String userDatabaseOrigem;
    /**
     * {@link ParametroObrigatorioUnificacaoEnum#PASSWORD_DATABASE_ORIGEM}
     */
    private final String passwordDatabaseOrigem;

    // ==== Destino

    /**
     * {@link ParametroObrigatorioUnificacaoEnum#HOST_AND_PORT_DESTINO}
     */
    private final String hostAndPortDestino;
    /**
     * {@link ParametroObrigatorioUnificacaoEnum#DATABASE_NAME_DESTINO}
     */
    private final String databaseNameDestino;
    /**
     * {@link ParametroObrigatorioUnificacaoEnum#USER_DATABASE_DESTINO}
     */
    private final String userDatabaseDestino;
    /**
     * {@link ParametroObrigatorioUnificacaoEnum#PASSWORD_DATABASE_DESTINO}
     */
    private final String passwordDatabaseDestino;

    // ==== Referente a transa��o

    /**
     * Veja mais detalhes em {@link ParametroObrigatorioUnificacaoEnum#OPERACAO_DESTINO_TRANSACIONAL}.
     */
    private final OperacaoDestinoTransacionalOpcao operacaoDestinoTransacionalOpcao;

    ConjuntoParametrosObrigatorioUnificacao(Character separadorEntreChaveValor,
                                            String hostAndPortOrigem,
                                            String databaseNameOrigem,
                                            String userDatabaseOrigem,
                                            String passwordDatabaseOrigem,
                                            String hostAndPortDestino,
                                            String databaseNameDestino,
                                            String userDatabaseDestino,
                                            String passwordDatabaseDestino,
                                            OperacaoDestinoTransacionalOpcao operacaoDestinoTransacionalOpcao) {
        this.separadorEntreChaveValor = separadorEntreChaveValor;
        this.hostAndPortOrigem = hostAndPortOrigem;
        this.databaseNameOrigem = databaseNameOrigem;
        this.userDatabaseOrigem = userDatabaseOrigem;
        this.passwordDatabaseOrigem = passwordDatabaseOrigem;
        this.hostAndPortDestino = hostAndPortDestino;
        this.databaseNameDestino = databaseNameDestino;
        this.userDatabaseDestino = userDatabaseDestino;
        this.passwordDatabaseDestino = passwordDatabaseDestino;
        this.operacaoDestinoTransacionalOpcao = operacaoDestinoTransacionalOpcao;
    }

    Character getSeparadorEntreChaveValor() {
        return separadorEntreChaveValor;
    }

    public String getHostAndPortOrigem() {
        return hostAndPortOrigem;
    }

    public String getDatabaseNameOrigem() {
        return databaseNameOrigem;
    }

    public String getUserDatabaseOrigem() {
        return userDatabaseOrigem;
    }

    public String getPasswordDatabaseOrigem() {
        return passwordDatabaseOrigem;
    }

    public String getHostAndPortDestino() {
        return hostAndPortDestino;
    }

    public String getDatabaseNameDestino() {
        return databaseNameDestino;
    }

    public String getUserDatabaseDestino() {
        return userDatabaseDestino;
    }

    public String getPasswordDatabaseDestino() {
        return passwordDatabaseDestino;
    }

    public OperacaoDestinoTransacionalOpcao getOperacaoDestinoTransacionalOpcao() {
        return operacaoDestinoTransacionalOpcao;
    }
}
