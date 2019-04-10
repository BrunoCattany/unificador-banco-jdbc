package unificacao.to;

import unificacao.AbstractOrquestradorUnificadorDadosJDBC;
import unificacao.UnificadorFilho;
import unificacao.wrapper.ConnectionUnificacao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Responsável por reunir as informações mais relevantes da unificação realizada por {@link AbstractOrquestradorUnificadorDadosJDBC}. <br>
 * <b>Seu objetivo é que possa servir de auditoria futura.</b>
 *
 * @author Bruno Cattany
 * @since 04/04/2019
 */
public class AuditoriaUnificacaoDadosTO {

    /**
     * Deve guardar a exata data de quando uma determinada unificação tenha começado.
     */
    private Date dataIniciounificacao;
    /**
     * Deve guardar a exata data de quando uma determinada unificação tenha terminado.
     */
    private Date dataFimunificacao;
    /**
     * Deve guardar o usuário da máquina que está executando uma determinada unificação.
     */
    private String usuarioMaquinaExecutora;
    /**
     * Deve guardar o nome da máquina que está executando uma determinada unificação.
     */
    private String hostNameMaquinaExecutora;
    /**
     * Deve guardar o tempo total de duração em segundos de uma determinada unificação.
     */
    private Float tempoExecucaounificacaoSegundos;
    /**
     * Deve guardar as operações executadas nas implementações de {@link UnificadorFilho#executar(AbstractOrquestradorUnificadorDadosJDBC, ConnectionUnificacao, ConnectionUnificacao)}.
     */
    private List<String> operacoesExecutadas = new LinkedList<String>();

    @Override
    public String toString() {
        return "AuditoriaUnificacaoDadosTO {"
                + "\n\tdataIniciounificacao=" + dataIniciounificacao + ","
                + "\n\tdataFimunificacao=" + dataFimunificacao + ","
                + "\n\tusuarioMaquinaExecutora=" + usuarioMaquinaExecutora + ","
                + "\n\thostNameMaquinaExecutora=" + hostNameMaquinaExecutora + ","
                + "\n\ttempoExecucaounificacaoSegundos=" + tempoExecucaounificacaoSegundos + ","
                + "\n\toperacoesExecutadas={\n" + operacoesExecutadasString() + "\n\t}"
                + "\n}";
    }

    private String operacoesExecutadasString() {
        StringBuilder sb = new StringBuilder();
        int cont = 1;
        for (String operacao : operacoesExecutadas) {
            operacao = operacao.replaceAll("\n", "\n\t\t\t");

            sb.append("\t\tOperação nº ").append(cont++).append(":\n\t\t\t").append(operacao).append("\n\n");
        }

        return isNotBlank(sb.toString())
                ? sb.substring(0, sb.toString().lastIndexOf("\n\n")) : null;
    }

    public Date getDataIniciounificacao() {
        return dataIniciounificacao;
    }

    public void setDataIniciounificacao(Date dataIniciounificacao) {
        this.dataIniciounificacao = dataIniciounificacao;
    }

    public Date getDataFimunificacao() {
        return dataFimunificacao;
    }

    public void setDataFimunificacao(Date dataFimunificacao) {
        this.dataFimunificacao = dataFimunificacao;
    }

    public String getUsuarioMaquinaExecutora() {
        return usuarioMaquinaExecutora;
    }

    public void setUsuarioMaquinaExecutora(String usuarioMaquinaExecutora) {
        this.usuarioMaquinaExecutora = usuarioMaquinaExecutora;
    }

    public String getHostNameMaquinaExecutora() {
        return hostNameMaquinaExecutora;
    }

    public void setHostNameMaquinaExecutora(String hostNameMaquinaExecutora) {
        this.hostNameMaquinaExecutora = hostNameMaquinaExecutora;
    }

    public Float getTempoExecucaounificacaoSegundos() {
        return tempoExecucaounificacaoSegundos;
    }

    public void setTempoExecucaounificacaoSegundos(Float tempoExecucaounificacaoSegundos) {
        this.tempoExecucaounificacaoSegundos = tempoExecucaounificacaoSegundos;
    }

    public List<String> getOperacoesExecutadas() {
        return operacoesExecutadas;
    }

    public void setOperacoesExecutadas(List<String> operacoesExecutadas) {
        this.operacoesExecutadas = operacoesExecutadas;
    }
}
