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
    public void testFA() {
        System.out.println("F");
        
        
        double expResult = 1.0;
        
        
        double result = elemFunc.FA(mesh.getPoints().get(0).getCoordinates(), 0);
        assertEquals(expResult, result, delta);
        
        result = elemFunc.FA(mesh.getPoints().get(1).getCoordinates(), 1);
        assertEquals(expResult, result, delta);
        
        result = elemFunc.FA(mesh.getPoints().get(2).getCoordinates(), 2);
        assertEquals(expResult, result, delta);
    }
 @Test
    public void testF() {
       double  S = 0.5; 
       double result = elemFunc.integrate(ElemFuncType.F, ElemFuncType.F, 0, 1);
       double expValue = S /12;
       assertEquals(expValue, result, delta);
       
       result = elemFunc.integrate(ElemFuncType.dFdx, ElemFuncType.dFdx, 0, 0);
       expValue = S;
       assertEquals(expValue, result, delta);
       /*double XM = (elemFunc.p1[0] + elemFunc.p2[0] + elemFunc.p3[0])/3;
       double YM = (elemFunc.p1[1] + elemFunc.p2[1] + elemFunc.p3[1])/3;*/
       
       /*double expValue = S*(elemFunc.getA()[0] + elemFunc.getB()[0] * XM + elemFunc.getG()[0] * YM);*/
       //double expValue = S /12;
       
    }

    @Test
    public void testIntegrate() {
       double result = elemFunc.integrate(ElemFuncType.I, ElemFuncType.I, 0, 0);
       assertEquals(0.5, result, delta);
    }
    
}
