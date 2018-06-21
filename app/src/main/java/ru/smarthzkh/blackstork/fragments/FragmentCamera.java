package ru.smarthzkh.blackstork.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import ru.smarthzkh.blackstork.MainActivity;
import ru.smarthzkh.blackstork.R;

import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCamera.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCamera#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCamera extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView textView;

    public FragmentCamera() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCamera.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCamera newInstance(String param1, String param2) {
        FragmentCamera fragment = new FragmentCamera();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fView = inflater.inflate(R.layout.fragment_camera, container, false);

        textView = fView.findViewById(R.id.scanText);
        final Button button = fView.findViewById(R.id.scanner);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanQR();
            }
        });
        return fView;
    }

    public void scanQR() {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(getActivity(), "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                try {
                    if (format.equals("QR_CODE")) {
                        Map<String, String> map = new LinkedHashMap<String, String>();
                        for (String keyValue : contents.split(" *\\| *")) {
                            String[] pairs = keyValue.split(" *= *", 2);
                            map.put(pairs[0].toLowerCase(), pairs.length == 1 ? "" : pairs[1]);
                            if (pairs.length != 1)
                                textView.append(pairs[0].toLowerCase() + ": " + pairs[1] + "\n");

                            Bundle extras = new Bundle();
                            extras.putSerializable("HashMap", (Serializable) map);
                            getActivity().getIntent().putExtras(extras);
                            android.support.v4.app.FragmentTransaction fragTrans = getActivity().getSupportFragmentManager().beginTransaction();
                            fragTrans.replace(R.id.container, new FragmentSaveResult());
                            fragTrans.commit();
                        }

                        //TODO: заменить

                        try {
                            String login_url = "https://partner.rficb.ru/alba/input/";
                            URL url = new URL(login_url);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setDoInput(true);
                            OutputStream outputStream =httpURLConnection.getOutputStream(); //TODO: Ошибка. Не создается?
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            String post_data = URLEncoder.encode("key", "UTF-8")+"="+URLEncoder.encode("aHpwuQQgo2+YEGKwPDVD/aBNec0gW5tGea6wnre5754=", "UTF-8")+"&"
                                    +URLEncoder.encode("cost", "UTF-8")+"="+URLEncoder.encode("1", "UTF-8")+"&"
                                    +URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode("sasha97.21@mail.ru", "UTF-8")+"&"
                                    +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode("Оплата жкх", "UTF-8");

                            bufferedWriter.write(post_data);
                            bufferedWriter.flush();
                            bufferedWriter.close();
                            outputStream.close();
                            InputStream inputStream = httpURLConnection.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            String line;
                            String result = "";

                            while((line = bufferedReader.readLine()) != null) {
                                result += line;
                            }
                            bufferedReader.close();
                            inputStream.close();
                            httpURLConnection.disconnect();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                    } else {
                        Toast toast = Toast.makeText(getActivity(), "Это не QR код", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                catch (Exception e) {

                }
            }
        }
    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
