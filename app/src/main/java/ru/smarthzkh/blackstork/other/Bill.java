package ru.smarthzkh.blackstork.other;

import android.content.Intent;
import android.net.Uri;
import android.renderscript.Element;
import android.util.Base64;

import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import ru.smarthzkh.blackstork.R;


public class Bill {
    private static final String []consts = {"name", "personalacc", "bankname", "bic", "correspacc", "purpose",
                                            "lastname", "firstname", "middlename", "payeraddress", "mode",
                                            "phone", "addamount" , "sum", "payeeinn", "paymperiod", "email"};
    private static Map<String, String> map = new HashMap<>();

    public static String name = "";
    public static String phone = "";
    public static String address = "";
    public static String email = "";
    public static String purpose = "";


    private static void changeKeys() {
        if(map.containsKey("amount")) {
            String s = map.get("amount");
            map.remove("amount");
            map.put("sum", s);
        }

        if(map.containsKey("recipinn")) {
            String s = map.get("recipinn");
            map.remove("recipinn");
            map.put("payeeinn", s);
        }

        if(map.containsKey("paymdest")) {
            String s = map.get("paymdest");
            map.remove("paymdest");
            map.put("purpose", s);
        }

        if(!map.containsKey("purpose")) {
            map.put("purpose", purpose);
        }

        if(map.containsKey("paymperiod")) {
            if(map.get("paymperiod").length() != 6) {
                String s = map.get("paymperiod");
                map.put("paymperiod", s.charAt(0) + s.charAt(1) + "20" + s.charAt(2) + s.charAt(3));
            }
        }
    }

    public static List<String> notFindedFieldsList(Map<String, String> values) {
        map = values;
        changeKeys();
        List<String> arr = new ArrayList<String>(Arrays.asList(consts));
        for(Map.Entry<String, String> entry : values.entrySet()) {
            for (Iterator<String> iter = arr.iterator(); iter.hasNext();) {
                String aConst = iter.next();
                if (entry.getKey().equals(aConst)) {
                    iter.remove();
                }
            }
        }

        return arr;
    }

    public static List<String> addAndCheckNotFindedList(Map<String, String> addToValues) {
        for(Map.Entry<String, String> entry : addToValues.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                if (map.get(entry.getKey()) == null || Objects.equals(map.get(entry.getKey()), "")) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }

        List<String> arr = new ArrayList<String>(Arrays.asList(consts));
        changeKeys();
        for(Map.Entry<String, String> entry : map.entrySet()) {
            for (Iterator<String> iter = arr.iterator(); iter.hasNext();) {
                String aConst = iter.next();
                if (entry.getKey().equals(aConst)) {
                    iter.remove();
                }
            }
        }
        return arr;
    }


    public static List<String> notFindedFieldsList(Map<String, String> values, Map<String, String> addToValues) {

        for(Map.Entry<String, String> entry : addToValues.entrySet()) {
            if (!values.containsKey(entry.getKey())) {
                if (values.get(entry.getKey()) != null && !Objects.equals(values.get(entry.getKey()), "")) {
                    values.put(entry.getKey(), entry.getValue());
                }
            }
        }

        map = values;
        List<String> arr = Arrays.asList(consts);
        changeKeys();
        for(Map.Entry<String, String> entry : values.entrySet()) {
            for (Iterator<String> iter = arr.iterator(); iter.hasNext();) {
                String aConst = iter.next();
                if (entry.getKey().equals(aConst)) {
                    iter.remove();
                }
            }
        }
        return arr;
    }

    public static String getTextField(String key) {
        switch (key) {
            case "name": return "Наименование получателя платежа";
            case "personalacc": return "Номер счета получателя платежа (р/с)";
            case "bankname": return "Наименование банка получателя платежа";
            case "bic": return "Банковский Идентификационный Код";
            case "correspacc": return "Корреспондентский счет банка";
            case "payeeinn": return "ИНН получателя платежа";
            case "lastname": return "Фамилия плательщика";
            case "firstname": return "Имя плательщика";
            case "middlename": return "Отчество плательщика (если есть)"; //
            case "purpose": return "За что оплачиваете квитанцию?"; //
            case "payeraddress": return "Ваше место проживания. Город, адрес, включая квартиру.";
            case "sum": return "Сумма платежа. В копейках."; //
            case "phone": return "Номер телефона"; //
            case "email": return "Email адрес"; //
            case "addamount": return "Пени. В копейках";
            case "paymperiod": return "Дата квитанции. Пример: 022018. ММГГГГ";
        }
        return key;
    }

