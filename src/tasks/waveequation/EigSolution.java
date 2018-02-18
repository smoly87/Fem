/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.waveequation;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Andrey
 */
public class EigSolution {
    //Matrix consists of eig Vectors, which are represented as column
    protected RealMatrix H;
    protected RealVector HC;
    protected double[] w;

    public RealMatrix getH() {
        return H;
    }

    public RealVector getHC() {
        return HC;
    }

    public double[] getW() {
        return w;
    }

    public EigSolution(RealMatrix H, RealVector HC, double[] w) {
        this.H = H;
        this.HC = HC;
        this.w = w;
    }
}
