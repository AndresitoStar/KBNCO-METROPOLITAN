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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cu.tko.kbnco_metro.MainActivity;
import cu.tko.kbnco_metro.R;
import cu.tko.kbnco_metro.logica.Sms;
import cu.tko.kbnco_metro.logica.TIPO_MONEDA;
import cu.tko.kbnco_metro.logica.TIPO_OPERACIONES;
import cu.tko.kbnco_metro.logica.TIPO_SERVICIO;
import cu.tko.kbnco_metro.logica.TIPO_TRANSACCION;
import cu.tko.kbnco_metro.logica.Transaccion;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistorialFrg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorialFrg extends Fragment {
    private ListView historialListView;
    private HistorialAdapter adapter;
    List<Sms> lista;

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
        lista = ((MainActivity)getActivity()).utils.mensajes;
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
                String content = lista.get(i).messageContent;
                builder.setMessage(content);
                builder.create().show();
            }
        });

        return view;
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
            return lista.size();
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
    }
}
