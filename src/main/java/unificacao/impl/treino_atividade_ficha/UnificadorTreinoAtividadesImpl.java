package br.com.pactosolucoes.atualizadb.processo.unificacao.impl.treino_atividade_ficha;

import br.com.pactosolucoes.atualizadb.processo.unificacao.AbstractOrquestradorUnificadorDadosJDBC;
import br.com.pactosolucoes.atualizadb.processo.unificacao.UnificadorFilho;

import java.util.LinkedHashSet;

/**
 * @author Bruno Cattany
 * @since 01/04/2019
 */
public class UnificadorTreinoAtividadesImpl extends AbstractOrquestradorUnificadorDadosJDBC {

    private UnificadorTreinoAtividadesImpl(String[] args) {
        super(args);
    }

    public static void main(String... args) {
        new UnificadorTreinoAtividadesImpl(args).iniciar();
    }

    @Override
    protected LinkedHashSet<UnificadorFilho> criarSequenciaOrquestradaunificadoresFilhos() {
        LinkedHashSet<UnificadorFilho> set = new LinkedHashSet<UnificadorFilho>();

        set.add(new UnificacaoTreinoCategoriaFicha());
        set.add(new UnificadorTreinoFicha());

        return set;
    }

}