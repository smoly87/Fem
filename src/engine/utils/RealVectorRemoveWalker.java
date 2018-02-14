/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils;

import java.util.ArrayList;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealVectorPreservingVisitor;

/**
 *
 * @author Andrey
 */
public class RealVectorRemoveWalker implements RealVectorPreservingVisitor{

    protected OpenMapRealVector K;
    protected OpenMapRealVector R;
    
    protected ArrayList<Integer> boundIndexes;
  
    protected TargetStatus rowTargetStatus;
    
    public RealVectorRemoveWalker(OpenMapRealVector K, ArrayList<Integer> boundIndexes) {
        this.K = K;
        this.boundIndexes = boundIndexes;
        int N = K.getDimension();
        R = new OpenMapRealVector(N - boundIndexes.size());
        rowTargetStatus = new TargetStatus(boundIndexes);
    }

    public OpenMapRealVector getR() {
        return R;
    }
    
    @Override
    public void start(int dimension, int start, int end) {
        
    }

    @Override
    public void visit(int index, double value) {
        if(rowTargetStatus.compare(index) == TargetCompareResult.OrdinaryItem){
            R.setEntry(index - rowTargetStatus.getOffset(), value);
        }
    }

    @Override
    public double end() {
        return 0;
    }
    
}
