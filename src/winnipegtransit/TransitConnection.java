package winnipegtransit;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.google.gson.*;
import org.json.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author owen
 */
public class TransitConnection {
    private static BufferedReader in;
    private static final String API_KEY = "?api-key=bEUgnZTNurbZtGAnBnJT";
    private static final String WT_URL = "http://api.winnipegtransit.com/";
    private static URL requestURL;
    private static Gson gson;
    private static String stopNo;
    private static String strLine;
    private static StringBuilder jsonInfo;
    private static JSONObject stopInfo;
    
    public TransitConnection(String stopNo)
    {
        stopNo = stopNo;
        gson = new Gson();
        jsonInfo = new StringBuilder();
        
        //create a request to the API for the passed in stop
        try
        {
            requestURL = new URL(WT_URL + "stops/" + stopNo + ".json" + API_KEY);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        
        //read the stream from the URL into a buffered reader
        try
        {
            in = new BufferedReader(new InputStreamReader(requestURL.openStream()));
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        } 
        
        //read a line in from the BufferedReader and append it to a string
        try
        {
            while ((strLine = in.readLine()) != null )
            {
                jsonInfo.append(strLine);
            }
            
            in.close();
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
        
        //convert the JsonInfo reader into a string
        stopInfo = new JSONObject(jsonInfo.toString());
        
        //stopInfo = gson.fromJson(in, stopInfo.class);
    }
    
    public JSONObject getJsonInfo()
    {
        return stopInfo;
    }
    
    
}
