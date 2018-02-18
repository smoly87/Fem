/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Andrey
 */
public class BoundaryConditions {
    protected double[] boundValues;
    protected ArrayList<Integer> boundIndexes;
    protected int dim;

    
    public double[] getBoundValues() {
        return boundValues;
    }

    public ArrayList<Integer> getBoundIndexes() {
        return boundIndexes;
    }

    public BoundaryConditions(double[] boundValues, ArrayList<Integer> boundIndexes, int dim) {
        this.boundValues = boundValues;
        this.boundIndexes = boundIndexes;
    }
    
    public BoundaryConditions(double[] boundValues, ArrayList<Integer> boundIndexes) {
        this.boundValues = boundValues;
        this.boundIndexes = boundIndexes;
        this.dim = 1;
    }
    
    public BoundaryConditions(double[] boundValues, Integer[] boundIndexes) {
        this(boundValues,  new ArrayList<>(Arrays.asList(boundIndexes)));        
    }
    
    public int getPointIndex(int seqInd){
        return boundIndexes.get(seqInd);
    }
    
    public Vector getBoundaryValue(int seqInd){
        int startInd = dim * seqInd;
        double[] values = new double[dim];
        System.arraycopy(boundValues, startInd, values, 0, dim);
        return new Vector(boundValues);
    }
    
    public int getNodesCount(){
        return boundIndexes.size();
    }
    
    public double getBoundaryValue1d(int seqInd){
        return boundValues[seqInd];
    }
    
}
