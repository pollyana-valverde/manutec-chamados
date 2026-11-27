package br.com.techinova.manutec.ui;

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
import br.com.techinova.manutec.databinding.ActivityMaquinasBinding;
import br.com.techinova.manutec.model.Maquina;
import br.com.techinova.manutec.ui.adapter.MaquinaAdapter;
import br.com.techinova.manutec.util.MensagensErro;

/**
 * Lista de máquinas cadastradas (C1), atualizada em tempo real (NC2). Tocar na máquina
 * abre o histórico dela (C6); o botão ao lado mostra o QR.
 */
public class MaquinasActivity extends BaseActivity implements MaquinaAdapter.Acoes {

    private ActivityMaquinasBinding binding;
    private MaquinaAdapter adapter;
    private ListenerRegistration escuta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaquinasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.maquinas_titulo), true);

        adapter = new MaquinaAdapter(this);
        binding.lista.setLayoutManager(new LinearLayoutManager(this));
        binding.lista.setAdapter(adapter);
        binding.estadoVazio.iconeVazio.setImageResource(R.drawable.ic_factory);
        binding.estadoVazio.tituloVazio.setText(R.string.maquinas_vazio_titulo);
        binding.estadoVazio.mensagemVazio.setText(R.string.maquinas_vazio_mensagem);

        binding.botaoNova.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroMaquinaActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Snapshot listener: a lista se atualiza sozinha quando outro aparelho grava
        escuta = FirebaseFirestore.getInstance().collection("maquinas")
                .orderBy("codigo")
                .addSnapshotListener((snap, erro) -> {
                    binding.progresso.setVisibility(View.GONE);
                    if (erro != null) {
                        Snackbar.make(binding.getRoot(), MensagensErro.de(this, erro), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    List<Maquina> maquinas = new ArrayList<>();
                    if (snap != null) {
                        for (DocumentSnapshot doc : snap.getDocuments()) maquinas.add(Maquina.de(doc));
                    }
                    adapter.submitList(maquinas);
                    binding.estadoVazio.getRoot().setVisibility(maquinas.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (escuta != null) escuta.remove();
    }

    @Override
    public void aoClicar(@NonNull Maquina maquina) {
        startActivity(HistoricoMaquinaActivity.intent(this, maquina.codigo, maquina.nome, maquina.setor));
    }

    @Override
    public void aoVerQr(@NonNull Maquina maquina) {
        startActivity(QrMaquinaActivity.intent(this, maquina));
    }
}
