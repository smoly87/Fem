/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elemfunc.d1.quad;

import engine.ElemFunc;
import engine.ElemFuncType;
import engine.Element;
import engine.Mesh;
import engine.Vector;
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
public class QuadNTest {
    protected  Element elem;
    protected Mesh mesh;
    protected ElemFunc elemFunc;
    protected ArrayList<Vector> points = new ArrayList<>();
    protected double delta;
    
    public QuadNTest() {
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
        elemFunc = new QuadNBuilder().build(elem);
    }
    
    @After
    public void tearDown() {
    }
    protected Element createElem(){ 
       points = new ArrayList<>();
       points.add(new Vector(new double[]{0.0, 0.0, 0.0}));
       points.add(new Vector(new double[]{0.5, 0.0, 0.0}));
       points.add(new Vector(new double[]{1.0, 0.0, 0.0}));

       ArrayList<Integer> nodesList = new ArrayList<>();
       nodesList.add(0);
       nodesList.add(1);
       nodesList.add(2);
       
       mesh = new Mesh(points);
       Element elem = new Element(mesh, nodesList);
       return elem;
   }
    /**
     * Test of F method, of class QuadN.
     */
    @Test
    public void testF() {
       
        for(int i = 0; i < 3; i++){
             double result = elemFunc.FA(points.get(i).getCoordinates(), i);
             assertEquals(1.0, result, delta);
        }
      
        // TODO review the generated test code and remove the default call to fail.
      
    }
    
    @Test
    public void testIntegrate() {
        double result = elemFunc.integrate(ElemFuncType.I, ElemFuncType.I, 0, 0);
        assertEquals(1.0, result, delta);
    }
    
    
}
