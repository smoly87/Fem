/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fem;

import engine.Task;
import org.apache.commons.math3.linear.RealVector;
import tasks.heatEquation1d;

/**
 *
 * @author Andrey
 */
public class Fem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        heatEquation1d task =new heatEquation1d(10);
        RealVector X = task.solve();
        System.out.println(X.toString());
    }
    
}
