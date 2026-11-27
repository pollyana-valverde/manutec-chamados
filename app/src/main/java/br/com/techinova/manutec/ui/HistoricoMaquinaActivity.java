package br.com.techinova.manutec.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import br.com.techinova.manutec.databinding.ActivityHistoricoMaquinaBinding;
import br.com.techinova.manutec.model.Chamado;
import br.com.techinova.manutec.model.OrdenacaoChamados;
import br.com.techinova.manutec.model.ResumoHistorico;
import br.com.techinova.manutec.ui.adapter.ChamadoAdapter;
import br.com.techinova.manutec.util.Formatos;
import br.com.techinova.manutec.util.MensagensErro;

/** Histórico da máquina com todos os chamados e a duração de cada reparo (C6). */
public class HistoricoMaquinaActivity extends BaseActivity {

    private static final String EXTRA_CODIGO = "codigo";
    private static final String EXTRA_NOME = "nome";
    private static final String EXTRA_SETOR = "setor";

    private ActivityHistoricoMaquinaBinding binding;
    private ChamadoAdapter adapter;
    private ListenerRegistration escuta;
    private String codigo;

    @NonNull
    public static Intent intent(@NonNull Context ctx, @NonNull String codigo,
                                @Nullable String nome, @Nullable String setor) {
        return new Intent(ctx, HistoricoMaquinaActivity.class)
                .putExtra(EXTRA_CODIGO, codigo)
                .putExtra(EXTRA_NOME, nome)
                .putExtra(EXTRA_SETOR, setor);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoricoMaquinaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.historico_titulo), true);

        codigo = getIntent().getStringExtra(EXTRA_CODIGO);
        if (codigo == null) {
            finish();
            return;
        }
        String nome = getIntent().getStringExtra(EXTRA_NOME);
        binding.textoMaquina.setText(nome == null ? codigo : codigo + " · " + nome);
        binding.textoSetor.setText(getIntent().getStringExtra(EXTRA_SETOR));
        binding.numeroTotal.rotulo.setText(R.string.rotulo_chamados);
        binding.numeroMedio.rotulo.setText(R.string.rotulo_tempo_medio);
        binding.numeroParado.rotulo.setText(R.string.rotulo_tempo_parado);

        adapter = new ChamadoAdapter(c -> startActivity(DetalheChamadoActivity.intent(this, c.id)));
        binding.lista.setLayoutManager(new LinearLayoutManager(this));
        binding.lista.setAdapter(adapter);
        binding.estadoVazio.iconeVazio.setImageResource(R.drawable.ic_history);
        binding.estadoVazio.tituloVazio.setText(R.string.historico_vazio_titulo);
        binding.estadoVazio.mensagemVazio.setText(R.string.historico_vazio_mensagem);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (codigo == null) return;
        escuta = FirebaseFirestore.getInstance().collection("chamados")
                .whereEqualTo("maquinaCodigo", codigo)
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
                    chamados.sort(OrdenacaoChamados.RECENTES);
                    adapter.submitList(chamados);
                    mostrarResumo(ResumoHistorico.de(chamados));
                    binding.estadoVazio.getRoot().setVisibility(chamados.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (escuta != null) escuta.remove();
    }

    private void mostrarResumo(@NonNull ResumoHistorico r) {
        binding.numeroTotal.valor.setText(String.valueOf(r.total));
        binding.numeroMedio.valor.setText(r.tempoMedioMs < 0
                ? getString(R.string.sem_dados) : Formatos.duracao(r.tempoMedioMs));
        binding.numeroParado.valor.setText(r.encerrados == 0
                ? getString(R.string.sem_dados) : Formatos.duracao(r.tempoTotalMs));
    }
}
