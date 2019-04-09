package br.com.pactosolucoes.atualizadb.processo.unificacao;

import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.ConnectionUnificacao;

import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.OperacaoSQL.*;

/**
 * Constantes de uso exclusivo da classe {@link AbstractOrquestradorUnificadorDadosJDBC}.
 *
 * @author Bruno Cattany
 * @since 03/04/2019
 */
public class UnificadorConstantes {

    // ============================== Utilit�rios ==============================

    public static final String COLUNA_CODIGO = "codigo";
    public static final String COLUNA_ID_EXTERNO = "idexterno";
    public static final String FROM = " FROM ";
    static final String ORDER_BY = " ORDER BY ";
    static final String MAX = " MAX ";
    static final String COUNT = " COUNT ";
    static final String MIN = " MIN ";
    static final String GROUP_BY = " GROUP_BY ";

    // ============================== Defini��es ==============================

    static final String JDBC_PROTOCOL = "jdbc:postgresql://";

    // ============================== Cr�nometros ==============================

    /**
     * Respons�vel por contar o tempo de execu��o de uma instru��o SQL escrita em {@link UnificadorFilho#executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)}.
     */
    static final ThreadLocal<Long> TEMPO_OPERACAO_THREADLOCAL = new ThreadLocal<Long>();
    /**
     * Respons�vel por contar o tempo de execu��o de toda unifica��o escrita em {@link UnificadorFilho#executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)}.
     */
    static final ThreadLocal<Long> TEMPO_TOTAL_UNIFICACAO_THREADLOCAL = new ThreadLocal<Long>();
    /**
     * Respons�vel por contar o tempo de foi gasto recolhendo as informa��es da m�quina executora.
     */
    static final ThreadLocal<Long> TEMPO_INFORMACOES_MAQUINA_EXECUTORA = new ThreadLocal<Long>();

    // ============================== Mensagens ==============================

    static final String MSG_INFO_INFORMACOES_MAQUINA_EXECUTORA = "Informa��es da m�quina executora: {\n%s\n}\n";
    static final String MSG_INFO_TEMPO_INFORMACOES_MAQUINA_EXECUTORA = "Tempo para gerar as informa��es da m�quina: %s\n";

    // ==== Mensagens de Falhas

    static final String MSG_FALHA_EXECUCAO_PROCESSO = "Houve alguma falha durante a execu��o do processo. VOU REALIZAR ROLLBACK DAS TRANSA��ES ATIVAS!";
    static final String MSG_FALHA_ROLLBACK = "Houve alguma falha durante a execu��o do rollback!";
    static final String MSG_FALHA_CLOSE_CONNECTION = "Houve alguma falha para fechar a conex�o!";
    static final String MSG_FALHA_NAO_FOI_POSSIVEL_IDENTIFICAR_unificador_FILHO = "N�o foi poss�vel identificar o unificador filho da rodada!"
            + "\nVerifique a implementa��o de 'AbstractOrquestradorUnificadorDadosJDBC.criarSequenciaOrquestradaunificadoresFilhos'";

    // ==== Mensagens de Informa��o

    static final String MSG_INFO_PARAMETROS_ARGUMENTOS = "Os argumentos passados foram: \n%s\n";
    static final String MSG_INFO_CRIANDO_CONEXAO_URL = "Ser� criado a conex�o de %s, dado a url: %s";
    static final String MSG_INFO_CONEXOES_CRIADA = "Conex�es criadas!\n";
    static final String MSG_INFO_CRIANDO_CONEXOES = "Irei criar as conex�es...";

    static final String MSG_INFO_CONEXAO_DESTINO_TRANSACIONAL = "--> O tipo de transa��o na conex�o destino foi definida como (%s).";
    static final String MSG_INFO_EXECUTAR_COMMIT = "Sendo assim, ao final do processo ser� realizado um commit!\n";
    static final String MSG_INFO_AUTO_COMMIT = "Sendo assim, todas as opera��es ser�o auto-commitadas!\n";
    static final String MSG_INFO_EXECUTAR_ROLLBACK = "Sendo assim, ao final do processo ser� realizado um rollback!\n";

    static final String MSG_INFO_COMMIT_REALIZADO = "Conex�o de %s, teve a transa��o comitada!";
    static final String MSG_INFO_SIMULACAO_REALIZADA = "### SIMULA��O REALIZADA! Foi executado um ROLLBACK na conex�o de %s! ###";

    static final String MSG_INFO_EXECUTANDO_PROCESSO = "(%s) -> Irei executar a instru��o SQL de n�mero {#%s}, cujo a descri��o �: \"%s\"";
    static final String MSG_INFO_PROCESSO_EXECUTADO = "Instru��o de n�mero {#%s} executada com sucesso. Dura��o da instru��o: %s";
    static final String MSG_INFO_FIM_EXECUCAO_PROCESSO = "Terminei toda a unifica��o do orquestador de unifica��es: (%s)";

    private static final String NESTA_OPERACAO = "Nesta opera��o de ";
    private static final String TOTAL_DE_LINHAS = ", um total de (%s linhas) foram ";
    static final String MSG_INFO_LINHAS_CONSULTADAS = NESTA_OPERACAO + SELECT + TOTAL_DE_LINHAS + "consultadas.\n";
    static final String MSG_INFO_LINHAS_AFETADAS = NESTA_OPERACAO + UPDATE + TOTAL_DE_LINHAS + "afetadas.";
    static final String MSG_INFO_LINHAS_INSERIDAS = NESTA_OPERACAO + INSERT + TOTAL_DE_LINHAS + "inseridas.";

    static final String MSG_INFO_ROLLBACK_SUCESSO = "Realizei ROLLBACK da conex�o de %s!";
    static final String MSG_INFO_FECHAMENTO_CONEXAO_SUCESSO = "Fechei a conex�o de %s!";
    static final String MSG_INFO_TEMPO_TOTAL_UNIFICACAO = "Tempo total de excecu��o da unifica��o: %s segundos.";
    static final String MSG_INFO_OBJETO_AUDITORIA_CONSTRUIDA = "Objeto de auditoria a ser salvo: \n%s";

    static final String MSG_INFO_UNIFICACAO_FILHA_INICIANDO = "============ Ir� iniciar a unifica��o da classe filha: %s ============";
    static final String MSG_INFO_UNIFICACAO_FILHA_FINALIZADO = "============ unifica��o da classe filha %s encerrada! ============ \n\n";

    public enum OperacaoSQL {
        SELECT,
        UPDATE,
        INSERT
    }

}
