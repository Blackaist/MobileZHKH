package ru.smarthzkh.blackstork.other;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


//Работает на JSON
public class SaveLoadFile {

    private Context context;
    private String filename = "data";

    public SaveLoadFile(Context context) {
        this.context = context.getApplicationContext();
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            try {
                if( !file.createNewFile() ) {
                    Toast.makeText(context, "Не удалось создать файл!", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public SaveLoadFile(Context context, String filename) {
        this.filename = filename;
        this.context = context.getApplicationContext();
        File file = new File(context.getFilesDir(), filename);
        if (!file.exists()) {
            try {
                if( !file.createNewFile() ) {
                    Toast.makeText(context, "Не удалось создать файл!", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void Write(String name, String phone, String address, String email) {
        try {
            FileOutputStream fout = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fout);
            osw.write(name+"\n");
            osw.write(phone+"\n");
            osw.write(address+"\n");
            osw.write(email+"\n");
            //osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void Write(Map<String, String> map) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = Read();
            if (jsonArray == null)
                jsonArray = new JSONArray();

            FileOutputStream fout = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fout);

            for(Map.Entry<String, String> entry: map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            jsonArray.put(jsonObject);
            jsonArray = sortArray(jsonArray, "paymperiod");
            osw.write(jsonArray.toString());
            //osw.flush();
            osw.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void Write(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = Read();
            if (jsonArray == null)
                jsonArray = new JSONArray();

            FileOutputStream fout = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fout);

            for(int i = 0; i < jsonArray.length(); i++) {
                if(jsonArray.getJSONObject(i).get("paymperiod").equals(jsonObject.get("paymperiod")) &&
                        jsonArray.getJSONObject(i).get("purpose").equals(jsonObject.get("purpose"))) {
                    jsonArray.remove(i);
                    break;
                }
            }
            jsonArray.put(jsonObject);
            jsonArray = sortArray(jsonArray, "paymperiod");
            osw.write(jsonArray.toString());
            //osw.flush();
            osw.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void Rewrite(JSONArray jsonArray) {
        try {
            if (jsonArray == null)
                return;

            FileOutputStream fout = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fout);

            osw.write(jsonArray.toString());
            //osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray sortArray(JSONArray jsonArr, final String sortBy) {
        JSONArray sortedJsonArray = new JSONArray();
        try {
            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonArr.length(); i++) {
                jsonValues.add(jsonArr.getJSONObject(i));
            }
            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = "";
                    String valB = "";
                    try {
                        valA = (String) a.get(sortBy);
                        valB = (String) b.get(sortBy);
                        valA = valA.substring(2, 6) + valA.substring(0, 2);
                        valB = valB.substring(2, 6) + valB.substring(0, 2);
                    } catch (JSONException e) {
                        //do something
                    }
                    return -valA.compareTo(valB);
                    //if you want to change the sort order, simply use the following:
                    //return -valA.compareTo(valB);
                }
            });
            for (int i = 0; i < jsonArr.length(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sortedJsonArray;
    }


    public String[] ReadTemplate() {
        try{
            FileInputStream fout = context.openFileInput(filename);
            BufferedReader fin = new BufferedReader(new InputStreamReader(fout));
            try {
                String s;
                int i = 0;
                String returned[] = new String[4];
                while ((s = fin.readLine()) != null)
                    returned[i++] = s;
                fin.close();
                if (returned.length != 4)
                    return null;
                return returned;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray Read() {
        try{
            FileInputStream fout = context.openFileInput(filename);
            BufferedReader fin = new BufferedReader(new InputStreamReader(fout));
            try {
                String s = fin.readLine();
                if (s == null || s.equals(""))
                    return null;
                return new JSONArray(s);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
