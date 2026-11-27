package br.com.techinova.manutec.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ActivityQrMaquinaBinding;
import br.com.techinova.manutec.model.Maquina;

/** Mostra o QR da máquina (conteúdo = código) e permite compartilhar a imagem para imprimir (C1). */
public class QrMaquinaActivity extends BaseActivity {

    private static final String EXTRA_CODIGO = "codigo";
    private static final String EXTRA_NOME = "nome";

    private ActivityQrMaquinaBinding binding;
    private Bitmap qr;

    @NonNull
    public static Intent intent(@NonNull Context ctx, @NonNull Maquina maquina) {
        return new Intent(ctx, QrMaquinaActivity.class)
                .putExtra(EXTRA_CODIGO, maquina.codigo)
                .putExtra(EXTRA_NOME, maquina.nome);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrMaquinaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarAppBar(binding.raiz, binding.appBar, getString(R.string.qr_titulo), true);

        String codigo = getIntent().getStringExtra(EXTRA_CODIGO);
        String nome = getIntent().getStringExtra(EXTRA_NOME);
        if (codigo == null) {
            finish();
            return;
        }
        binding.textoCodigo.setText(codigo);
        binding.textoNome.setText(nome);

        try {
            qr = new BarcodeEncoder().encodeBitmap(codigo, BarcodeFormat.QR_CODE, 720, 720);
            binding.imagemQr.setImageBitmap(qr);
        } catch (Exception e) {
            Snackbar.make(binding.getRoot(), R.string.erro_gerar_qr, Snackbar.LENGTH_LONG).show();
            binding.botaoCompartilhar.setEnabled(false);
        }
        binding.botaoCompartilhar.setOnClickListener(v -> compartilhar(codigo, nome));
    }

    /** Monta uma etiqueta (QR + código + nome) e abre o compartilhamento do Android. */
    private void compartilhar(@NonNull String codigo, @Nullable String nome) {
        if (qr == null) return;
        int largura = qr.getWidth();
        int altura = qr.getHeight() + 170;
        Bitmap etiqueta = Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(etiqueta);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(qr, 0, 0, null);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.rgb(14, 58, 91));
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(64);
        canvas.drawText(codigo, largura / 2f, qr.getHeight() + 60, p);
        if (nome != null) {
            p.setTypeface(Typeface.DEFAULT);
            p.setTextSize(36);
            p.setColor(Color.DKGRAY);
            canvas.drawText(nome, largura / 2f, qr.getHeight() + 120, p);
        }

        try {
            File pasta = new File(getCacheDir(), "qr");
            if (!pasta.exists() && !pasta.mkdirs()) throw new IOException("pasta");
            File arquivo = new File(pasta, "qr_" + codigo + ".png");
            try (FileOutputStream out = new FileOutputStream(arquivo)) {
                etiqueta.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", arquivo);
            Intent envio = new Intent(Intent.ACTION_SEND)
                    .setType("image/png")
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(envio, getString(R.string.acao_compartilhar_qr)));
        } catch (IOException e) {
            Snackbar.make(binding.getRoot(), R.string.erro_gerar_qr, Snackbar.LENGTH_LONG).show();
        }
    }
}
