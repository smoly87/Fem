/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.waveequation;

import timesolver.AnalyticEigSolver;
import elemfunc.d1.Element1d;
import elemfunc.d1.LinN;
import elemfunc.d1.LinNBuilder;
import timesolver.FemTimeSolver1dQuad;
import elemfunc.d1.quad.QuadNBuilder;
import engine.BoundaryConditions;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import timesolver.FemTimeSolver;
import timesolver.FemTimeSolver1d;
import engine.Mesh;
import engine.SimpleMeshBuilder;
import engine.Task;
import engine.Vector;
import engine.utils.common.MathUtils;
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
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.linear.ArrayRealVector;
import timesolver.FDMTimeSolver;
/**
 *
 * @author Andrey
 */
public class waveEquation1d extends Task{
    protected int elemNum;
    protected int timeSteps;
    
    protected OpenMapRealMatrix C;
    protected FemTimeSolver timeSolver; 
    
    public waveEquation1d(int elemNum, int timeSteps) {
        this.elemNum = elemNum;
        this.timeSteps = timeSteps;

    }

    public FemTimeSolver getTimeSolver() {
        return timeSolver;
    }

    @Override
    protected void initMatrixes(int N) {
        super.initMatrixes(N); //To change body of generated methods, choose Tools | Templates.
        C = new OpenMapRealMatrix(N, N);
    }
    
    protected void fillMatrixes(){
        K = fillGlobalStiffness(K, this::Klm);
        C = fillGlobalStiffness(C, this::Clm);
    }
    
    protected double Clm(Element elem, Integer l, Integer m){
        return elem.getElemFunc().integrate(ElemFuncType.F, ElemFuncType.F, l, m);
    }
    
    protected double Klm(Element elem, Integer l, Integer m){
        return elem.getElemFunc().integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m);
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
        
        //mesh = SimpleMeshBuilder.create1dLineMesh(elemNum, true);
  
        
        mesh = SimpleMeshBuilder.create1dLineMesh(elemNum, true);
       
        
        this.initMatrixes(mesh.getNodesCount());
        
        fillMatrixes();
        applySpatialBoundaryConditions();
         
    }

    public double[][] solveAnalytics(){
        init();
        OpenMapRealVector Y0 = getInitialConditions(mesh.getPoints());
        Y0 = removeElemsForBoundConds(Y0, boundaryConitions);
        AnalyticEigSolver solver = new AnalyticEigSolver();
        EigSolution sol = solver.solveOdeSecondOrder(K, C, Y0);
        double[] T  = MathUtils.linSpace(0, 1, timeSteps);
        double[][] X =  solver.getSolutionValues(sol, T, boundaryConitions);
        double[][] X0 = new double[X.length + 1][X[0].length];
        
        X0[0] = restoreBoundary(T, boundaryConitions);
        return X;
    }
    
    public double[][] solve(){
        init();
                       
        timeSolver = new FemTimeSolver1dQuad();
        OpenMapRealVector Y0 = getInitialConditions(mesh.getPoints());
        Y0 = removeElemsForBoundConds(Y0, boundaryConitions);
        Pair<OpenMapRealMatrix, OpenMapRealVector> Gmatrixes = timeSolver.buildTimeSystem(C, K, Y0, timeSteps, 0, 1);
        OpenMapRealMatrix M = Gmatrixes.getV1();
         OpenMapRealVector MF = Gmatrixes.getV2();
       // DecompositionSolver solver = new LUDecomposition(Gmatrixes.getV1()).getSolver();
        DecompositionSolver solver =new QRDecomposition(Gmatrixes.getV1()).getSolver();
        //boolean T  = solver.isNonSingular();
       // NewtonRaphsonSolver newtSolver = new NewtonRaphsonSolver();
        RealVector X = solver.solve(Gmatrixes.getV2()); 
        
        // -1 cause first time node was cutting off by applying for boundary conditions.
        int Tn = timeSolver.getTimeMesh().getNodesCount() - 1;
        double[][] res = convertSolution(X, timeSolver.getBoundaryConitions(), Tn);
        return res;
    }
    
    protected double[][] restoreBoundaryFDM(double[][]X, BoundaryConditions timeBoundaryCond){
        int BN = boundaryConitions.getNodesCount();
        int N = X[1].length ;
        double[][] res = new double[X.length][N + BN];
        for(int t = 0; t < timeSteps; t++){
            double[] values = new double[N];
            values = restoreBoundary(X[t], boundaryConitions); 
            res[t + 1] = values;
        }
        
        res[0] = restoreBoundary(timeBoundaryCond.getBoundNodes(), boundaryConitions);
        
        return res;
    }
    
    public double[][] solveFDM(){
        init();
                       
        FDMTimeSolver timeSolverFDM = new FDMTimeSolver();
        OpenMapRealVector Y0 = getInitialConditions(mesh.getPoints());
        Y0 = removeElemsForBoundConds(Y0, boundaryConitions);
        int Tn = 20;
        double[][]X = timeSolverFDM.solve(C, K, Y0, Tn, 0, 1);
        
        
        //double[][] res = convertSolution(new ArrayRealVector, timeSolver.getBoundaryConitions(), Tn);
        double[][] res = restoreBoundaryFDM(X,  timeSolver.getBoundaryConitions());
        return res;
    }
}
