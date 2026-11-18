package br.com.techinova.manutec.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityAguardandoPerfilBinding;
import br.com.techinova.manutec.util.Sessao;

/** Mostrada para quem tem conta mas ainda não recebeu um perfil no console (NC4). */
public class AguardandoPerfilActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAguardandoPerfilBinding binding = ActivityAguardandoPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.app_name), false);

        binding.textoUid.setText(getString(R.string.aguardando_uid, String.valueOf(Sessao.uid())));
        binding.botaoVerificar.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sair, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.acaoSair) {
            confirmarSair();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
