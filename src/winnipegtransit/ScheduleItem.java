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
public class ScheduleItem {
    private String routeName;
    private ArrayList<BusArrival> arrivals;
    
    public ScheduleItem(String routeName, ArrayList<BusArrival> arrivals)
    {
        this.routeName = routeName;
        this.arrivals = arrivals;
    }
    
    public String getRouteName()
    {
        return routeName;
    }
    
    public ArrayList<BusArrival> getBusArrivals()
    {
        return arrivals;
    }
    
    public String toString()
    {
        return routeName + "\n" + arrivals.toString();
    }
    
    
}
