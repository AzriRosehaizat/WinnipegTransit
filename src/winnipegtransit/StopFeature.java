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
public class StopFeature {
    private String name;
    private int count;
    
    public StopFeature(String name, int count)
    {
        this.name = name;
        this.count = count;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getCount()
    {
        return count;
    }
}
