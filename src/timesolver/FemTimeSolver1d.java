/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timesolver;

import elemfunc.d1.LinNBuilder;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.Mesh;
import engine.SimpleMeshBuilder;
import org.apache.commons.math3.linear.OpenMapRealMatrix;

/**
 *
 * @author Andrey
 */
public class FemTimeSolver1d extends FemTimeSolver {

    public FemTimeSolver1d() {
        funcBuilder = new LinNBuilder();
    }

    @Override
    protected OpenMapRealMatrix getSysBlock(Element elem, int l, int m){
        ElemFunc elemFunc = elem.getElemFunc();
        double CKoof =  -elemFunc.integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m);
        double KKoof =  elemFunc.integrate(ElemFuncType.F, ElemFuncType.F, l, m);
        
        OpenMapRealMatrix CLoc = (OpenMapRealMatrix)CB.scalarMultiply(CKoof);
        OpenMapRealMatrix KLoc = (OpenMapRealMatrix)KB.scalarMultiply(KKoof);
        
        return CLoc.add(KLoc);
    }

    @Override
    protected Mesh createTimeMesh(int tStepsCnt) {
         return SimpleMeshBuilder.create1dLineMesh(tStepsCnt, false); 
    }
}
