/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.meshloader;

import engine.Element;
import engine.Mesh;
import engine.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class MeshLoaderGmsh extends MeshLoader{
    protected ArrayList<Vector> points;
    protected ArrayList<Element> elements;
    protected ArrayList<Integer> convHullIndexes;

    public ArrayList<Integer> getConvHullIndexes() {
        return convHullIndexes;
    }
    
    protected boolean useNumerator;
    
    protected Mesh mesh;
    
    public MeshLoaderGmsh(boolean useNumerator) {
        super();
        this.useNumerator = useNumerator;
    }
    
    protected enum GMSHMode{
        NODES, ELEMENTS, CONV_HULL, NONE
    }
    @Override
    public Mesh loadMesh(String fileName) {
        
         GMSHMode sectionMode = GMSHMode.NONE;
         String path = MeshLoaderGmsh.class.getResource(fileName).getPath();
         try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String curLine;
            
            while (( curLine = br.readLine()) != null) {
                if(curLine.startsWith("$")){
                    sectionMode = selectMode(curLine, br);
                    continue;
                }

                switch(sectionMode){
                    case NODES:
                        points.add(parsePoint(curLine));
                        break;
                    case ELEMENTS:
                        elements.add(parseElement(curLine));
                        break;
                    case CONV_HULL:
                        convHullIndexes.add(Integer.parseInt(curLine));
                        break;
                }
                
            }
         }catch(IOException ex){
             return null;
         }
         

         return mesh;
    }
    
    protected GMSHMode selectMode(String curLine, BufferedReader br) throws IOException{
        GMSHMode sectionMode;
        switch (curLine) {
            case "$Nodes":
                sectionMode = GMSHMode.NODES;
                int nodesCount = Integer.parseInt(br.readLine());
                points = new ArrayList<>(nodesCount);
                break;
            case "$Elements":
                sectionMode = GMSHMode.ELEMENTS;
                int elemCount = Integer.parseInt(br.readLine());
                elements = new ArrayList<>(elemCount);
                mesh = new Mesh(points);
                break;
            case "$ConvHull":
                sectionMode = GMSHMode.CONV_HULL;
                int convCount = Integer.parseInt(br.readLine());
                convHullIndexes = new ArrayList<>(convCount);
                break;    
           default:
                sectionMode = GMSHMode.NONE;

                break;
        }
        return sectionMode;
    }
    
    protected Element parseElement(String elemString){
       String[] coordsStrPart = elemString.split(" ");
       int N = coordsStrPart.length;
       int offset = 0;
       if(useNumerator) {
           N--;
           offset = 1;
       }
       
       ArrayList<Integer>nodesList = new ArrayList<>(N);
       
       for(int i = offset; i < coordsStrPart.length; i++){
          nodesList.add(Integer.parseInt(coordsStrPart[i])) ;
       }
       Element elem  = new Element(mesh, nodesList);
       return elem;
    }
    
    protected Vector parsePoint(String pointCoordsStr){
       String[] coordsStrPart = pointCoordsStr.split(" ");
       int N = coordsStrPart.length;
       int offset = 0;
       if(useNumerator) {
           N--;
           offset = 1;
       }
       double[] coordinates = new double[N];
       
       for(int i = offset; i < coordsStrPart.length; i++){
           coordinates[i] = Double.parseDouble(coordsStrPart[i]);
       }
       
       return new Vector(coordinates);
    }
    
}
