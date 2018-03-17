package cu.tko.kbnco_metro.logica;

/**
 * Created by aleguerra05 on 3/16/2018.
 */

public enum TIPO_TRANSACCION {
    DEBITO("DB","Debito"),
    CREDITO("CR","Credito"),
    DEFAULT("DF","Default");

    public String texto;
    public String title;

    TIPO_TRANSACCION(String texto, String title){
        this.texto = texto;
        this.title = title;
    }

    public static TIPO_TRANSACCION Identificar(String cadena)
    {
        TIPO_TRANSACCION tipo_transaccion = TIPO_TRANSACCION.DEFAULT;
        if(cadena!=null){
            for (TIPO_TRANSACCION tipo : TIPO_TRANSACCION.values()) {
                if (cadena.toUpperCase().contains(tipo.texto)) {
                    tipo_transaccion = tipo;
                }
            }
        }
        return tipo_transaccion;
    }

    @Override
    public String toString() {
        return title;
    }
}
