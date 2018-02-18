/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils.common;



/**
 *
 * @author Andrey
 */
@FunctionalInterface
public interface TripleFunction<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3);
}
    

