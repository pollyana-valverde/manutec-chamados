package br.com.techinova.manutec.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.Objects;

/** Documento chamados/{id}. */
public class Chamado {

    public String id;
    public String maquinaCodigo;
    public String maquinaNome;
    public String setor;
    public String descricao;
    public Prioridade prioridade;
    public StatusChamado status;
    public String abertoPorUid;
    public String abertoPorNome;
    public String tecnicoUid;
    public String tecnicoNome;
    public String solucao;
    @Nullable
    public Date abertoEm;
    @Nullable
    public Date assumidoEm;
    @Nullable
    public Date fechadoEm;

    @NonNull
    public static Chamado de(@NonNull DocumentSnapshot doc) {
        Chamado c = new Chamado();
        c.id = doc.getId();
        c.maquinaCodigo = texto(doc, "maquinaCodigo");
        c.maquinaNome = texto(doc, "maquinaNome");
        c.setor = texto(doc, "setor");
        c.descricao = texto(doc, "descricao");
        c.prioridade = Prioridade.de(doc.getString("prioridade"));
        c.status = StatusChamado.de(doc.getString("status"));
        c.abertoPorUid = doc.getString("abertoPorUid");
        c.abertoPorNome = texto(doc, "abertoPorNome");
        c.tecnicoUid = doc.getString("tecnicoUid");
        c.tecnicoNome = texto(doc, "tecnicoNome");
        c.solucao = texto(doc, "solucao");
        c.abertoEm = data(doc, "abertoEm");
        c.assumidoEm = data(doc, "assumidoEm");
        c.fechadoEm = data(doc, "fechadoEm");
        return c;
    }

    /** Duração do reparo em milissegundos (abertura até fechamento), ou -1 se ainda não fechou. */
    public long duracaoMs() {
        if (abertoEm == null || fechadoEm == null) return -1;
        return Math.max(0, fechadoEm.getTime() - abertoEm.getTime());
    }

    private static String texto(DocumentSnapshot doc, String campo) {
        String v = doc.getString(campo);
        return v == null ? "" : v;
    }

    /**
     * ESTIMATE: logo após gravar, o horário do servidor ainda não chegou; usamos a hora
     * local como estimativa para a tela não mostrar "vazio" por um instante.
     */
    @Nullable
    private static Date data(DocumentSnapshot doc, String campo) {
        Timestamp t = doc.getTimestamp(campo, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
        return t == null ? null : t.toDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chamado)) return false;
        Chamado c = (Chamado) o;
        return Objects.equals(id, c.id) && status == c.status && prioridade == c.prioridade
                && Objects.equals(descricao, c.descricao) && Objects.equals(tecnicoUid, c.tecnicoUid)
                && Objects.equals(solucao, c.solucao) && Objects.equals(abertoEm, c.abertoEm)
                && Objects.equals(assumidoEm, c.assumidoEm) && Objects.equals(fechadoEm, c.fechadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, prioridade, abertoEm, fechadoEm);
    }
}
