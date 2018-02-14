/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks;

import elemfunc.d1.Element1d;
import elemfunc.d1.LinN;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.Mesh;
import engine.SimpleMeshBuilder;
import engine.Task;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Andrey
 */
public class heatEquation1d extends Task{
    protected int elemNum;

    public heatEquation1d(int elemNum) {
        this.elemNum = elemNum;
    }
    
    protected void init() {
        
        Mesh mesh = SimpleMeshBuilder.create1dLineMesh(elemNum);
        ArrayList<Element> elements = mesh.getElements();
        
        ElemFunc elemFunc = new LinN();
        double h = 1/(double)mesh.getNodesCount();
        
        this.initMatrixes(mesh.getNodesCount());
        
        for(int i = 0; i < elemNum; i++){
            Element elem = elements.get(i);
            double[][] KLoc = this.fillStiffnessMatrix(elem, elemFunc, ElemFuncType.dFdx, ElemFuncType.dFdx, 0, h);
            this.arrangeInGlobalStiffness(KLoc, elem.getNodesList());
        }
        
        double[] QBound = new double[]{100, 200};
        Integer[] boundNodes = new Integer[]{0, mesh.getNodesCount()-1};
        ArrayList<Integer> boundIndexes = new ArrayList<>(Arrays.asList(boundNodes));
        
        this.applyBoundaryConditions(boundIndexes, QBound);
       
         
    }
    
    public RealVector solve(){
        init();
        DecompositionSolver solver = new LUDecomposition(K).getSolver();
        RealVector X = solver.solve(F); 
        return X;
    }
}
