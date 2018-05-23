/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import elemfunc.d1.Element1d;
import elemfunc.d1.LinN;
import engine.BoundaryConditions;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.Mesh;
import engine.SimpleMeshBuilder;
import engine.Task;
import engine.utils.common.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Andrey
 */
public class phiEquation1d1 extends Task{
    protected int elemNum;

    public phiEquation1d1(int elemNum) {
        this.elemNum = elemNum;
    }
 
    
    protected double Klm(Element elem, Integer l, Integer m){
        return elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.d2Fdx, l, m)-elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.F, l, m);
        //Weak form
        //return elem.getElemFunc().integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m)+elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.F, l, m);
    }
    
    protected void init() {
        
        mesh = SimpleMeshBuilder.create1dLineMeshQuad(1, true);
         
        this.initMatrixes(mesh.getNodesCount());
        
        K = fillGlobalStiffness(K, this::Klm);
        
        double[] QBound = new double[]{0, 1};
        Integer[] boundNodes = new Integer[]{0, mesh.getNodesCount()-1};
        boundaryConitions = new BoundaryConditions(QBound, boundNodes);
        
        Pair<OpenMapRealMatrix, OpenMapRealVector> res = this.applyBoundaryConditions(K, F, boundaryConitions);
        this.K = res.getV1();
        this.F = res.getV2();
       
         
    }
    
    public double[] solve(){
        init();
        DecompositionSolver solver = new LUDecomposition(K).getSolver();
        RealVector X = solver.solve(F);
        double[] R = restoreBoundary(X.toArray(), boundaryConitions);
        return R;
    }
}
