package br.com.techinova.manutec;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import br.com.techinova.manutec.model.Chamado;
import br.com.techinova.manutec.model.ResumoHistorico;
import br.com.techinova.manutec.model.StatusChamado;
import br.com.techinova.manutec.util.Formatos;

/** Confere o cálculo e o texto da duração dos reparos (C5/C6). */
public class FormatosTest {

    private static final long MIN = 60_000L;

    @Test
    public void duracaoEmMinutosHorasEDias() {
        assertEquals("menos de 1 min", Formatos.duracao(30_000));
        assertEquals("45 min", Formatos.duracao(45 * MIN));
        assertEquals("1 h 05 min", Formatos.duracao(65 * MIN));
        assertEquals("2 d 3 h", Formatos.duracao((2 * 24 + 3) * 60 * MIN));
    }

    @Test
    public void tempoDesde() {
        Date inicio = new Date(0);
        assertEquals("agora", Formatos.tempoDesde(inicio, 20_000));
        assertEquals("há 12 min", Formatos.tempoDesde(inicio, 12 * MIN));
        assertEquals("agora", Formatos.tempoDesde(null, 12 * MIN));
    }

    private static Chamado encerrado(long minutos) {
        Chamado c = new Chamado();
        c.status = StatusChamado.ENCERRADO;
        c.abertoEm = new Date(0);
        c.fechadoEm = new Date(minutos * MIN);
        return c;
    }

    @Test
    public void duracaoDoChamadoUsaAberturaEFechamento() {
        assertEquals(90 * MIN, encerrado(90).duracaoMs());
        Chamado aberto = new Chamado();
        aberto.status = StatusChamado.ABERTO;
        aberto.abertoEm = new Date(0);
        assertEquals(-1, aberto.duracaoMs());
    }

    @Test
    public void resumoCalculaMediaSoDosEncerrados() {
        Chamado aberto = new Chamado();
        aberto.status = StatusChamado.ABERTO;
        aberto.abertoEm = new Date(0);

        ResumoHistorico r = ResumoHistorico.de(Arrays.asList(encerrado(30), encerrado(90), aberto));

        assertEquals(3, r.total);
        assertEquals(2, r.encerrados);
        assertEquals(120 * MIN, r.tempoTotalMs);
        assertEquals(60 * MIN, r.tempoMedioMs);
    }
}
