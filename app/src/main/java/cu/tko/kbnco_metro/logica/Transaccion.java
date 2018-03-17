package cu.tko.kbnco_metro.logica;

import java.util.Date;

/**
 * Created by aleguerra05 on 3/16/2018.
 */

public class Transaccion{
    public Date fecha;
    public TIPO_SERVICIO servicio;
    public TIPO_TRANSACCION operacion;
    public Double monto;
    public TIPO_MONEDA moneda;
    public String noTransaccion;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transaccion) {
            return ((Transaccion) obj).noTransaccion.equals(this.noTransaccion);
        }else
            return false;
    }

    @Override
    public String toString()
    {
        return this.noTransaccion;
    }
}