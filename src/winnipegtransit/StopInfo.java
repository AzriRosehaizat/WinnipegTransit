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
public class StopInfo {
    private String name;
    private String latitude;
    private String longitude;
    
    public StopInfo(String name, String latitude, String longitude)
    {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getLatitude()
    {
        return latitude;
    }
    
    public String getLongitude()
    {
        return longitude;
    }
}
