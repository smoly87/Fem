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
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author Andrey
 */
public class LinUniformTriangle extends ElemFunc2d{
    protected double det;
    
    protected double[] p1 ;
    protected double[] p2 ;
    protected double[] p3 ;
    
    protected double a;
    protected double b;
    protected double g;

    public LinUniformTriangle(Element element) {
        super(element);
        p1 = pointValues(element, 0);
        p2 = pointValues(element, 1);
        p3 = pointValues(element, 2);
        
        det = countDet(element);
        countCoofs();
    }
    
    protected double[] pointValues(Element element, int nodeInd){
        int ind = element.getNodesList().get(nodeInd);
        Vector point = element.getMesh().getPoints().get(ind);
        return point.getCoordinates();
    }
    
    protected double countDet(Element element){
       
        
        RealMatrix detMat = new Array2DRowRealMatrix(new double[][]{
            {1, p1[0], p1[1]},
            {1, p2[0], p2[1]},
            {1, p3[0], p3[1]},
        });
        
       return new  LUDecomposition(detMat).getDeterminant();
    }
    
    protected void countCoofs(){
        double d2 = det * 2;
        a = (p2[0]*p3[1] - p3[0]*p2[1])/d2;
        b = (p2[1] - p3[1])/d2;
        g = (p3[0] - p2[0])/d2;
    }
    
    @Override
    public double F(double[] c, int funcNum) {
        double x = c[0];
        double y = c[1];
        return a + b*x + g*y;
    }

  

    @Override
    public double dFdx(double[] c, int funcNum) {
       return b;
    }
    
    @Override
    public double dFdy(double[] c, int funcNum) {
       return g;
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
