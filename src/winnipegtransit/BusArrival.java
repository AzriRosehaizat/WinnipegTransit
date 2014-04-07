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
 * 
 * An object used to store bus arrival times, which are then added to a
 * schedule item object.
 */
public class BusArrival {
    //string variable for the busses name
    private String busName;
    
    //date variable for the busses arrival time.
    private Date arrivalTime;
    
    public BusArrival(String busName, Date arrivalTime)
    {
        //populate the attributes with values passed in as parameters
        this.busName = busName;
        this.arrivalTime = arrivalTime;
    }
    
    //getter allowing access to the busName
    public String getBusName()
    {
        return busName;
    }
    
    //getter allowing access to the arrival time
    public Date getArrivalTime()
    {
        return arrivalTime;
    }
    
    //to string to print out the values of the object attributes. Not used in
    //runtime for now.
    public String toString()
    {
        return busName + " - " + arrivalTime + "\n";
    }
}
