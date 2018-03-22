package cu.tko.kbnco_metro.logica;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by a.guerra on 3/22/2018.
 */

public class Utils {
    public List<Sms> mensajes;
    public List<Transaccion> transacciones;
    Context context;

    public Utils()
    {
        context = BanmetApp.getAppContext();
        readSmsAndTransactions();
    }

    public void readSmsAndTransactions() {
        mensajes = new ArrayList<>();
        transacciones = new ArrayList<>();

        Uri uriSms = Uri.parse(("content://sms/inbox"));
        Cursor cursor = context.getContentResolver()
                .query(uriSms, new String[]{"_id", "address", "date", "body",
                                "type", "read"}, "type=1", null,
                        "date" + " COLLATE LOCALIZED ASC");
        if (cursor != null) {
            cursor.moveToLast();
            if (cursor.getCount() > 0) {
                do {
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    if (address != null && address.equalsIgnoreCase("pagoxmovil")) {
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        Long timestamp = Long.parseLong(date);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp);
                        DateFormat formatter = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            formatter = new SimpleDateFormat("d 'de' MMMM 'del' yyyy - h:mm:ss a", Locale.forLanguageTag("ES"));
                        }
                        else{
                            formatter = new SimpleDateFormat("d 'de' MMMM 'del' yyyy - h:mm:ss a", Locale.US);
                        }
                        Sms message = new Sms();
                        message.messageContent = cursor.getString(cursor.getColumnIndex("body"));
                        message.fecha = formatter.format(calendar.getTime());
                        message.fecha_date = calendar.getTime();
                        PoblarTransacciones(message);
                        mensajes.add(message);
                    }
                } while (cursor.moveToPrevious());
            }
            cursor.close();
        }
    }

    private void PoblarTransacciones(Sms message) {

        Transaccion transaccion = new Transaccion();

        //Decodificando los mensajes de Tipo Ultimas Operaciones
        if (TIPO_OPERACIONES.identificar(message.messageContent.trim()) == TIPO_OPERACIONES.ULTIMAS_OPERACIONES){
            String[] lines = message.messageContent.split("\n");

            for (int i = 2; i< lines.length;i++) {

                if (lines[i].contains("INFO:"))
                    continue;

                transaccion = new Transaccion();
                String[] items = lines[i].split(";");
                String dateStr = items[0].trim();
                /////////////////////////////////////////////////////////////////
                SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                try {
                    date = fmt.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                /////////////////////////////////////////////////////////////////
                transaccion.fecha = date;
                /////////////////////////////////////////////////////////////////
                transaccion.servicio = TIPO_SERVICIO.Identificar(items[1].trim());
                transaccion.operacion = TIPO_TRANSACCION.Identificar(items[2].trim());
                transaccion.monto = Double.parseDouble(items[3].trim());
                transaccion.moneda = TIPO_MONEDA.Identificar(items[4].trim());
                transaccion.noTransaccion = (items[5].trim()).split(" ")[0].trim();

                if (!transacciones.contains(transaccion))
                    transacciones.add(transaccion);
            }
        }
        //Decodificando Factura Pagada
        if (TIPO_OPERACIONES.identificar(message.messageContent.trim()) == TIPO_OPERACIONES.FACTURA_PAGADA){
            String[] lines = message.messageContent.split("\n");

            transaccion = new Transaccion();
            transaccion.fecha = message.fecha_date;
            transaccion.servicio = TIPO_SERVICIO.Identificar(lines[0]);
            transaccion.operacion = TIPO_TRANSACCION.Identificar(lines[4]);
            transaccion.monto = Double.parseDouble(lines[2].trim().split(" ")[2].trim());
            transaccion.moneda = TIPO_MONEDA.Identificar(lines[2].trim().split(" ")[3].trim());
            transaccion.noTransaccion = lines[3].trim().split(" ")[2].trim();
            if (!transacciones.contains(transaccion))
                transacciones.add(transaccion);

        }
        //Decodificando Transferencia de Saldo
        if (TIPO_OPERACIONES.identificar(message.messageContent.trim()) == TIPO_OPERACIONES.TRANSFERENCIA_RX_SALDO){
            String[] lines = message.messageContent.split("\n");

            transaccion = new Transaccion();
            transaccion.fecha = message.fecha_date;
            transaccion.servicio = TIPO_SERVICIO.Identificar(lines[0]);
            transaccion.operacion = TIPO_TRANSACCION.CREDITO;
            transaccion.monto = Double.parseDouble(lines[0].trim().split(" ")[10].trim());
            transaccion.moneda = TIPO_MONEDA.Identificar(lines[0].trim().split(" ")[11].trim());
            transaccion.noTransaccion = lines[0].trim().split(" ")[14].trim();
            if (!transacciones.contains(transaccion))
                transacciones.add(transaccion);
        }
    }
}
