package unificacao.impl.treino_atividade_ficha;

import unificacao.AbstractUnificadorCodigoOrigemDestinoMapeavel;
import unificacao.CodigoOrigemRetornavel;
import unificacao.UnificadorFilho;
import unificacao.Unificavel;
import unificacao.metadata.ChaveEstrangeiraFK;
import unificacao.metadata.NomeColuna;
import unificacao.wrapper.ConnectionUnificacao;

import java.util.Date;

/**
 * Deve realizar a unificação da tabela 'ficha' do módulo do TreinoWeb.
 *
 * @author Bruno Cattany
 * @since 07/04/2019
 */
public class UnificadorTreinoFicha extends AbstractUnificadorCodigoOrigemDestinoMapeavel implements UnificadorFilho<UnificadorTreinoAtividadesImpl> {

    @Override
    public String getNomeTabelaAlvo() {
        return "ficha";
    }

    @Override
    public void executar(UnificadorTreinoAtividadesImpl unificadorOrquestrador, ConnectionUnificacao conexaoOrigem, ConnectionUnificacao conexaoDestino) throws Exception {
        unificadorOrquestrador.realizarUnificacaoViaReflection(conexaoOrigem, conexaoDestino, Ficha.class);
    }

    public static class Ficha implements Unificavel, CodigoOrigemRetornavel {

        @NomeColuna("codigo")
        private Integer codigo;

        @NomeColuna("ativo")
        private Boolean ativo;

        @NomeColuna("mensagemaluno")
        private String mensagemAluno;

        @NomeColuna("nome")
        private String nome;

        @NomeColuna("ultimaexecucao")
        private Date ultimaExecucao;

        @NomeColuna("usarcomopredefinida")
        private Boolean usarComoPredefinida;

        @NomeColuna("versao")
        private Integer versao;

        @NomeColuna("categoria_codigo")
        @ChaveEstrangeiraFK(tabelaReferenciavelName = UnificadorTreinoCategoriaFicha.class)
        private Integer categoriaCodigo;

        @Override
        public Integer getCodigo() {
            return codigo;
        }

        public void setCodigo(Integer codigo) {
            this.codigo = codigo;
        }

        public Boolean getAtivo() {
            return ativo;
        }

        public void setAtivo(Boolean ativo) {
            this.ativo = ativo;
        }

        public String getMensagemAluno() {
            return mensagemAluno;
        }

        public void setMensagemAluno(String mensagemAluno) {
            this.mensagemAluno = mensagemAluno;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public Date getUltimaExecucao() {
            return ultimaExecucao;
        }

        public void setUltimaExecucao(Date ultimaExecucao) {
            this.ultimaExecucao = ultimaExecucao;
        }

        public Boolean getUsarComoPredefinida() {
            return usarComoPredefinida;
        }

        public void setUsarComoPredefinida(Boolean usarComoPredefinida) {
            this.usarComoPredefinida = usarComoPredefinida;
        }

        public Integer getVersao() {
            return versao;
        }

        public void setVersao(Integer versao) {
            this.versao = versao;
        }

        public Integer getCategoriaCodigo() {
            return categoriaCodigo;
        }

        public void setCategoriaCodigo(Integer categoriaCodigo) {
            this.categoriaCodigo = categoriaCodigo;
        }
    }
}
