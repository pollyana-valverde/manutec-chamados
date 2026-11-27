package br.com.techinova.manutec.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Formatação de datas e durações em português. Sem dependências do Android (testável). */
public final class Formatos {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final long MINUTO = 60_000L;
    private static final long HORA = 60 * MINUTO;
    private static final long DIA = 24 * HORA;

    private Formatos() {
    }

    /** Duração legível: "menos de 1 min", "45 min", "1 h 05 min", "2 d 3 h". */
    @NonNull
    public static String duracao(long ms) {
        if (ms < MINUTO) return "menos de 1 min";
        long dias = ms / DIA;
        long horas = (ms % DIA) / HORA;
        long minutos = (ms % HORA) / MINUTO;
        if (dias > 0) return dias + " d " + horas + " h";
        if (horas > 0) return String.format(PT_BR, "%d h %02d min", horas, minutos);
        return minutos + " min";
    }

    /** "há 12 min", "há 1 h 05 min"; datas futuras ou nulas viram "agora". */
    @NonNull
    public static String tempoDesde(@Nullable Date inicio, long agoraMs) {
        if (inicio == null) return "agora";
        long ms = agoraMs - inicio.getTime();
        if (ms < MINUTO) return "agora";
        return "há " + duracao(ms);
    }

    /** "18/11/2026 14:32". */
    @NonNull
    public static String dataHora(@Nullable Date data) {
        if (data == null) return "—";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", PT_BR).format(data);
    }

    /** "14:32". */
    @NonNull
    public static String hora(@Nullable Date data) {
        if (data == null) return "—";
        return new SimpleDateFormat("HH:mm", PT_BR).format(data);
    }
}
