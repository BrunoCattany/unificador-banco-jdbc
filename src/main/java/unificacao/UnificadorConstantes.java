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

    // ============================== Utilitários ==============================

    public static final String COLUNA_CODIGO = "codigo";
    public static final String COLUNA_ID_EXTERNO = "idexterno";
    public static final String FROM = " FROM ";
    static final String ORDER_BY = " ORDER BY ";
    static final String MAX = " MAX ";
    static final String COUNT = " COUNT ";
    static final String MIN = " MIN ";
    static final String GROUP_BY = " GROUP_BY ";

    // ============================== Definições ==============================

    static final String JDBC_PROTOCOL = "jdbc:postgresql://";

    // ============================== Crônometros ==============================

    /**
     * Responsável por contar o tempo de execução de uma instrução SQL escrita em {@link UnificadorFilho#executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)}.
     */
    static final ThreadLocal<Long> TEMPO_OPERACAO_THREADLOCAL = new ThreadLocal<Long>();
    /**
     * Responsável por contar o tempo de execução de toda unificação escrita em {@link UnificadorFilho#executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)}.
     */
    static final ThreadLocal<Long> TEMPO_TOTAL_UNIFICACAO_THREADLOCAL = new ThreadLocal<Long>();
    /**
     * Responsável por contar o tempo de foi gasto recolhendo as informações da máquina executora.
     */
    static final ThreadLocal<Long> TEMPO_INFORMACOES_MAQUINA_EXECUTORA = new ThreadLocal<Long>();

    // ============================== Mensagens ==============================

    static final String MSG_INFO_INFORMACOES_MAQUINA_EXECUTORA = "Informações da máquina executora: {\n%s\n}\n";
    static final String MSG_INFO_TEMPO_INFORMACOES_MAQUINA_EXECUTORA = "Tempo para gerar as informações da máquina: %s\n";

    // ==== Mensagens de Falhas

    static final String MSG_FALHA_EXECUCAO_PROCESSO = "Houve alguma falha durante a execução do processo. VOU REALIZAR ROLLBACK DAS TRANSAÇÔES ATIVAS!";
    static final String MSG_FALHA_ROLLBACK = "Houve alguma falha durante a execução do rollback!";
    static final String MSG_FALHA_CLOSE_CONNECTION = "Houve alguma falha para fechar a conexão!";
    static final String MSG_FALHA_NAO_FOI_POSSIVEL_IDENTIFICAR_unificador_FILHO = "Não foi possível identificar o unificador filho da rodada!"
            + "\nVerifique a implementação de 'AbstractOrquestradorUnificadorDadosJDBC.criarSequenciaOrquestradaunificadoresFilhos'";

    // ==== Mensagens de Informação

    static final String MSG_INFO_PARAMETROS_ARGUMENTOS = "Os argumentos passados foram: \n%s\n";
    static final String MSG_INFO_CRIANDO_CONEXAO_URL = "Será criado a conexão de %s, dado a url: %s";
    static final String MSG_INFO_CONEXOES_CRIADA = "Conexões criadas!\n";
    static final String MSG_INFO_CRIANDO_CONEXOES = "Irei criar as conexões...";

    static final String MSG_INFO_CONEXAO_DESTINO_TRANSACIONAL = "--> O tipo de transação na conexão destino foi definida como (%s).";
    static final String MSG_INFO_EXECUTAR_COMMIT = "Sendo assim, ao final do processo será realizado um commit!\n";
    static final String MSG_INFO_AUTO_COMMIT = "Sendo assim, todas as operações serão auto-commitadas!\n";
    static final String MSG_INFO_EXECUTAR_ROLLBACK = "Sendo assim, ao final do processo será realizado um rollback!\n";

    static final String MSG_INFO_COMMIT_REALIZADO = "Conexão de %s, teve a transação comitada!";
    static final String MSG_INFO_SIMULACAO_REALIZADA = "### SIMULAÇÃO REALIZADA! Foi executado um ROLLBACK na conexão de %s! ###";

    static final String MSG_INFO_EXECUTANDO_PROCESSO = "(%s) -> Irei executar a instrução SQL de número {#%s}, cujo a descrição é: \"%s\"";
    static final String MSG_INFO_PROCESSO_EXECUTADO = "Instrução de número {#%s} executada com sucesso. Duração da instrução: %s";
    static final String MSG_INFO_FIM_EXECUCAO_PROCESSO = "Terminei toda a unificação do orquestador de unificações: (%s)";

    private static final String NESTA_OPERACAO = "Nesta operação de ";
    private static final String TOTAL_DE_LINHAS = ", um total de (%s linhas) foram ";
    static final String MSG_INFO_LINHAS_CONSULTADAS = NESTA_OPERACAO + SELECT + TOTAL_DE_LINHAS + "consultadas.\n";
    static final String MSG_INFO_LINHAS_AFETADAS = NESTA_OPERACAO + UPDATE + TOTAL_DE_LINHAS + "afetadas.";
    static final String MSG_INFO_LINHAS_INSERIDAS = NESTA_OPERACAO + INSERT + TOTAL_DE_LINHAS + "inseridas.";

    static final String MSG_INFO_ROLLBACK_SUCESSO = "Realizei ROLLBACK da conexão de %s!";
    static final String MSG_INFO_FECHAMENTO_CONEXAO_SUCESSO = "Fechei a conexão de %s!";
    static final String MSG_INFO_TEMPO_TOTAL_UNIFICACAO = "Tempo total de excecução da unificação: %s segundos.";
    static final String MSG_INFO_OBJETO_AUDITORIA_CONSTRUIDA = "Objeto de auditoria a ser salvo: \n%s";

    static final String MSG_INFO_UNIFICACAO_FILHA_INICIANDO = "============ Irá iniciar a unificação da classe filha: %s ============";
    static final String MSG_INFO_UNIFICACAO_FILHA_FINALIZADO = "============ unificação da classe filha %s encerrada! ============ \n\n";

    public enum OperacaoSQL {
        SELECT,
        UPDATE,
        INSERT
    }

}
