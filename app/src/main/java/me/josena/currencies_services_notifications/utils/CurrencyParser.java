package me.josena.currencies_services_notifications.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import me.josena.currencies_services_notifications.data.Currency;

public class CurrencyParser {

    private Context context;
    private final String CURRENCIES_FILE = "currencies.json";

    public CurrencyParser(Context context) {

        this.context = context;
    }

    public ArrayList<Currency> obtainCurrencies() {

        //Get filesDir outside activity --> inside just use getFilesDir()
        File filesDir = context.getFilesDir();
        //Get file stored in device
        File file = new File(filesDir, CURRENCIES_FILE);
        //If there is something in file
        if (file.length() > 0) {
            //Init array

            try {
                //Treat json object
                JSONObject jsonObject = readJSON(file);
                JSONArray jsonArray = jsonObject.getJSONArray("currencies");
                JSONObject jsonDollarObject = jsonArray.getJSONObject(0);
                JSONObject jsonEuroObject = jsonArray.getJSONObject(1);
                //Store json values into pojo array
                ArrayList<Currency> currencies = new ArrayList<Currency>() {
                    {
                        add(new Currency(jsonDollarObject.getString("name"), jsonDollarObject.getDouble("value")));
                        add(new Currency(jsonEuroObject.getString("name"), jsonEuroObject.getDouble("value")));
                    }
                };

                return currencies;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private JSONObject readJSON(File file) {

        String json = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                json += line;
            }
            bufferedReader.close();
            return new JSONObject(json);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}