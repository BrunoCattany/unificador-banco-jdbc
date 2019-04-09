package util;

/**
 * Responsável por contar o tempo de execução entre 2 trechos de código, ou seja {@link #iniciarCronometro(ThreadLocal)} e {@link #encerrarCronometro(ThreadLocal)}.
 * <p>
 * Exemplo de declaração de uma {@link ThreadLocal}:
 *
 * <pre>
 * static final ThreadLocal&lt;Long&gt; TEMPO_OPERACAO_THREADLOCAL = new ThreadLocal&lt;Long&gt;();
 * </pre>
 * </p>
 *
 * @author Bruno Cattany
 * @since 06/04/2019
 */
public class CronometroTempoThreadLocal {

    private static final String MSG_FALHA_TEMPO_INICIAL_CONTADOR = "Não foi possível recuperar o tempo inicial do contador.";

    public static void iniciarCronometro(ThreadLocal<Long> longThreadLocal) {
        longThreadLocal.set(System.currentTimeMillis());
    }

    public static String encerrarCronometroString(ThreadLocal<Long> longThreadLocal) {
        final long tempoDecorrido = encerrarCronometro(longThreadLocal);

        if (tempoDecorrido == -1) {
            return MSG_FALHA_TEMPO_INICIAL_CONTADOR;
        }

        return formartarTempoCronometroComLabel(tempoDecorrido);
    }

    public static long encerrarCronometro(ThreadLocal<Long> longThreadLocal) {
        final Long tempoStartado = longThreadLocal.get();
        if (tempoStartado == null) {
            return -1;
        }

        longThreadLocal.remove();

        return System.currentTimeMillis() - tempoStartado;
    }

    public static String formartarTempoCronometroComLabel(long tempo) {
        return formartarTempoCronometro(tempo) + " segundos";
    }

    public static double formartarTempoCronometro(long tempo) {
        return Uteis.arredondar(tempo * 0.001, 4, 0);
    }

}
