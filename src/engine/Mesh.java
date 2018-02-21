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
public class Mesh {
    protected ArrayList<Vector> points;
    protected ArrayList<Element> elements;


   

    public ArrayList<Vector> getPoints() {
        return points;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public int getNodesCount(){
        return points.size();
    }
    
    public Mesh(ArrayList<Vector> points) {
        this.points = points;
        this.elements = new ArrayList<>();

    }
    
    public void applyElemFunc(ElemFuncBuilder funcBuilder){
        for(Element elem : elements){
            elem.setElemFunc(funcBuilder.build(elem));
        }
    }
    
    public void addElement(Element elem){
        elements.add(elem);
    }
}
