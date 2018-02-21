/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import elemfunc.d1.LinN;
import elemfunc.d1.LinNBuilder;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.Mesh;
import engine.SysBlockBuilder;
import engine.Task;
import engine.utils.common.MathUtils;
import engine.utils.common.Pair;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author Andrey
 */
public class FemTimeSolver1d extends Task{

    protected Mesh timeMesh ;
    
    protected OpenMapRealMatrix KB;
    protected OpenMapRealMatrix CB;

    public Mesh getTimeMesh() {
        return timeMesh;
    }

    
    public FemTimeSolver1d() {
       
    }
    
    protected BoundaryConditions getTimeBoundaryConditions(int NSp, OpenMapRealVector Y0){
        
        Integer[] timeBoundIndexes = MathUtils.linSpace(NSp);
        return new BoundaryConditions(Y0.toArray(), timeBoundIndexes);
    }
    
    public Pair<OpenMapRealMatrix, OpenMapRealVector> buildTimeSystem(
                OpenMapRealMatrix C, OpenMapRealMatrix K,  OpenMapRealVector Y0,  
                int tStepsCnt, double tFrom, double tTo){
        timeMesh = SimpleMeshBuilder.create1dLineMesh(tStepsCnt); 
        timeMesh.applyElemFunc(new LinNBuilder());
        
        int NSp = C.getRowDimension();
        int NG = NSp * (tStepsCnt+1);
        
        OpenMapRealVector FG = new OpenMapRealVector(NG);
        
        this.KB = K;
        this.CB = C;
        
        OpenMapRealMatrix KG = buildSystem(timeMesh, this::getSysBlock, NSp);        
        boundaryConitions = getTimeBoundaryConditions(NSp, Y0);
        Pair<OpenMapRealMatrix, OpenMapRealVector> res = applyBoundaryConditions(KG, FG, boundaryConitions);

        return res;
    }
    
    protected OpenMapRealMatrix getSysBlock(Element elem, int l, int m){
        ElemFunc elemFunc = elem.getElemFunc();
        double CKoof =  -elemFunc.integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m);
        double KKoof =  elemFunc.integrate(ElemFuncType.F, ElemFuncType.F, l, m);
        
        OpenMapRealMatrix CLoc = (OpenMapRealMatrix)CB.scalarMultiply(CKoof);
        OpenMapRealMatrix KLoc = (OpenMapRealMatrix)KB.scalarMultiply(KKoof);
        
        return CLoc.add(KLoc);
    }
}
