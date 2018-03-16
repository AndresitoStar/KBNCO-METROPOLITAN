package cu.tko.kbnco_metro.logica;

/**
 * Created by TKO_PC on 14/03/2018.
 */

public enum TIPO_OPERACIONES {
    DEFAULT("Default","Operacion"),
    CONSULTAR_SALDO("Banco Metropolitano:  La consulta de saldo","Consulta de saldo"),
    FACTURA("Banco Metropolitano:  Factura: ","Factura"),
    AUTENTICAR("Usted se ha autenticado en la plataforma","Autenticacion exitosa"),
    REGISTRAR_SUCESS("Banco Metropolitano:  La operacion de registro fue completada","Registro exitoso"),
    ERROR("Error ","Error"),
    ULTIMAS_OPERACIONES("Banco Metropolitano Ultimas operaciones","Ultimas operaciones");

    public String inicioMsg;
    public String title;

    TIPO_OPERACIONES(String inicioMsg, String title) {
        this.inicioMsg = inicioMsg;
        this.title = title;
    }

    public static TIPO_OPERACIONES identificar(String cadena){
        TIPO_OPERACIONES tipo_operaciones = TIPO_OPERACIONES.DEFAULT;
        if (cadena != null){
            for (TIPO_OPERACIONES tipo : TIPO_OPERACIONES.values()) {
                if (cadena.startsWith(tipo.inicioMsg)) {
                    tipo_operaciones = tipo;
                }
            }
        }
        return tipo_operaciones;
    }

    @Override
    public String toString() {
        return title;
    }
}
