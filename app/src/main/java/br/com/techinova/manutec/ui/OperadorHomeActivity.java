package br.com.techinova.manutec.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityOperadorHomeBinding;
import br.com.techinova.manutec.databinding.DialogoCodigoBinding;
import br.com.techinova.manutec.model.Chamado;
import br.com.techinova.manutec.model.Maquina;
import br.com.techinova.manutec.ui.adapter.ChamadoAdapter;
import br.com.techinova.manutec.util.MensagensErro;
import br.com.techinova.manutec.util.Sessao;
import br.com.techinova.manutec.util.Validacao;

/**
 * Home do operador: lê o QR da máquina para abrir chamado (C2) e acompanha os
 * chamados que abriu, atualizados em tempo real (NC2).
 */
public class OperadorHomeActivity extends BaseActivity {

    private ActivityOperadorHomeBinding binding;
    private ChamadoAdapter adapter;
    private ListenerRegistration escuta;

    /** Abre a câmera do ZXing e recebe o texto lido do QR. */
    private final ActivityResultLauncher<ScanOptions> leitorQr =
            registerForActivityResult(new ScanContract(), resultado -> {
                if (resultado.getContents() != null) abrirChamado(resultado.getContents());
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOperadorHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.home_operador_titulo), false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getString(R.string.ola_usuario, Sessao.nome()));
        }

        adapter = new ChamadoAdapter(null);
        binding.lista.setLayoutManager(new LinearLayoutManager(this));
        binding.lista.setAdapter(adapter);
        binding.estadoVazio.tituloVazio.setText(R.string.operador_vazio_titulo);
        binding.estadoVazio.mensagemVazio.setText(R.string.operador_vazio_mensagem);

        binding.botaoLerQr.setOnClickListener(v -> lerQr());
        binding.botaoDigitarCodigo.setOnClickListener(v -> digitarCodigo());
    }

    @Override
    protected void onStart() {
        super.onStart();
        String uid = Sessao.uid();
        if (uid == null) {
            Sessao.sair(this);
            return;
        }
        // O filtro por abertoPorUid é obrigatório: as regras só deixam o operador ler os próprios chamados
        escuta = FirebaseFirestore.getInstance().collection("chamados")
                .whereEqualTo("abertoPorUid", uid)
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
                    // Mais recentes primeiro
                    chamados.sort((a, b) -> data(b.abertoEm).compareTo(data(a.abertoEm)));
                    adapter.submitList(chamados);
                    binding.estadoVazio.getRoot().setVisibility(chamados.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (escuta != null) escuta.remove();
    }

    private static Date data(@Nullable Date d) {
        return d == null ? new Date(0) : d;
    }

    private void lerQr() {
        ScanOptions opcoes = new ScanOptions()
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                .setPrompt(getString(R.string.scan_prompt))
                .setBeepEnabled(true)
                .setOrientationLocked(false);
        leitorQr.launch(opcoes);
    }

    /** Alternativa à câmera (útil no emulador): digitar o código impresso abaixo do QR. */
    private void digitarCodigo() {
        DialogoCodigoBinding dialogo = DialogoCodigoBinding.inflate(getLayoutInflater());
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialogo_codigo_titulo)
                .setView(dialogo.getRoot())
                .setNegativeButton(R.string.acao_cancelar, null)
                .setPositiveButton(R.string.acao_continuar,
                        (d, w) -> abrirChamado(Validacao.texto(dialogo.campoCodigo)))
                .show();
    }

    private void abrirChamado(@NonNull String conteudoLido) {
        String codigo = Maquina.normalizarCodigo(conteudoLido);
        if (!Maquina.codigoValido(codigo)) {
            Snackbar.make(binding.getRoot(), R.string.erro_qr_invalido, Snackbar.LENGTH_LONG).show();
            return;
        }
        startActivity(AbrirChamadoActivity.intent(this, codigo));
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
