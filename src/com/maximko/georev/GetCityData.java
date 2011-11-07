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
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
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
            if (jsonObj.has("lat")) builder.append("Координаты объекта:\nШирота: " + jsonObj.getString("lat") + "\n");
            if (jsonObj.has("lon")) builder.append("Долгота: " + jsonObj.getString("lon") 
                                                               + "\n\n" + "Сведения об объекте: \n");
            if (addressObj.has("country")) builder.append("" + addressObj.getString("country") + "\n");
            if (addressObj.has("administrative")) builder.append(addressObj.getString("administrative") + "\n");
            if (addressObj.has("state")) builder.append(addressObj.getString("state") + "\n");
            if (addressObj.has("state_district")) builder.append(addressObj.getString("state_district") + "\n");
            if (addressObj.has("county")) builder.append(addressObj.getString("county") + "\n");
            if (addressObj.has("city")) builder.append(addressObj.getString("city") + "\n");
            if (addressObj.has("town")) builder.append(addressObj.getString("town") + "\n");
            if (addressObj.has("suburb")) builder.append(addressObj.getString("suburb") + "\n");
            if (addressObj.has("postcode")) builder.append("Почтовый индекс: " + addressObj.getString("postcode") + "\n");
            if (addressObj.has("pedestrian")) builder.append(addressObj.getString("pedestrian") + "\n");
            if (addressObj.has("road")) builder.append(addressObj.getString("road") + "\n");
            if (addressObj.has("university")) builder.append("Университет: " + addressObj.getString("university") + "\n");
            if (addressObj.has("house_number")) builder.append("Дом #" + addressObj.getString("house_number"));
            finalData = builder.toString();
        } catch (JSONException ex) {
            Logger.getLogger(GetCityData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return finalData;
    }

}
