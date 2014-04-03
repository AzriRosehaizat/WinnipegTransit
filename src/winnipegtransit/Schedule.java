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
    
    public Schedule(String name, ArrayList<ScheduleItem> scheduleItems)
    {
        this.scheduleItems = scheduleItems;
    }
    
    public ArrayList<ScheduleItem> getScheduleItems()
    {
        return scheduleItems;
    }
    

    
    public String toString()
    {
        return scheduleItems.toString();
    }
    
}
