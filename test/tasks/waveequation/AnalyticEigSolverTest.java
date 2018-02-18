/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.waveequation;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
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
public class AnalyticEigSolverTest {
    
    public AnalyticEigSolverTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

   
    @Test
    public void testSolveOdeSecondOrder() {
        System.out.println("solveOdeSecondOrder");
        RealMatrix A =  new Array2DRowRealMatrix(new double[][]
         {
            {-2.0, 1.0},  
            {1.0   -2.0}
          }
        );
        RealMatrix C = null;
        RealVector Y0 = null;
        AnalyticEigSolver instance = new AnalyticEigSolver();
        EigSolution expResult = null;
        EigSolution result = instance.solveOdeSecondOrder(A, C, Y0);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
