/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timesolver;

import engine.BoundaryConditions;
import engine.Task;
import engine.utils.common.MathUtils;
import engine.utils.openmap.Convertion;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import tasks.waveequation.EigSolution;

/**
 *
 * @author Andrey
 */
public class AnalyticEigSolver {

    /**
     * This method solves Second order ODE. 
     * Pay attention that matrix for first order derivatives is zero.
     * @param A Coefficient of second order derivatives d/dx^2
     * @param C Coefficient before x
     */
    public EigSolution solveOdeSecondOrder(RealMatrix A, RealMatrix C, RealVector Y0){
        RealMatrix CInv = new LUDecomposition(C).getSolver().getInverse();
        // Coefficients are scaled that way Second order derivative Coefficients are identy.
        A = CInv.multiply(A);
        
        EigenDecomposition decomp = new EigenDecomposition(A);
        double[] w = decomp.getRealEigenvalues();
        
        //Matrix building from eig Vectors for find coefficients before them from initial conditions. 
        int N = w.length;
        Array2DRowRealMatrix H = new Array2DRowRealMatrix(N, N);
        
        for(int i = 0; i < N; i++){
            RealVector eigVec = decomp.getEigenvector(i);
            H.setColumn(i, eigVec.toArray());
        } 
        
        DecompositionSolver solver = new LUDecomposition(H).getSolver();
        RealVector HC = solver.solve(Y0); 
        
        return new EigSolution(H, HC, w);
        
    }

    /**
     * Return value of solution at defined moment of time
     * @param solution
     * @param t time moment
     * @return 
     */
    public double[] F(EigSolution solution, double t){
        double[] w = solution.getW();
        RealVector HC = solution.getHC();
        RealMatrix H = solution.getH();
        int N = w.length;
        //Full coeficients which consider coef before exponent and eigen vector both.
        double[] HCF = new double[N];
        
        for(int i = 0; i < N; i++){
            double v = HC.getEntry(i) * Math.cos(Math.sqrt(w[i]) * t);
            HCF[i] = v;
        }
       
        
        RealMatrix R = H.multiply(new Array2DRowRealMatrix( Convertion.ColVector2Matrix(HCF)));
        return R.getColumn(0);
    }
    
    public double[][] getSolutionValues(EigSolution solution, double[]T, BoundaryConditions boundaryCond){
       int N = solution.getW().length; 
       double[][]R = new double[T.length][N + boundaryCond.getNodesCount()];
       for(int t = 0 ; t < T.length; t++){
          double[] values = F(solution, T[t]);
          int z = MathUtils.countZeros(values);
          R[t] = Task.restoreBoundary(values, boundaryCond);
       } 
       return R;
    }
    
     
    
}
