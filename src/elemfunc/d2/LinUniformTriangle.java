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
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;

/**
 *
 * @author Andrey
 */
public class LinUniformTriangle extends ElemFunc2d implements UnivariateFunction{
    protected double det;

    public double[] getP1() {
        return p1;
    }

    public double[] getP2() {
        return p2;
    }

    public double[] getP3() {
        return p3;
    }

    public double[] getA() {
        return a;
    }

    public double[] getB() {
        return b;
    }

    public double[] getG() {
        return g;
    }
    
    protected double[] p1 ;
    protected double[] p2 ;
    protected double[] p3 ;
    
    protected double[] a;
    protected double[] b;
    protected double[] g;
    protected RealMatrix detMat;
    protected DecompositionSolver coofSolver;
    protected LinTriangleWrapper innerInteg;
    protected SimpsonIntegrator integrator;
    protected SimpsonIntegrator integrator2;
    protected double L1;
    protected double J;
    protected RealMatrix Jac;
    protected RealMatrix derivativesCoofs;
    
    public LinUniformTriangle(Element element) {
        super(element);
         funcsCount = 3;
        
        p1 = pointValues(element, 0);
        p2 = pointValues(element, 1);
        p3 = pointValues(element, 2);
        countJ();
        
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
        
        integrator = new SimpsonIntegrator();
         integrator2 = new SimpsonIntegrator();
        innerInteg = new LinTriangleWrapper(this);
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
    
  
    public double FA(double[] c, int funcNum) {
        double x = c[0];
        double y = c[1];
        return a[funcNum] + b[funcNum]*x + g[funcNum]*y;
    }

  

  
    public double dFdxA(double[] c, int funcNum) {
       return b[funcNum];
    }
    
  
    public double dFdyA(double[] c, int funcNum) {
       return g[funcNum];
    }
    
    protected int summ(int[] arr){
        int s = 0;
        for(int i = 0; i < arr.length; i++){
            s += arr[i];
        }
        return s;
    }
    
    protected int facProd(int[] arr){
        int s = 1;
        for(int i = 0; i < arr.length; i++){
            s *=  CombinatoricsUtils.factorial(arr[i]);
        }
        return s;
    }
    
    @Override
    public double integrate( ElemFuncType type1, ElemFuncType type2, int l, int m) {
        //CombinatoricsUtils.factorial(m);
        int[] v = new int[3];
        if(type1 == ElemFuncType.F) v[l]++;
        if(type2 == ElemFuncType.F) v[m]++;
       
        double sq = ((double)facProd(v) /(double)(CombinatoricsUtils.factorial(summ(v) + 2))) * det;
        return sq;
         //setCurElemParams(elem, type1, type2, l, m);
   
        /* double JL = 1.0;
         if(type1 == ElemFuncType.F) JL = J;
       return  JL * integrator.integrate(20, this, 0, 0.999999);*/
       /*double v1 = applyFuncCall(f1, 0);
       double v2 = applyFuncCall(f2, 1);
       //TODO: Figure out multiplier 0.5 is necessary or not.
       return 0.5*det * v1 *v2;*/
    }
    
    protected double applyFuncCall(BiFunction f, int argNum, double[] coords){
        int funcNum = argNum == 0 ? funcParams.getFuncNum1() : funcParams.getFuncNum2();
        //This is done to save uniform approach
        //But absolutely obviously that it could e released by just multiple numeric coofs
        return (double) f.apply(coords, funcNum);
    }

    @Override
    public double value(double L1) {
        this.L1 = L1;
        return integrator2.integrate(20, innerInteg, 0, 1 - L1 );
    }
    
    public double innerValue(double L2){
        //if(L1 + L2 > 1) return 0;
        double[] args = new double[]{1-L1-L2,L1, L2};
        double v1 = applyFuncCall(f1, 0, args);
        double v2 = applyFuncCall(f2, 1, args);
        return v1*v2;
    }

    protected void countJ(){
        Jac = new Array2DRowRealMatrix(new double[][]{
            { -p1[0]+p2[0], -p1[0]+p3[0]},
            { -p1[1]+p2[1], -p1[1]+p3[1]},
            
        });
        
        Array2DRowRealMatrix Koofs = new Array2DRowRealMatrix(new double[][]{
            {-1, 1, 0},
            {-1, 0, 1},
        });
        
        Jac = Jac.transpose();
        LUDecomposition decomp = new  LUDecomposition(Jac);
        J = decomp.getDeterminant();
        RealMatrix JInv = decomp.getSolver().getInverse();
        derivativesCoofs = JInv.multiply(Koofs);
       // System.out.println("8");
    }


    @Override
    public double F(double[] c, int funcNum) {
        return c[funcNum] ;
    }

    @Override
    public double dFdx(double[] c, int funcNum) {
        return derivativesCoofs.getEntry(0, funcNum);
    }
    
    @Override
    public double dFdy(double[] c, int funcNum) {
        return derivativesCoofs.getEntry(1, funcNum);
    }
}
