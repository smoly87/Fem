/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils.common;

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
}
