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
 * This class holds information about a specific route at the queried stop.
 */
public class ScheduleItem {
    
    //storage variable for the name of the route
    private String routeName;
    
    //storage location for a list of bus arrival objects
    private ArrayList<BusArrival> arrivals;
    
    //constructor for a ScheduleItem object
    public ScheduleItem(String routeName, ArrayList<BusArrival> arrivals)
    {
        //populate the class attributes with the values passed in as parameters
        this.routeName = routeName;
        this.arrivals = arrivals;
    }
    
    //allows access to the name of the route
    public String getRouteName()
    {
        return routeName;
    }
    
    //allows access to the list of bus arrivals
    public ArrayList<BusArrival> getBusArrivals()
    {
        return arrivals;
    }
    
    //unused to string method.
    public String toString()
    {
        return routeName + "\n" + arrivals.toString();
    }
    
    
}
