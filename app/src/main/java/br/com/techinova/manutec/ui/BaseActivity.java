package br.com.techinova.manutec.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ViewAppbarBinding;
import br.com.techinova.manutec.util.Sessao;

/**
 * Base de todas as telas: liga o modo "edge-to-edge" (obrigatório a partir do Android 15)
 * e configura a barra superior.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ícones claros na status bar (fundo azul) e escuros na barra de navegação
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
    }

    /** Configura a toolbar com título e, se pedido, a seta de voltar. */
    protected void configurarAppBar(@NonNull View raiz, @NonNull ViewAppbarBinding appBar,
                                    @NonNull CharSequence titulo, boolean mostrarVoltar) {
        setSupportActionBar(appBar.toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(titulo);
            ab.setDisplayHomeAsUpEnabled(mostrarVoltar);
        }
        configurarInsets(raiz, appBar.getRoot());
    }

    /**
     * Afasta o conteúdo das barras do sistema: o "topo" recebe o espaço da status bar
     * e a raiz recebe o espaço da barra de navegação e do teclado.
     */
    protected void configurarInsets(@NonNull View raiz, @NonNull View topo) {
        final int topoPadTop = topo.getPaddingTop();
        final int raizL = raiz.getPaddingLeft();
        final int raizR = raiz.getPaddingRight();
        final int raizB = raiz.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            topo.setPadding(topo.getPaddingLeft(), topoPadTop + barras.top,
                    topo.getPaddingRight(), topo.getPaddingBottom());
            v.setPadding(raizL + barras.left, v.getPaddingTop(), raizR + barras.right,
                    raizB + Math.max(barras.bottom, teclado.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    /** Pergunta antes de sair da conta (menu "Sair" das telas iniciais). */
    protected void confirmarSair() {
        new MaterialAlertDialogBuilder(this)
                .setMessage(R.string.sair_confirmacao)
                .setNegativeButton(R.string.acao_cancelar, null)
                .setPositiveButton(R.string.acao_sair, (d, w) -> Sessao.sair(this))
                .show();
    }
}
