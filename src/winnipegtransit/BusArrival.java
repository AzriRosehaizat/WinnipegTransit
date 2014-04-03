/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package winnipegtransit;
import java.util.Date;

/**
 *
 * @author owen
 */
public class BusArrival {
    private String busName;
    private Date arrivalTime;
    
    public BusArrival(String busName, Date arrivalTime)
    {
        this.busName = busName;
        this.arrivalTime = arrivalTime;
    }
    
    public String getBusName()
    {
        return busName;
    }
    
    public Date getArrivalTime()
    {
        return arrivalTime;
    }
    
    public String toString()
    {
        return busName + " - " + arrivalTime + "\n";
    }
}
