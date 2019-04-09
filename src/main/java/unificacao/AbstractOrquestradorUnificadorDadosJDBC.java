package br.com.pactosolucoes.atualizadb.processo.unificacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.enums.DirecaoConexao;
import br.com.pactosolucoes.atualizadb.processo.unificacao.enums.OperacaoDestinoTransacionalOpcao;
import br.com.pactosolucoes.atualizadb.processo.unificacao.exception.FalhaAbrirConexaoJDBCException;
import br.com.pactosolucoes.atualizadb.processo.unificacao.exception.FalhaTransacaoException;
import br.com.pactosolucoes.atualizadb.processo.unificacao.exception.UnificadorGenericException;
import br.com.pactosolucoes.atualizadb.processo.unificacao.exception.TabelaReferenciavelSemCodigoOrigemRetornavelException;
import br.com.pactosolucoes.atualizadb.processo.unificacao.parametrizacao.ConjuntoParametrosObrigatorioUnificacao;
import br.com.pactosolucoes.atualizadb.processo.unificacao.parametrizacao.ConjuntoParametrosObrigatorioUnificacaoFactory;
import br.com.pactosolucoes.atualizadb.processo.unificacao.parametrizacao.ParametroObrigatorioUnificacaoEnum;
import br.com.pactosolucoes.atualizadb.processo.unificacao.to.AuditoriaUnificacaoDadosTO;
import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.ConnectionUnificacao;
import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.MapaCodigoOrigemDestino;
import negocio.comuns.utilitarias.Calendario;
import negocio.comuns.utilitarias.Uteis;
import negocio.comuns.utilitarias.info_execucao.InformacaoMaquinaExecutora;
import negocio.comuns.utilitarias.info_execucao.InformacaoMaquinaExecutoraFactory;
import org.postgresql.util.PSQLException;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.*;
import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.OperacaoSQL.*;
import static br.com.pactosolucoes.atualizadb.processo.unificacao.enums.DirecaoConexao.DESTINO;
import static br.com.pactosolucoes.atualizadb.processo.unificacao.enums.DirecaoConexao.ORIGEM;
import static java.lang.String.format;
import static negocio.comuns.utilitarias.CronometroTempoThreadLocal.*;
import static negocio.comuns.utilitarias.Uteis.logar;

