package br.com.techinova.manutec.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityCadastroBinding;
import br.com.techinova.manutec.util.MensagensErro;
import br.com.techinova.manutec.util.Validacao;

/**
 * Cadastro com e-mail e senha (NC1). Cria o documento usuarios/{uid} SEM o campo perfil:
 * quem define o perfil é o grupo, pelo console do Firebase (NC4). As regras do Firestore
 * recusam qualquer tentativa do app de gravar o perfil.
 */
public class CadastroActivity extends BaseActivity {

    private ActivityCadastroBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.cadastro_titulo), true);

        Validacao.limparErroAoDigitar(binding.campoNome, binding.campoEmail,
                binding.campoSenha, binding.campoConfirmar);
        binding.botaoCriar.setOnClickListener(v -> criarConta());
    }

    private void criarConta() {
        boolean ok = Validacao.minimo(binding.campoNome, 3, R.string.erro_nome_curto);
        ok &= Validacao.email(binding.campoEmail);
        ok &= Validacao.senha(binding.campoSenha);
        if (ok) {
            ok = Validacao.checar(binding.campoConfirmar,
                    Validacao.texto(binding.campoSenha).equals(Validacao.texto(binding.campoConfirmar)),
                    R.string.erro_senhas_diferentes);
        }
        if (!ok) return;

        final String nome = Validacao.texto(binding.campoNome);
        final String email = Validacao.texto(binding.campoEmail);
        carregando(true);

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, Validacao.texto(binding.campoSenha))
                .onSuccessTask(resultado -> {
                    FirebaseUser usuario = resultado.getUser();
                    // Guarda o nome no próprio Auth para exibir nas telas
                    return usuario.updateProfile(new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nome).build())
                            .onSuccessTask(nada -> {
                                Map<String, Object> dados = new HashMap<>();
                                dados.put("nome", nome);
                                dados.put("email", email);
                                dados.put("criadoEm", FieldValue.serverTimestamp());
                                return FirebaseFirestore.getInstance()
                                        .collection("usuarios").document(usuario.getUid())
                                        .set(dados);
                            });
                })
                .addOnSuccessListener(nada -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    carregando(false);
                    Snackbar.make(binding.getRoot(), MensagensErro.de(this, e), Snackbar.LENGTH_LONG).show();
                });
    }

    private void carregando(boolean sim) {
        binding.progresso.setVisibility(sim ? View.VISIBLE : View.INVISIBLE);
        binding.botaoCriar.setEnabled(!sim);
    }
}
