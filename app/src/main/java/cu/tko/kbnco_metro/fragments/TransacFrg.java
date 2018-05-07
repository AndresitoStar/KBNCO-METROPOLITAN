package cu.tko.kbnco_metro.fragments;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.VolumeShaper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

import cu.tko.kbnco_metro.MainActivity;
import cu.tko.kbnco_metro.R;
import cu.tko.kbnco_metro.logica.Saldo;
import cu.tko.kbnco_metro.logica.Sms;
import cu.tko.kbnco_metro.logica.TIPO_MONEDA;
import cu.tko.kbnco_metro.logica.TIPO_OPERACIONES;
import cu.tko.kbnco_metro.logica.TIPO_SERVICIO;
import cu.tko.kbnco_metro.logica.TIPO_TRANSACCION;
import cu.tko.kbnco_metro.logica.Transaccion;
import cu.tko.kbnco_metro.logica.Utils;

import static java.lang.Long.*;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransacFrg#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransacFrg extends Fragment {
    private ListView historialListView;
    private TextView saldoText;
    private HistorialAdapter adapter;
    private List<Transaccion> transacciones;
    private List<Saldo> saldos;

    public TransacFrg() {
        // Required empty public constructor

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
        adapter = new HistorialAdapter(getContext(), R.layout.transac_item, transacciones);
        transacciones = ((MainActivity)getActivity()).utils.transacciones;
        saldos = ((MainActivity)getActivity()).utils.saldos;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transac, container, false);

        saldoText = view.findViewById(R.id.saldo_txt);

        saldoText.setText(saldos.get(0).monto.toString());

        historialListView = view.findViewById(R.id.transac_list);
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

    /**
     * CLASE ADAPTER PARA CONTROLAR LA LISTA DEL INICIO
     */
    private class HistorialAdapter extends ArrayAdapter {

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
                formatter = new SimpleDateFormat("d 'de' MMMM 'del' yyyy", Locale.forLanguageTag("ES"));
            }
            else{
                formatter = new SimpleDateFormat("d 'de' MMMM 'del' yyyy", Locale.US);
            }

            String tipoHistorial = transacciones.get(position).operacion.toString()+" "+transacciones.get(position).monto.toString();
            String fechaHistorial = transacciones.get(position).servicio.toString()+" "+formatter.format(transacciones.get(position).fecha);
            String saldoHistorial = transacciones.get(position).saldo.toString()+" "+transacciones.get(position).moneda;

            //Seteando el tipo de historial
            TextView tvTitulo = (TextView) convertView.findViewById(R.id.hist_tipo);
            tvTitulo.setText(tipoHistorial);

            //Seteando fecha
            TextView tvCantidad = (TextView) convertView.findViewById(R.id.hist_fecha);
            tvCantidad.setText(fechaHistorial);

            //Seteando el saldo
            TextView tvSaldo = (TextView) convertView.findViewById(R.id.hist_saldo);
            tvSaldo.setText(saldoHistorial);

            //Cambiando color
            LinearLayout elelemtLoyout = (LinearLayout) convertView.findViewById(R.id.elementLayout);
            if (transacciones.get(position).operacion == TIPO_TRANSACCION.CREDITO)
                elelemtLoyout.setBackgroundColor(Color.BLUE);
            else
                elelemtLoyout.setBackgroundColor(Color.RED);
            return convertView;
        }

        public List<Transaccion> getLista() {
            return transacciones;
        }

    }

}
