package winnipegtransit;
//import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
//import com.google.gson.*;
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
    private BufferedReader in;
    private final String API_KEY = "api-key=bEUgnZTNurbZtGAnBnJT";
    private final String WT_URL = "http://api.winnipegtransit.com/";
    private URL requestURL;
    //private static Gson gson;
    private String stopNo;
    private String strLine;
    private StringBuilder jsonInfo;
    private JSONObject stopInfo;
    private JSONObject scheduleInfo;
    
    //new strings to avoid breaking old ones.
    //private static ArrayList<URL> urls;
    private URL stopInfoURL;
    private URL stopScheduleInfoURL;
    
    public TransitConnection(String stopNo)
    {
        stopNo = stopNo;
        
       //urls = new ArrayList<URL>();
        
        
        //create a request to the API for the passed in stop
        try
        {
            //requestURL = new URL(WT_URL + "stops/" + stopNo + ".json" + API_KEY);
            
            //new
            stopInfoURL = new URL(WT_URL + "stops/" + stopNo + ".json?" + API_KEY);
            stopScheduleInfoURL = new URL(WT_URL + "stops/" + stopNo + "/schedule.json?max-results-per-route=3&" + API_KEY);
            
            //urls.add(stopInfoURL);
            //urls.add(stopScheduleInfoURL);
            
            //urls.trimToSize();
            
            stopInfo = retrieveFromWeb(stopInfoURL);
            scheduleInfo = retrieveFromWeb(stopScheduleInfoURL);
            

            
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        
        /*
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
                */
    }
    
    private JSONObject retrieveFromWeb(URL url)
    {
        jsonInfo = new StringBuilder();
        in = null;
       //read the stream from the URL into a buffered reader
        try
        {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
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
        return new JSONObject(jsonInfo.toString());
        
        //stopInfo = gson.fromJson(in, stopInfo.class);
    }
    
    public JSONObject getStopInfo()
    {
        return stopInfo;
    }
    
    public JSONObject getScheduleInfo()
    {
        return scheduleInfo;
    }
    
    
}
