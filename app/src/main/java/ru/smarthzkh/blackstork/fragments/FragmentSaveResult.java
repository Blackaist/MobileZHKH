package ru.smarthzkh.blackstork.fragments;

import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ru.smarthzkh.blackstork.MainActivity;
import ru.smarthzkh.blackstork.R;
import ru.smarthzkh.blackstork.other.Bill;
import ru.smarthzkh.blackstork.other.SaveLoadFile;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_PHONE;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;

public class FragmentSaveResult extends Fragment {

    private List<String> notFindedValues;
    private final static int SIZE_OF_LISTS = 8;
    private Map<String, String> map = new LinkedHashMap<>();
    private String[] list2;

    private OnFragmentInteractionListener mListener;

    public FragmentSaveResult() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Мобильный ЖКХ");
        final View fView = inflater.inflate(R.layout.fragment_save_result, container, false);

        final Button btnSave = fView.findViewById(R.id.save_btn);
        final Button btnPay = fView.findViewById(R.id.pay_btn);
        final TextView textView = fView.findViewById(R.id.textView_around_popup);
        final ImageView imageView = fView.findViewById(R.id.imageView3);

        try {
            map = (LinkedHashMap<String, String>) getActivity().getIntent().getSerializableExtra("HashMap");
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("payed", "no");
        map.put("mode", "0");
        Bill.setPurpose("0");
        textView.setText(Bill.purpose);
        notFindedValues = Bill.notFindedFieldsList(map);


        Button btnPopup = fView.findViewById(R.id.button_popup);

        btnSave.setVisibility(View.INVISIBLE);
        btnPay.setVisibility(View.INVISIBLE);

        addEditTextLines(fView);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt( map.get("sum")) <= 0)
                    map.put("payed", "yes");
                else
                    map.put("payed", "no");
                List<String> list = Bill.notFindedFieldsList(map);
                if(allFieldsOK(fView)) {
                    SaveLoadFile sl = new SaveLoadFile(Objects.requireNonNull(getContext()));
                    sl.Write(Bill.getMap());
                    System.out.println(Bill.getMap().toString());

                    android.support.v4.app.FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                    fragTrans.replace(R.id.container, new FragmentBillingList());
                    fragTrans.commit();
                }


            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: должен открыться сайт. Квитанция архивируется в случае удачной оплаты (инфу брать с api)

