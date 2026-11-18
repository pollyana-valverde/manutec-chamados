package br.com.techinova.manutec.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

/** Documento maquinas/{codigo}. O código é o id do documento e o conteúdo do QR. */
public class Maquina {

    public final String codigo;
    public final String nome;
    public final String setor;

    public Maquina(@NonNull String codigo, String nome, String setor) {
        this.codigo = codigo;
        this.nome = nome == null ? "" : nome;
        this.setor = setor == null ? "" : setor;
    }

    @NonNull
    public static Maquina de(@NonNull DocumentSnapshot doc) {
        return new Maquina(doc.getId(), doc.getString("nome"), doc.getString("setor"));
    }

    /** Código em maiúsculas e sem espaços, como fica gravado e no QR. */
    @NonNull
    public static String normalizarCodigo(String bruto) {
        return bruto == null ? "" : bruto.trim().toUpperCase(java.util.Locale.ROOT);
    }

    /** 2 a 20 caracteres, começando por letra ou número; só letras, números e hífen. */
    public static boolean codigoValido(String codigo) {
        return codigo != null && codigo.matches("[A-Z0-9][A-Z0-9-]{1,19}");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Maquina)) return false;
        Maquina m = (Maquina) o;
        return codigo.equals(m.codigo) && nome.equals(m.nome) && setor.equals(m.setor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, nome, setor);
    }
}
