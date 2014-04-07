/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package winnipegtransit;
import java.util.ArrayList;

/**
 *
 * @author owen
 * 
 * Class used to bind stop info, stop features and bus arrivals together as a 
 * schedule
 */
public class Schedule {

    //storage variable for a list of schedule items
    private ArrayList<ScheduleItem> scheduleItems;
    
    //storage variable for a collection of stop information
    private StopInfo stopInfo;
    
    //storage variable for a list of stop features
    private ArrayList<StopFeature> stopFeatures;
    
    //constructor for the Schedule object.
    public Schedule(ArrayList<ScheduleItem> scheduleItems, StopInfo stopInfo, ArrayList<StopFeature> stopFeatures)
    {
        //populate the class variables with the values passed in as parameters.
        this.scheduleItems = scheduleItems;
        this.stopInfo = stopInfo;
        this.stopFeatures = stopFeatures;
    }
    
    //getter allowing access to the schedule items list
    public ArrayList<ScheduleItem> getScheduleItems()
    {
        return scheduleItems;
    }
    
    //getter allowing access to the stop information
    public StopInfo getStopInfo()
    {
        return stopInfo;
    }
    
    //getter allowing access to the stop features list
    public ArrayList<StopFeature> getStopFeatures()
    {
        return stopFeatures;
    }
    
    //unused to string method.
    public String toString()
    {
        return scheduleItems.toString();
    }
    
}