                map.put("payed", "yes");
                List<String> list = Bill.notFindedFieldsList(map);
                if(allFieldsOK(fView)) {

                    SaveLoadFile sl = new SaveLoadFile(Objects.requireNonNull(getContext()));
                    sl.Write(Bill.getMap());
                    System.out.println(Bill.getMap().toString());

                    android.support.v4.app.FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                    fragTrans.replace(R.id.container, new FragmentBillingList());
                    fragTrans.commit();
                }
            }
        });

        btnPopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);

                popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        for(int i = 0; i < 4; i++) {
                            LinearLayout ll2 = fView.findViewById(i + 1000);
                            if(ll2 == null)
                                break;
                            LinearLayout ll = fView.findViewById(R.id.linear_not_finded_text);
                            ll.removeView(ll2);
                        }
                        boolean r = checkPurpose(item);
                        if (r)
                            textView.setText(Bill.purpose);
                        createEditText(item);
                        return r;
                    }

                    private void createEditText(MenuItem item) {
                        String list1[] = new String[0];
                        if(item.getItemId() == R.id.menu2)
                            list1 = new String[]{"Холодная вода, куб.м", "Тариф, коп", "Горячая вода, куб.м", "Тариф, коп", "Электроэнергия ночь+день, кВт в час", "Тариф, коп", "Отопление, Гкал/кв.м", "Тариф, коп"};
                        else if (item.getItemId() == R.id.menu3)
                            list1 = new String[]{"Природный газ", "Тариф, коп"};
                        list2 = list1;

                        if(list1.length != 0) {
                            LinearLayout ll = fView.findViewById(R.id.linear_not_finded_text);

                            int realId = 0;
                            for(int j = 0; j < list1.length / 2; j++) {
                                LinearLayout ll2 = new LinearLayout(getContext());//fView.findViewById(R.id.linear_not_finded_text);
                                ll2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                for (int i = 0; i < 2; i++) {
                                    TextInputLayout tip = new TextInputLayout(getContext());
                                    TextInputEditText et = new TextInputEditText(getContext());

                                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    TextInputLayout.LayoutParams tp = new TextInputLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    p.weight = 1;

                                    et.setLayoutParams(tp);
                                    tip.setLayoutParams(p);

                                    et.setId(realId + 100);
                                    et.setSingleLine(true);

                                    et.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);

                                    et.setHint(list1[realId]);
                                    et.setGravity(Gravity.CENTER);
                                    tip.addView(et);
                                    ll2.addView(tip);
                                    realId++;
                                }
                                ll2.setId(1000 + j);
                                ll.addView(ll2);
                            }

                        }
                    }

                    private boolean checkPurpose(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                imageView.setImageResource(Bill.getImageByMode("0"));
                                Bill.setPurpose("0"); map.put("mode", "0"); map.put("purpose", Bill.getPurposeByMode("0"));
                                return true;
                            case R.id.menu2:
                                imageView.setImageResource(Bill.getImageByMode("1"));
                                Bill.setPurpose("1"); map.put("mode", "1"); map.put("purpose", Bill.getPurposeByMode("1"));
                            return true;
                            case R.id.menu3:
                                imageView.setImageResource(Bill.getImageByMode("2"));
                                Bill.setPurpose("2"); map.put("mode", "2"); map.put("purpose", Bill.getPurposeByMode("2"));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
        return fView;
    }

    private void addEditTextLines(final View view) {
        Button btnSave = view.findViewById(R.id.save_btn);
        Button btnPay = view.findViewById(R.id.pay_btn);
        LinearLayout ll = view.findViewById(R.id.linear_not_finded_text);

        for(int i = 0; i < notFindedValues.size(); i++) {
            TextInputLayout tip = new TextInputLayout(getContext());
            final TextInputEditText et = new TextInputEditText(getContext());

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextInputLayout.LayoutParams tp = new TextInputLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            et.setLayoutParams(tp);
            tip.setLayoutParams(p);


            et.setHint(Bill.getTextField(notFindedValues.get(i)));
            et.setGravity(Gravity.CENTER);

            String s[] = Bill.name.split(" ");
            if (s.length > 1) {
                switch (notFindedValues.get(i)) {
                    case "firstname":
                        et.setText(s[1]);
                        break;
                    case "lastname":
                        et.setText(s[0]);
                        break;
                    case "middlename":
                        if (s.length == 3)
                            et.setText(s[2]);
                        break;
                    case "phone":
                        et.setText(Bill.phone);
                        et.setInputType(TYPE_CLASS_PHONE);
                        break;
                    case "email":
                        et.setText(Bill.email);
                        et.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case "payeraddress":
                        et.setText(Bill.address);
                        break;
                    case "paymperiod":
                        DateFormat df = new SimpleDateFormat("MMyyyy", new Locale("ru"));
                        et.setText(df.format(Calendar.getInstance().getTime()));
                        et.setInputType(TYPE_CLASS_NUMBER);
                        break;
                    case "addamount":
                    case "sum":
                    case "bic":
                    case "personalacc":
                    case "correspacc":
                    case "payeeinn":
                        et.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                }
            }

            et.setId(i + 1);
            et.setSingleLine(true);
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Button btnSave = view.findViewById(R.id.save_btn);
                    Button btnPay = view.findViewById(R.id.pay_btn);
                    for(int i = 0; i < notFindedValues.size(); i++) {
                        TextInputEditText et = view.findViewById(i + 1);
                        if(et == null || et.getText() == null || et.getText().toString().equals("")) {
                            changeButtonVisibility(btnPay, false);
                            changeButtonVisibility(btnSave, false);
                            return;
                        }
                    }
                    changeButtonVisibility(btnPay, true);
                    changeButtonVisibility(btnSave, true);
                }
            });
            tip.addView(et);
            ll.addView(tip);
        }

        changeButtonVisibility(btnPay, true);
        changeButtonVisibility(btnSave, true);
        for(int i = 0; i < notFindedValues.size(); i++) {
            TextInputEditText et = view.findViewById(i + 1);
            if (et == null || et.getText() == null || et.getText().toString().equals("")) {
                changeButtonVisibility(btnPay, false);
                changeButtonVisibility(btnSave, false);
                break;
            }
        }
    }

    public void changeButtonVisibility(Button btn, boolean turnOn) {
        if (turnOn)
            btn.setVisibility(View.VISIBLE);
        else
            btn.setVisibility(View.GONE);
    }

    private Boolean allFieldsOK(View fView) {
        Map<String, String> newValues = new HashMap<>();
        int size = notFindedValues.size();

        for (int i = 0; i < size; i++) {
            TextInputEditText et = fView.findViewById(i + 1);
            if (et == null || et.getText() == null || et.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Пропущено поле! " + notFindedValues.get(i), Toast.LENGTH_LONG).show();
                return false;
            }
            newValues.put(notFindedValues.get(i), et.getText().toString());
        }

        if (list2 != null) {
            for (int i = 0; i < list2.length; i++) {
                TextInputEditText et = fView.findViewById(i + 100);
                if (et == null || et.getText() == null || et.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Пропущено поле! " + list2[i], Toast.LENGTH_LONG).show();
                    return false;
                }
                if (i % 2 == 0)
                    newValues.put("volume" + (int) Math.ceil(i / 2), et.getText().toString());
                else
                    newValues.put("tariff" + (int) Math.ceil(i / 2), et.getText().toString());
            }

            List<String> list = Bill.addAndCheckNotFindedList(newValues);
            return list.isEmpty();
        }
        return true;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
