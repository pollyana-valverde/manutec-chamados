package br.com.techinova.manutec.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import br.com.techinova.manutec.databinding.ItemMaquinaBinding;
import br.com.techinova.manutec.model.Maquina;

/** Lista de máquinas (RecyclerView, NC5). */
public class MaquinaAdapter extends ListAdapter<Maquina, MaquinaAdapter.VH> {

    public interface Acoes {
        void aoClicar(@NonNull Maquina maquina);

        void aoVerQr(@NonNull Maquina maquina);
    }

    private final Acoes acoes;

    public MaquinaAdapter(@NonNull Acoes acoes) {
        super(DIFF);
        this.acoes = acoes;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemMaquinaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Maquina m = getItem(position);
        holder.b.textoCodigo.setText(m.codigo);
        holder.b.textoNome.setText(m.nome);
        holder.b.textoSetor.setText(m.setor);
        holder.b.getRoot().setOnClickListener(v -> acoes.aoClicar(m));
        holder.b.botaoQr.setOnClickListener(v -> acoes.aoVerQr(m));
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemMaquinaBinding b;

        VH(ItemMaquinaBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    private static final DiffUtil.ItemCallback<Maquina> DIFF = new DiffUtil.ItemCallback<Maquina>() {
        @Override
        public boolean areItemsTheSame(@NonNull Maquina a, @NonNull Maquina b) {
            return a.codigo.equals(b.codigo);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Maquina a, @NonNull Maquina b) {
            return a.equals(b);
        }
    };
}
