/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import elemfunc.d1.LinN;
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
    
    protected ElemFunc elemFunc;
    protected Mesh timeMesh ;


    public Mesh getTimeMesh() {
        return timeMesh;
    }

    
    public FemTimeSolver1d() {
        elemFunc = new LinN();
    }
    
    protected BoundaryConditions getTimeBoundaryConditions(int NSp, OpenMapRealVector Y0){
        Integer[] timeBoundIndexes = MathUtils.linSpace(NSp);
        return new BoundaryConditions(Y0.toArray(), timeBoundIndexes);
    }
    
    public Pair<OpenMapRealMatrix, OpenMapRealVector> buildTimeSystem(
                OpenMapRealMatrix C, OpenMapRealMatrix K,  OpenMapRealVector Y0,  
                int tStepsCnt, double tFrom, double tTo){
        timeMesh = SimpleMeshBuilder.create1dLineMesh(tStepsCnt);
        ArrayList<Element> elems = timeMesh.getElements();
        
        int NSp = C.getRowDimension();
        int NG = NSp * (tStepsCnt+1);
        
        OpenMapRealMatrix KG = new OpenMapRealMatrix(NG, NG);
        OpenMapRealVector FG = new OpenMapRealVector(NG);
        
        double ht = ( tTo - tFrom)/(tStepsCnt-1);
        
        for(int i = 0; i < elems.size(); i++){
            double t = ht * i;
            Element elem = elems.get(i);
            ArrayList<Integer> nodesList = elem.getNodesList();
            int N = nodesList.size();
            for(int l = 0; l < N; l++){
                for(int m = 0; m < N; m++){
                    int gl = nodesList.get(l);
                    int gm = nodesList.get(m);
                    OpenMapRealMatrix BCell = getSysBlock(C, K, elem, l, m, t);
                    KG = this.arrangeSubMatrix(KG, BCell, gl, gm);
                }
            }
        }
        
        boundaryConitions = getTimeBoundaryConditions(NSp, Y0);
        Pair<OpenMapRealMatrix, OpenMapRealVector> res = applyBoundaryConditions(KG, FG, boundaryConitions);

        return res;
    }
    
    protected OpenMapRealMatrix getSysBlock(OpenMapRealMatrix C, OpenMapRealMatrix K, Element elem, int l, int m, double t){
        double CKoof =  -elemFunc.integrate(elem, ElemFuncType.dFdx, ElemFuncType.dFdx, l, m);
        double KKoof =  elemFunc.integrate(elem, ElemFuncType.F, ElemFuncType.F, l, m);
        
        OpenMapRealMatrix CLoc = (OpenMapRealMatrix)C.scalarMultiply(CKoof);
        OpenMapRealMatrix KLoc = (OpenMapRealMatrix)K.scalarMultiply(KKoof);
        
        return CLoc.add(KLoc);
    }
}
