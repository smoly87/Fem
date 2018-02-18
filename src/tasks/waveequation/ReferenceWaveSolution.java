/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.waveequation;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.PI;
/**
 *
 * @author Andrey
 */
public class ReferenceWaveSolution {
    public double[][] getReferenceSolution(double[] T, double[] X, double a ){
        int TN  =  T.length;
        int XN = X.length;
        
        double[][] F = new double[TN][XN];
        for(int ti = 0; ti < TN; ti++){
            double t = T[ti];
            for(int xi = 0; xi < XN; xi++){
                double x = X[xi];
                F[ti][xi] = cos(a* PI * t) * sin(x * PI);
            }
        }
        
        return F;
    }
}
