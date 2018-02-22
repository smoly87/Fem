/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.membrane;

import tasks.waveequation.*;
import elemfunc.d1.Element1d;
import elemfunc.d1.LinN;
import elemfunc.d1.LinNBuilder;
import elemfunc.d2.LinUniformTriangleBuilder;
import engine.BoundaryConditions;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.FemTimeSolver1d;
import engine.Mesh;
import engine.SimpleMeshBuilder;
import engine.Task;
import engine.Vector;
import engine.meshloader.MeshLoaderGmsh;
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
public class waveEquation2d extends Task{
    protected int elemNum;
    protected int timeSteps;
    
    protected OpenMapRealMatrix C;
    protected FemTimeSolver1d timeSolver; 
    
    public waveEquation2d(int elemNum, int timeSteps) {
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
        return elem.getElemFunc().integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, l, m) + 
               elem.getElemFunc().integrate(ElemFuncType.dFdy, ElemFuncType.dFdy, l, m);
    }
     
    protected void applySpatialBoundaryConditions(){
        ArrayList<Integer> boundInd = mesh.getConvHullInd();
        double[] QBound = new double[boundInd.size()];
        boundaryConitions = new BoundaryConditions(QBound, boundInd);
     
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
            double y = point.getCoordinates()[1];
            
            values[i] = sin(x * PI)*sin(y * PI);
            
        }
        
        return new OpenMapRealVector(values);
    }
    
    protected void init() {
        
        MeshLoaderGmsh gmshLoader = new MeshLoaderGmsh(false);
        mesh = gmshLoader.loadMesh("/assets/test.msh");
        mesh.applyElemFunc(new LinUniformTriangleBuilder());
        
        this.initMatrixes(mesh.getNodesCount());
        
        fillMatrixes();
        applySpatialBoundaryConditions();
         
    }

    public double[][] solve(){
        init();
                       
        timeSolver = new FemTimeSolver1d();
        OpenMapRealVector Y0 = getInitialConditions(mesh.getPoints());
        Pair<OpenMapRealMatrix, OpenMapRealVector> Gmatrixes = timeSolver.buildTimeSystem(C, K, Y0, timeSteps, 0, 1);
        OpenMapRealMatrix M = Gmatrixes.getV1();
        
       // DecompositionSolver solver = new LUDecomposition(Gmatrixes.getV1()).getSolver();
        DecompositionSolver solver =new QRDecomposition(Gmatrixes.getV1()).getSolver();
        //boolean T  = solver.isNonSingular();
       // NewtonRaphsonSolver newtSolver = new NewtonRaphsonSolver();
        RealVector X = solver.solve(Gmatrixes.getV2()); 
        
        return convertSolution(X, timeSolver, timeSteps);
    }
}
