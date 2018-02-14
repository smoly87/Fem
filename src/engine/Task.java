/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import engine.utils.RealVectorRemoveWalker;
import engine.utils.SparseMatrixRemoverWalker;
import engine.utils.RealMatrixWalkType;
import java.util.ArrayList;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixChangingVisitor;
import org.apache.commons.math3.linear.SparseFieldMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;

/**
 *
 * @author Andrey
 */
public class Task {
    protected OpenMapRealMatrix K;
    protected OpenMapRealVector F;
    
    
    protected void initMatrixes(int N){
        K = new OpenMapRealMatrix(N, N);
        F = new OpenMapRealVector(N);
    }
    
    protected void removeRowsAndColumnsForBoundConds(ArrayList<Integer> boundIndexes){
       int N = K.getRowDimension();
       
       SparseMatrixRemoverWalker visitorRows = new SparseMatrixRemoverWalker(K, boundIndexes, RealMatrixWalkType.ROWS);
       K.walkInRowOrder(visitorRows);
       K = visitorRows.getR();
       
       RealVectorRemoveWalker vecWalker = new RealVectorRemoveWalker(F, boundIndexes);
       F.walkInDefaultOrder(vecWalker);
       F = vecWalker.getR();
    }
    
    protected void applyBoundaryConditions(ArrayList<Integer> boundIndexes, double[] boundValues){
        int N = K.getRowDimension();
        for(int k = 0; k < boundIndexes.size(); k++){
            int i = boundIndexes.get(k);
            double Qbound = boundValues[k];
            for(int j=0; j < N; j++){
                if(j != i){
                     F.addToEntry(j, - K.getEntry(j, i) * Qbound);
                     K.setEntry(j, i, 0);
                }
               
            }
       }
       this.removeRowsAndColumnsForBoundConds(boundIndexes);
       
    }
    
    protected double[][] fillStiffnessMatrix(Element elem, ElemFunc elemFunc, ElemFuncType type1, ElemFuncType type2, double minV, double maxV){
      int N = elem.nodesList.size();
    
      double[][] KLoc = new double[N][N];
      for(int k = 0; k < N;k++){
         int c = k;
         for(int r = 0; r < N-k;r++){
            KLoc[r][c] = elemFunc.integrate(elem, type1, type2, r, c, minV, maxV);
            KLoc[c][r] = KLoc[r][c]; 
            c++;
         } 
      }
     
      return KLoc;
    }
    
    protected void arrangeInGlobalStiffness(double[][] KLoc, ArrayList<Integer> numsList){
        int N = numsList.size();
        for(int l = 0; l < N; l++){
            for(int m = 0; m < N; m++){
                int i = numsList.get(l);
                int j = numsList.get(m);
                K.addToEntry(i, j, KLoc[l][m]);
            }
        }
    }
    
    
    protected void arrangeSubMatrix(double[][] subMatrix, int row, int col){
        K.setSubMatrix(subMatrix, row, col);
    }
}
