package unificacao.impl.treino_atividade_ficha;

import unificacao.AbstractUnificadorCodigoOrigemDestinoMapeavel;
import unificacao.CodigoOrigemRetornavel;
import unificacao.UnificadorFilho;
import unificacao.Unificavel;
import unificacao.metadata.NomeColuna;
import unificacao.wrapper.ConnectionUnificacao;

/**
 * Deve realizar a unificação da tabela 'categoriaficha' do módulo do TreinoWeb. <br>
 * O que impede a duplicação de registros é a <b>categoriaficha_nome_key</b>, atribuído a coluna <b>nome</b>.
 *
 * @author Bruno Cattany
 * @since 05/04/2019
 */
class UnificadorTreinoCategoriaFicha extends AbstractUnificadorCodigoOrigemDestinoMapeavel implements UnificadorFilho<UnificadorTreinoAtividadesImpl> {

    @Override
    public String getNomeTabelaAlvo() {
        return "categoriaficha";
    }

    @Override
    public void executar(UnificadorTreinoAtividadesImpl unificadorOrquestrador,
                         ConnectionUnificacao conexaoOrigem,
                         ConnectionUnificacao conexaoDestino) throws Exception {
        unificadorOrquestrador.realizarUnificacaoViaReflection(conexaoOrigem, conexaoDestino, CategoriaFicha.class);
    }

    public static class CategoriaFicha implements Unificavel, CodigoOrigemRetornavel {

        @NomeColuna("codigo")
        private Integer codigo;

        @NomeColuna("nome")
        private String nome;

        @Override
        public Integer getCodigo() {
            return codigo;
        }

        public String getNome() {
            return nome;
        }

        public void setCodigo(Integer codigo) {
            this.codigo = codigo;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

}
