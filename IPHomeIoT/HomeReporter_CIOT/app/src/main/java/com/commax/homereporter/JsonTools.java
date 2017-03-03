package com.commax.homereporter;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class JsonTools {

    static final String DEBUG_TAG = "JsonTools";

    public JsonTools() {

    }

    public int restSIZE(String what, String str) throws IOException {

        JSONObject jsonObject;
        String result="";

//        Log.d(DEBUG_TAG, "restGet : str : "+str);
        try {
            if(str.length()>0) {
                jsonObject = new JSONObject(str);
                try {
                    result = jsonObject.getString(what);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(what.equalsIgnoreCase(NameSpace.LOCATIONS)) {
                    JSONObject subLocationObj = new JSONObject(result);
                    result = subLocationObj.getString(NameSpace.LOCATION);
                }

                JSONArray jsonArray = new JSONArray(result);
                return jsonArray.length();
            }else {
                return 0;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){    //content.isEmpty() or ""
            e.printStackTrace();
        }
        return 0;
    }


    public String makeResult(String locality, String admin2, String admin1, String country){

        String result = "";

        try {
            Log.d(DEBUG_TAG, "makeResult = locality : " + locality + " / admin2 : " + admin2 + " / admin1 : " + admin1 + " / country : " + country);

            if (!TextUtils.isEmpty(locality)) {
                result = locality;
            }

            if ((!TextUtils.isEmpty(result)) && (!TextUtils.isEmpty(admin2))) {
                result = result + ", ";
            }

            if (!TextUtils.isEmpty(admin2)) {
                result = result + admin2;
            }

            if ((!TextUtils.isEmpty(result)) && (!TextUtils.isEmpty(admin1))) {
                result = result + ", ";
            }

            if (!TextUtils.isEmpty(admin1)) {
                result = result + admin1;
            }

            if ((!TextUtils.isEmpty(result)) && (!TextUtils.isEmpty(country))) {
                result = result + ", ";
            }

            if (!TextUtils.isEmpty(country)) {

                result = result + country;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public String reduceSpace(String str){

        try {
            if ((str != null) && (!TextUtils.isEmpty(str))) {

                for (int i = 0; i < str.length(); i++) {

                    if (str.substring(0, 1).equalsIgnoreCase(" ")) {

                        str = str.substring(1);
//                        Log.d(DEBUG_TAG, "reduceSpace : " + str);

                    } else {
                        return str;
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }

    public String restGET(String where, String str, int index, String what) throws IOException {

        JSONObject jsonObject;
        String result="";

//        Log.d(DEBUG_TAG, "restGet : str : "+str);
        try {

            String woeidinfo = "";

            if(str.length()>0) {
                jsonObject = new JSONObject(str);
                try {
                    woeidinfo = jsonObject.getString(where);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(NameSpace.TEST){
                    JSONObject location_obj = new JSONObject(woeidinfo);
                    String str_location = location_obj.getString(NameSpace.LOCATION);

                    JSONArray subLocationArr = new JSONArray(str_location);
                    String this_index = subLocationArr.getString(index);
                    JSONObject sub_obj = new JSONObject(this_index);

                    if(what.equalsIgnoreCase(NameSpace.LOCATION)){

                        String county = sub_obj.getString(NameSpace.COUNTY);
                        JSONObject county_obj = new JSONObject(county);
                        String code = county_obj.getString(NameSpace.CODE);

                        String admin1 = sub_obj.getString(NameSpace.ADMIN1);
                        JSONObject admin1_obj = new JSONObject(admin1);
                        String name_admin1 = admin1_obj.getString(NameSpace.NAME);

                        String admin2 = sub_obj.getString(NameSpace.ADMIN2);
                        JSONObject admin2_obj = new JSONObject(admin2);
                        String name_admin2 = admin2_obj.getString(NameSpace.NAME);

                        String locality = sub_obj.getString(NameSpace.LOCALITY);
                        JSONObject locality_obj = new JSONObject(locality);
                        String name_locality = locality_obj.getString(NameSpace.NAME);

//                        result = name_admin2+", "+name_admin1+", "+code;

                        if((name_locality.equalsIgnoreCase(name_admin2))||(name_locality.equalsIgnoreCase(name_admin1))){
                            name_locality="";
                        }

                        result = makeResult(name_locality, name_admin2, name_admin1, code);

                    }else if(what.equalsIgnoreCase(NameSpace.WOEID)){


                        String admin2 = sub_obj.getString(NameSpace.ADMIN2);
                        JSONObject admin2_obj = new JSONObject(admin2);
                        String name_admin2 = admin2_obj.getString(NameSpace.NAME);

                        String locality = sub_obj.getString(NameSpace.LOCALITY);
                        JSONObject locality_obj = new JSONObject(locality);
                        String name_locality = locality_obj.getString(NameSpace.NAME);

                        String woeid = "";

                        if(TextUtils.isEmpty(woeid)){
                            String code_locality = locality_obj.getString(NameSpace.AREACODE);
                            woeid = code_locality;
                        }

                        if(TextUtils.isEmpty(woeid)){
                            String code_admin2 = admin2_obj.getString(NameSpace.AREACODE);
                            woeid = code_admin2;
                        }

                        if(TextUtils.isEmpty(woeid)){
                            String code_admin2 = admin2_obj.getString(NameSpace.AREACODE);
                            woeid = code_admin2;
                        }

                        if(TextUtils.isEmpty(woeid)){
                            String admin1 = sub_obj.getString(NameSpace.ADMIN1);
                            JSONObject admin1_obj = new JSONObject(admin1);
                            String code_admin1 = admin1_obj.getString(NameSpace.AREACODE);
                            woeid = code_admin1;
                        }

                        if(TextUtils.isEmpty(woeid)){
                            String county = sub_obj.getString(NameSpace.COUNTY);
                            JSONObject county_obj = new JSONObject(county);
                            String code_country = county_obj.getString(NameSpace.AREACODE);
                            woeid = code_country;
                        }

                        result = woeid;
                    }
                }else {
                    JSONArray jsonArray = new JSONArray(woeidinfo);
                    JSONObject sub_json= new JSONObject(jsonArray.get(index).toString());
                    result = sub_json.getString(what);
                }

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){    //content.isEmpty() or ""
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> split(String value, String symbol) {
        ArrayList<String> ret = new ArrayList<String>();
        try {
            value = value.trim();
            while (value.contains(symbol)) {
                try {
                    String string = value.substring(0, value.indexOf(symbol));
                    ret.add(string);
                    value = value.substring(value.indexOf(symbol) + 1);
                } catch (Exception e) {
                    //
                }
            }
            if (value.length() > 0) {
                ret.add(value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
