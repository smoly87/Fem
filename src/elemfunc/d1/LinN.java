/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elemfunc.d1;

import com.sun.jndi.toolkit.ctx.PartialCompositeContext;
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
public class LinN extends ElemFunc implements UnivariateFunction{
    protected int elemNum;
   
    protected FuncParams funcParams;
    
    protected TripleFunction<double[], Integer,Element, Double> f1;
    protected TripleFunction<double[], Integer,Element, Double> f2;

    public LinN() {
        funcParams = new FuncParams();
    }
    
    @Override
    public double F(double[] c, int funcNum, Element elem) {
        double res = 0;
        double x = c[0];
        Element1d elem1d = (Element1d)elem;
        double h = elem1d.getH();
        
        switch (funcNum){
            case 0:
                res = 1 - x/h;
                break;
            case 1:
                res = x/h;
                break;
        }
        
        return res;
    }

    @Override
    public double dF(double[] c, int funcNum,  Element elem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double dFdx(double[] c, int funcNum,  Element elem) {
        double res = 0;
        double x = c[0];
        Element1d elem1d = (Element1d)elem;
        double h = elem1d.getH();
        
        switch (funcNum){
            case 0:
                res = -1/h;
                break;
            case 1:
                res = 1/h;
                break;
        }
        
        return res;
    }

     
    protected TripleFunction<double[], Integer,Element, Double> getFuncRef(ElemFuncType type){
        TripleFunction<double[], Integer,Element, Double>  res = null;
        switch(type){
            
            case dFdx:
                res = this::dFdx;
        }
        return res;
    }
    
    @Override
    public double integrate(Element elem, ElemFuncType type1, ElemFuncType type2,int l, int m, double minV, double maxV) {
         f1 = getFuncRef(type1);
         f2 = getFuncRef(type2);
                
         funcParams.setCurElem(elem);
         funcParams.setFuncNum1(l);
         funcParams.setFuncNum2(m);
         
         SimpsonIntegrator integrator = new SimpsonIntegrator();
         return integrator.integrate(10, this, minV, maxV);         
    }

    protected double applyFuncCall(TripleFunction f, int argNum, double x){
        int funcNum = argNum == 0 ? funcParams.getFuncNum1() : funcParams.getFuncNum2();
        return (double) f.apply(new double[]{x}, funcNum, funcParams.getCurElem());
    }
    
    @Override
    public double value(double x) {
       double v1 = applyFuncCall(f1, 0, x); 
       double v2 = applyFuncCall(f2, 1, x); ; 
       return v1 * v2;
    }
    
}
