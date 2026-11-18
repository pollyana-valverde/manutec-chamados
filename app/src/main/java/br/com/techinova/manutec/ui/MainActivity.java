package br.com.techinova.manutec.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.techinova.manutec.databinding.ActivityMainBinding;
import br.com.techinova.manutec.util.Perfis;
import br.com.techinova.manutec.util.Sessao;

/**
 * Tela de entrada. O Firebase Auth mantém a sessão salva no aparelho (NC1), então ao
 * reabrir o app o usuário já cai direto na tela do seu perfil, lido em usuarios/{uid} (NC4).
 */
public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarInsets(binding.raiz, binding.raiz);

        binding.botaoTentarNovamente.setOnClickListener(v -> carregarPerfil());
        binding.botaoSair.setOnClickListener(v -> Sessao.sair(this));
        carregarPerfil();
    }

    private void carregarPerfil() {
        FirebaseUser usuario = Sessao.usuario();
        if (usuario == null) {
            abrir(LoginActivity.class);
            return;
        }
        mostrarErro(false);
        FirebaseFirestore.getInstance()
                .collection("usuarios").document(usuario.getUid())
                .get()
                .addOnSuccessListener(this::rotear)
                .addOnFailureListener(e -> mostrarErro(true));
    }

    private void rotear(DocumentSnapshot doc) {
        String perfil = doc.getString("perfil");
        if (Perfis.TECNICO.equals(perfil)) {
            abrir(TecnicoHomeActivity.class);
        } else if (Perfis.OPERADOR.equals(perfil)) {
            abrir(OperadorHomeActivity.class);
        } else {
            // Conta sem perfil (ou com perfil desconhecido): não entra em nenhuma área
            abrir(AguardandoPerfilActivity.class);
        }
    }

    private void abrir(Class<?> tela) {
        Intent intent = new Intent(this, tela);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarErro(boolean erro) {
        binding.progresso.setVisibility(erro ? View.GONE : View.VISIBLE);
        binding.textoErro.setVisibility(erro ? View.VISIBLE : View.GONE);
        binding.botaoTentarNovamente.setVisibility(erro ? View.VISIBLE : View.GONE);
        binding.botaoSair.setVisibility(erro ? View.VISIBLE : View.GONE);
    }
}
