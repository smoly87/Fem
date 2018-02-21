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

    public Element1d(Mesh mesh, ArrayList<Integer> nodesList) {
        super(mesh, nodesList);
        double[] p1 = mesh.getPoints().get(nodesList.get(0)).getCoordinates();
        double[] p2 = mesh.getPoints().get(nodesList.get(1)).getCoordinates();
        
        this.h = p2[0] - p1[0];
    }

    public double getH() {
        return h;
    }
}
