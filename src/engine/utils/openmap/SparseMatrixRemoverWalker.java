/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils.openmap;

import java.util.ArrayList;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;

/**
 *
 * @author Andrey
 */
public class SparseMatrixRemoverWalker implements RealMatrixPreservingVisitor{

    protected OpenMapRealMatrix K;
    
    protected OpenMapRealMatrix R;
    
    protected ArrayList<Integer> boundIndexes;

 
    protected TargetStatus rowTargetStatus;
    protected TargetStatus colTargetStatus;
    
    protected int curRow;
    protected int targetRow = -1;   
    public OpenMapRealMatrix getR() {
        return R;
    }

    public void setR(OpenMapRealMatrix R) {
        this.R = R;
    }
    
  

 
    
    public SparseMatrixRemoverWalker(OpenMapRealMatrix K, ArrayList<Integer> boundIndexes, RealMatrixWalkType walkType) {
        this.K = K;
        this.boundIndexes = boundIndexes;
        
        int Nd = K.getRowDimension() ;
                
        
        Nd -=boundIndexes.size();
        R = new OpenMapRealMatrix(Nd, Nd);
        
        rowTargetStatus = new TargetStatus(boundIndexes);
        rowTargetStatus.setTargetOffset(1);
        colTargetStatus = new TargetStatus(boundIndexes);
        
    }
    
    protected void processElement(int row, int column, double value, TargetCompareResult  rowResult,TargetCompareResult colResult){
        switch(colResult){
                case OrdinaryItem:
                    R.setEntry(row - rowTargetStatus.getOffset(), column - colTargetStatus.getOffset(), value);
                    break;
               /* case AllTagetsReached:
                    colTargetStatus.reset();
                    break;*/
              }
    }
    
    @Override
    public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
        
    }

    @Override
    public void visit(int row, int column, double value) { 
        if(row == targetRow){
            return;
        }
        if(row != curRow){
            colTargetStatus.reset();
        }
        TargetCompareResult colResult = colTargetStatus.compare(column);
        TargetCompareResult rowResult = rowTargetStatus.compare(row) ;
        switch(rowResult){
           case  OrdinaryItem: 
              processElement(row, column, value,rowResult, colResult);
              break;
           case TargetReached:
              targetRow = row;
              break;
            
        }
        
        curRow = row;
    }

    @Override
    public double end() {
      return 0;
    }
    
}
