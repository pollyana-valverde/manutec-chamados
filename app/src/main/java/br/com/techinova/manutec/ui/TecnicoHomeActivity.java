package br.com.techinova.manutec.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityTecnicoHomeBinding;
import br.com.techinova.manutec.model.Chamado;
import br.com.techinova.manutec.model.OrdenacaoChamados;
import br.com.techinova.manutec.model.StatusChamado;
import br.com.techinova.manutec.ui.adapter.ChamadoAdapter;
import br.com.techinova.manutec.util.MensagensErro;
import br.com.techinova.manutec.util.Sessao;

/**
 * Fila de chamados abertos para o técnico (C3), ordenada por prioridade e depois por
 * horário de abertura. O snapshot listener atualiza a tela sozinho (NC2).
 */
public class TecnicoHomeActivity extends BaseActivity {

    private static final long ATUALIZAR_TEMPOS_MS = 30_000;

    private ActivityTecnicoHomeBinding binding;
    private ChamadoAdapter adapter;
    private ListenerRegistration escuta;
    private final Handler relogio = new Handler(Looper.getMainLooper());

    /** Atualiza o "aberto há X min" dos cartões sem precisar de nova leitura no Firestore. */
    private final Runnable atualizarTempos = new Runnable() {
        @Override
        public void run() {
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            relogio.postDelayed(this, ATUALIZAR_TEMPOS_MS);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTecnicoHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.home_tecnico_titulo), false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.ola_usuario, Sessao.nome()));
        }

        adapter = new ChamadoAdapter(null);
        binding.lista.setLayoutManager(new LinearLayoutManager(this));
        binding.lista.setAdapter(adapter);
        binding.estadoVazio.tituloVazio.setText(R.string.fila_vazia_titulo);
        binding.estadoVazio.mensagemVazio.setText(R.string.fila_vazia_mensagem);
    }

    @Override
    protected void onStart() {
        super.onStart();
        escutarFila();
        relogio.postDelayed(atualizarTempos, ATUALIZAR_TEMPOS_MS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (escuta != null) escuta.remove();
        relogio.removeCallbacks(atualizarTempos);
    }

    private void escutarFila() {
        escuta = FirebaseFirestore.getInstance().collection("chamados")
                .whereEqualTo("status", StatusChamado.ABERTO.valor)
                .addSnapshotListener((snap, erro) -> {
                    binding.progresso.setVisibility(View.GONE);
                    if (erro != null) {
                        Snackbar.make(binding.getRoot(), MensagensErro.de(this, erro), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    List<Chamado> chamados = new ArrayList<>();
                    if (snap != null) {
                        for (DocumentSnapshot doc : snap.getDocuments()) chamados.add(Chamado.de(doc));
                    }
                    // Ordenação no app: dispensa índice composto no Firestore
                    chamados.sort(OrdenacaoChamados.FILA);
                    adapter.submitList(chamados);
                    binding.textoContagem.setText(getResources()
                            .getQuantityString(R.plurals.fila_contagem, chamados.size(), chamados.size()));
                    binding.estadoVazio.getRoot().setVisibility(chamados.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tecnico, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.acaoMaquinas) {
            startActivity(new Intent(this, MaquinasActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.acaoSair) {
            confirmarSair();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
