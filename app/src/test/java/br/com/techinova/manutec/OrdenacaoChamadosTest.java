package br.com.techinova.manutec;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.techinova.manutec.model.Chamado;
import br.com.techinova.manutec.model.OrdenacaoChamados;
import br.com.techinova.manutec.model.Prioridade;

/** Confere a ordem da fila do técnico (C3). */
public class OrdenacaoChamadosTest {

    private static Chamado chamado(String id, Prioridade p, long abertoEmMs) {
        Chamado c = new Chamado();
        c.id = id;
        c.prioridade = p;
        c.abertoEm = new Date(abertoEmMs);
        return c;
    }

    @Test
    public void filaOrdenaPorPrioridadeDepoisPorHorario() {
        List<Chamado> lista = new ArrayList<>(Arrays.asList(
                chamado("baixa-antigo", Prioridade.BAIXA, 1_000),
                chamado("alta-recente", Prioridade.ALTA, 5_000),
                chamado("media", Prioridade.MEDIA, 2_000),
                chamado("alta-antigo", Prioridade.ALTA, 3_000)));

        lista.sort(OrdenacaoChamados.FILA);

        assertEquals("alta-antigo", lista.get(0).id);
        assertEquals("alta-recente", lista.get(1).id);
        assertEquals("media", lista.get(2).id);
        assertEquals("baixa-antigo", lista.get(3).id);
    }

    @Test
    public void recentesPrimeiro() {
        List<Chamado> lista = new ArrayList<>(Arrays.asList(
                chamado("velho", Prioridade.ALTA, 1_000),
                chamado("novo", Prioridade.BAIXA, 9_000)));

        lista.sort(OrdenacaoChamados.RECENTES);

        assertEquals("novo", lista.get(0).id);
    }
}
