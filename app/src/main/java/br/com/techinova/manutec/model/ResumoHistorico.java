package br.com.techinova.manutec.model;

import androidx.annotation.NonNull;

import java.util.List;

/** Números do histórico de uma máquina (C6 + complementar de tempo médio de reparo). */
public class ResumoHistorico {

    public final int total;
    public final int encerrados;
    /** Soma das durações dos reparos encerrados, em ms. */
    public final long tempoTotalMs;
    /** Média das durações dos reparos encerrados, em ms; -1 se nenhum foi encerrado. */
    public final long tempoMedioMs;

    private ResumoHistorico(int total, int encerrados, long tempoTotalMs) {
        this.total = total;
        this.encerrados = encerrados;
        this.tempoTotalMs = tempoTotalMs;
        this.tempoMedioMs = encerrados == 0 ? -1 : tempoTotalMs / encerrados;
    }

    @NonNull
    public static ResumoHistorico de(@NonNull List<Chamado> chamados) {
        int encerrados = 0;
        long soma = 0;
        for (Chamado c : chamados) {
            long d = c.duracaoMs();
            if (c.status == StatusChamado.ENCERRADO && d >= 0) {
                encerrados++;
                soma += d;
            }
        }
        return new ResumoHistorico(chamados.size(), encerrados, soma);
    }
}
