/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import elemfunc.FuncParams;
import engine.utils.common.TripleFunction;
import java.util.function.BiFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author Andrey
 */
public abstract class ElemFunc {
   public abstract double F(double[] c, int funcNum);
   public abstract double dFdx(double[] c, int funcNum);
   
   public abstract double integrate( ElemFuncType type1, ElemFuncType type2,int l, int m);
   
    
   protected BiFunction<double[], Integer, Double> f1;
   protected BiFunction<double[], Integer, Double> f2;
   protected FuncParams funcParams;
   
   protected Element elem;

    public ElemFunc(Element elem) {
        this.elem = elem;
        funcParams = new FuncParams();
    }
   
   protected void setCurElemParams(Element elem, ElemFuncType type1, ElemFuncType type2,int l, int m ){
         f1 = getFuncRef(type1);
         f2 = getFuncRef(type2);
                
         funcParams.setCurElem(elem);
         funcParams.setFuncNum1(l);
         funcParams.setFuncNum2(m);
   }
   
   protected BiFunction<double[], Integer, Double> getFuncRef(ElemFuncType type){
        BiFunction<double[], Integer, Double>  res = null;
        switch(type){
            case F:
                res = this::F;
                break;
            case dFdx:
                res = this::dFdx;
                
        }
        return res;
    }
        
   
}
