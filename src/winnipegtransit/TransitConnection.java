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
            //cast the generic object into a JSONArray object
            routeScheduleArray = (JSONArray)unknownType;

            //only gets the first route.
            routeInfo = routeScheduleArray.getJSONObject(1).getJSONObject("route");

            //create a new ScheduleItem array list
            scheduleItems = new ArrayList<ScheduleItem>();

            //for every item in the route schedule array
            for (int i = 0; i < routeScheduleArray.length(); i++)
            {

                //we need to again test to see if the item retrieved is an Array or an Object
                anotherUnknownType = routeScheduleArray.getJSONObject(i).getJSONObject("scheduled-stops").get("scheduled-stop");

                //if its an object
                if (anotherUnknownType instanceof JSONObject)
                {
                    //cast the generic object into a JSONObject
                    routeScheduleObject = (JSONObject)anotherUnknownType;
                    
                    //extract the current route's name
                    routeName = routeScheduleArray.getJSONObject(i).getJSONObject("route").getString("name");

                    //create a new BusArrival array list
                    arrivals = new ArrayList<BusArrival>();

                    //extract the bus name and arrival time from the routeScheduleObject
                    busName = routeScheduleObject.getJSONObject("variant").getString("name"); 
                    arrival = routeScheduleObject.getJSONObject("times").getJSONObject("arrival").getString("estimated");
                    arrivalTime = javax.xml.bind.DatatypeConverter.parseDateTime(arrival).getTime();
                    
                    //add a new BusArrival to the arrivals array list
                    arrivals.add(new BusArrival(busName, arrivalTime));

                    //trim it down to its actual size
                    arrivals.trimToSize();

                    //add a new scheduleItem using the route name and arrivals array list
                    scheduleItems.add(new ScheduleItem(routeName, arrivals));

                }
                //however, if it is an Array
                else
                {
                    //get the scheduled stops from the scheduled-stop array
                    schedules = routeScheduleArray.getJSONObject(i).getJSONObject("scheduled-stops").getJSONArray("scheduled-stop");
                    
                    //extract the routes name 
                    routeName = routeScheduleArray.getJSONObject(i).getJSONObject("route").getString("name");
                    
                    //create a new arrayList of BusArrival objects
                    arrivals = new ArrayList<BusArrival>();

                    //for every item in the schedules array
                    for (int j = 0; j < schedules.length(); j++)
                    {
                       //get the current bus object
                       bus = schedules.getJSONObject(j);
                       
                       //and the bus name
                       busName = bus.getJSONObject("variant").getString("name"); //gets set three times. thats ok. 

                       //get the busses arrival time
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


                       //parse the arrival time into a date object
                       arrivalTime = javax.xml.bind.DatatypeConverter.parseDateTime(arrival).getTime();

                       //add a new BusArrival object to the arrivals Array list
                       arrivals.add(new BusArrival(busName, arrivalTime));
                    }

                    //when all schedule items are processed 
                    arrivals.trimToSize();
                    
                    //add a new schedule item using the route name and arrivals array list
                    scheduleItems.add(new ScheduleItem(routeName, arrivals));
                }

            }
            
            //trim the schedule items array list
            scheduleItems.trimToSize();
            
            //build a new Schedule object using the scheduleItems Array list, stopInformation object,
            //and stopFeatures Array List
            sc = new Schedule(scheduleItems, stopInfo, stopFeats);                
        }
    }
    
    //gathers the required stop features information for a specific stop and populates
    //the global StopFeatures array list
    private static void buildStopFeatures(String stopNo) throws IOException,
            org.json.JSONException, NullPointerException, MalformedURLException
    {
        //storage variables used during processing
        URL stopFeaturesURL;
        JSONObject stopFeatures;
        JSONArray featuresArray;
        JSONObject featuresObject;
        JSONObject currentObj;
        Object stopFeaturesObj;
        String name;
        int count;
        
        //create the stopFeatures array list        
        //should return this instead of populating a global object in a future revision
        stopFeats = new ArrayList<StopFeature>();

        //create a new URL object that gets the required info for the stop number provided
        //as a parameter
        stopFeaturesURL = new URL(WT_URL + "stops/" + stopNo + "/features.json?" + API_KEY);

        //get the JSON information from the URL and store it in a JSONObject
        stopFeatures = retrieveFromWeb(stopFeaturesURL);
        
        try
        {
            //get the stop features as a generic object
            //there is potential that there may not be a stop-feature object. Patched with a try-catch.
            stopFeaturesObj = stopFeatures.getJSONObject("stop-features").get("stop-feature");

        
            //if the stop features are stored in an object
            if (stopFeaturesObj instanceof JSONObject)
            {
                featuresObject = stopFeatures.getJSONObject("stop-features").getJSONObject("stop-feature");
                name = featuresObject.getString("name");
                count = featuresObject.getInt("count");
                stopFeats.add(new StopFeature(name, count));
            }
            //if the features are stored in an array
            else
            {
                //get the stopfeature array as a JSONArray
                featuresArray = stopFeatures.getJSONObject("stop-features").getJSONArray("stop-feature");

                //for every stop feature 
                for (int i = 0; i < featuresArray.length(); i++)
                {
                    //get the current object within the array
                    currentObj = featuresArray.getJSONObject(i);

                    //extract its name and count values
                    name = currentObj.getString("name");
                    count = currentObj.getInt("count");

                    //add a new StopFeature object to the ArrayList
                    stopFeats.add(new StopFeature(name, count));
                }
            }
        }
        catch (JSONException jex)
        {
            //do nothing. ArrayList should be evaluated as empty on the GUI class.
        }

        //trim the stopFeatures array list
        stopFeats.trimToSize();
    }

    //parses a string passed into it into a date object
    private static Date parseToDate(String dateString)
    {
        return javax.xml.bind.DatatypeConverter.parseDateTime(dateString).getTime();
        
    }
    
    //checks the Winnipeg Transit system time
    private static Date checkTime() throws IOException,
            MalformedURLException, NullPointerException
    {
        //variables for processing
        URL timeURL;
        JSONObject timeJson;
        Date queryDateTime = null;        
        String dateString;

        //create a new URL Object to get the time information
        timeURL = new URL(WT_URL + "/time.json?" + API_KEY);
        
        //retrieve the time information from the web
        timeJson = retrieveFromWeb(timeURL);

        //place the time information into a local string
        dateString = timeJson.getString("time");

        //convert the string into a date object
        queryDateTime = parseToDate(dateString);

        //return the date object back
        return queryDateTime;
    }
    
    //runs the process of buiding a Schedule item for a specific stop number
    public static Schedule getScheduleInfo(String stopNo) throws IOException
    {
        //build the stop features object for the stop number
        buildStopFeatures(stopNo);
        
        //build the Schedule object for the stop number
        buildScheduleInfo(stopNo);
        
        //return the global Schedule object
        return sc;
    }
    
    //gets the time by calling the checkTime method and returns the resulting date object
    public static Date getTime() throws IOException
    {
        //storage for the Date object
        Date queryDateTime;
        
        //get the Date object from the checkTime method
        //store it in the date variable
        queryDateTime = checkTime();
        
        //return the retrieved Date object
        return queryDateTime;
    }

}
