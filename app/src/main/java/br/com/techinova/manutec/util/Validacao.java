package br.com.techinova.manutec.util;

import android.text.Editable;
import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.textfield.TextInputLayout;

import br.com.techinova.manutec.R;

/**
 * Validação dos formulários (NC6). Cada método mostra a mensagem em português no
 * próprio campo e devolve true quando o valor está correto. Nada aqui lança exceção.
 */
public final class Validacao {

    private Validacao() {
    }

    /** Texto do campo sem espaços nas pontas. Nunca devolve null. */
    @NonNull
    public static String texto(@NonNull TextInputLayout campo) {
        EditText edit = campo.getEditText();
        if (edit == null) return "";
        Editable e = edit.getText();
        return e == null ? "" : e.toString().trim();
    }

    public static boolean obrigatorio(@NonNull TextInputLayout campo) {
        return checar(campo, !texto(campo).isEmpty(), R.string.erro_campo_obrigatorio);
    }

    public static boolean minimo(@NonNull TextInputLayout campo, int minimo, @StringRes int erro) {
        String valor = texto(campo);
        if (valor.isEmpty()) return checar(campo, false, R.string.erro_campo_obrigatorio);
        return checar(campo, valor.length() >= minimo, erro);
    }

    public static boolean email(@NonNull TextInputLayout campo) {
        String valor = texto(campo);
        if (valor.isEmpty()) return checar(campo, false, R.string.erro_campo_obrigatorio);
        return checar(campo, Patterns.EMAIL_ADDRESS.matcher(valor).matches(), R.string.erro_email_invalido);
    }

    public static boolean senha(@NonNull TextInputLayout campo) {
        return minimo(campo, 6, R.string.erro_senha_curta);
    }

    /** Mostra ou limpa o erro no campo. */
    public static boolean checar(@NonNull TextInputLayout campo, boolean ok, @StringRes int erro) {
        campo.setError(ok ? null : campo.getContext().getString(erro));
        return ok;
    }

    /** Limpa o erro assim que o usuário volta a digitar. */
    public static void limparErroAoDigitar(@Nullable TextInputLayout... campos) {
        if (campos == null) return;
        for (TextInputLayout campo : campos) {
            EditText edit = campo.getEditText();
            if (edit == null) continue;
            edit.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (campo.getError() != null) campo.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }
}
