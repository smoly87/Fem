/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elemfunc.d1.quad;

import elemfunc.d1.*;
import engine.utils.common.TripleFunction;
import com.sun.jndi.toolkit.ctx.PartialCompositeContext;
import elemfunc.FuncParams;
import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.Vector;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import java.util.function.Function;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
/**
 *
 * @author Andrey
 */
public class QuadN extends ElemFunc implements UnivariateFunction{
    protected int elemNum;
   
  

    public QuadN(Element elem) {
        super(elem);
         funcsCount = 3;
    }
    
    @Override
    public double F(double[] c, int funcNum) {
        double res = 0;
        double x = c[0];
     
        switch (funcNum){
            case 0:
                res = x * (x-1)/2;
                break;
            case 1:
                res = -(x-1)*(x+1);
                break;
            case 2:
                res = x*(x+1)/2;
                break;
        }
        
        return res;
    }

    @Override
    public double dFdx(double[] c, int funcNum) {
        double res = 0;
        double x = c[0];
       
        switch (funcNum){
            case 0:
                res = (2*x - 1)/2;
                break;
            case 1:
                res = -2*x;
                break;
            case 2:
                res = (2*x+1)/2;
                break;
        }
        double m = 2/getH();
        return m*res;
    }

    public double d2Fdx(double[] c, int funcNum) {
        double res = 0;
        switch (funcNum){
            case 0:
                res = 1;
                break;
            case 1:
                res = -2;
                break;
            case 2:
                res = 1;
                break;
        }
        
        double m = (double)2/getH();
        return m*m*res;
    }

    public double getH(){
        double[]p0 = getPointCoordinates(0);
        double[]p2 = getPointCoordinates(2);
        double h = p2[0] - p0[0];
        return h;
    }
    
    protected double[] getPointCoordinates(int pointINd){
        return elem.getMesh().getPoints().get(elem.getNodesList().get(pointINd)).getCoordinates();
    }
    
    
    @Override
    protected double[] absCoordToLocal(double[]c ){
        double[] xc = getPointCoordinates(1);
        double h = getH();
        double[] lc ={2*(c[0] - xc[0])/h};
        return lc;
    }
    
    @Override
    public double integrate( ElemFuncType type1, ElemFuncType type2,int l, int m ) {
         
         
         
         double minV = -1;
         double maxV = 1;
         double J = getH()/2;        
         
         setCurElemParams(elem, type1, type2, l, m);
      
         SimpsonIntegrator integrator = new SimpsonIntegrator();
         return J * integrator.integrate(1000, this, minV, maxV);         
    }

    protected double applyFuncCall(BiFunction f, int argNum, double x){
        int funcNum = argNum == 0 ? funcParams.getFuncNum1() : funcParams.getFuncNum2();
        return (double) f.apply(new double[]{x}, funcNum);
    }
    
    @Override
    public double value(double x) {
       double v1 = applyFuncCall(f1, 0, x); 
       double v2 = applyFuncCall(f2, 1, x); ; 
       return v1 * v2;
    }

    @Override
    protected BiFunction<double[], Integer, Double> getFuncRef(ElemFuncType type) {
        BiFunction<double[], Integer, Double> res = super.getFuncRef(type); 
        if(res == null){
  
          switch(type){
            case d2Fdx:
                res = this::d2Fdx;
                break;
          }
        }
        return res;
    }
    
    
}
