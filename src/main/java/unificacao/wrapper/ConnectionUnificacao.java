package unificacao.wrapper;

import unificacao.enums.DirecaoConexao;

import java.sql.Connection;

/**
 * Classe Wrapper que encapsula uma união entre {@link #connection} e a sua {@link #direcaoConexao}.
 *
 * @author Bruno Cattany
 * @since 02/04/2019
 */
public class ConnectionUnificacao {

    private final Connection connection;
    private final DirecaoConexao direcaoConexao;

    public ConnectionUnificacao(Connection connection, DirecaoConexao direcaoConexao) {
        this.connection = connection;
        this.direcaoConexao = direcaoConexao;
    }

    public Connection getConnection() {
        return connection;
    }

    public DirecaoConexao getDirecaoConexao() {
        return direcaoConexao;
    }

}
