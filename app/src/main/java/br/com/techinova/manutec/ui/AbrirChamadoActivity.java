package br.com.techinova.manutec.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityAbrirChamadoBinding;
import br.com.techinova.manutec.model.Maquina;
import br.com.techinova.manutec.model.Prioridade;
import br.com.techinova.manutec.model.StatusChamado;
import br.com.techinova.manutec.util.MensagensErro;
import br.com.techinova.manutec.util.Sessao;
import br.com.techinova.manutec.util.Validacao;

/**
 * O operador abre um chamado para a máquina lida no QR (C2). A máquina vem preenchida
 * e não pode ser trocada; ele informa só a descrição e a prioridade.
 */
public class AbrirChamadoActivity extends BaseActivity {

    private static final String EXTRA_CODIGO = "codigo";

    private ActivityAbrirChamadoBinding binding;
    @Nullable
    private Maquina maquina;

    @NonNull
    public static Intent intent(@NonNull Context ctx, @NonNull String codigoMaquina) {
        return new Intent(ctx, AbrirChamadoActivity.class).putExtra(EXTRA_CODIGO, codigoMaquina);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAbrirChamadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.abrir_chamado_titulo), true);

        Validacao.limparErroAoDigitar(binding.campoDescricao);
        binding.grupoPrioridade.addOnButtonCheckedListener((g, id, marcado) -> {
            if (marcado) binding.erroPrioridade.setVisibility(View.GONE);
        });
        binding.botaoEnviar.setOnClickListener(v -> enviar());
        binding.botaoVoltar.setOnClickListener(v -> finish());

        String codigo = getIntent().getStringExtra(EXTRA_CODIGO);
        carregarMaquina(codigo == null ? "" : codigo);
    }

    private void carregarMaquina(@NonNull String codigo) {
        if (!Maquina.codigoValido(codigo)) {
            mostrarNaoEncontrada(codigo);
            return;
        }
        FirebaseFirestore.getInstance().collection("maquinas").document(codigo).get()
                .addOnSuccessListener(doc -> {
                    binding.progresso.setVisibility(View.GONE);
                    if (!doc.exists()) {
                        mostrarNaoEncontrada(codigo);
                        return;
                    }
                    maquina = Maquina.de(doc);
                    binding.textoMaquinaCodigo.setText(maquina.codigo);
                    binding.textoMaquinaNome.setText(getString(R.string.maquina_codigo_setor,
                            maquina.nome, maquina.setor));
                    binding.conteudo.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    binding.progresso.setVisibility(View.GONE);
                    binding.painelErro.setVisibility(View.VISIBLE);
                    binding.textoErro.setText(MensagensErro.de(this, e));
                });
    }

    private void mostrarNaoEncontrada(@NonNull String codigo) {
        binding.progresso.setVisibility(View.GONE);
        binding.painelErro.setVisibility(View.VISIBLE);
        binding.textoErro.setText(getString(R.string.erro_maquina_nao_encontrada, codigo));
    }

    @Nullable
    private Prioridade prioridadeEscolhida() {
        int id = binding.grupoPrioridade.getCheckedButtonId();
        if (id == R.id.botaoAlta) return Prioridade.ALTA;
        if (id == R.id.botaoMedia) return Prioridade.MEDIA;
        if (id == R.id.botaoBaixa) return Prioridade.BAIXA;
        return null;
    }

    private void enviar() {
        if (maquina == null) return;
        Prioridade prioridade = prioridadeEscolhida();
        boolean ok = Validacao.minimo(binding.campoDescricao, 10, R.string.erro_descricao_curta);
        binding.erroPrioridade.setVisibility(prioridade == null ? View.VISIBLE : View.GONE);
        if (!ok || prioridade == null) return;

        Map<String, Object> dados = new HashMap<>();
        dados.put("maquinaCodigo", maquina.codigo);
        dados.put("maquinaNome", maquina.nome);
        dados.put("setor", maquina.setor);
        dados.put("descricao", Validacao.texto(binding.campoDescricao));
        dados.put("prioridade", prioridade.valor);
        dados.put("prioridadeOrdem", prioridade.ordem);
        dados.put("status", StatusChamado.ABERTO.valor);
        dados.put("abertoPorUid", Sessao.uid());
        dados.put("abertoPorNome", Sessao.nome());
        // Horário do servidor: não depende do relógio do celular
        dados.put("abertoEm", FieldValue.serverTimestamp());

        enviando(true);
        FirebaseFirestore.getInstance().collection("chamados").add(dados)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, R.string.chamado_aberto, Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    enviando(false);
                    Snackbar.make(binding.getRoot(), MensagensErro.de(this, e), Snackbar.LENGTH_LONG).show();
                });
    }

    private void enviando(boolean sim) {
        binding.progressoEnvio.setVisibility(sim ? View.VISIBLE : View.INVISIBLE);
        binding.botaoEnviar.setEnabled(!sim);
    }
}
