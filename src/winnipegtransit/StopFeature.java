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
 * Class that contains information about a specific stop feature.
 */
public class StopFeature {
    
    //storage location for the name of the stop feature
    private String name;
    
    //storage location for the count of this feature at the stop
    private int count;
    
    //constructor for the StopFeature object
    public StopFeature(String name, int count)
    {
        //assigns the values passed in as parameters to the appropriate
        //variables
        this.name = name;
        this.count = count;
    }
    
    //allows access to the name value
    public String getName()
    {
        return name;
    }
    
    //allows access to the count value
    public int getCount()
    {
        return count;
    }
}
