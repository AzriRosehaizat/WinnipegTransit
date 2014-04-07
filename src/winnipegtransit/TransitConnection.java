package winnipegtransit;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
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
    
    //creates an APIKey object so that the API key can be accessed
    private static APIKey keyMaker = new APIKey();
    
    //gets the API key from the API key object
    private static final String API_KEY = keyMaker.getAPIKey();
    
    //string containing the winnipeg tranist URL
    private static final String WT_URL = "http://api.winnipegtransit.com/";
    
    //storage variables for the Schedule and list of features
    private static Schedule sc;
    private static ArrayList<StopFeature> stopFeats;

    //method used to retrieve JOSON information from a URL
    private static JSONObject retrieveFromWeb(URL url) throws IOException, JSONException
    {
        //storage variables used during processing
        BufferedReader in;
        StringBuilder jsonInfo;
        JSONObject toReturn = null;
        String strLine;
        jsonInfo = new StringBuilder();
        in = null;
        
       //read the stream from the URL into a buffered reader
        in = new BufferedReader(new InputStreamReader(url.openStream()));
        
        //while there is information to be read from the input stream
        while ((strLine = in.readLine()) != null )
        {
            //append each line to a string object
            jsonInfo.append(strLine);
        }
        
        //when there is no information left, close the stream
        in.close();
        
        //return the string as a new JSON object.
        toReturn = new JSONObject(jsonInfo.toString());

        //and return it.
        return toReturn;       

    }
    
    //This method extracts the schedule information for a specific stop number.
    private static void buildScheduleInfo(String stopNo) throws IOException,
            org.json.JSONException, NullPointerException, MalformedURLException
    {
        //storage variables used for storing information during processing
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
        StopInfo stopInfo;
        
        //build the URL object for the information that we need to retrieve, patching in the stop number passed in
        //as a parameter
        stopScheduleInfoURL = new URL(WT_URL + "stops/" + stopNo + "/schedule.json?max-results-per-route=3&" + API_KEY);
        
        //retreve the schedule JSON string from the web using the retrieveFromWeb method
        scheduleInfo = retrieveFromWeb(stopScheduleInfoURL);

        //get a JSON object containting the stop information
        stop = scheduleInfo.getJSONObject("stop-schedule").getJSONObject("stop");
        
        //create a JSON Object containing the geographic information
        geo = stop.getJSONObject("centre").getJSONObject("geographic");

        //retrieve specific stop information from the stop and geo JSON objects.
        String stopName = stop.getString("name");
        String latitude = geo.getString("latitude");
        String longitude = geo.getString("longitude");

        //create a stopInfo object from the name, lattitude and longitude
        //will be built into a Schedule object further into the class
        stopInfo = new StopInfo(stopName, latitude, longitude);
        
        //get a JSON Object containging the route schedule information for the stop
        allSchedules = scheduleInfo.getJSONObject("stop-schedule").getJSONObject("route-schedules");            

        //in order to determine if the route schedule information is stored in an Object or an Array
        //get the route schedule object and place it into a generic Object.
        //then test to see if it is an instance of a JSONArray or JSONObject and take appropriate 
        //action
        unknownType = allSchedules.get("route-schedule");

        //if it is a JSONObject, then no array is present and cannot be iterated.
        if (unknownType instanceof JSONObject)
        {
            //cast the unknownType into a JSONObject
            routeScheduleObject = (JSONObject)unknownType;
            
            //get the route info into another object
            routeInfo = routeScheduleObject.getJSONObject("route");
            
            //extract the name fro the routeInfo object
            name = routeInfo.getString("name");

            //get the single route stop information from the routeSchedue object.
            singleRoute = routeScheduleObject.getJSONObject("scheduled-stops");
            
            //get the array containing the scheduled stop information
            routeScheduleArray = singleRoute.getJSONArray("scheduled-stop");

            //create a new scheduleItem array list
            scheduleItems = new ArrayList<ScheduleItem>();

            //create a new BusArrival array list
            arrivals = new ArrayList<BusArrival>();

            //for every item in the routeSchedule array
            for (int j = 0; j < routeScheduleArray.length(); j++)
            {
                //extract the bus object from the array at the current position
                bus = routeScheduleArray.getJSONObject(j);
                
                //and then store the bus name in a variable
                busName = bus.getJSONObject("variant").getString("name");
                
                //then get the time information for the current bus.            
                try
                {
                    //there are cases where a bus only has a departure time, and not an arrival time. I let the JSONException handle
                    //these cases.
                    arrival = bus.getJSONObject("times").getJSONObject("arrival").getString("estimated");
                }
                catch (JSONException jex)
                {
                    arrival = bus.getJSONObject("times").getJSONObject("departure").getString("estimated");
                }                
                
                //convert the arrival time string into a date object
                arrivalTime = javax.xml.bind.DatatypeConverter.parseDateTime(arrival).getTime();
                
                //add a BusArrival object to the arrivals Array List
                arrivals.add(new BusArrival(busName, arrivalTime));
            }

            //when all arrivals are processed, add a new schedule item to the scheduleItems array list
            //there will only be one item. I might change this around later.
            scheduleItems.add(new ScheduleItem(name, arrivals));

            //create the new schedule item
            sc = new Schedule(scheduleItems, stopInfo, stopFeats);   

        }
        
        //if its a JSONArray
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

                           try
                           {
                               //there are cases where a bus only has a departure time, and not an arrival time. I let the JSONException handle
                               //these cases.
                               arrival = bus.getJSONObject("times").getJSONObject("arrival").getString("estimated");
                           }
                           catch (JSONException jex)
                           {
                               arrival = bus.getJSONObject("times").getJSONObject("departure").getString("estimated");
                           }


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
    
    private static void buildStopFeatures(String stopNo) throws IOException,
            org.json.JSONException, NullPointerException, MalformedURLException
    {
        URL stopFeaturesURL;
        JSONObject stopFeatures;
        JSONArray featuresArray;
        JSONObject featuresObject;
        JSONObject currentObj;
        Object stopFeaturesObj;
        String name;
        int count;
        stopFeats = new ArrayList<StopFeature>();

        stopFeaturesURL = new URL(WT_URL + "stops/" + stopNo + "/features.json?" + API_KEY);

        stopFeatures = retrieveFromWeb(stopFeaturesURL);
        
        stopFeaturesObj = stopFeatures.getJSONObject("stop-features").get("stop-feature");
        
        if (stopFeaturesObj instanceof JSONObject)
        {
            featuresObject = stopFeatures.getJSONObject("stop-features").getJSONObject("stop-feature");
            name = featuresObject.getString("name");
            count = featuresObject.getInt("count");
            stopFeats.add(new StopFeature(name, count));
        }
        else
        {
        
            featuresArray = stopFeatures.getJSONObject("stop-features").getJSONArray("stop-feature");

            for (int i = 0; i < featuresArray.length(); i++)
            {
                currentObj = featuresArray.getJSONObject(i);
                name = currentObj.getString("name");
                count = currentObj.getInt("count");
                stopFeats.add(new StopFeature(name, count));
            }
        }

        stopFeats.trimToSize();
    }

    private static Date parseToDate(String dateString)
    {
        return javax.xml.bind.DatatypeConverter.parseDateTime(dateString).getTime();
        
    }
    
    private static Date checkTime() throws IOException,
            MalformedURLException, NullPointerException
    {
        URL timeURL;
        JSONObject timeJson;
        Date queryDateTime = null;
        
        String dateString;

        timeURL = new URL(WT_URL + "/time.json?" + API_KEY);
        timeJson = retrieveFromWeb(timeURL);

        dateString = timeJson.getString("time");

        queryDateTime = parseToDate(dateString);

        return queryDateTime;
    }
    
    public static Schedule getScheduleInfo(String stopNo) throws IOException
    {
        buildStopFeatures(stopNo);
        buildScheduleInfo(stopNo);
        return sc;
    }
    
    public static Date getTime() throws IOException
    {
        Date queryDateTime;
        queryDateTime = checkTime();
        return queryDateTime;
    }

}
