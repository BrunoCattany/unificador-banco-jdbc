package br.com.pactosolucoes.atualizadb.processo.unificacao.impl.treino_atividade_ficha;

import br.com.pactosolucoes.atualizadb.processo.unificacao.AbstractUnificadorTabelaReferenciavel;
import br.com.pactosolucoes.atualizadb.processo.unificacao.CodigoOrigemRetornavel;
import br.com.pactosolucoes.atualizadb.processo.unificacao.ColunasInseriveisSQL;
import br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorFilho;
import br.com.pactosolucoes.atualizadb.processo.unificacao.wrapper.ConnectionUnificacao;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.COLUNA_CODIGO;
import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.FROM;
import static br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorConstantes.OperacaoSQL.SELECT;

/**
 * Deve realizar a unificação da tabela 'categoriaficha' do módulo do TreinoWeb. <br>
 * O que impede a duplicação de registros é a <b>categoriaficha_nome_key</b>, atribuído a coluna <b>nome</b>.
 *
 * @author Bruno Cattany
 * @since 05/04/2019
 */
class UnificacaoTreinoCategoriaFicha extends AbstractUnificadorTabelaReferenciavel implements UnificadorFilho<UnificadorTreinoAtividadesImpl> {

    @Override
    public String getNomeTabelaAlvo() {
        return "categoriaficha";
    }

    @Override
    public void executar(UnificadorTreinoAtividadesImpl unificadorOrquestrador, ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino) throws Exception {
        List<ColunasInseriveisSQL> categoriasFichas = consultarCategoriasFichasOrigem(unificadorOrquestrador, conexaoOrigem);

        unificadorOrquestrador.inserirApartirUltimoCodigo(
                conexaoDestino,
                "nome",
                categoriasFichas
        );
    }

    private List<ColunasInseriveisSQL> consultarCategoriasFichasOrigem(UnificadorTreinoAtividadesImpl unificador,
                                                                       ConnectionUnificacao conexaoOrigem) throws Exception {
        ResultSet rs = unificador.executarConsultaComFeedbackOrdenadoCrescentementePeloCodigo("Consultando todos os registros de 'categoriasficha'.", conexaoOrigem,
                SELECT + " " + COLUNA_CODIGO + ", nome " + FROM + getNomeTabelaAlvo());

        List<ColunasInseriveisSQL> categoriasFichas = new LinkedList<ColunasInseriveisSQL>();
        while (unificador.nextResult(rs)) {
            categoriasFichas.add(
                    new CategoriaFicha(
                            rs.getInt(COLUNA_CODIGO),
                            rs.getString("nome")
                    )
            );
        }

        return categoriasFichas;
    }

    private class CategoriaFicha implements ColunasInseriveisSQL, CodigoOrigemRetornavel {

        private Integer codigo;
        private String nome;

        CategoriaFicha(Integer codigo, String nome) {
            this.codigo = codigo;
            this.nome = nome;
        }

        @Override
        public String colunasValorString() {
            return '\'' + nome + '\'';
        }

        @Override
        public Integer getCodigoOrigem() {
            return codigo;
        }
    }

}
