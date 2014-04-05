package winnipegtransit;
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
    private static APIKey keyMaker = new APIKey();
    private static final String API_KEY = keyMaker.getAPIKey();
    private static final String WT_URL = "http://api.winnipegtransit.com/";
    private static StopInfo stopInfo;
    private static Schedule sc;
    private static ArrayList<StopFeature> stopFeats;

    private static JSONObject retrieveFromWeb(URL url)
    {
        BufferedReader in;
        StringBuilder jsonInfo;
        JSONObject toReturn = null;
        String strLine;
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
    
    private static void buildScheduleInfo(String stopNo)
    {
        String name = null;
        Object unknownType;
        Object anotherUnknownType;
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
        JSONObject stop;
        JSONObject geo;        
        JSONArray schedules;
        URL stopScheduleInfoURL;
        JSONObject scheduleInfo;
        
        try
        {
            stopScheduleInfoURL = new URL(WT_URL + "stops/" + stopNo + "/schedule.json?max-results-per-route=3&" + API_KEY);
            scheduleInfo = retrieveFromWeb(stopScheduleInfoURL);
            
            stop = scheduleInfo.getJSONObject("stop-schedule").getJSONObject("stop");
            geo = stop.getJSONObject("centre").getJSONObject("geographic");

            String stopName = stop.getString("name");
            String latitude = geo.getString("latitude");
            String longitude = geo.getString("longitude");

            stopInfo = new StopInfo(stopName, latitude, longitude);
            
            
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
               
                sc = new Schedule(scheduleItems, stopInfo, stopFeats);   
                  
            }
            else if (unknownType instanceof JSONArray)
            {
                routeScheduleArray = (JSONArray)unknownType;
                
                //only gets the first route.
                routeInfo = routeScheduleArray.getJSONObject(1).getJSONObject("route");
                   
                    scheduleItems = new ArrayList<ScheduleItem>();

                    for (int i = 0; i < routeScheduleArray.length(); i++)
                    {
                        
                        anotherUnknownType = routeScheduleArray.getJSONObject(i).getJSONObject("scheduled-stops").get("scheduled-stop");
                        
                        if (anotherUnknownType instanceof JSONObject)
                        {
                            routeScheduleObject = (JSONObject)anotherUnknownType;
                            routeName = routeScheduleArray.getJSONObject(i).getJSONObject("route").getString("name");
                            
                            arrivals = new ArrayList<BusArrival>();

                            busName = routeScheduleObject.getJSONObject("variant").getString("name"); 
                            arrival = routeScheduleObject.getJSONObject("times").getJSONObject("arrival").getString("estimated");
                            arrivalTime = javax.xml.bind.DatatypeConverter.parseDateTime(arrival).getTime();
                            arrivals.add(new BusArrival(busName, arrivalTime));
                            
                            arrivals.trimToSize();
                            
                            scheduleItems.add(new ScheduleItem(routeName, arrivals));

                        }
                        else
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

                    }
                    scheduleItems.trimToSize();
                    sc = new Schedule(scheduleItems, stopInfo, stopFeats);                
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
        catch (MalformedURLException malex)
        {
            //do nothing
        }

    }
    
    private static void buildStopFeatures(String stopNo)
    {
        URL stopFeaturesURL;
        JSONObject stopFeatures;
        JSONArray features;
        JSONObject currentObj;
        String name;
        int count;
        stopFeats = new ArrayList<StopFeature>();
        try
        {
            stopFeaturesURL = new URL(WT_URL + "stops/" + stopNo + "/features.json?" + API_KEY);
            
            stopFeatures = retrieveFromWeb(stopFeaturesURL);
            
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
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        } 
    }

    private static Date parseToDate(String dateString)
    {
        return javax.xml.bind.DatatypeConverter.parseDateTime(dateString).getTime();
        
    }
    
    private static Date checkTime()
    {
        URL timeURL;
        JSONObject timeJson;
        Date queryDateTime = null;
        
        String dateString;
        
        try
        {
            timeURL = new URL(WT_URL + "/time.json?" + API_KEY);
            timeJson = retrieveFromWeb(timeURL);
            
            dateString = timeJson.getString("time");
            
            queryDateTime = parseToDate(dateString);
            
            
        }
        catch (MalformedURLException malx)
        {
            malx.printStackTrace();
        }
        catch (IOException iox)
        {
            iox.printStackTrace();
        }
        catch (NullPointerException nex)
        {
            nex.printStackTrace();
        } 
        
        return queryDateTime;
    }
    
    public static Schedule getScheduleInfo(String stopNo)
    {
        buildStopFeatures(stopNo);
        buildScheduleInfo(stopNo);
        return sc;
    }
    
    public static Date getTime()
    {
        Date queryDateTime;
        queryDateTime = checkTime();
        return queryDateTime;
    }

}
