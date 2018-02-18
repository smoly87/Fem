/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils.openmap;

import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;

/**
 *
 * @author Andrey
 */
public class SubMatrixArrangeWalker implements RealMatrixPreservingVisitor{

    protected OpenMapRealMatrix K;
    protected int startRow;
    protected int startCol;

    public SubMatrixArrangeWalker(OpenMapRealMatrix targetMatr, int startRow, int startCol) {
        this.K = targetMatr;
        
        this.startRow = startRow;
        this.startCol = startCol;
    }
    
    @Override
    public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
       
    }

    @Override
    public void visit(int row, int column, double value) {
        K.addToEntry(startRow + row, startCol + column, value);
    }

    @Override
    public double end() {
        return 0;
    }
    
}
