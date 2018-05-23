/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timesolver;

import engine.utils.openmap.Convertion;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Andrey
 */
public class FDMTimeSolver {
    public double [][]  solve(OpenMapRealMatrix C, OpenMapRealMatrix K,  OpenMapRealVector Y0,  
                int tStepsCnt, double tFrom, double tTo){
        double X[][] = new double[tStepsCnt][C.getRowDimension()];
        
        LUDecomposition decomp = new  LUDecomposition(C);
        RealMatrix CInv = decomp.getSolver().getInverse();
        
        OpenMapRealMatrix Ck = Convertion.ColVector2MatrixOM(Y0.toArray());
        OpenMapRealMatrix Ck_prev = Ck;
        double dt = (tTo - tFrom)/tStepsCnt;
        double dt2 = dt* dt;
        
        for(int t = 0; t < tStepsCnt; t++){
            RealMatrix KcM = K.multiply(Ck);
            RealVector Kc =  KcM.getColumnVector(0);
            RealVector d1 =  Y0.append(Kc.mapMultiply(-1.0)).mapMultiply(dt2);
            RealMatrix p1 = CInv.multiply(Convertion.ColVector2MatrixOM(d1.toArray()));
            RealMatrix p2 = Ck.scalarMultiply(2).add(Ck_prev.scalarMultiply(-1.0)) ;
            
            X[t+1] = p1.add(p2).getColumn(0);
            
            Ck_prev = Ck;
            Ck = Convertion.ColVector2MatrixOM(X[t+1]);
        }
        
        return X;
    }
}
