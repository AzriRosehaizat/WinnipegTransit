/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package winnipegtransit;

/**
 *
 * @author owen
 * 
 * Class that stores information about the stop
 */
public class StopInfo {
    
    //storage location for the stop name
    private String name;
    
    //storage location for the stop's lattitude
    private String latitude;
    
    //storage location for the stop's longitude
    private String longitude;
    
    //constructor for the stop 
    public StopInfo(String name, String latitude, String longitude)
    {
        //populate the class attributes with values passed in as parameters 
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        
    }
    
    //allows access to the stop name
    public String getName()
    {
        return name;
    }
    
    //allows access to the stop lattitude
    public String getLatitude()
    {
        return latitude;
    }
    
    //allows access to the stops longitude
    public String getLongitude()
    {
        return longitude;
    }
}
