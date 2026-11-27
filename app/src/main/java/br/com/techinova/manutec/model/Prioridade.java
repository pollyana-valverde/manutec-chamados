package br.com.techinova.manutec.model;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import br.com.techinova.manutec.R;

/**
 * Prioridade do chamado. "ordem" é gravada no Firestore (prioridadeOrdem) para a fila
 * ficar ordenada: 1 = alta vem primeiro.
 */
public enum Prioridade {
    ALTA("alta", 1, R.string.prioridade_alta, R.color.prioridade_alta),
    MEDIA("media", 2, R.string.prioridade_media, R.color.prioridade_media),
    BAIXA("baixa", 3, R.string.prioridade_baixa, R.color.prioridade_baixa);

    public final String valor;
    public final int ordem;
    @StringRes
    public final int rotulo;
    @ColorRes
    public final int cor;

    Prioridade(String valor, int ordem, int rotulo, int cor) {
        this.valor = valor;
        this.ordem = ordem;
        this.rotulo = rotulo;
        this.cor = cor;
    }

    /** Converte o texto gravado no Firestore. Valor desconhecido vira BAIXA. */
    @NonNull
    public static Prioridade de(String valor) {
        for (Prioridade p : values()) {
            if (p.valor.equals(valor)) return p;
        }
        return BAIXA;
    }
}
