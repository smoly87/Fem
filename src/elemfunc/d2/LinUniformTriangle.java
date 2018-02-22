/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elemfunc.d2;

import engine.ElemFunc;
import engine.ElemFunc2d;
import engine.ElemFuncType;
import engine.Element;
import engine.Vector;
import engine.utils.common.TripleFunction;
import java.awt.Point;
import java.util.function.BiFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Andrey
 */
public class LinUniformTriangle extends ElemFunc2d{
    protected double det;
    
    protected double[] p1 ;
    protected double[] p2 ;
    protected double[] p3 ;
    
    protected double[] a;
    protected double[] b;
    protected double[] g;
    protected RealMatrix detMat;
    protected DecompositionSolver coofSolver;
    public LinUniformTriangle(Element element) {
        super(element);
        p1 = pointValues(element, 0);
        p2 = pointValues(element, 1);
        p3 = pointValues(element, 2);
        
        detMat = new Array2DRowRealMatrix(new double[][]{
            {1, p1[0], p1[1]},
            {1, p2[0], p2[1]},
            {1, p3[0], p3[1]},
        });
        coofSolver = new QRDecomposition(detMat).getSolver();
        
        det = countDet(element);
         
        a = new double[3];
        b = new double[3];
        g = new double[3]; 
        
        countCoofs(0);
        countCoofs(1);
        countCoofs(2);
    }
    
    protected double[] pointValues(Element element, int nodeInd){
        int ind = element.getNodesList().get(nodeInd);
        Vector point = element.getMesh().getPoints().get(ind);
        return point.getCoordinates();
    }
    
    protected double countDet(Element element){
       
        
       
        
       return new  LUDecomposition(detMat).getDeterminant();
    }
    
    protected void countCoofs(int funcNum){
        double[] bv = new double[3];
        bv[funcNum] = 1;
        
        RealVector bRv = new ArrayRealVector(bv);
        RealVector koofsV = coofSolver.solve(bRv);
        double[] koofs = koofsV.toArray();
        
        a[funcNum] = koofs[0];
        b[funcNum] = koofs[1];
        g[funcNum] = koofs[2];
    }
    
    @Override
    public double F(double[] c, int funcNum) {
        double x = c[0];
        double y = c[1];
        return a[funcNum] + b[funcNum]*x + g[funcNum]*y;
    }

  

    @Override
    public double dFdx(double[] c, int funcNum) {
       return b[funcNum];
    }
    
    @Override
    public double dFdy(double[] c, int funcNum) {
       return g[funcNum];
    }
    
    @Override
    public double integrate( ElemFuncType type1, ElemFuncType type2, int l, int m) {
       setCurElemParams(elem, type1, type2, l, m);
       double v1 = applyFuncCall(f1, 0);
       double v2 = applyFuncCall(f2, 1);
       return det * v1 *v2;
    }
    
    protected double applyFuncCall(BiFunction f, int argNum){
        int funcNum = argNum == 0 ? funcParams.getFuncNum1() : funcParams.getFuncNum2();
        //This is done to save uniform approach
        //But absolutely obviously that it could e released by just multiple numeric coofs
        return (double) f.apply(new double[]{0,0}, funcNum);
    }
}
