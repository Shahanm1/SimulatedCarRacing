package champ2009client;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 4, 2008
 * Time: 12:19:22 PM
 */
public interface Controller {

    public Action control(SensorModel sensors);
    
    public void reset(); // called at the beginning of each new trial
    
    public void shutdown();

}
