package cu.tko.kbnco_metro.logica;

/**
 * Created by aleguerra05 on 3/16/2018.
 */

public enum TIPO_SERVICIO {
    ATM("AY","Cajero Automatico"),
    TELEFONO("TELF","TELEF","Telefono"),
    ELECTRICIDAD("ELEC","Electricidad"),
    TRANSFERENCIA("TRAN","Transferencia"),
    SALARIO("EV","Salario"),
    INTERES("IO","Interes"),
    DEFAULT("DF","Default");


    public String texto;
    public String texto_alt;
    public String title;

    TIPO_SERVICIO(String texto, String title){
        this.texto = texto;
        this.title = title;
    }
    TIPO_SERVICIO(String texto,String texto_alt, String title){
        this.texto = texto;
        this.texto_alt = texto_alt;
        this.title = title;
    }

    public static TIPO_SERVICIO Identificar(String cadena)
    {
        TIPO_SERVICIO tipo_transaccion = TIPO_SERVICIO.DEFAULT;
        if(cadena!=null){
            for (TIPO_SERVICIO tipo : TIPO_SERVICIO.values()) {
                if (cadena.toUpperCase().contains(tipo.texto))
                    tipo_transaccion = tipo;
                if (tipo.texto_alt!=null&&cadena.toUpperCase().contains(tipo.texto_alt))
                    tipo_transaccion = tipo;
            }
        }
        return tipo_transaccion;
    }

    @Override
    public String toString() {
        return title;
    }
}
