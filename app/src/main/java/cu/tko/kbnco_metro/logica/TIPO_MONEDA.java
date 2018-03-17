package cu.tko.kbnco_metro.logica;

/**
 * Created by aleguerra05 on 3/16/2018.
 */

public enum TIPO_MONEDA {
    CUC("CUC","Cuc"),
    CUP("CUP","Cup"),
    DEFAULT("DF","Default");

    public String texto;
    public String title;

    TIPO_MONEDA(String texto, String title){
        this.texto = texto;
        this.title = title;
    }

    public static TIPO_MONEDA Identificar(String cadena)
    {
        TIPO_MONEDA tipo_transaccion = TIPO_MONEDA.DEFAULT;
        if(cadena!=null){
            for (TIPO_MONEDA tipo : TIPO_MONEDA.values()) {
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
