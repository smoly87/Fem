/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elemfunc.d1;

import engine.Element;
import engine.Mesh;
import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class Element1d extends Element{
    protected double h;

    public Element1d(ArrayList<Integer> nodesList,  double h) {
        super(nodesList);
        this.h = h;
    }

    public double getH() {
        return h;
    }
}
