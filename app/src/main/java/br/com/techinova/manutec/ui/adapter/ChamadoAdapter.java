package br.com.techinova.manutec.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import br.com.techinova.manutec.R;
import br.com.techinova.manutec.databinding.ItemChamadoBinding;
import br.com.techinova.manutec.model.Chamado;
import br.com.techinova.manutec.model.StatusChamado;
import br.com.techinova.manutec.util.Formatos;

/** Lista de chamados usada pelo operador, pela fila do técnico e pelo histórico (NC5). */
public class ChamadoAdapter extends ListAdapter<Chamado, ChamadoAdapter.VH> {

    public interface AoClicar {
        void aoClicar(@NonNull Chamado chamado);
    }

    @Nullable
    private final AoClicar aoClicar;

    public ChamadoAdapter(@Nullable AoClicar aoClicar) {
        super(DIFF);
        this.aoClicar = aoClicar;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemChamadoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Chamado c = getItem(position);
        Context ctx = h.itemView.getContext();

        h.b.textoMaquina.setText(c.maquinaCodigo + " · " + c.maquinaNome);
        h.b.textoSetor.setText(c.setor);
        h.b.textoDescricao.setText(c.descricao);

        int corPrioridade = ContextCompat.getColor(ctx, c.prioridade.cor);
        h.b.faixaPrioridade.setBackgroundColor(corPrioridade);
        h.b.etiquetaPrioridade.setText(c.prioridade.rotulo);
        h.b.etiquetaPrioridade.setBackgroundTintList(ColorStateList.valueOf(corPrioridade));

        h.b.etiquetaStatus.setText(c.status.rotulo);
        h.b.etiquetaStatus.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(ctx, c.status.cor)));
        h.b.textoRodape.setText(rodape(ctx, c));

        if (aoClicar != null) {
            h.itemView.setOnClickListener(v -> aoClicar.aoClicar(c));
        } else {
            h.itemView.setOnClickListener(null);
            h.itemView.setClickable(false);
        }
    }

    @NonNull
    private static String rodape(@NonNull Context ctx, @NonNull Chamado c) {
        if (c.status == StatusChamado.ENCERRADO) {
            return ctx.getString(R.string.item_encerrado, Formatos.duracao(c.duracaoMs()), c.tecnicoNome);
        }
        if (c.status == StatusChamado.EM_ATENDIMENTO) {
            return ctx.getString(R.string.item_em_atendimento, c.tecnicoNome, Formatos.hora(c.assumidoEm));
        }
        return ctx.getString(R.string.item_aberto_ha,
                Formatos.tempoDesde(c.abertoEm, System.currentTimeMillis()), c.abertoPorNome);
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemChamadoBinding b;

        VH(ItemChamadoBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    private static final DiffUtil.ItemCallback<Chamado> DIFF = new DiffUtil.ItemCallback<Chamado>() {
        @Override
        public boolean areItemsTheSame(@NonNull Chamado a, @NonNull Chamado b) {
            return a.id.equals(b.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Chamado a, @NonNull Chamado b) {
            return a.equals(b);
        }
    };
}
