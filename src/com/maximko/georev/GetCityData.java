package com.maximko.georev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class GetCityData {

    public String getJSONData(double lat, double lon) {
        String data = "Ошибка получения данных.";
        try {
            BufferedReader in = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://nominatim.openstreetmap.org/reverse?format=json&lat="
                                                    + lat + "&lon=" + lon + "&zoom=18&addressdetails=1");
            HttpResponse response = httpclient.execute(httpget);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line).append(NL);
            }
            in.close();
            data = sb.toString();
        } catch (IOException ex) {
            Logger.getLogger(GetCityData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
    
    public String getData(double lat, double lon) {
        String jsonData = getJSONData(lat, lon);
        String finalData = "Ошибка парсинга данных.";
        try {
            StringBuilder builder = new StringBuilder("");
            JSONObject jsonObj = new JSONObject(jsonData);
            JSONObject addressObj = jsonObj.getJSONObject("address");
            if (jsonObj.has("lat")) builder.append("Координаты объекта:\nШирота: ").append(jsonObj.getString("lat")).append("\n");
            if (jsonObj.has("lon")) builder.append("Долгота: ").append(jsonObj.getString("lon")).append("\n\n" + "Сведения об объекте: \n");
            if (addressObj.has("country")) builder.append("").append(addressObj.getString("country")).append("\n");
            if (addressObj.has("administrative")) builder.append(addressObj.getString("administrative")).append("\n");
            if (addressObj.has("state")) builder.append(addressObj.getString("state")).append("\n");
            if (addressObj.has("state_district")) builder.append(addressObj.getString("state_district")).append("\n");
            if (addressObj.has("county")) builder.append(addressObj.getString("county")).append("\n");
            if (addressObj.has("city")) builder.append(addressObj.getString("city")).append("\n");
            if (addressObj.has("town")) builder.append(addressObj.getString("town")).append("\n");
            if (addressObj.has("suburb")) builder.append(addressObj.getString("suburb")).append("\n");
            if (addressObj.has("postcode")) builder.append("Почтовый индекс: ").append(addressObj.getString("postcode")).append("\n");
            if (addressObj.has("pedestrian")) builder.append(addressObj.getString("pedestrian")).append("\n");
            if (addressObj.has("road")) builder.append(addressObj.getString("road")).append("\n");
            if (addressObj.has("university")) builder.append("Университет: ").append(addressObj.getString("university")).append("\n");
            if (addressObj.has("house_number")) builder.append("Дом #").append(addressObj.getString("house_number"));
            finalData = builder.toString();
        } catch (JSONException ex) {
            Logger.getLogger(GetCityData.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (jsonData == null) {
            finalData = "Ошибка получения данных";
        }
        return finalData;
    }

}

