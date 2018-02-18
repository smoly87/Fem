/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.waveequation;

import elemfunc.d1.Element1d;
import elemfunc.d1.LinN;
import engine.BoundaryConditions;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.FemTimeSolver1d;
import engine.Mesh;
import engine.SimpleMeshBuilder;
import engine.Task;
import engine.Vector;
import engine.utils.common.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealVector;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.PI;
/**
 *
 * @author Andrey
 */
public class waveEquation1d extends Task{
    protected int elemNum;
    protected int timeSteps;
    
    protected OpenMapRealMatrix C;
    protected FemTimeSolver1d timeSolver; 
    
    public waveEquation1d(int elemNum, int timeSteps) {
        this.elemNum = elemNum;
        this.timeSteps = timeSteps;
    }

    @Override
    protected void initMatrixes(int N) {
        super.initMatrixes(N); //To change body of generated methods, choose Tools | Templates.
         C = new OpenMapRealMatrix(N, N);
    }
    
    protected void fillMatrixes(){
       
        ArrayList<Element> elements = mesh.getElements();
        
        elemFunc = new LinN();
     
      
        for(int i = 0; i < elemNum; i++){
            Element elem = elements.get(i);
            
            double[][] CLoc = this.fillStiffnessMatrix(elem, elemFunc, ElemFuncType.F, ElemFuncType.F);
            C = this.arrangeInGlobalStiffness(C, CLoc, elem.getNodesList());
            
            double[][] KLoc = this.fillStiffnessMatrix(elem, elemFunc, ElemFuncType.dFdx, ElemFuncType.dFdx);
            K = this.arrangeInGlobalStiffness(K, KLoc, elem.getNodesList());
        }
    }
    
    protected void applySpatialBoundaryConditions(){
        double[] QBound = new double[]{0, 0};
        Integer[] boundNodes = new Integer[]{0, mesh.getNodesCount()-1};
        boundaryConitions = new BoundaryConditions(QBound, boundNodes);
     
        Pair<OpenMapRealMatrix, OpenMapRealVector> res = this.applyBoundaryConditions(K, F, boundaryConitions);
        
        this.K = res.getV1();
        this.F = res.getV2();
        this.C = removeElemsForBoundConds(C, boundaryConitions);
       
    }
    
    protected OpenMapRealVector getInitialConditions(ArrayList<Vector> points){
        int N = points.size();
        double[] values = new double[N];
         
        for(int i = 0; i < N; i++){
            Vector point = points.get(i);
            double x = point.getCoordinates()[0];
            
            values[i] = sin(x * PI);
            //System.out.println(values[i]);
        }
        
        return new OpenMapRealVector(values);
    }
    
    protected void init() {
        
        mesh = SimpleMeshBuilder.create1dLineMesh(elemNum);
        this.initMatrixes(mesh.getNodesCount());
        
        fillMatrixes();
        applySpatialBoundaryConditions();
         
    }
    
    protected double[][] convertSolution(RealVector X, int timeSteps ){
        double [] data = X.toArray();
        int N = data.length / timeSteps;
        double[][] res = new double[timeSteps + 1][N + boundaryConitions.getNodesCount()];
        for(int t = 0; t < timeSteps; t++){
            double[] values = new double[N];
            System.arraycopy(data, t * N, values, 0, N);
            res[t + 1] = restoreBoundary(values, boundaryConitions);
        }
        
        res[0] = timeSolver.getBoundaryConitions().getBoundNodes();
        
        return res ;
    }
    
    public double[][] solve(){
        init();
                       
        timeSolver = new FemTimeSolver1d();
        OpenMapRealVector Y0 = getInitialConditions(mesh.getPoints());
        Pair<OpenMapRealMatrix, OpenMapRealVector> Gmatrixes = timeSolver.buildTimeSystem(C, K, Y0, timeSteps, 0, 1);
        OpenMapRealMatrix M = Gmatrixes.getV1();
        
        DecompositionSolver solver = new LUDecomposition(Gmatrixes.getV1()).getSolver();
        //boolean T  = solver.isNonSingular();
                
        RealVector X = solver.solve(Gmatrixes.getV2()); 
        
        return convertSolution(X, timeSteps);
    }
}
