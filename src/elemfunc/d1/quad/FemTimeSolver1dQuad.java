/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elemfunc.d1.quad;

import engine.*;
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
public class FemTimeSolver1dQuad extends FemTimeSolver{

    public FemTimeSolver1dQuad() {
        funcBuilder = new QuadNBuilder();
    }

 
   
    @Override
    protected OpenMapRealMatrix getSysBlock(Element elem, int l, int m){
        ElemFunc elemFunc = elem.getElemFunc();
        double CKoof =  elemFunc.integrate(ElemFuncType.d2Fdx, ElemFuncType.d2Fdx, l, m);
        double KKoof =  elemFunc.integrate(ElemFuncType.F, ElemFuncType.F, l, m);
        
        OpenMapRealMatrix CLoc = (OpenMapRealMatrix)CB.scalarMultiply(CKoof);
        OpenMapRealMatrix KLoc = (OpenMapRealMatrix)KB.scalarMultiply(KKoof);
        
        return CLoc.add(KLoc);
    }

    @Override
    protected Mesh createTimeMesh(int tStepsCnt) {
        return SimpleMeshBuilder.create1dLineMeshQuad(tStepsCnt, true);
    }
}
