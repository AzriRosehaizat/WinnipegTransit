/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package winnipegtransit;

/**
 *
 * @author owen
 */
public class BusArrival {
    private String busName;
    private String arrivalTime;
    
    public BusArrival(String busName, String arrivalTime)
    {
        this.busName = busName;
        this.arrivalTime = arrivalTime;
    }
    
    public String getBusName()
    {
        return busName;
    }
    
    public String getArrivalTime()
    {
        return arrivalTime;
    }
    
    public String toString()
    {
        return busName + " - " + arrivalTime + "\n";
    }
}
