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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cu.tko.kbnco_metro.R;
import cu.tko.kbnco_metro.logica.TIPO_OPERACIONES;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistorialFrg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorialFrg extends Fragment {
    private ListView historialListView;
    private HistorialAdapter adapter;

    public HistorialFrg() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistorialFrg.
     */
    // TODO: Rename and change types and number of parameters
    public static HistorialFrg newInstance() {
        HistorialFrg fragment = new HistorialFrg();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        List<Sms> lista = readSms();
        adapter = new HistorialAdapter(getContext(), R.layout.historial_item, lista);
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
                String content = adapter.getLista().get(i).messageContent;
                builder.setMessage(content);
                builder.create().show();
            }
        });

        return view;
    }

    private class Sms {
        public String messageNumber;
        public String messageContent;
        public String fecha;
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
                        smsInbox.add(message);
                    }
                } while (cursor.moveToPrevious());
            }
            cursor.close();
        }

        return smsInbox;

    }

    /**
     * CLASE ADAPTER PARA CONTROLAR LA LISTA DEL INICIO
     */
    private class HistorialAdapter extends ArrayAdapter {

        private List<Sms> lista;

        /**
         * Constructor
         *
         * @param context  The current context.
         * @param resource The resource ID for a layout file containing a TextView to use when
         *                 instantiating views.
         * @param objects  The objects to represent in the ListView.
         */
        public HistorialAdapter(Context context, int resource, List<Sms> objects) {
            super(context, resource, objects);
            lista = objects;
        }

        @Override
        public int getCount() {
            return getLista().size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.historial_item, null);
            }

            String tipoHistorial = TIPO_OPERACIONES.identificar(lista.get(position).messageContent.trim()).toString();
            String fechaHistorial = lista.get(position).fecha;

            //Seteando el tipo de historial
            TextView tvTitulo = (TextView) convertView.findViewById(R.id.hist_tipo);
            tvTitulo.setText(tipoHistorial);

            //Seteando estadisticas
            TextView tvCantidad = (TextView) convertView.findViewById(R.id.hist_fecha);
            tvCantidad.setText(fechaHistorial);

            return convertView;
        }

        public List<Sms> getLista() {
            return lista;
        }
    }

}
