/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils.common;

import java.util.Arrays;

/**
 *
 * @author Andrey
 */
public class MathUtils {
    public static double[] linSpace(double from, double to, int N ){
        double[] res = new double[N];
        double h = (to - from)/(N-1);
        
        for(int i = 0; i < N; i++){
            res[i] = i*h;
        } 
        
        return res;
    }
    
    public static Integer[] linSpace(int N){
        Integer[] res = new Integer[N];
        for(int i = 0; i < N;i++){
            res[i] = i;
        }
        return res;
    }
    
    public static int countZeros(double[]X){
        double eps = 0.0001;
        int c =0;
        for(int i = 0; i < X.length; i++){
            if(Math.abs(X[i]) < eps) c++;
        }
        return c;
        
    }
    
    public static double arrMax(double[] X){
        double max = X[0];
        for(int i = 1; i < X.length; i++){
            if(X[i] >max) max = X[i];
        }
        return max;
    }
    
    public static double arrMax(double[][] X){
        double max = arrMax(X[0]);
        for(int i = 1; i < X.length; i++){
            double val = arrMax(X[i]) ;
            if( val > max) max = val;
        }
        return max;
    }
}
