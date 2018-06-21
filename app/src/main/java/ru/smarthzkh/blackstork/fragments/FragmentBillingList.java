package ru.smarthzkh.blackstork.fragments;

import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

import ru.smarthzkh.blackstork.R;
import ru.smarthzkh.blackstork.other.BillingListAdapter;
import ru.smarthzkh.blackstork.other.SaveLoadFile;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPay.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBillingList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recyclerView;
    LinearLayoutManager  mLayoutManager;
    RecyclerView.Adapter mAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentBillingList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPay.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentBillingList newInstance(String param1, String param2) {
        FragmentBillingList fragment = new FragmentBillingList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Мои Квитанции");
        View fView = inflater.inflate(R.layout.fragment_bill, container, false);

        final boolean[] flags = {false, true, true, true};
        recyclerView = fView.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new BillingListAdapter(Objects.requireNonNull(getContext()), flags, getActivity());
        recyclerView.setAdapter(mAdapter);

        final CheckBox checkBox = fView.findViewById(R.id.checkBox2);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flags[0] = checkBox.isChecked();
                mAdapter = new BillingListAdapter(Objects.requireNonNull(getContext()), flags, getActivity());
                recyclerView.setAdapter(mAdapter);
            }
        });

        final CheckBox checkBoxHouse = fView.findViewById(R.id.checkBox3);
        checkBoxHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flags[1] = checkBoxHouse.isChecked();

                mAdapter = new BillingListAdapter(Objects.requireNonNull(getContext()), flags, getActivity());
                recyclerView.setAdapter(mAdapter);
            }
        });

        final CheckBox checkBoxWaterElectricity = fView.findViewById(R.id.checkBox4);
        checkBoxWaterElectricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flags[2] = checkBoxWaterElectricity.isChecked();

                mAdapter = new BillingListAdapter(Objects.requireNonNull(getContext()), flags, getActivity());
                recyclerView.setAdapter(mAdapter);
            }
        });

        final CheckBox checkBoxGas = fView.findViewById(R.id.checkBox5);
        checkBoxGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flags[3] = checkBoxGas.isChecked();

                mAdapter = new BillingListAdapter(Objects.requireNonNull(getContext()), flags, getActivity());
                recyclerView.setAdapter(mAdapter);
            }
        });

        return fView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Мобильный ЖКХ");
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
