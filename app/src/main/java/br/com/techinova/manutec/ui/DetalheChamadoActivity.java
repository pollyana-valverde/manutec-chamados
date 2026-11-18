package br.com.techinova.manutec.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityDetalheChamadoBinding;
import br.com.techinova.manutec.model.Chamado;
import br.com.techinova.manutec.model.StatusChamado;
import br.com.techinova.manutec.util.Formatos;
import br.com.techinova.manutec.util.MensagensErro;
import br.com.techinova.manutec.util.Sessao;
import br.com.techinova.manutec.util.Validacao;

/** Detalhe do chamado para o técnico: assumir (C4) e encerrar com a solução (C5). */
public class DetalheChamadoActivity extends BaseActivity {

    private static final String EXTRA_ID = "chamadoId";

    private ActivityDetalheChamadoBinding binding;
    private DocumentReference ref;
    private ListenerRegistration escuta;
    @Nullable
    private Chamado chamado;

    @NonNull
    public static Intent intent(@NonNull Context ctx, @NonNull String chamadoId) {
        return new Intent(ctx, DetalheChamadoActivity.class).putExtra(EXTRA_ID, chamadoId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalheChamadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.detalhe_titulo), true);

        String id = getIntent().getStringExtra(EXTRA_ID);
        if (id == null) {
            finish();
            return;
        }
        ref = FirebaseFirestore.getInstance().collection("chamados").document(id);
        binding.botaoAssumir.setOnClickListener(v -> assumir());
        binding.botaoEncerrar.setOnClickListener(v -> confirmarEncerrar());
        binding.botaoHistorico.setOnClickListener(v -> {
            if (chamado != null) {
                startActivity(HistoricoMaquinaActivity.intent(this, chamado.maquinaCodigo,
                        chamado.maquinaNome, chamado.setor));
            }
        });
        Validacao.limparErroAoDigitar(binding.campoSolucao);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ref == null) return;
        // Escuta o documento: se outro técnico assumir, a tela muda na hora
        escuta = ref.addSnapshotListener((doc, erro) -> {
            binding.progresso.setVisibility(View.GONE);
            if (erro != null) {
                Snackbar.make(binding.getRoot(), MensagensErro.de(this, erro), Snackbar.LENGTH_LONG).show();
                return;
            }
            if (doc == null || !doc.exists()) {
                finish();
                return;
            }
            chamado = Chamado.de(doc);
            mostrar(chamado);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (escuta != null) escuta.remove();
    }

    private void mostrar(@NonNull Chamado c) {
        binding.conteudo.setVisibility(View.VISIBLE);
        binding.textoMaquina.setText(c.maquinaCodigo + " · " + c.maquinaNome);
        binding.textoSetor.setText(c.setor);
        binding.textoDescricao.setText(c.descricao);

        binding.etiquetaPrioridade.setText(c.prioridade.rotulo);
        binding.etiquetaPrioridade.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, c.prioridade.cor)));
        binding.etiquetaStatus.setText(c.status.rotulo);
        binding.etiquetaStatus.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(this, c.status.cor)));

        binding.textoLinhaDoTempo.setText(linhaDoTempo(c));
        binding.botaoAssumir.setVisibility(c.status == StatusChamado.ABERTO ? View.VISIBLE : View.GONE);

        boolean souEu = c.tecnicoUid != null && c.tecnicoUid.equals(Sessao.uid());
        binding.painelEncerrar.setVisibility(
                c.status == StatusChamado.EM_ATENDIMENTO && souEu ? View.VISIBLE : View.GONE);
        binding.painelSolucao.setVisibility(c.status == StatusChamado.ENCERRADO ? View.VISIBLE : View.GONE);
        binding.textoSolucao.setText(c.solucao);
    }

    @NonNull
    protected String linhaDoTempo(@NonNull Chamado c) {
        StringBuilder sb = new StringBuilder();
        sb.append("• ").append(getString(R.string.linha_aberto, Formatos.dataHora(c.abertoEm), c.abertoPorNome));
        if (c.status != StatusChamado.ABERTO) {
            sb.append("\n• ").append(getString(R.string.linha_assumido,
                    Formatos.dataHora(c.assumidoEm), c.tecnicoNome));
        }
        if (c.status == StatusChamado.ENCERRADO) {
            sb.append("\n• ").append(getString(R.string.linha_encerrado, Formatos.dataHora(c.fechadoEm)));
            sb.append("\n• ").append(getString(R.string.linha_duracao, Formatos.duracao(c.duracaoMs())));
        }
        return sb.toString();
    }

    /**
     * C4: assumir dentro de uma TRANSAÇÃO. O Firestore lê o status e grava na mesma
     * operação; se dois técnicos tocarem juntos, só o primeiro consegue. O segundo recebe
     * a mensagem de que o chamado já está em atendimento e nada muda.
     */
    private void assumir() {
        final String uid = Sessao.uid();
        final String nome = Sessao.nome();
        final String outroTecnico = getString(R.string.outro_tecnico);
        if (uid == null) return;

        acao(true);
        FirebaseFirestore.getInstance().runTransaction(tx -> {
                    DocumentSnapshot atual = tx.get(ref);
                    if (!atual.exists()) return getString(R.string.erro_chamado_inexistente);
                    if (!StatusChamado.ABERTO.valor.equals(atual.getString("status"))) {
                        String quem = atual.getString("tecnicoNome");
                        return getString(R.string.erro_ja_assumido, quem == null ? outroTecnico : quem);
                    }
                    Map<String, Object> mudancas = new HashMap<>();
                    mudancas.put("status", StatusChamado.EM_ATENDIMENTO.valor);
                    mudancas.put("tecnicoUid", uid);
                    mudancas.put("tecnicoNome", nome);
                    mudancas.put("assumidoEm", FieldValue.serverTimestamp());
                    tx.update(ref, mudancas);
                    return null; // null = deu certo
                })
                .addOnSuccessListener(problema -> {
                    acao(false);
                    if (problema == null) {
                        Snackbar.make(binding.getRoot(), R.string.chamado_assumido, Snackbar.LENGTH_LONG).show();
                    } else {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle(R.string.titulo_nao_foi_possivel)
                                .setMessage(problema)
                                .setPositiveButton(R.string.acao_ok, null)
                                .show();
                    }
                })
                .addOnFailureListener(e -> {
                    acao(false);
                    Snackbar.make(binding.getRoot(), MensagensErro.de(this, e), Snackbar.LENGTH_LONG).show();
                });
    }

    private void confirmarEncerrar() {
        if (chamado == null) return;
        if (!Validacao.minimo(binding.campoSolucao, 10, R.string.erro_solucao_curta)) return;
        new MaterialAlertDialogBuilder(this)
                .setMessage(getString(R.string.encerrar_confirmacao, chamado.maquinaCodigo))
                .setNegativeButton(R.string.acao_cancelar, null)
                .setPositiveButton(R.string.acao_encerrar, (d, w) -> encerrar())
                .show();
    }

    /** C5: grava a solução e a data de fechamento (horário do servidor). */
    private void encerrar() {
        Map<String, Object> mudancas = new HashMap<>();
        mudancas.put("status", StatusChamado.ENCERRADO.valor);
        mudancas.put("solucao", Validacao.texto(binding.campoSolucao));
        mudancas.put("fechadoEm", FieldValue.serverTimestamp());

        acao(true);
        ref.update(mudancas)
                .addOnSuccessListener(nada -> {
                    acao(false);
                    Snackbar.make(binding.getRoot(), R.string.chamado_encerrado, Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    acao(false);
                    Snackbar.make(binding.getRoot(), MensagensErro.de(this, e), Snackbar.LENGTH_LONG).show();
                });
    }

    protected void acao(boolean emAndamento) {
        binding.progressoAcao.setVisibility(emAndamento ? View.VISIBLE : View.INVISIBLE);
        binding.botaoAssumir.setEnabled(!emAndamento);
        binding.botaoEncerrar.setEnabled(!emAndamento);
    }
}
