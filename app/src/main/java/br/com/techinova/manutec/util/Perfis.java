package br.com.techinova.manutec.util;

/**
 * Perfis possíveis no documento usuarios/{uid} (campo "perfil").
 * O perfil é definido pelo grupo no console do Firebase (NC4), nunca pelo app.
 */
public final class Perfis {
    public static final String OPERADOR = "operador";
    public static final String TECNICO = "tecnico";

    private Perfis() {
    }
}