/**
 * <h2>Responsabilidades e abstra��o:</h2>
 *
 * <ul>
 * <li>O gerenciamento de conex�es/transa��es em um cen�rio de unifica��o de dados entre duas conex�es JDBC distintas.</li>
 * <li>Opera��es comuns realizada em unifica��es de dados.</li>
 * <li>Registro de log durante as etapas de unifica��o.</li>
 * <li>Comportamento de cronometrar as opera��es realizadas via JDBC.</li>
 * <li>Constru��o de um objeto-entidade respons�vel por representar uma auditoria dos processos executados na unifica��o.</li>
 * </ul>
 *
 * <hr>
 *
 * <h2>Cen�rio proposto:</h2>
 *
 * Quando houver necessidade de uma unifica��o de dados entre:
 * <ul>
 * <li>Uma conex�o JDBC de {@link DirecaoConexao#ORIGEM}</li>
 * <li>Uma conex�o JDBC de {@link DirecaoConexao#DESTINO}</li>
 * </ul>
 *
 * Sendo poss�vel os seguintes cen�rios:
 *
 * <ul>
 * <li>Dados de um banco de origem populado -> Para um banco de destino novo (em branco)</li>
 * <li>Dados de um banco de origem populado -> Para um banco de destino populado (em uso)</li>
 * </ul>
 *
 * <hr>
 *
 * <h2>Pontos importantes:</h2>
 * <ul>
 * <li>Cria conex�es mediante par�metros via argumentos. Veja o construtor {@link #AbstractOrquestradorUnificadorDadosJDBC(String[])}</li>
 * <li>Gerencia as conex�es, podendo ser um cen�rio transacional ou n�o, vulgo par�metro {@link ParametroObrigatorioUnificacaoEnum#OPERACAO_DESTINO_TRANSACIONAL}</li>
 * <li>Trata exce��es e loga todo o processo de unifica��o - Veja em {@link #tratarExcecaoExecucaounificacao}</li>
 * </ul>
 *
 * <hr>
 *
 * <h2>Pr�-requisitos para execu��o:</h2>
 *
 * Esta classe deve ser executada via <b>m�todo Main</b> e conter os par�metros necess�rios para sua execu��o,
 * preenchendo um {@link ConjuntoParametrosObrigatorioUnificacao}, atrav�s dos argumentos informados.
 *
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public abstract class AbstractOrquestradorUnificadorDadosJDBC {

    private final ConjuntoParametrosObrigatorioUnificacao parametros;

    // ============================== Objetos usados no processo da unifica��o ==============================

    /**
     * DOCME DOUBT
     */
    private UnificadorFilho unificadorFilhoExecucaoRodada;

    /**
     * DOCME
     */
    private Integer ultimoCodigoTabelaDestinoExecucaoRodada;

    private LinkedHashMap<Class<? extends UnificadorFilho>, UnificadorFilho> mapaunificadoresFilhos;

    // ============================== Entidades ==============================

    private AuditoriaUnificacaoDadosTO auditoriaunificacaoDados = new AuditoriaUnificacaoDadosTO();

    // ============================== Auxiliares ==============================

    /**
     * Respons�vel por contar as execu��o das instru��es SQL escritas em {@link UnificadorFilho#executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)}.
     */
    private int contadorExecucaoInstrucaoSql;

    protected AbstractOrquestradorUnificadorDadosJDBC(String[] args) {
        Uteis.debug = true;

        logarArgumentosDefault(args);

        parametros = ConjuntoParametrosObrigatorioUnificacaoFactory
                .getInstance()
                .toConjuntoParametrosObrigatoriounificacao(args);

        recuperarInformacoesMaquinaExecutora();
    }

    // ============================== Abstract ==============================

    /**
     * Este m�todo deve ser chamada apenas uma vez.
     *
     * @return Deve retornar a defini��o da sequ�ncia de {@link UnificadorFilho} a serem executados, <b>mediante ordem de inser��o</b>, aos quais ser�o executados por {@link #executarOperacoes()}.
     */
    protected abstract LinkedHashSet<? extends UnificadorFilho> criarSequenciaOrquestradaunificadoresFilhos();

    // ============================== Public ==============================

    // ==== Opera��es encapsuladas/utilit�rias JDBC ====

    public ResultSet executarConsultaComFeedbackOrdenadoCrescentementePeloCodigo(String descricaoInstrucao, ConnectionUnificacao connection, String sql) throws Exception {
        return executarComFeedback(SELECT, descricaoInstrucao, connection, sql);
    }

    public ResultSet executarConsultaComFeedback(String descricaoInstrucao, ConnectionUnificacao connection, String sql) throws Exception {
        return executarComFeedback(SELECT, descricaoInstrucao, connection, sql);
    }

    public void executarInsertComFeedback(String descricaoInstrucao, ConnectionUnificacao connection, String sql) throws Exception {
        executarComFeedback(INSERT, descricaoInstrucao, connection, sql);
    }

    public void executarUpdateComFeedback(String descricaoInstrucao, ConnectionUnificacao connection, String sql) throws Exception {
        executarComFeedback(UPDATE, descricaoInstrucao, connection, sql);
    }

    public boolean nextResult(ResultSet resultSet) throws SQLException {
        boolean hasNext = resultSet.next();

        if (resultSet.isLast()) {
            logar(MSG_INFO_LINHAS_CONSULTADAS, resultSet.getRow());
        }

        return hasNext;
    }

    public void commitarTransacao(ConnectionUnificacao conexao) throws SQLException {
        conexao.getConnection().commit();
        logar(MSG_INFO_COMMIT_REALIZADO, conexao.getDirecaoConexao());
    }

    public void realizarRollbackTransacao(ConnectionUnificacao conexao) throws SQLException {
        _executarRollbackConexao(conexao);
    }

    public void fecharConexao(ConnectionUnificacao conexao) throws SQLException {
        conexao.getConnection().close();
        logar(MSG_INFO_FECHAMENTO_CONEXAO_SUCESSO, conexao.getDirecaoConexao());
    }

    // ==== Utilit�rios ====

    public int consultarUltimoCodigo(ConnectionUnificacao conexao, String nomeTabela) throws Exception {
        ResultSet rs = executarConsultaComFeedback("Consultando o �ltimo c�digo de '" + nomeTabela + "'.", conexao,
                "SELECT max(codigo) FROM " + nomeTabela);

        nextResult(rs);
        return rs.getInt(1);
    }

    /**
     * A tabela que haver� a inser��o, � mediante o {@link #unificadorFilhoExecucaoRodada}.
     *
     * Este m�todo auxiliar far�:
     *
     * <ul>
     * <li>
     * 1. Consulta na tabela de {@link DirecaoConexao#DESTINO}
     * </li> para saber qual � o �ltimo c�digo.
     * <li>
     * 2. (Condicional) Caso o {@link #unificadorFilhoExecucaoRodada} for uma inst�ncia de {@link TabelaReferenciavel}, � criado um mapa
     * para armazenar o c�digo original e o c�digo novo a ser gerado para o registro que est� sendo unificado.
     * </li>
     * <li>
     * 3. (Condicional) Imediatamente posterior ao <b>passo 2</b>, se a condi��o for verdadeira, � criado uma coluna: {@link UnificadorConstantes#COLUNA_ID_EXTERNO}
     * respons�vel por armazenar o c�digo original da unifica��o.
     * </li>
     * <li>
     * 4. Realiza a unifica��o dos dados mediante a massa de dados em <code>colunasInseriveisSQL</code>.
     * </li>
     * </ul>
     *
     * @param conexaoDestino       que ser� inserido.
     * @param colunasParaInserir   indica as colunas a serem inseridas, separadas por virgula.
     *                             Este trecho ser� inclu�do na constru��o da instru��o INSERT, por exemplo: <br><br>
     *                             Dado a tabela pessoa, caso voc� informe <b>"nome, idade"</b>, isto resultar� em -> <br>
     *                             <b>INSERT INTO pessoa (codigo, <i>nome, idade</i>)</b>. <br><br>
     *                             Vale ressaltar que {@link UnificadorConstantes#COLUNA_CODIGO} sempre ser� especificada.
     * @param colunasInseriveisSQL uma lista de {@link ColunasInseriveisSQL}, ao qual ser� resgatado os valores a serem
     *                             inseridos mediante {@link ColunasInseriveisSQL#colunasValorString()}
     */
    public void inserirApartirUltimoCodigo(ConnectionUnificacao conexaoDestino,
                                           String colunasParaInserir,
                                           List<ColunasInseriveisSQL> colunasInseriveisSQL) throws Exception {
        int ultimoCodigoTabelaDestino = consultarUltimoCodigo(conexaoDestino, unificadorFilhoExecucaoRodada.getNomeTabelaAlvo());
        ultimoCodigoTabelaDestinoExecucaoRodada = ultimoCodigoTabelaDestino;

        final boolean devePreencherMapCodigoOrigemDestino = devePreencherMapCodigoOrigemDestino(colunasInseriveisSQL.get(0));

        StringBuilder sb = new StringBuilder();
        for (ColunasInseriveisSQL colunas : colunasInseriveisSQL) {
            ultimoCodigoTabelaDestino++;
            montarValoresInserirApartirUltimoCodigo(sb, colunas, ultimoCodigoTabelaDestino, devePreencherMapCodigoOrigemDestino);
        }

        final String finalValues = sb.toString().replaceAll(", $", "");

        _inserirApartirUltimoCodigo(conexaoDestino, colunasParaInserir, finalValues, devePreencherMapCodigoOrigemDestino);
    }

    // ============================== Protected ==============================

    /**
     * <h1>Deve ser chamado em um m�todo Main.</h1>
     */
    protected void iniciar() {
        executarOperacoes();
    }

    // ============================== Private ==============================

    /**
     * <ul>
     * <li>Cria as conex��es de <b>ORIGEM</b> e <b>DESTINO</b></li>
     * <li>Configura se � transacional ou n�o</li>
     * <li>Executa s unifica��es a partir de: {@link UnificadorFilho#executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)}</li>,
     * dado uma lista de {@link #criarSequenciaOrquestradaunificadoresFilhos()}.
     * <li>Trata exce��es, dependendo se � transacional ou n�o e loga as falhas</li>
     * </ul>
     */
    private void executarOperacoes() {
        logar(MSG_INFO_CRIANDO_CONEXOES);
        ConnectionUnificacao conexaoOrigem = getConnectionunificacaoDatabase(ORIGEM);
        ConnectionUnificacao conexaoDestino = getConnectionunificacaoDatabase(DESTINO);
        logar(MSG_INFO_CONEXOES_CRIADA);

        try {
            executarProcedimentosAntesunificacao(conexaoOrigem, conexaoDestino);

            mapaunificadoresFilhos = constuirMapaSequenciaunificadoresFilhos();
            for (Class<? extends UnificadorFilho> clazz : mapaunificadoresFilhos.keySet()) {
                unificadorFilhoExecucaoRodada = mapaunificadoresFilhos.get(clazz);
                validarunificadorFilhoExecucaoRodada(unificadorFilhoExecucaoRodada);

                final String nameunificadorFilho = unificadorFilhoExecucaoRodada.getClass().getSimpleName();

                logar(MSG_INFO_UNIFICACAO_FILHA_INICIANDO, nameunificadorFilho);
                unificadorFilhoExecucaoRodada.executar(this, conexaoOrigem, conexaoDestino);
                logar(MSG_INFO_UNIFICACAO_FILHA_FINALIZADO, nameunificadorFilho);
            }

            executarProcedimentosDepoisunificacao();

            encerrarTransacao(conexaoDestino, parametros.getOperacaoDestinoTransacionalOpcao());
        } catch (Exception e) {
            tratarExcecaoExecucaounificacao(conexaoOrigem, conexaoDestino, e);
        } finally {
            encerrarConexoes(conexaoOrigem, conexaoDestino);
            logar(MSG_INFO_FIM_EXECUCAO_PROCESSO, getClass().getName());
            logar(MSG_INFO_OBJETO_AUDITORIA_CONSTRUIDA, auditoriaunificacaoDados);
        }
    }

    private LinkedHashMap<Class<? extends UnificadorFilho>, UnificadorFilho> constuirMapaSequenciaunificadoresFilhos() {
        LinkedHashMap<Class<? extends UnificadorFilho>, UnificadorFilho> map = new LinkedHashMap<Class<? extends UnificadorFilho>, UnificadorFilho>();

        for (UnificadorFilho unificador : criarSequenciaOrquestradaunificadoresFilhos()) {
            map.put(unificador.getClass(), unificador);
        }

        return map;
    }

    private void executarProcedimentosAntesunificacao(ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino) throws SQLException {
        conexaoOrigem.getConnection().setAutoCommit(true);
        conexaoDestino.getConnection().setAutoCommit(definirTransacaoConexaoDestino(parametros.getOperacaoDestinoTransacionalOpcao()));

        contadorExecucaoInstrucaoSql = 0;

        iniciarCronometro(TEMPO_TOTAL_UNIFICACAO_THREADLOCAL);

        auditoriaunificacaoDados.setDataIniciounificacao(Calendario.hoje());
    }

    private void executarProcedimentosDepoisunificacao() {
        auditoriaunificacaoDados.setDataFimunificacao(Calendario.hoje());

        final long tempoDuracao = encerrarCronometro(TEMPO_TOTAL_UNIFICACAO_THREADLOCAL);
        auditoriaunificacaoDados.setTempoExecucaounificacaoSegundos((float) formartarTempoCronometro(tempoDuracao));
        logar(MSG_INFO_TEMPO_TOTAL_UNIFICACAO, formartarTempoCronometroComLabel(tempoDuracao));
    }

    private void encerrarTransacao(ConnectionUnificacao conexaoDestino, OperacaoDestinoTransacionalOpcao operacaoDestinoTransacionalOpcao) throws SQLException {
        switch (operacaoDestinoTransacionalOpcao) {
            case TRUE:
                conexaoDestino.getConnection().commit();
                logar(MSG_INFO_COMMIT_REALIZADO, conexaoDestino.getDirecaoConexao());
                break;

            case SIMULACAO:
                conexaoDestino.getConnection().rollback();
                logar(MSG_INFO_SIMULACAO_REALIZADA, conexaoDestino.getDirecaoConexao());
                break;
        }
    }

    /**
     * @return <b>TRUE</b> se conseguiu preencher, ou seja, significa que trata-se de um unificador {@link TabelaReferenciavel} e que possui {@link CodigoOrigemRetornavel}.
     */
    private void preencherMapCodigoOrigemDestino(ColunasInseriveisSQL colunasInseriveisSQL, Integer codigoDestino) {
        final TabelaReferenciavel tabelaReferenciavel = getunificadorFilhoExecucaoRodadaAsTabelaReferenciavel();
        final CodigoOrigemRetornavel codigoOrigemRetornavel = getColunasInseriveisAsCodigoOrigemRetornavel(colunasInseriveisSQL);

        assert tabelaReferenciavel != null;
        assert codigoOrigemRetornavel != null;

        final MapaCodigoOrigemDestino mapCodigoOrigemDestino = tabelaReferenciavel.getMapCodigoOrigemDestino();

        final Integer codigoOrigem = codigoOrigemRetornavel.getCodigoOrigem();
        validarMatematicamenteCodigosunificacao(codigoOrigem, codigoDestino);

        mapCodigoOrigemDestino.putCodigoDestinoChaveadoPorCodigoOrigem(codigoOrigem, codigoDestino);
    }

    private boolean definirTransacaoConexaoDestino(OperacaoDestinoTransacionalOpcao operacaoDestinoTransacionalOpcao) {
        logar(MSG_INFO_CONEXAO_DESTINO_TRANSACIONAL, operacaoDestinoTransacionalOpcao);

        logarDetalhesTransacaoConexaoDestino(operacaoDestinoTransacionalOpcao);
        switch (operacaoDestinoTransacionalOpcao) {
            case TRUE:
            case SIMULACAO:
                return false;

            case FALSE:
                return true;

            default:
                throw new UnificadorGenericException(format("N�o foi poss�vel definir o tipo de transa��o da conex�o de destino, dado a dire��o informada: %s",
                        operacaoDestinoTransacionalOpcao));

        }
    }

    private void logarDetalhesTransacaoConexaoDestino(OperacaoDestinoTransacionalOpcao operacaoDestinoTransacionalOpcao) {
        switch (operacaoDestinoTransacionalOpcao) {
            case TRUE:
                logar(MSG_INFO_EXECUTAR_COMMIT);
                break;

            case SIMULACAO:
                logar(MSG_INFO_EXECUTAR_ROLLBACK);
                break;

            case FALSE:
                logar(MSG_INFO_AUTO_COMMIT);
                break;
        }
    }

    private ResultSet executarComFeedback(OperacaoSQL operacaoSQL, String descricaoInstrucao, ConnectionUnificacao connection, String sql) throws SQLException {
        contadorExecucaoInstrucaoSql++;

        logar(MSG_INFO_EXECUTANDO_PROCESSO, connection.getDirecaoConexao(), contadorExecucaoInstrucaoSql, descricaoInstrucao);

        iniciarCronometro(TEMPO_OPERACAO_THREADLOCAL);
        ResultSet resultSet = executarSQL(operacaoSQL, connection, sql);

        logar(MSG_INFO_PROCESSO_EXECUTADO, contadorExecucaoInstrucaoSql, encerrarCronometroString(TEMPO_OPERACAO_THREADLOCAL));

        auditoriaunificacaoDados.getOperacoesExecutadas().add("(" + connection.getDirecaoConexao() + ") -> " + descricaoInstrucao + "\n\n" + sql);

        return resultSet;
    }

    @Nullable
    private ResultSet executarSQL(OperacaoSQL operacaoSQL, ConnectionUnificacao connection, String sql) throws SQLException {
        ResultSet resultSet = null;

        switch (operacaoSQL) {
            case SELECT:
                if (isNotConsultaAgrupadora(sql)) {
                    sql += ORDER_BY + COLUNA_CODIGO;
                }
                resultSet = connection.getConnection().prepareStatement(sql).executeQuery();
                break;

            case UPDATE:
                int quantidadeLinhasAfetadas = connection.getConnection().prepareStatement(sql).executeUpdate();
                logar(MSG_INFO_LINHAS_AFETADAS, quantidadeLinhasAfetadas);
                break;

            case INSERT:
                int quantidadeLinhasInseridas = connection.getConnection().prepareStatement(sql).executeUpdate();
                logar(MSG_INFO_LINHAS_INSERIDAS, quantidadeLinhasInseridas);
                break;

            default:
                throw new UnificadorGenericException(
                        format(
                                "N�o foi poss�vel executar o SQL, pois o tipo de opera��o sql (%s) n�o foi implementado!", operacaoSQL
                        )
                );
        }

        return resultSet;
    }

    private void tratarExcecaoExecucaounificacao(ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino, Exception e) {
        try {
            logarFalhaTransacao(MSG_FALHA_EXECUCAO_PROCESSO, e);
            executarRollbackConexoes(conexaoOrigem, conexaoDestino);
        } catch (SQLException ex) {
            throw logarLancarFalhaTransacao(MSG_FALHA_ROLLBACK, ex);
        }
    }

    private void executarRollbackConexoes(ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino) throws SQLException {
        _executarRollbackConexao(conexaoOrigem);
        _executarRollbackConexao(conexaoDestino);
    }

    private void _executarRollbackConexao(ConnectionUnificacao conexao) throws SQLException {
        try {
            if (conexao.getConnection().getTransactionIsolation() != Connection.TRANSACTION_NONE) {
                conexao.getConnection().rollback();
                logar(MSG_INFO_ROLLBACK_SUCESSO, conexao.getDirecaoConexao());
            }
        } catch (PSQLException ignore) {
            // O m�todo 'getTransactionIsolation()' lan�a uma exce��o quando a transa��o j� est� inacess�vel.
        }
    }

    private void encerrarConexoes(ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino) {
        try {
            if (!conexaoDestino.getConnection().isClosed()) {
                conexaoDestino.getConnection().close();
                logar(MSG_INFO_FECHAMENTO_CONEXAO_SUCESSO, conexaoDestino.getDirecaoConexao());
            }

            if (!conexaoOrigem.getConnection().isClosed()) {
                conexaoOrigem.getConnection().close();
                logar(MSG_INFO_FECHAMENTO_CONEXAO_SUCESSO, conexaoOrigem.getDirecaoConexao());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logar(MSG_FALHA_CLOSE_CONNECTION);
        }
    }

    private FalhaTransacaoException logarLancarFalhaTransacao(String mensagem, Exception e) {
        FalhaTransacaoException exception = criarTransacaoException(mensagem, e);
        logar(exception, getClass());

        return exception;
    }

    private void logarFalhaTransacao(String mensagem, Exception e) {
        logar(criarTransacaoException(mensagem, e), getClass());
    }

    private FalhaTransacaoException criarTransacaoException(String mensagem, Exception e) {
        return new FalhaTransacaoException(mensagem, e);
    }

    private ConnectionUnificacao getConnectionunificacaoDatabase(DirecaoConexao direcaoConexao) {
        return new ConnectionUnificacao(getConnectionDatabase(direcaoConexao), direcaoConexao);
    }

    private Connection getConnectionDatabase(DirecaoConexao direcaoConexao) {
        switch (direcaoConexao) {
            case ORIGEM:
                return getConnectionunificacaoDatabase(
                        builderUrlBancoPorDirecaoConexao(direcaoConexao), parametros.getUserDatabaseOrigem(), parametros.getPasswordDatabaseOrigem(), direcaoConexao
                );

            case DESTINO:
                return getConnectionunificacaoDatabase(
                        builderUrlBancoPorDirecaoConexao(direcaoConexao), parametros.getUserDatabaseDestino(), parametros.getPasswordDatabaseDestino(), direcaoConexao
                );

            default:
                throw new UnificadorGenericException(format("N�o foi poss�vel criar uma conex�o dado a dire��o informada: %s", direcaoConexao));
        }
    }

    private Connection getConnectionunificacaoDatabase(String url, String usarDatabase, String passwordDatabase, DirecaoConexao direcaoConexao) {
        try {
            logar(MSG_INFO_CRIANDO_CONEXAO_URL, direcaoConexao, url);
            return DriverManager.getConnection(url, usarDatabase, passwordDatabase);
        } catch (SQLException e) {
            throw new FalhaAbrirConexaoJDBCException(ORIGEM, url, e);
        }
    }

    private String builderUrlBancoPorDirecaoConexao(DirecaoConexao direcaoConexao) {
        switch (direcaoConexao) {
            case ORIGEM:
                return _builderUrlBanco(parametros.getHostAndPortOrigem(), parametros.getDatabaseNameOrigem());
            case DESTINO:
                return _builderUrlBanco(parametros.getHostAndPortDestino(), parametros.getDatabaseNameDestino());

            default:
                throw new UnificadorGenericException(format("N�o foi poss�vel constuir a url de conex�o dado a dire��o informada: %s", direcaoConexao));
        }
    }

    private String _builderUrlBanco(String hostAndPort, String databaseName) {
        return JDBC_PROTOCOL + hostAndPort + "/" + databaseName;
    }

    private void validarunificadorFilhoExecucaoRodada(UnificadorFilho unificadorFilhoExecucaoRodada) {
        if (unificadorFilhoExecucaoRodada == null) {
            throw new UnificadorGenericException(MSG_FALHA_NAO_FOI_POSSIVEL_IDENTIFICAR_unificador_FILHO);
        }
    }

    /**
     * Teoricamente em uma opera��o de unifica��o aonde a consulta de origem foi feito ordenado pelo c�digo,
     * o <code>codigoDestino</code> sempre:
     *
     * <ul>
     * <li>Ser� maior que o <code>codigoOrigem</code></li>
     * <li>Seu valor subtra�do pelo <code>codigoOrigem</code>, ser� sempre o {@link #ultimoCodigoTabelaDestinoExecucaoRodada}</li>
     * </ul>
     *
     * @throws UnificadorGenericException quando o resultado da subtra��o for diferente de {@link #ultimoCodigoTabelaDestinoExecucaoRodada}.
     */
    private void validarMatematicamenteCodigosunificacao(Integer codigoOrigem, Integer codigoDestino) throws UnificadorGenericException {
        if (codigoDestino - codigoOrigem != ultimoCodigoTabelaDestinoExecucaoRodada) {
            throw new UnificadorGenericException(
                    format(
                            "O c�digo de origem e de destino n�o correspondem entre si, dado a sequ�ncia que deveriam seguir."
                                    + "\nO c�digo de destino (%s) menos o c�digo de origem (%s), deveria ser a diferen�a do �ltimo c�digo consultado, que foi (%s).",
                            codigoDestino, codigoOrigem, ultimoCodigoTabelaDestinoExecucaoRodada
                    )
            );
        }
    }

    private void logarArgumentosDefault(String[] args) {
        String arrayCadaArgsEmUmaLinha = Arrays.toString(args).replace(",", ",\n");
        logar(MSG_INFO_PARAMETROS_ARGUMENTOS, arrayCadaArgsEmUmaLinha);
    }

    private void recuperarInformacoesMaquinaExecutora() {
        iniciarCronometro(TEMPO_INFORMACOES_MAQUINA_EXECUTORA);
        final InformacaoMaquinaExecutora informacoesMaquinaExecutora = InformacaoMaquinaExecutoraFactory.getInstance().getInformacoesMaquinaExecutora();
        final String tempoExecucao = encerrarCronometroString(TEMPO_INFORMACOES_MAQUINA_EXECUTORA);

        logar(MSG_INFO_INFORMACOES_MAQUINA_EXECUTORA, informacoesMaquinaExecutora);
        logar(MSG_INFO_TEMPO_INFORMACOES_MAQUINA_EXECUTORA, tempoExecucao);

        auditoriaunificacaoDados.setUsuarioMaquinaExecutora(informacoesMaquinaExecutora.getUsuarioMaquina());
        auditoriaunificacaoDados.setHostNameMaquinaExecutora(informacoesMaquinaExecutora.getHostnameLocal());

    }

    private boolean isNotConsultaAgrupadora(String sql) {
        return !sql.toUpperCase().contains(MAX.trim()) &&
                !sql.toUpperCase().contains(COUNT.trim()) &&
                !sql.toUpperCase().contains(MIN.trim()) &&
                !sql.toUpperCase().contains(GROUP_BY.trim());
    }

    private TabelaReferenciavel getunificadorFilhoExecucaoRodadaAsTabelaReferenciavel() {
        if (unificadorFilhoExecucaoRodada != null && unificadorFilhoExecucaoRodada instanceof TabelaReferenciavel) {
            return (TabelaReferenciavel) unificadorFilhoExecucaoRodada;
        }

        return null;
    }

    private CodigoOrigemRetornavel getColunasInseriveisAsCodigoOrigemRetornavel(ColunasInseriveisSQL colunasInseriveisSQL) {
        if (colunasInseriveisSQL instanceof CodigoOrigemRetornavel) {
            return (CodigoOrigemRetornavel) colunasInseriveisSQL;
        }

        return null;
    }

    private MapaCodigoOrigemDestino getMapaCodigoOrigemDestinounificadorFilho() {
        final TabelaReferenciavel tabelaRefenciavel = getunificadorFilhoExecucaoRodadaAsTabelaReferenciavel();

        return tabelaRefenciavel != null ? tabelaRefenciavel.getMapCodigoOrigemDestino() : null;
    }

    private void montarValoresInserirApartirUltimoCodigo(StringBuilder sb, ColunasInseriveisSQL colunas, int ultimoCodigoTabelaDestino,
                                                         boolean devePreencherMapCodigoOrigemDestino) {
        if (devePreencherMapCodigoOrigemDestino) {
            preencherMapCodigoOrigemDestino(colunas, ultimoCodigoTabelaDestino);
        }

        sb.append("(")
                .append(ultimoCodigoTabelaDestino).append(", ")
                .append(colunas.colunasValorString());

        if (devePreencherMapCodigoOrigemDestino) {
            sb.append(", ").append(getColunasInseriveisAsCodigoOrigemRetornavel(colunas).getCodigoOrigem());
        }

        sb.append("), ");
    }

    private void _inserirApartirUltimoCodigo(ConnectionUnificacao conexaoDestino,
                                             String colunasParaInserir,
                                             String finalValues,
                                             boolean devePreencherMapCodigoOrigemDestino) throws Exception {
        if (devePreencherMapCodigoOrigemDestino) {
            final String descricaoCriacaoColuna = "Criando a coluna " + COLUNA_ID_EXTERNO + " em " + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + ".";
            executarUpdateComFeedback(descricaoCriacaoColuna, conexaoDestino,
                    "ALTER TABLE " + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + " ADD COLUMN " + COLUNA_ID_EXTERNO + " INTEGER"
            );
        }

        final String descricaoInsercaoRegistros = "Inserindo registros em '" + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + "'.";
        final String colunasParaInserirFinal = devePreencherMapCodigoOrigemDestino ?
                " (codigo, " + colunasParaInserir + ", " + COLUNA_ID_EXTERNO + ")"
                : " (codigo, " + colunasParaInserir + ")";

        executarInsertComFeedback(descricaoInsercaoRegistros, conexaoDestino,
                "INSERT INTO " + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + colunasParaInserirFinal + " VALUES " + finalValues
        );
    }

    /**
     * @return <b>TRUE</b> se conseguiu preencher, ou seja, significa que trata-se de um unificador {@link TabelaReferenciavel} e que possui {@link CodigoOrigemRetornavel}.
     */
    private boolean devePreencherMapCodigoOrigemDestino(ColunasInseriveisSQL colunasInseriveisSQL) {
        final TabelaReferenciavel tabelaReferenciavel = getunificadorFilhoExecucaoRodadaAsTabelaReferenciavel();
        final CodigoOrigemRetornavel codigoOrigemRetornavel = getColunasInseriveisAsCodigoOrigemRetornavel(colunasInseriveisSQL);

        if (tabelaReferenciavel != null) {
            if (codigoOrigemRetornavel == null) {
                throw new TabelaReferenciavelSemCodigoOrigemRetornavelException(unificadorFilhoExecucaoRodada);
            }

            return true;
        }

        return false;
    }

}
