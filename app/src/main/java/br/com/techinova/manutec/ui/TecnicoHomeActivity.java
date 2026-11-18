package br.com.techinova.manutec.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityTecnicoHomeBinding;
import br.com.techinova.manutec.util.Sessao;

/** Tela inicial do perfil tecnico (as funcionalidades entram nas próximas histórias). */
public class TecnicoHomeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTecnicoHomeBinding binding = ActivityTecnicoHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.home_tecnico_titulo), false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.ola_usuario, Sessao.nome()));
        }
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
