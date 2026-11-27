package br.com.techinova.manutec.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityCadastroMaquinaBinding;
import br.com.techinova.manutec.model.Maquina;
import br.com.techinova.manutec.util.MensagensErro;
import br.com.techinova.manutec.util.Validacao;

/**
 * Cadastro de máquina com código, nome e setor (C1). O código vira o id do documento
 * (maquinas/{codigo}) e é o texto gravado no QR.
 */
public class CadastroMaquinaActivity extends BaseActivity {

    private ActivityCadastroMaquinaBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroMaquinaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.cadastro_maquina_titulo), true);

        Validacao.limparErroAoDigitar(binding.campoCodigo, binding.campoNome, binding.campoSetor);
        binding.botaoSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        String codigo = Maquina.normalizarCodigo(Validacao.texto(binding.campoCodigo));
        boolean ok;
        if (codigo.isEmpty()) {
            ok = Validacao.obrigatorio(binding.campoCodigo);
        } else {
            ok = Validacao.checar(binding.campoCodigo, Maquina.codigoValido(codigo), R.string.erro_codigo_invalido);
        }
        ok &= Validacao.minimo(binding.campoNome, 3, R.string.erro_nome_maquina_curto);
        ok &= Validacao.checar(binding.campoSetor, !Validacao.texto(binding.campoSetor).isEmpty(),
                R.string.erro_setor_obrigatorio);
        if (!ok) return;

        Maquina maquina = new Maquina(codigo, Validacao.texto(binding.campoNome),
                Validacao.texto(binding.campoSetor));
        Map<String, Object> dados = new HashMap<>();
        dados.put("codigo", maquina.codigo);
        dados.put("nome", maquina.nome);
        dados.put("setor", maquina.setor);
        dados.put("criadoEm", FieldValue.serverTimestamp());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("maquinas").document(codigo);
        carregando(true);
        // Transação: só cria se o código ainda não existir (não sobrescreve outra máquina)
        db.runTransaction(tx -> {
                    if (tx.get(ref).exists()) return false;
                    tx.set(ref, dados);
                    return true;
                })
                .addOnSuccessListener(criada -> {
                    carregando(false);
                    if (Boolean.TRUE.equals(criada)) {
                        Toast.makeText(this, getString(R.string.maquina_salva, codigo), Toast.LENGTH_SHORT).show();
                        startActivity(QrMaquinaActivity.intent(this, maquina));
                        finish();
                    } else {
                        Validacao.checar(binding.campoCodigo, false, R.string.erro_codigo_existente);
                    }
                })
                .addOnFailureListener(e -> {
                    carregando(false);
                    Snackbar.make(binding.getRoot(), MensagensErro.de(this, e), Snackbar.LENGTH_LONG).show();
                });
    }

    private void carregando(boolean sim) {
        binding.progresso.setVisibility(sim ? View.VISIBLE : View.INVISIBLE);
        binding.botaoSalvar.setEnabled(!sim);
    }
}
