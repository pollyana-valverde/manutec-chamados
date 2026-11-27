package br.com.techinova.manutec.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestoreException;

import br.com.techinova.manutec.R;

/** Converte as exceções do Firebase em mensagens em português para o usuário. */
public final class MensagensErro {

    private MensagensErro() {
    }

    @NonNull
    public static String de(@NonNull Context ctx, @Nullable Exception e) {
        int res;
        if (e instanceof FirebaseAuthWeakPasswordException) {
            res = R.string.erro_senha_fraca;
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            res = R.string.erro_email_em_uso;
        } else if (e instanceof FirebaseAuthInvalidCredentialsException
                || e instanceof FirebaseAuthInvalidUserException) {
            res = R.string.erro_login_invalido;
        } else if (e instanceof FirebaseNetworkException) {
            res = R.string.erro_sem_conexao;
        } else if (e instanceof FirebaseTooManyRequestsException) {
            res = R.string.erro_muitas_tentativas;
        } else if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException.Code code = ((FirebaseFirestoreException) e).getCode();
            if (code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                res = R.string.erro_sem_permissao;
            } else if (code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                res = R.string.erro_sem_conexao;
            } else {
                res = R.string.erro_generico;
            }
        } else {
            res = R.string.erro_generico;
        }
        return ctx.getString(res);
    }
}
