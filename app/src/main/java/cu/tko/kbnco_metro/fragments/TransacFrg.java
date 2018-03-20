package cu.tko.kbnco_metro.fragments;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cu.tko.kbnco_metro.R;
import cu.tko.kbnco_metro.logica.Sms;
import cu.tko.kbnco_metro.logica.TIPO_MONEDA;
import cu.tko.kbnco_metro.logica.TIPO_OPERACIONES;
import cu.tko.kbnco_metro.logica.TIPO_SERVICIO;
import cu.tko.kbnco_metro.logica.TIPO_TRANSACCION;
import cu.tko.kbnco_metro.logica.Transaccion;

import static java.lang.Long.*;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransacFrg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransacFrg extends Fragment {
    private ListView historialListView;
    private HistorialAdapter adapter;
    private List<Transaccion> transacciones;

    public TransacFrg() {
        // Required empty public constructor
        transacciones = new ArrayList<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistorialFrg.
     */
    // TODO: Rename and change types and number of parameters
    public static TransacFrg newInstance() {
        TransacFrg fragment = new TransacFrg();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        List<Sms> lista = readSms();
        adapter = new HistorialAdapter(getContext(), R.layout.transac_item, transacciones);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_historial, container, false);
        historialListView = view.findViewById(R.id.historial_list);
        historialListView.setAdapter(adapter);
        historialListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Detalles de la operacion");
                String content = adapter.getLista().get(i).monto.toString()+" "+adapter.getLista().get(i).moneda.toString();
                builder.setMessage(content);
                builder.create().show();
            }
        });

        return view;
    }

    public List<Sms> readSms() {
        ArrayList<Sms> smsInbox = new ArrayList<Sms>();

        Uri uriSms = Uri.parse(("content://sms/inbox"));
        Cursor cursor = getActivity().getContentResolver()
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
                        Long timestamp = parseLong(date);
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

                        smsInbox.add(message);
                    }
                } while (cursor.moveToPrevious());
            }
            cursor.close();
        }
        return smsInbox;
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

    /**
     * CLASE ADAPTER PARA CONTROLAR LA LISTA DEL INICIO
     */
    private class HistorialAdapter extends ArrayAdapter {

        //private List<Transaccion> lista;

        /**
         * Constructor
         *
         * @param context  The current context.
         * @param resource The resource ID for a layout file containing a TextView to use when
         *                 instantiating views.
         * @param objects  The objects to represent in the ListView.
         */
        public HistorialAdapter(Context context, int resource, List<Transaccion> objects) {
            super(context, resource, objects);
            transacciones = objects;
        }

        @Override
        public int getCount() {
            return getLista().size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.transac_item, null);
            }

            DateFormat formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                formatter = new SimpleDateFormat("d 'de' MMMM 'del' yyyy - h:mm:ss a", Locale.forLanguageTag("ES"));
            }
            else{
                formatter = new SimpleDateFormat("d 'de' MMMM 'del' yyyy - h:mm:ss a", Locale.US);
            }

            String tipoHistorial = transacciones.get(position).operacion.toString()+" "+transacciones.get(position).monto.toString();
            String fechaHistorial = transacciones.get(position).servicio.toString()+" "+formatter.format(transacciones.get(position).fecha);

            //Seteando el tipo de historial
            TextView tvTitulo = (TextView) convertView.findViewById(R.id.hist_tipo);
            tvTitulo.setText(tipoHistorial);

            //Seteando estadisticas
            TextView tvCantidad = (TextView) convertView.findViewById(R.id.hist_fecha);
            tvCantidad.setText(fechaHistorial);

            return convertView;
        }

        public List<Transaccion> getLista() {
            return transacciones;
        }
    }

}
