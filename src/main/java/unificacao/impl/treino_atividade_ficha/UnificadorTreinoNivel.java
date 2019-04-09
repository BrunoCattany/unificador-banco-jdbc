package br.com.pactosolucoes.atualizadb.processo.unificacao.impl.treino_atividade_ficha;

import br.com.pactosolucoes.atualizadb.processo.unificacao.ColunasInseriveisSQL;
import br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorFilho;
import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.ConnectionUnificacao;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Bruno Cattany
 * @since 05/04/2019
 */
@Deprecated
class UnificadorTreinoNivel implements UnificadorFilho<UnificadorTreinoAtividadesImpl> {

    /*
     * O problema desta unificação é por conta da coluna "ordem".
     */

    @Override
    public String getNomeTabelaAlvo() {
        return "nivel";
    }

    @Override
    public void executar(UnificadorTreinoAtividadesImpl unificadorOrquestrador, ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino) throws Exception {
        List<ColunasInseriveisSQL> niveis = consultarNivelOrigem(unificadorOrquestrador, conexaoOrigem);

        unificadorOrquestrador.inserirApartirUltimoCodigo(
                conexaoDestino,
                "nome, ordem",
                niveis
        );
    }

    private List<ColunasInseriveisSQL> consultarNivelOrigem(UnificadorTreinoAtividadesImpl unificador,
                                                            ConnectionUnificacao conexaoOrigem) throws Exception {
        ResultSet rs = unificador.executarConsultaComFeedbackOrdenadoCrescentementePeloCodigo("Consultando todos os registros de 'nivel'.", conexaoOrigem,
                "SELECT nome, ordem FROM " + getNomeTabelaAlvo());

        List<ColunasInseriveisSQL> niveis = new LinkedList<ColunasInseriveisSQL>();
        while (unificador.nextResult(rs)) {
            niveis.add(
                    new Nivel(
                            rs.getString("nome"),
                            rs.getString("ordem")
                    )
            );
        }

        return niveis;
    }

    private class Nivel implements ColunasInseriveisSQL {

        private String nome;
        private String ordem;

        Nivel(String nome, String ordem) {
            this.nome = nome;
            this.ordem = ordem;
        }

        @Override
        public String colunasValorString() {
            return '\'' + nome + '\'' + ", " + ordem;
        }
    }
}