    public static Map<String, String> getMap() {
        return map;
    }

    public static void setPurpose(String mode) {
        map.put("mode", mode);
        Bill.purpose = getPurposeByMode(mode);
    }

    public static String getPurposeByMode(String mode) {
        switch (mode) {
            case "0":
                return "Оплата квитанции за ЖКХ";
            case "1":
                return "Оплата квитанции за электроэнергию, воду и газ";
            case "2":
                return "Оплата квитанции за природный газ";
            default:
                return "Error in getPurposeByMode";
        }
    }

    public static int getImageByMode(String mode) {
        switch (mode) {
            case "0":
                return R.drawable.house;
            case "1":
                return R.drawable.water_electricity;
            case "2":
                return R.drawable.gas;
            default:
                return R.drawable.ic_menu_manage;
        }
    }

    public static Uri payBill(JSONObject object) throws JSONException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, URISyntaxException {
        //Не участвуют: kpp, category, persAcc (лицевой счет)
        Map <String, String>  billMap = new HashMap<>();
        if(object.get("payed").equals("no")) {
            billMap.put("cost", String.valueOf((Integer.parseInt(object.get("sum").toString()) + Integer.parseInt(object.get("addamount").toString())) / 100 ));
            billMap.put("background", "1");
            billMap.put("type", "spg_test");
            billMap.put("phone_number", object.get("phone").toString());
            billMap.put("email", object.get("email").toString());
            billMap.put("order_id", "0");
            billMap.put("comment", "01.18");
            billMap.put("verbose", "1");
            billMap.put("version", "2.0");
            billMap.put("service_id", "79501");
            billMap.put("test", "operator_cancel");
            billMap.put("transfer_type", "bank");
            billMap.put("recipient_inn", object.get("payeeinn").toString());
            billMap.put("recipient_account", object.get("personalacc").toString());
            billMap.put("recipient_bank_id", object.get("bic").toString());
            billMap.put("recipient_bank_correspondent_account", object.get("correspacc").toString());

            billMap.put("name", object.get("purpose").toString() + " на " + object.get("paymperiod").toString());
            billMap.put("payer_name", object.get("lastname").toString() + " " + object.get("firstname").toString() + " " + object.get("middlename").toString());
            billMap.put("recipient_name", object.get("name").toString());
            billMap.put("recipient_bank_name", object.get("bankname").toString());

        }

        String value = sign("GET", "https://partner.rficb.ru/alba/input/", billMap, "secretkey");
        value = value.substring(0, value.length() - 1);
        billMap.put("check", value);

        return Uri.parse(addQueryStringToUrlString("https://partner.rficb.ru/alba/input/", billMap));
    }

    public static String addQueryStringToUrlString(String url, final Map<String, String> parameters) throws UnsupportedEncodingException {
        if (parameters == null) {
            return url;
        }

        for (Map.Entry<String, String> parameter : parameters.entrySet()) {

            final String encodedKey = URLEncoder.encode(parameter.getKey(), "UTF-8");
            final String encodedValue = URLEncoder.encode(parameter.getValue(), "UTF-8");

            if (!url.contains("?")) {
                url += "?" + encodedKey + "=" + encodedValue;
            } else {
                url += "&" + encodedKey + "=" + encodedValue;
            }
        }

        return url;
    }

    private static String sign(String method, String url, Map<String, String> params, String secretKey)
            throws URISyntaxException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        URI uri = new URI(url);

        List keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (Object key: keys) {
            if (sb.length() > 0) {
                sb.append("&");
            }


            sb.append(String.format("%s=%s", key.toString(), URLEncoder.encode(params.get(key.toString()), "UTF-8").replaceAll("\\+", "%20")));
        }
        String urlParameters = sb.toString();
        String data = method.toUpperCase() + "\n" +
                uri.getHost() + "\n" +
                uri.getPath() + "\n" +
                urlParameters;
        //System.out.println(data);
        Mac hmacInstance = Mac.getInstance("HmacSHA256");
        Charset charSet = Charset.forName("UTF-8");
        SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(charSet.encode(secretKey).array(), "HmacSHA256");
        hmacInstance.init(keySpec);

        return new String (Base64.encode(hmacInstance.doFinal(data.getBytes("UTF-8")), Base64.DEFAULT));
    }
}
