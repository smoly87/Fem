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
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Andrey
 */
public class heatEquation1dSys extends Task{
    protected int elemNum;
    private final int BLOCK_SIZE = 2;
    
    public heatEquation1dSys(int elemNum) {
        this.elemNum = elemNum;
    }
 
    
    
    protected void init() {
        
        mesh = SimpleMeshBuilder.create1dLineMesh(elemNum);
        ArrayList<Element> elements = mesh.getElements();
        
        ElemFunc elemFunc = new LinN();
        int N = mesh.getNodesCount();
       
        K = buildSystem(mesh, this::getSysBlock, BLOCK_SIZE);
        F = new OpenMapRealVector(new double[BLOCK_SIZE * N]);
        
        double[] QBound = new double[]{100, 200};
        Integer[] boundNodes = new Integer[]{0, mesh.getNodesCount()-1};
        boundaryConitions = new BoundaryConditions(QBound, boundNodes);
        
        Pair<OpenMapRealMatrix, OpenMapRealVector> res = this.applyBoundaryConditions(K, F, boundaryConitions);
        this.K = res.getV1();
        this.F = res.getV2();
       
         
    }
    
    
    protected RealMatrix getSysBlock(Element elem, Integer l, Integer m){
        double a11 = elemFunc.integrate(elem, ElemFuncType.F, ElemFuncType.F, l, m);
        double a12 = elemFunc.integrate(elem, ElemFuncType.F, ElemFuncType.dFdx, l, m);
        double a21 = elemFunc.integrate(elem, ElemFuncType.F, ElemFuncType.F, l, m);
        double a22 = 0;
        return new Array2DRowRealMatrix(new double[][]{
            {a11, a12},
            {a21, a22}    
        });
    }
    
    public double[] solve(){
        init();
        DecompositionSolver solver = new LUDecomposition(K).getSolver();
        RealVector X = solver.solve(F);
        double[] R = restoreBoundary(X.toArray(), boundaryConitions);
        return R;
    }
}
