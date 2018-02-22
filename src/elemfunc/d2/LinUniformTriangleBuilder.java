/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elemfunc.d2;

import elemfunc.d1.*;
import engine.ElemFunc;
import engine.ElemFuncBuilder;
import engine.Element;

/**
 *
 * @author Andrey
 */
public class LinUniformTriangleBuilder implements ElemFuncBuilder{

    @Override
    public ElemFunc build(Element elem) {
        return new LinUniformTriangle(elem);
    }
    
}
