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
 */
public class Schedule {

    private ArrayList<ScheduleItem> scheduleItems;
    private StopInfo stopInfo;
    private ArrayList<StopFeature> stopFeatures;
    
    public Schedule(ArrayList<ScheduleItem> scheduleItems, StopInfo stopInfo, ArrayList<StopFeature> stopFeatures)
    {
        this.scheduleItems = scheduleItems;
        this.stopInfo = stopInfo;
        this.stopFeatures = stopFeatures;
    }
    
    public ArrayList<ScheduleItem> getScheduleItems()
    {
        return scheduleItems;
    }
    
    public StopInfo getStopInfo()
    {
        return stopInfo;
    }
    
    public ArrayList<StopFeature> getStopFeatures()
    {
        return stopFeatures;
    }
    

    
    public String toString()
    {
        return scheduleItems.toString();
    }
    
}
