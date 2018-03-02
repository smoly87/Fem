/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.waveequation;

/**
 *
 * @author Andrey
 */
public class SolutionPrecisionTester {
    protected static final double testMinEps = 0.00000001;
    /**
     * 
     * @param X Solution for testing
     * @param X0 Reference solution
     * @param absValue If this parameter is switched to true, 
     * so method returns absolute precision 
     * and in other case it returns relative
     * @return 
     */
    public static double getEnergyCriteriaPrecision(double[][]X, double[][] X0, boolean absValue){
        int Tn = X0.length;
        int N = X0[0].length;
        
        double sumErr = 0;
        
        for(int ti = 0; ti < Tn; ti++){
            double err = 0;
            int NR = 0;
            for(int i = 0; i < N; i++){
                double X2 =X[ti][i]*X[ti][i];
                double X02 = X0[ti][i]*X0[ti][i];
                double curErr = Math.abs(X2 - X02);
                if(!absValue && Math.abs(X02) > testMinEps ){
                     curErr = curErr/(X02);
                     NR++;
                }
                err += curErr;
            }
            if(absValue) NR = N;
            sumErr += err/NR;
        }
        
        return sumErr/Tn;
    }
    public static double getLengthCriteriaPrecision(double[][]X, double[][] X0, boolean absValue){
        int Tn = X0.length;
        int N = X0[0].length;
        
        double sumErr = 0;
        
        for(int ti = 0; ti < Tn; ti++){
            double err = 0;
            for(int i = 0; i < N; i++){
                double X2 =X[ti][i];
                double X02 = X0[ti][i];
                double curErr = Math.abs(X2 - X02);
                if(!absValue && Math.abs(X02) > testMinEps){
                     curErr = curErr/(X02);
                }
                err += curErr;
            }
            sumErr += err/N;
        }
        
        return sumErr/Tn;
    }
}
