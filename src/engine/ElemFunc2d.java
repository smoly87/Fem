/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import elemfunc.FuncParams;
import engine.utils.common.TripleFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 *
 * @author Andrey
 */
public abstract class ElemFunc2d extends ElemFunc{

    public ElemFunc2d(Element elem) {
        super(elem);
    }
     public abstract double dFdy(double[] c, int funcNum) ;
   
}
