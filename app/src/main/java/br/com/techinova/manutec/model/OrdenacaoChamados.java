package br.com.techinova.manutec.model;

import java.util.Comparator;
import java.util.Date;

/** Regras de ordenação das listas de chamados. */
public final class OrdenacaoChamados {

    private OrdenacaoChamados() {
    }

    /**
     * Fila do técnico (C3): primeiro a prioridade (alta → média → baixa) e, dentro da
     * mesma prioridade, quem foi aberto há mais tempo.
     */
    public static final Comparator<Chamado> FILA = (a, b) -> {
        int porPrioridade = Integer.compare(a.prioridade.ordem, b.prioridade.ordem);
        if (porPrioridade != 0) return porPrioridade;
        return Long.compare(ms(a.abertoEm), ms(b.abertoEm));
    };

    /** Mais recentes primeiro (listas do operador e histórico). */
    public static final Comparator<Chamado> RECENTES = (a, b) -> Long.compare(ms(b.abertoEm), ms(a.abertoEm));

    /** Data nula (ainda não sincronizada) vai para o fim da fila. */
    private static long ms(Date d) {
        return d == null ? Long.MAX_VALUE : d.getTime();
    }
}
