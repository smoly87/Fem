/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.waveequation;

import elemfunc.d1.Element1d;
import elemfunc.d1.LinN;
import elemfunc.d1.LinNBuilder;
import engine.BoundaryConditions;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.FemTimeSolver1d;
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
        
        mesh = SimpleMeshBuilder.create1dLineMesh(elemNum);
        mesh.applyElemFunc(new LinNBuilder());
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
                       
        timeSolver = new FemTimeSolver1d();
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
        
        double[][] res = convertSolution(X, timeSolver.getBoundaryConitions(), timeSteps);
        return res;
    }
}
