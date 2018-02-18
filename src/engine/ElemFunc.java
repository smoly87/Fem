/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author Andrey
 */
public abstract class ElemFunc {
   public abstract double F(double[] c, int funcNum,  Element elem);
   public abstract double dF(double[] c, int funcNum,  Element elem);
   public abstract double dFdx(double[] c, int funcNum,  Element elem);
   
   public abstract double integrate(Element elem, ElemFuncType type1, ElemFuncType type2,int l, int m);
        
   
}
