package br.com.pactosolucoes.atualizadb.processo.unificacao.impl.treino_atividade_ficha;

import br.com.pactosolucoes.atualizadb.processo.unificacao.ColunasInseriveisSQL;
import br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorFilho;
import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.ConnectionUnificacao;

import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.COLUNA_CODIGO;
import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.FROM;
import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.OperacaoSQL.SELECT;

/**
 * DOCME
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public class UnificadorTreinoFicha implements UnificadorFilho<UnificadorTreinoAtividadesImpl> {

    @Override
    public String getNomeTabelaAlvo() {
        return "ficha";
    }

    @Override
    public void executar(UnificadorTreinoAtividadesImpl unificadorOrquestrador, ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino) throws Exception {
        List<ColunasInseriveisSQL> categoriasFichas = consultarFichasOrigem(unificadorOrquestrador, conexaoOrigem);
    }

    private List<ColunasInseriveisSQL> consultarFichasOrigem(UnificadorTreinoAtividadesImpl unificador,
                                                             ConnectionUnificacao conexaoOrigem) throws Exception {
        ResultSet rs = unificador.executarConsultaComFeedbackOrdenadoCrescentementePeloCodigo("Consultando todos os registros de 'categoriasficha'.", conexaoOrigem,
                SELECT + " " + COLUNA_CODIGO + ", nome " + FROM + getNomeTabelaAlvo());

        List<ColunasInseriveisSQL> fichas = new LinkedList<ColunasInseriveisSQL>();
        while (unificador.nextResult(rs)) {
            fichas.add(
                    new Ficha(
                            rs.getInt(COLUNA_CODIGO),
                            rs.getBoolean("ativo"),
                            rs.getString("mensagemAluno"),
                            rs.getString("nome"),
                            rs.getDate("ultimaexecucao"),
                            rs.getBoolean("usarcomopredefinida"),
                            rs.getInt("versao"),
                            0,
                            0 // STOPHERE
                    )
            );
        }

        return fichas;
    }

    private class Ficha implements ColunasInseriveisSQL {

        private Integer codigo;
        private Boolean ativo;
        private String mensagemAluno;
        private String nome;
        private Date ultimaExecucao;
        private Boolean usarComoPredefinida;
        private Integer versao;
        private Integer categoriaCodigo;
        private Integer nivelCodigo;

        public Ficha(Integer codigo, Boolean ativo, String mensagemAluno, String nome,
                     Date ultimaExecucao, Boolean usarComoPredefinida, Integer versao, Integer categoriaCodigo, Integer nivelCodigo) {
            this.codigo = codigo;
            this.ativo = ativo;
            this.mensagemAluno = mensagemAluno;
            this.nome = nome;
            this.ultimaExecucao = ultimaExecucao;
            this.usarComoPredefinida = usarComoPredefinida;
            this.versao = versao;
            this.categoriaCodigo = categoriaCodigo;
            this.nivelCodigo = nivelCodigo;
        }

        @Override
        public String colunasValorString() {
            return ativo + ", '" + mensagemAluno + "'" + ", '" + nome + "', '" + ultimaExecucao + "', " + usarComoPredefinida
                    + ", " + versao + ", " + categoriaCodigo + ", " + nivelCodigo;
        }

    }

}
