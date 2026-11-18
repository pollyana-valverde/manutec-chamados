package br.com.techinova.manutec.model;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import br.com.techinova.manutec.R;

/** Ciclo de vida do chamado: aberto → em_atendimento → encerrado. */
public enum StatusChamado {
    ABERTO("aberto", R.string.status_aberto, R.color.status_aberto),
    EM_ATENDIMENTO("em_atendimento", R.string.status_em_atendimento, R.color.status_atendimento),
    ENCERRADO("encerrado", R.string.status_encerrado, R.color.status_encerrado);

    public final String valor;
    @StringRes
    public final int rotulo;
    @ColorRes
    public final int cor;

    StatusChamado(String valor, int rotulo, int cor) {
        this.valor = valor;
        this.rotulo = rotulo;
        this.cor = cor;
    }

    @NonNull
    public static StatusChamado de(String valor) {
        for (StatusChamado s : values()) {
            if (s.valor.equals(valor)) return s;
        }
        return ABERTO;
    }
}
