package unificacao;

import unificacao.enums.DirecaoConexao;
import unificacao.enums.OperacaoDestinoTransacionalOpcao;
import unificacao.exception.CampoSemNomeColunaException;
import unificacao.exception.FalhaAbrirConexaoJDBCException;
import unificacao.exception.FalhaTransacaoException;
import unificacao.exception.UnificadorGenericException;
import unificacao.metadata.ChaveEstrangeiraFK;
import unificacao.metadata.NomeColuna;
import unificacao.parametrizacao.ConjuntoParametrosObrigatorioUnificacao;
import unificacao.parametrizacao.ConjuntoParametrosObrigatorioUnificacaoFactory;
import unificacao.parametrizacao.ParametroObrigatorioUnificacaoEnum;
import unificacao.to.AuditoriaUnificacaoDadosTO;
import unificacao.wrapper.ConnectionUnificacao;
import unificacao.wrapper.MapaCodigoOrigemDestino;
import negocio.comuns.utilitarias.Calendario;
import negocio.comuns.utilitarias.Uteis;
import negocio.comuns.utilitarias.info_execucao.InformacaoMaquinaExecutora;
import negocio.comuns.utilitarias.info_execucao.InformacaoMaquinaExecutoraFactory;
import org.postgresql.util.PSQLException;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static unificacao.UnificadorConstantes.*;
import static unificacao.UnificadorConstantes.OperacaoSQL.*;
import static unificacao.UnificadorConstantes.PojoMethod.GET;
import static unificacao.UnificadorConstantes.PojoMethod.SET;
import static unificacao.enums.DirecaoConexao.DESTINO;
import static unificacao.enums.DirecaoConexao.ORIGEM;
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
 * <li>Atrav�s do m�todo utilit�rio {@link #realizarUnificacaoViaReflection(ConnectionUnificacao, ConnectionUnificacao, Class)},
 * � poss�vel que as consultas e inser��es da unifica��o da tabela em quest�o e tamb�m considerando as refer�ncias de chave estrangeiras, sejam completamente feitas via Reflection</li>
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
     * Representa o migrador que est� sendo executado na rodada, este migrador, que veio de {@link #criarSequenciaOrquestradaunificadoresFilhos()}.
     */
    private UnificadorFilho unificadorFilhoExecucaoRodada;

    /**
     * Respons�vel por manter em mem�ria e fornecer em f�cil acesso o {@link TabelaReferenciavel#getMapCodigoOrigemDestino()}, para que
     * se possa fazer a unifica��o de {@link ChaveEstrangeiraFK}.
     */
    private LinkedHashMap<Class<? extends UnificadorFilho>, UnificadorFilho> mapaUnificadoresFilhos;

    // ============================== Entidades ==============================

    /**
     * TO respons�vel por guardar dados para uma futura auditoria.
     */
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
     * </h2>Para uso deste m�todo, � necess�rio respeitar a especifica��o detalhada em {@link Unificavel}.</h2>
     *
     * Este m�todo auxiliar far�:
     *
     * <ul>
     * <li>
     * 1. Consulta todos as colunas da inst�ncia <code>clazz</code> em quest�o, levando em considera��o os campos que est�o
     * anotados com {@link NomeColuna}. Tal � realizado em {@link #consultarByNomeColuna(ConnectionUnificacao, Class)}.
     * </li>
     * <li>
     * 2. Consulta na tabela de {@link DirecaoConexao#DESTINO} para saber qual � o �ltimo c�digo, atrav�s de {@link #consultarUltimoCodigo(ConnectionUnificacao, String)}.
     * </li>
     * <li>
     * 2. Cria um mapa para armazenar o c�digo original e o c�digo novo a ser gerado para o registro que est� sendo unificado.
     * </li>
     * <li>
     * 4. Cria uma coluna: {@link UnificadorConstantes#COLUNA_ID_EXTERNO} respons�vel por armazenar o c�digo original da unifica��o.
     * </li>
     * <li>
     * 4. Realiza a unifica��o dos dados mediante a massa de dados em {@link #executarOperacoesInserirApartirUltimoCodigo(ConnectionUnificacao, String, String)}.
     * </li>
     * </ul>
     *
     * @param conexaoDestino que ser� inserido.
     * @param clazz          uma classe do tipo {@link Unificavel}, que ser� usada como base para recuperar os {@link NomeColuna}.
     */
    public void realizarUnificacaoViaReflection(ConnectionUnificacao conexaoOrigem,
                                                ConnectionUnificacao conexaoDestino,
                                                Class<? extends Unificavel> clazz) throws Exception {
        final List<Unificavel> unificaveis = consultarByNomeColuna(conexaoOrigem, clazz);

        if (!unificaveis.isEmpty()) {
            int ultimoCodigoTabelaDestino = consultarUltimoCodigo(conexaoDestino, unificadorFilhoExecucaoRodada.getNomeTabelaAlvo());

            StringBuilder sb = new StringBuilder();
            for (Unificavel unificavel : unificaveis) {
                ultimoCodigoTabelaDestino++;
                montarValoresInserirApartirUltimoCodigo(sb, unificavel, ultimoCodigoTabelaDestino);
            }

            final String finalValues = retornarStringRemovendoUltimoConcatenador(sb.toString());

            executarOperacoesInserirApartirUltimoCodigo(
                    conexaoDestino,
                    getCamposAnotadoComNomeColunasConcatenados(unificaveis.get(0).getClass()),
                    finalValues
            );
        } else {
            throw new UnificadorGenericException(MSG_FALHA_LISTA_MSG_COLUNAS_SQL_VAZIA);
        }
    }

    /**
     * Respons�vel por iterar todo o <code>resultSet</code> e criar uma lista do tipo <code>clazz</code> informado,
     * com os valores preenchidos no mesmo, atrav�s da invoca��o do m�todo {@link PojoMethod#SET} via <b>Reflection</b>.
     */
    private List<Unificavel> getListByIterateResultSet(ResultSet resultSet, Class<? extends Unificavel> clazz) throws SQLException {
        List<Unificavel> listaUnificaveis = new ArrayList<Unificavel>();

        final Map<String, Method> mapMetodosSet = mapearMetodosSetClasse(clazz);
        final LinkedHashSet<Field> colunasAlvos = getCamposAnotadoComNomeColuna(clazz);

        while (nextResult(resultSet)) {
            try {
                final Unificavel unificavelInstance = clazz.newInstance();

                for (Field field : colunasAlvos) {
                    Object value = getResultSetByType(resultSet, field);
                    final Method method = mapMetodosSet.get(SET.name().toLowerCase() + field.getName().toLowerCase());

                    if (method != null) {
                        method.invoke(unificavelInstance, value);
                    } else {
                        throw new UnificadorGenericException(
                                format(
                                        MSG_FALHA_METODO_NAO_ENCONTRADO_VIA_REFLECTION,
                                        SET,
                                        field.getName()
                                )
                        );
                    }
                }

                listaUnificaveis.add(unificavelInstance);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        return listaUnificaveis;
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

            mapaUnificadoresFilhos = constuirMapaSequenciaunificadoresFilhos();
            for (Class<? extends UnificadorFilho> clazz : mapaUnificadoresFilhos.keySet()) {
                unificadorFilhoExecucaoRodada = mapaUnificadoresFilhos.get(clazz);
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

    private void preencherMapCodigoOrigemDestino(Unificavel unificavel, Integer codigoDestino) {
        final TabelaReferenciavel tabelaReferenciavel = getunificadorFilhoExecucaoRodadaAsTabelaReferenciavel();
        final CodigoOrigemRetornavel codigoOrigemRetornavel = getColunasInseriveisAsCodigoOrigemRetornavel(unificavel);

        assert tabelaReferenciavel != null;
        assert codigoOrigemRetornavel != null;

        final MapaCodigoOrigemDestino mapCodigoOrigemDestino = tabelaReferenciavel.getMapCodigoOrigemDestino();

        final Integer codigoOrigem = codigoOrigemRetornavel.getCodigo();

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

    private CodigoOrigemRetornavel getColunasInseriveisAsCodigoOrigemRetornavel(Unificavel unificavel) {
        if (unificavel instanceof CodigoOrigemRetornavel) {
            return (CodigoOrigemRetornavel) unificavel;
        }

        return null;
    }

    private void montarValoresInserirApartirUltimoCodigo(StringBuilder sb, Unificavel unificavel, int ultimoCodigoTabelaDestino) {
        preencherMapCodigoOrigemDestino(unificavel, ultimoCodigoTabelaDestino);

        sb.append("(")
                .append(ultimoCodigoTabelaDestino).append(", ")
                .append(getValoresCamposAnotadoComNomeColunasConcatenadosParaSQL(unificavel)).append(", ")
                .append(getColunasInseriveisAsCodigoOrigemRetornavel(unificavel).getCodigo())
                .append("), ");
    }

    private List<Unificavel> consultarByNomeColuna(ConnectionUnificacao conexaoOrigem, Class<? extends Unificavel> clazz) throws Exception {
        ResultSet rs = executarConsultaComFeedbackOrdenadoCrescentementePeloCodigo(
                "Consultando todos os registros de " + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + ".",
                conexaoOrigem,
                SELECT + " " + getCamposAnotadoComNomeColunasConcatenados(clazz) + FROM + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo());

        return getListByIterateResultSet(rs, clazz);
    }

    private void executarOperacoesInserirApartirUltimoCodigo(ConnectionUnificacao conexaoDestino,
                                                             String colunasParaInserir,
                                                             String finalValues) throws Exception {
        final String descricaoCriacaoColuna = "Criando a coluna " + COLUNA_ID_EXTERNO + " em " + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + ".";
        executarUpdateComFeedback(descricaoCriacaoColuna, conexaoDestino,
                ALTER_TABLE + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + ADD_COLUMN_COLUNA_ID_EXTERNO_INTEGER
        );

        final String descricaoInsercaoRegistros = "Inserindo registros em '" + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + "'.";
        final String colunasParaInserirFinal = " (" + colunasParaInserir + ", " + COLUNA_ID_EXTERNO + ")";

        executarInsertComFeedback(descricaoInsercaoRegistros, conexaoDestino,
                INSERT_INTO + unificadorFilhoExecucaoRodada.getNomeTabelaAlvo() + colunasParaInserirFinal + VALUES + finalValues
        );
    }

    /**
     * @return os valores da annotation {@link NomeColuna} dos campos de {@link Unificavel}.
     */
    private String getCamposAnotadoComNomeColunasConcatenados(Class<? extends Unificavel> clazz) {
        StringBuilder sb = new StringBuilder();

        for (String nomeColunasAlvo : getCamposAnotadoComNomeColunas(clazz)) {
            sb.append(nomeColunasAlvo).append(", ");
        }

        return retornarStringRemovendoUltimoConcatenador(sb.toString());
    }

    /**
     * @return os valores dos campos de {@link Unificavel} que est�o anotados com {@link NomeColuna}.
     */
    private String getValoresCamposAnotadoComNomeColunasConcatenadosParaSQL(Unificavel unificavel) {
        StringBuilder sb = new StringBuilder();
        final Class<? extends Unificavel> clazz = unificavel.getClass();

        final Map<String, Method> mapMetodosGet = mapearMetodosGetClasse(clazz);

        for (Field field : getCamposAnotadoComNomeColuna(clazz)) {
            if (!field.getName().equalsIgnoreCase(COLUNA_CODIGO)) {
                getValorCampoAnotadoParaSQL(sb, field, unificavel, mapMetodosGet);
            }
        }

        return retornarStringRemovendoUltimoConcatenador(sb.toString());
    }

    private void getValorCampoAnotadoParaSQL(StringBuilder sb, Field field, Unificavel unificavel,
                                             Map<String, Method> mapMetodosGet) {
        final ChaveEstrangeiraFK campoChaveEstrangeira = field.getAnnotation(ChaveEstrangeiraFK.class);
        if (campoChaveEstrangeira != null) {
            final UnificadorFilho unificadorFilho = mapaUnificadoresFilhos.get(campoChaveEstrangeira.tabelaReferenciavelName());

            if (unificadorFilho != null) {
                final Integer valorCampoChaveEstrangeiraFK = getValorCampoChaveEstrangeiraFK(unificavel, mapMetodosGet, field);

                if (valorCampoChaveEstrangeiraFK != 0) { // 0 significa que est� NULL no banco
                    final Integer codigoOrigemUnificado = ((TabelaReferenciavel) unificadorFilho)
                            .getMapCodigoOrigemDestino()
                            .getPorCodigoOrigem(valorCampoChaveEstrangeiraFK);

                    sb.append(codigoOrigemUnificado);
                } else {
                    sb.append(NULL);
                }
            } else {
                throw new UnificadorGenericException(
                        format(
                                MSG_FALHA_INSTANCIA_MIGRADOR_NAO_ENCONTRADA,
                                campoChaveEstrangeira.tabelaReferenciavelName(),
                                mapaUnificadoresFilhos
                        )
                );
            }

        } else {
            sb.append(returnValueInSqlByType(unificavel, field, mapMetodosGet));
        }

        sb.append(", ");
    }

    private Integer getValorCampoChaveEstrangeiraFK(Unificavel unificavel, Map<String, Method> mapMetodosGet, Field field) {
        try {
            return (Integer) mapMetodosGet.get(GET.name().toLowerCase() + field.getName().toLowerCase()).invoke(unificavel);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        throw new UnificadorGenericException(
                format(
                        MSG_FALHA_NAO_CONSEGUI_INVOCAR_METODO_GET_COLUNA_FK,
                        field.getName()
                )
        );
    }

    private LinkedHashSet<String> getCamposAnotadoComNomeColunas(Class<? extends Unificavel> clazz) {
        LinkedHashSet<String> colunas = new LinkedHashSet<String>();

        for (Field field : getCamposAnotadoComNomeColuna(clazz)) {
            colunas.add(
                    field.getAnnotation(NomeColuna.class).value()
            );
        }

        return colunas;
    }

    private LinkedHashSet<Field> getCamposAnotadoComNomeColuna(Class<? extends Unificavel> clazz) {
        LinkedHashSet<Field> campos = new LinkedHashSet<Field>();

        for (Field declaredField : clazz.getDeclaredFields()) {
            final NomeColuna nomeColuna = declaredField.getAnnotation(NomeColuna.class);

            if (nomeColuna != null) {
                campos.add(declaredField);
            } else if (isNotSintetico(declaredField)) {
                throw new CampoSemNomeColunaException(clazz, declaredField);
            }
        }

        return campos;
    }

    /**
     * @see <a href="https://javapapers.com/core-java/java-synthetic-class-method-field/">Veja sobre m�todos sint�ticos.</a>
     */
    private boolean isNotSintetico(Field declaredField) {
        return !declaredField.isSynthetic();
    }

    private String retornarStringRemovendoUltimoConcatenador(String string) {
        return string.replaceAll(", $", "");
    }

    private Object getResultSetByType(ResultSet resultSet, Field field) throws SQLException {
        final String valueFromAnnotation = field.getAnnotation(NomeColuna.class).value();

        if (field.getType().equals(String.class)) {
            return resultSet.getString(valueFromAnnotation);
        } else if (field.getType().equals(Integer.class)) {
            return resultSet.getInt(valueFromAnnotation);
        } else if (field.getType().equals(Date.class)) {
            return resultSet.getTimestamp(valueFromAnnotation);
        } else if (field.getType().equals(Boolean.class)) {
            return resultSet.getBoolean(valueFromAnnotation);
        }

        return null;
    }

    private String returnValueInSqlByType(Unificavel unificavel, Field field, Map<String, Method> mapMetodosGet) {
        final Method method = mapMetodosGet.get(GET.name().toLowerCase() + field.getName().toLowerCase());
        try {
            if (method != null) {
                final Object value = method.invoke(unificavel);

                if (value != null) {
                    if (field.getType().equals(String.class)) {
                        return "'" + value + "'";
                    } else if (field.getType().equals(Integer.class)) {
                        return value.toString();
                    } else if (field.getType().equals(Date.class)) {
                        return "'" + value + "'";
                    } else if (field.getType().equals(Boolean.class)) {
                        return value.toString();
                    }
                }
            } else {
                throw new UnificadorGenericException(
                        format(
                                MSG_FALHA_METODO_NAO_ENCONTRADO_VIA_REFLECTION,
                                GET,
                                field.getName()
                        )
                );
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, Method> mapearMetodosGetClasse(Class<? extends Unificavel> clazz) {
        return _mapearMetodosClasse(clazz, GET);
    }

    private Map<String, Method> mapearMetodosSetClasse(Class<? extends Unificavel> clazz) {
        return _mapearMetodosClasse(clazz, SET);
    }

    private Map<String, Method> _mapearMetodosClasse(Class<? extends Unificavel> clazz, PojoMethod pojoMethod) {
        Map<String, Method> map = new HashMap<String, Method>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().startsWith(pojoMethod.name().toLowerCase())) {
                map.put(method.getName().toLowerCase(), method);
            }
        }

        return map;
    }

}
