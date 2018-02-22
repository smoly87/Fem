/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elemfunc.d2;

import engine.ElemFuncType;
import engine.Element;
import engine.Mesh;
import engine.Vector;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.BiFunction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrey
 */
public class LinUniformTriangleTest {
    protected  Element elem;
    protected Mesh mesh;
    protected LinUniformTriangle elemFunc;
    protected double delta = 0.0001;
    
    public LinUniformTriangleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
         elem = createElem();
         elemFunc = new LinUniformTriangle(elem);
    }
    
    @After
    public void tearDown() {
    }

   protected Element createElem(){ 
       ArrayList<Vector> points = new ArrayList<>();
       points.add(new Vector(new double[]{0.0, 0.0, 0.0}));
       points.add(new Vector(new double[]{1.0, 0.0, 0.0}));
       points.add(new Vector(new double[]{1.0, 1.0, 0.0}));

       ArrayList<Integer> nodesList = new ArrayList<>();
       nodesList.add(0);
       nodesList.add(1);
       nodesList.add(2);
       
       mesh = new Mesh(points);
       Element elem = new Element(mesh, nodesList);
       return elem;
   }
    /**
     * Test of F method, of class LinUniformTriangle.
     */
    @Test
    public void testF() {
        System.out.println("F");
        
        
        double expResult = 1.0;
        
        
        double result = elemFunc.F(mesh.getPoints().get(0).getCoordinates(), 0);
        assertEquals(expResult, result, delta);
        
        result = elemFunc.F(mesh.getPoints().get(1).getCoordinates(), 1);
        assertEquals(expResult, result, delta);
        
        result = elemFunc.F(mesh.getPoints().get(2).getCoordinates(), 2);
        assertEquals(expResult, result, delta);
    }

    @Test
    public void testDet() {
       double result = elemFunc.countDet(elem);
       assertEquals(1.0, result, delta);
    }
    
}
