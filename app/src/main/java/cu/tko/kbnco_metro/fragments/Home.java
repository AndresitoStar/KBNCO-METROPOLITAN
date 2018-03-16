package cu.tko.kbnco_metro.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import cu.tko.kbnco_metro.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    private HomeFragmentListener listener;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(HomeFragmentListener listener) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton imageButtonHistorial = view.findViewById(R.id.imageButtonHistorialHome);
        imageButtonHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.goHistorialFromHome();
            }
        });
        ImageButton imageButtonSaldo = view.findViewById(R.id.imageButtonSaldoHome);
        imageButtonSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String USSD = "*444*46" + Uri.encode("#");
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +USSD));
                startActivity(callIntent);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragmentListener) {
            listener = (HomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface HomeFragmentListener {
        void goHistorialFromHome();
    }

}
