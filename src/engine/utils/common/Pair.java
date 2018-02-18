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
public class Pair<T1, T2> {
    protected T1 v1;
    protected T2 v2;

    public T1 getV1() {
        return v1;
    }

    public T2 getV2() {
        return v2;
    }

    public Pair(T1 v1, T2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
}
