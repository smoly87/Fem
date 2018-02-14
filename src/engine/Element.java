/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class Element {
    protected ArrayList<Integer> nodesList;

    public ArrayList<Integer> getNodesList() {
        return nodesList;
    }
    protected Mesh mesh;

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Element(ArrayList<Integer> nodesList) {
        this.nodesList = nodesList;
    }
     
}
