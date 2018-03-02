/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.membrane;

import engine.Vector;
import java.awt.Point;
import tasks.waveequation.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.PI;
import java.util.ArrayList;
/**
 *
 * @author Andrey
 */
public class ReferenceWave2dSolution {
    public double[][] getReferenceSolution(double[] T, ArrayList<Vector> points, double a ){
        int TN  =  T.length;
        int XN = points.size();
        double[][] F = new double[TN][XN];
        a = 1;
        double w = a* PI * Math.sqrt(2);
        for(int ti = 0; ti < TN; ti++){
            double t = T[ti];
            for(int i = 0; i < XN; i++){
                Vector point = points.get(i);
                double x = point.getCoordinates()[0];
                double y = point.getCoordinates()[1];
                F[ti][i] = cos(w* t) * sin(x * PI) * sin(y * PI);
            }
        }
        
        return F;
    }
}
