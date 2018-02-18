/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils.openmap;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Andrey
 */
public class Convertion {
    public static RealMatrix ColVector2Matrix(double[] vec){
         int N = vec.length;
         double[][] data = new double[N][1]; 
         for(int i = 0; i < N; i++){
             data[i][0] = vec[i];
         }
         Array2DRowRealMatrix M =  new Array2DRowRealMatrix(data);
        
         return M;
    }
    
   
  
}
