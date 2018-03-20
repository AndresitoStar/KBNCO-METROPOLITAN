package cu.tko.kbnco_metro.logica;

/**
 * Created by TKO_PC on 14/03/2018.
 */

public enum TIPO_OPERACIONES {
    DEFAULT("Default","Operacion"),
    CONSULTAR_SALDO("Banco Metropolitano:  La consulta de saldo","Consulta de Saldo"),
    CONSULTAR_SALDO_ERROR("Fallo la consulta de saldo","Consulta de Saldo Fallida"),
    TRANSFERENCIA_TX_SALDO("Banco Metropolitano:  La Transferencia","Transferencia Enviada"),
    TRANSFERENCIA_RX_SALDO("Se ha realizado una transferencia","Transferencia Recibida"),
    TRANSFERENCIA_FALLIDA("Fallo la transferencia","Transferencia Fallida"),
    ERROR_FACTURA("Consulta de Servicio Error, Banco Metropolitano: Factura inexistente","Consulta Factura Inexistente"),
    FACTURA("Banco Metropolitano:  Factura: ","Consulta Factura"),
    FACTURA_PAGADA("Banco Metropolitano:  El pago de la factura ","Factura Pagada"),
    AUTENTICAR("Usted se ha autenticado en la plataforma","Autenticacion Exitosa"),
    INFO_CODIGO_ACTIVACION("El código de activación","Registro - Codigo Activación"),
    ERROR_CODIGO_ACTIVACION("Para obtener el codigo de activacion","Error - Codigo Activación"),
    REGISTRAR_SUCESS("Banco Metropolitano:  La operacion de registro fue completada","Registro Exitoso"),
    ERROR("Error ","Error"),
    ERROR_AUTENTICACION("Error de autenticacion","Error de Autenticación"),
    ERROR_SERVICIO_SIN_AUTENTICACION("Fallo la consulta de servicio. Para realizar esta operacion","Error de Consulta Factura"),
    ERROR_ULTIMAS_OPERACIONES("Fallo la consulta de las ultimas operaciones","Consulta de Ultimas Operaciones Fallida"),
    ULTIMAS_OPERACIONES("Banco Metropolitano Ultimas operaciones","Ultimas Operaciones");

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
