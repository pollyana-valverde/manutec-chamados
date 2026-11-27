package br.com.techinova.manutec.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.techinova.manutec.ui.LoginActivity;

/** Atalhos para o usuário logado no Firebase Authentication. */
public final class Sessao {

    private Sessao() {
    }

    @Nullable
    public static FirebaseUser usuario() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    public static String uid() {
        FirebaseUser u = usuario();
        return u == null ? null : u.getUid();
    }

    /** Nome para exibir: o displayName salvo no cadastro ou, na falta dele, o início do e-mail. */
    @NonNull
    public static String nome() {
        FirebaseUser u = usuario();
        if (u == null) return "";
        if (!TextUtils.isEmpty(u.getDisplayName())) return u.getDisplayName();
        String email = u.getEmail();
        if (email == null) return "";
        int arroba = email.indexOf('@');
        return arroba > 0 ? email.substring(0, arroba) : email;
    }

    /** Sai da conta e volta para o login, limpando a pilha de telas (NC1). */
    public static void sair(@NonNull Activity activity) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
