package br.com.techinova.manutec.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import br.com.techinova.manutec.databinding.ActivityLoginBinding;
import br.com.techinova.manutec.util.MensagensErro;
import br.com.techinova.manutec.util.Sessao;
import br.com.techinova.manutec.util.Validacao;

/** Login com e-mail e senha pelo Firebase Authentication (NC1). */
public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se já existe sessão, nem mostra o login
        if (Sessao.usuario() != null) {
            irParaInicio();
            return;
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarInsets(binding.raiz, binding.cabecalho);

        Validacao.limparErroAoDigitar(binding.campoEmail, binding.campoSenha);
        binding.botaoEntrar.setOnClickListener(v -> entrar());
        binding.editSenha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                entrar();
                return true;
            }
            return false;
        });
        binding.botaoCriarConta.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));
    }

    private void entrar() {
        boolean ok = Validacao.email(binding.campoEmail);
        ok &= Validacao.senha(binding.campoSenha);
        if (!ok) return;

        carregando(true);
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(Validacao.texto(binding.campoEmail),
                        Validacao.texto(binding.campoSenha))
                .addOnSuccessListener(r -> irParaInicio())
                .addOnFailureListener(e -> {
                    carregando(false);
                    Snackbar.make(binding.getRoot(), MensagensErro.de(this, e), Snackbar.LENGTH_LONG).show();
                });
    }

    private void irParaInicio() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void carregando(boolean sim) {
        binding.progresso.setVisibility(sim ? View.VISIBLE : View.INVISIBLE);
        binding.botaoEntrar.setEnabled(!sim);
        binding.botaoCriarConta.setEnabled(!sim);
    }
}
