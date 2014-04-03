package winnipegtransit;
//import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.json.*;
import java.util.ArrayList;
import java.util.Date;
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
    private String strLine;
    private StringBuilder jsonInfo;
    private JSONObject scheduleInfo;
    private URL stopScheduleInfoURL;
    private URL stopFeaturesURL;
    private JSONObject stopFeatures;
    private StopInfo s;
    private Schedule sc;
    private ArrayList<StopFeature> stopFeats;    
    
    public TransitConnection(String stopNo)
    {
        
        //create a request to the API for the passed in stop
        try
        {
            stopScheduleInfoURL = new URL(WT_URL + "stops/" + stopNo + "/schedule.json?max-results-per-route=3&" + API_KEY);
            stopFeaturesURL = new URL(WT_URL + "stops/" + stopNo + "/features.json?" + API_KEY);
            
            scheduleInfo = retrieveFromWeb(stopScheduleInfoURL);
            stopFeatures = retrieveFromWeb(stopFeaturesURL);
            
            buildStopInfo();
            buildScheduleInfo();
            buildStopFeatures();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }  

    }
    
    private JSONObject retrieveFromWeb(URL url)
    {
        JSONObject toReturn = null;
        jsonInfo = new StringBuilder();
        in = null;
       //read the stream from the URL into a buffered reader
        try
        {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            
            while ((strLine = in.readLine()) != null )
            {
                jsonInfo.append(strLine);
            }
            
            in.close();
            
            toReturn = new JSONObject(jsonInfo.toString());
        }
        catch (IOException iox)
        {
            //System.out.println("\nInvalid stop number or connection unavailable. Please try again.");
        }
        catch (JSONException jex)
        {
            //do nothing, for now.
        }

        return toReturn;       

    }
    
    private void buildStopInfo()
    {
        try 
        {
            JSONObject stop;
            JSONObject geo;
            stop = scheduleInfo.getJSONObject("stop-schedule").getJSONObject("stop");
            geo = stop.getJSONObject("centre").getJSONObject("geographic");

            String name = stop.getString("name");
            String latitude = geo.getString("latitude");
            String longitude = geo.getString("longitude");

            s = new StopInfo(name, latitude, longitude);
        }
        catch (org.json.JSONException jex)
        {
            jex.printStackTrace();
        }
        catch (NullPointerException nex)
        {
            //pointer is null because the information could not be pulled from the website.
            //appropriate message already displayed.
        }
       
    }
    
    private void buildScheduleInfo()
    {
        String name = null;
        Object unknownType;
        JSONArray routeScheduleArray;
        JSONObject routeScheduleObject;
        JSONObject routeInfo = null;
        JSONObject allSchedules;
        JSONObject bus;
        JSONObject singleRoute;
        ArrayList<BusArrival> arrivals;
        ArrayList<ScheduleItem> scheduleItems;
        String arrival;
        String busName;
        String routeName;
        Date arrivalTime;
        
        JSONArray schedules;
        try
        {
            allSchedules = scheduleInfo.getJSONObject("stop-schedule").getJSONObject("route-schedules");            
            
            unknownType = allSchedules.get("route-schedule");
            
            if (unknownType instanceof JSONObject)
            {
                routeScheduleObject = (JSONObject)unknownType;
                routeInfo = routeScheduleObject.getJSONObject("route");
                name = routeInfo.getString("name");
                
                singleRoute = routeScheduleObject.getJSONObject("scheduled-stops");
                routeScheduleArray = singleRoute.getJSONArray("scheduled-stop");

                scheduleItems = new ArrayList<ScheduleItem>();
                    
                arrivals = new ArrayList<BusArrival>();
                
                for (int j = 0; j < routeScheduleArray.length(); j++)
                {
                    bus = routeScheduleArray.getJSONObject(j);
                    busName = bus.getJSONObject("variant").getString("name"); 
                    arrival = bus.getJSONObject("times").getJSONObject("arrival").getString("estimated");
                    arrivalTime = javax.xml.bind.DatatypeConverter.parseDateTime(arrival).getTime();
                    arrivals.add(new BusArrival(busName, arrivalTime));
                }
                    
                scheduleItems.add(new ScheduleItem(name, arrivals));
               
                sc = new Schedule(scheduleItems);   
                  
            }
            else if (unknownType instanceof JSONArray)
            {
                //in order to fix incompatibility with some larger stops, another cast and check type operation will need to be implimented
                //to handle the larger stops, as they return an Array of objects that each contain a route schedule array.
                
                routeScheduleArray = (JSONArray)unknownType;
                
                //only gets the first route.
                routeInfo = routeScheduleArray.getJSONObject(1).getJSONObject("route");
                name = routeInfo.getString("name");

                scheduleItems = new ArrayList<ScheduleItem>();
                
                for (int i = 0; i < routeScheduleArray.length(); i++)
                {
                    schedules = routeScheduleArray.getJSONObject(i).getJSONObject("scheduled-stops").getJSONArray("scheduled-stop");
                    routeName = routeScheduleArray.getJSONObject(i).getJSONObject("route").getString("name");
                    
                    arrivals = new ArrayList<BusArrival>();
                
                    for (int j = 0; j < schedules.length(); j++)
                    {
                        bus = schedules.getJSONObject(j);
                        busName = bus.getJSONObject("variant").getString("name"); //gets set three times. thats ok. 
                        arrival = bus.getJSONObject("times").getJSONObject("arrival").getString("estimated");
                       arrivalTime = javax.xml.bind.DatatypeConverter.parseDateTime(arrival).getTime();
                       
                        arrivals.add(new BusArrival(busName, arrivalTime));
                    }
                    
                    
                    arrivals.trimToSize();
                    scheduleItems.add(new ScheduleItem(routeName, arrivals));

                }
                scheduleItems.trimToSize();
                sc = new Schedule(scheduleItems);                
            }

        }
        catch (org.json.JSONException jex)
        {
            jex.printStackTrace();
        }
        catch (NullPointerException nex)
        {
            //appropriate message already displayed to user.
        }
        
    }
    
    private void buildStopFeatures()
    {
        
        JSONArray features;
        JSONObject currentObj;
        String name;
        int count;
        stopFeats = new ArrayList<StopFeature>();
        try
        {
            features = stopFeatures.getJSONObject("stop-features").getJSONArray("stop-feature");
            
            for (int i = 0; i < features.length(); i++)
            {
                currentObj = features.getJSONObject(i);
                name = currentObj.getString("name");
                count = currentObj.getInt("count");
                stopFeats.add(new StopFeature(name, count));
            }

            stopFeats.trimToSize();

        }
        catch (org.json.JSONException jex)
        {
          //do nothing
        }
        catch (NullPointerException nex)
        {
            //appropriate message already displayed to user.
        }      
    }
    
    public StopInfo getStopInfo()
    {
        return s;
    }
    
    public Schedule getScheduleInfo()
    {
        return sc;
    }
    
    public ArrayList<StopFeature> getStopFeatures()
    {
        return stopFeats;
    }
    
    
}
