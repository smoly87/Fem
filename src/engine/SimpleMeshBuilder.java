/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import elemfunc.d1.Element1d;
import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class SimpleMeshBuilder {
    public static Mesh create1dLineMesh(int elemNum){
       // Mesh Mesh = new Mesh();
       int N = elemNum + 1;
       ArrayList<Vector> points = new ArrayList<>(N);
       ArrayList<Element> elements = new ArrayList<>(elemNum);
               
       double h = 1/(double)N;
       for(int i = 0; i < N; i++){
           points.add(new Vector(new double[]{h*i}));
       }
       
       for(int i = 0; i < elemNum; i++){
           ArrayList<Integer> nodesList = new ArrayList<>(2);
           
           nodesList.add(i);
           nodesList.add(i + 1);
           
           Element el = new Element1d(nodesList, h);
           elements.add(el);
       }
       
       Mesh mesh = new Mesh(points, elements);
       mesh.setNodesCount(N);
       
       return mesh;
    }
}
