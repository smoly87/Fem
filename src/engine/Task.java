/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import elemfunc.d1.LinN;
import engine.utils.common.Pair;
import engine.utils.openmap.RealVectorRemoveWalker;
import engine.utils.openmap.SparseMatrixRemoverWalker;
import engine.utils.openmap.RealMatrixWalkType;
import engine.utils.openmap.SubMatrixArrangeWalker;
import java.util.ArrayList;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixChangingVisitor;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SparseFieldMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;

/**
 *
 * @author Andrey
 */
public class Task {
    protected OpenMapRealMatrix K;
    protected OpenMapRealVector F;
    protected Mesh mesh;
    protected BoundaryConditions boundaryConitions;
 
    public BoundaryConditions getBoundaryConitions() {
        return boundaryConitions;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    protected final double MIN_ELEM = 10^-16;
    
    protected void initMatrixes(int N){
        K = new OpenMapRealMatrix(N, N);
        F = new OpenMapRealVector(N);
    }
    
    protected double[][] scalarMultiply(double[][] arr, double d){
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr[i].length; j++){
                arr[i][j] = d * arr[i][j]; 
            }
        }
        return arr;
    }
    
    protected OpenMapRealMatrix removeElemsForBoundConds(OpenMapRealMatrix K, BoundaryConditions boundaryConditions){       
       SparseMatrixRemoverWalker visitorRows = new SparseMatrixRemoverWalker(K, boundaryConditions.getBoundIndexes(), RealMatrixWalkType.ROWS);
       K.walkInRowOrder(visitorRows);
       return visitorRows.getR();
   
    }
    
    protected OpenMapRealVector removeElemsForBoundConds(OpenMapRealVector F, BoundaryConditions boundaryConditions){
       RealVectorRemoveWalker vecWalker = new RealVectorRemoveWalker(F, boundaryConditions.getBoundIndexes());
       F.walkInDefaultOrder(vecWalker);
       return vecWalker.getR();
    }
    
    
    /*protected void applyBoundNode(OpenMapRealMatrix KG, OpenMapRealVector FG, int i, int j, Vector v){
          for(int k = 0; k < v.)
         
    }*/

    
    protected Pair<OpenMapRealMatrix, OpenMapRealVector> applyBoundaryConditions(OpenMapRealMatrix KG, OpenMapRealVector FG, BoundaryConditions boundaryConditions){
       
        int N = KG.getRowDimension();
        for(int k = 0; k < boundaryConditions.getNodesCount(); k++){
            int i = boundaryConditions.getPointIndex(k);
            double Qbound = boundaryConditions.getBoundaryValue1d(k);
            for(int j=0; j < N; j++){
                if(j != i){
                     //TODO: Add source value to F
                     FG.addToEntry(j, -KG.getEntry(j, i) * Qbound);
                     KG.setEntry(j, i, 0);
                }
               
            }
       }
        
       KG = this.removeElemsForBoundConds(KG, boundaryConditions);
       FG = this.removeElemsForBoundConds(FG, boundaryConditions);
       return new Pair<>(KG, FG);
    }
    
    protected double[][] fillStiffnessMatrix(Element elem, KlmFunction KLMFunc){
      int N = elem.nodesList.size();
    
      double[][] KLoc = new double[N][N];
      for(int k = 0; k < N;k++){
         int c = k;
         for(int r = 0; r < N-k;r++){
            double v = KLMFunc.apply(elem, r, c);//elem.getElemFunc().integrate( type1, type2, r, c);
            KLoc[r][c] = v;
            KLoc[c][r] = KLoc[r][c]; 
            c++;
         } 
      }
     
      return KLoc;
    }
    
    protected OpenMapRealMatrix arrangeInGlobalStiffness(OpenMapRealMatrix M, double[][] KLoc, ArrayList<Integer> numsList){
        int N = numsList.size();
        for(int l = 0; l < N; l++){
            for(int m = 0; m < N; m++){
                int i = numsList.get(l);
                int j = numsList.get(m);  
                M.addToEntry(i, j, KLoc[l][m]);
            }
        }
        
        return M;
    }
    
    
    protected OpenMapRealMatrix arrangeSubMatrix(OpenMapRealMatrix M, double[][] subMatrix, int row, int col){
        int N = subMatrix.length;
        M.setSubMatrix(subMatrix, row * N, col * N);
        
        return M;
    }
    protected OpenMapRealMatrix arrangeSubMatrix(OpenMapRealMatrix M, OpenMapRealMatrix subMatrix, int row, int col){
        int N = subMatrix.getRowDimension();
        SubMatrixArrangeWalker walker = new SubMatrixArrangeWalker(M, row * N, col *N);
        subMatrix.walkInOptimizedOrder(walker);
        return M;
    }
    
    protected OpenMapRealMatrix buildSystem(Mesh mesh, SysBlockBuilder blockBuilder, int blockSize){
        ArrayList<Element> elems = mesh.getElements();
        int Nb = mesh.getNodesCount();
        OpenMapRealMatrix K = new OpenMapRealMatrix(Nb * blockSize, Nb * blockSize);
        for(int i = 0; i < elems.size(); i++){
            Element elem = elems.get(i);
            ArrayList<Integer> nodesList = elem.getNodesList();
            int N = nodesList.size();
            for(int l = 0; l < N; l++){
                for(int m = 0; m < N; m++){
                    int gl = nodesList.get(l);
                    int gm = nodesList.get(m);
                    OpenMapRealMatrix blockCell =  blockBuilder.apply(elem, l, m);
                    K = this.arrangeSubMatrix(K, blockCell, gl, gm);
                }
            }
        }
        return K;
    }
    
    protected OpenMapRealMatrix assembleBlockDiag(int N,  int Tsteps,  RealMatrix firstRowMatr, RealMatrix rowMatr, RealMatrix lastRowMatr){
        int gs = N * Tsteps;
        OpenMapRealMatrix G = new OpenMapRealMatrix(gs, gs);
       
        for(int r = 0; r < Tsteps; r++ ){ 
            
            RealMatrix cRow = null;
            int rFrom = r * N;
            int colFrom = r < 2 ? 0 : r * N;
             
            if( r == 0){
                cRow = firstRowMatr;
            } else if (r == Tsteps - 1){
                cRow = lastRowMatr;
            } else{
                cRow = rowMatr;
            }
 
            G.setSubMatrix(cRow.getData(), rFrom, colFrom);
        }
        
        return G;
    }
    
    public static double[] restoreBoundary(double[]X,  BoundaryConditions boundaryConditions){
        int BN = boundaryConditions.getNodesCount();
        int N = X.length + BN;
        double[] R = new double[N];
        int l = 0;
        int pointInd = boundaryConditions.getPointIndex(0);
        // From zero to first bound point
        System.arraycopy(X, 0, R, 0, pointInd);
        l = pointInd;
        for(int k = 0; k < BN - 1; k++){
            pointInd = boundaryConditions.getPointIndex(k);
            R[pointInd] = boundaryConditions.getBoundaryValue1d(k);
            int partLen = boundaryConditions.getPointIndex(k + 1) - pointInd - 1;
            System.arraycopy(X, l , R, pointInd+1,  partLen);
            l += partLen;
        }
        
       // Part from last index to end of X also should be considered.
        pointInd = boundaryConditions.getPointIndex(BN - 1);
        R[pointInd] = boundaryConditions.getBoundaryValue1d(BN - 1);
        System.arraycopy(X, l, R, pointInd+1, N - 1 - pointInd);
        
        return R;
    }
    
     protected double[][] convertSolution(RealVector X, BoundaryConditions boundaryCond, int timeSteps ){
        double [] data = X.toArray();
        int BN = boundaryConitions.getNodesCount();
        int N = data.length / timeSteps  ;
        double[][] res = new double[timeSteps + 1][N + BN];
        for(int t = 0; t < timeSteps; t++){
            double[] values = new double[N];
            System.arraycopy(data, t * N, values, 0, N);
            values = restoreBoundary(values, boundaryConitions); 
            res[t + 1] = values;
        }
        
        res[0] = restoreBoundary(boundaryCond.getBoundNodes(), boundaryConitions);
        
        return res ;
    }
     
    protected OpenMapRealMatrix fillGlobalStiffness(OpenMapRealMatrix M, KlmFunction klmFunc) {
        ArrayList<Element> elements = mesh.getElements();
        for (int i = 0; i < elements.size(); i++) {
            Element elem = elements.get(i);

            double[][] MLoc = fillStiffnessMatrix(elem, klmFunc);
            M = this.arrangeInGlobalStiffness(M, MLoc, elem.getNodesList());
        }
        
        return M;
    } 
    
    protected OpenMapRealMatrix fillGlobalStiffness(OpenMapRealMatrix M, double[][] MLoc) {
        ArrayList<Element> elements = mesh.getElements();
        for (int i = 0; i < elements.size(); i++) {
            Element elem = elements.get(i);

       
            M = this.arrangeInGlobalStiffness(M, MLoc, elem.getNodesList());
        }
        
        return M;
    } 
}
