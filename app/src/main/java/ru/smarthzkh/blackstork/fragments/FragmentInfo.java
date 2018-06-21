package ru.smarthzkh.blackstork.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;

import ru.smarthzkh.blackstork.R;
import ru.smarthzkh.blackstork.other.SaveLoadFile;

public class FragmentInfo extends Fragment {

    private JSONObject jsonObject;

    private OnFragmentInteractionListener mListener;

    public FragmentInfo() {
        // Required empty public constructor
    }


    public static FragmentInfo newInstance(String param1, String param2) {
        FragmentInfo fragment = new FragmentInfo();
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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Информация");
        View fView = inflater.inflate(R.layout.fragment_info, container, false);

        TextView name = fView.findViewById(R.id.info_nametext);
        TextView service = fView.findViewById(R.id.info_servicetext);
        TextView payeeinn = fView.findViewById(R.id.info_inntext);
        TextView personalacc = fView.findViewById(R.id.info_personalacctext);
        TextView bankname = fView.findViewById(R.id.info_banknametext);
        TextView bic = fView.findViewById(R.id.info_bictext);
        TextView kpp = fView.findViewById(R.id.info_kpptext);
        TextView correspacc = fView.findViewById(R.id.info_correspacctext);
        TextView persacc = fView.findViewById(R.id.info_persacctext);
        TextView paymperiod = fView.findViewById(R.id.info_paymperiodtext);
        TextView amount = fView.findViewById(R.id.info_amounttext);


        try {
            jsonObject = new JSONObject((String)getActivity().getIntent().getSerializableExtra("HashMap"));
            String sum = String.valueOf((Double.parseDouble( jsonObject.get("sum").toString()) + Double.parseDouble( jsonObject.get("addamount").toString())) / 100.0 ) + " р.";

            name.setText(jsonObject.get("name").toString());
            service.setText(jsonObject.get("purpose").toString());
            payeeinn.setText(jsonObject.get("payeeinn").toString());
            personalacc.setText(jsonObject.get("personalacc").toString());
            bankname.setText(jsonObject.get("bankname").toString());
            bic.setText(jsonObject.get("bic").toString());
            kpp.setText(jsonObject.get("kpp").toString());
            correspacc.setText(jsonObject.get("correspacc").toString());
            persacc.setText(jsonObject.get("persacc").toString());
            paymperiod.setText(new StringBuffer(jsonObject.get("paymperiod").toString()).insert(2, "/").toString());


            String bigText = sum;
            String list[] = new String[0];
            if(jsonObject.get("mode").equals("1"))
                list = new String[]{"Холодная вода", "Горячая вода", "Электроэнергия ночь+день",  "Отопление"};
            else if (jsonObject.get("mode").equals("2"))
                list = new String[]{"Природный газ"};

            if(list.length != 0)
                for(int i = 0; i < list.length; i++)
                    bigText += "\n" + list[i] + ": " + jsonObject.get("volume" + i) + "\tтариф: "+ jsonObject.get("tariff" + i);

            amount.setText(bigText);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Button btnDelete = fView.findViewById(R.id.delete_bill);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveLoadFile sl = new SaveLoadFile(Objects.requireNonNull(getContext()));
                JSONArray jsonArray = sl.Read();
                for(int i = 0; i < jsonArray.length(); i++) {
                    try {
                        if(jsonArray.getJSONObject(i).get("paymperiod").equals(jsonObject.get("paymperiod")) &&
                                jsonArray.getJSONObject(i).get("purpose").equals(jsonObject.get("purpose"))) {
                            jsonArray.remove(i);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                sl.Rewrite(jsonArray);


                android.support.v4.app.FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fragTrans.replace(R.id.container, new FragmentBillingList());
                fragTrans.commit();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
