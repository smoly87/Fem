/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import elemfunc.d1.LinNBuilder;
import engine.utils.common.MathUtils;
import engine.utils.common.Pair;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;

/**
 *
 * @author Andrey
 */
public abstract class FemTimeSolver extends Task{
   

    protected Mesh timeMesh ;
    
    protected OpenMapRealMatrix KB;
    protected OpenMapRealMatrix CB;
    protected ElemFuncBuilder funcBuilder;

    public Mesh getTimeMesh() {
        return timeMesh;
    }

    
    protected BoundaryConditions getTimeBoundaryConditions(int NSp, OpenMapRealVector Y0){
        
        Integer[] timeBoundIndexes = MathUtils.linSpace(NSp);
        return new BoundaryConditions(Y0.toArray(), timeBoundIndexes);
    }
    
    
    
    
    public  Pair<OpenMapRealMatrix, OpenMapRealVector> buildTimeSystem(
                OpenMapRealMatrix C, OpenMapRealMatrix K,  OpenMapRealVector Y0,  
                int tStepsCnt, double tFrom, double tTo){
        timeMesh = createTimeMesh(tStepsCnt); 
        timeMesh.applyElemFunc(funcBuilder);
        
        int NSp = C.getRowDimension();
        int NG = NSp * (timeMesh.getNodesCount());
        
        OpenMapRealVector FG = new OpenMapRealVector(NG);
        this.KB = K;
        this.CB = C;
        
        OpenMapRealMatrix KG = buildSystem(timeMesh, this::getSysBlock, NSp);        
        boundaryConitions = getTimeBoundaryConditions(NSp, Y0);
        Pair<OpenMapRealMatrix, OpenMapRealVector> res = applyBoundaryConditions(KG, FG, boundaryConitions);
        return res;
    }
       
    protected abstract OpenMapRealMatrix getSysBlock(Element elem, int l, int m);
    protected abstract Mesh createTimeMesh(int tStepsCnt);    
    
}
