/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import engine.meshloader.MeshLoaderGmsh;
import engine.utils.common.MathUtils;
import tasks.heatEquation1d;
import tasks.membrane.ReferenceWave2dSolution;
import tasks.membrane.waveEquation2d;
import tasks.phiEquation1d1;
import tasks.waveequation.ReferenceWaveSolution;
import tasks.waveequation.SolutionPrecisionTester;
import tasks.waveequation.waveEquation1d;

/**
 *
 * @author Andrey
 */
public class PolygonOfExperiments {
    protected double[][] getRefereneSolution(int spatSteps, int timeSteps){
        ReferenceWaveSolution refSol = new ReferenceWaveSolution();
        
        double[] TRange = MathUtils.linSpace(0, 1, timeSteps);
        double[] XRange = MathUtils.linSpace(0, 1, spatSteps);
        
        double[][] XRef =  refSol.getReferenceSolution(TRange, XRange, 1);
        
        return XRef;
    }
    protected double[][] getRefereneSolutionEx(int spatSteps, int timeSteps){
        double[][] XRef = getRefereneSolution(spatSteps, timeSteps);
        int N2 = XRef[0].length;
        double[][] XRefW0 = new double[XRef.length - 1][N2];
        int N = XRef.length - 1;
        System.arraycopy(XRef, 1, XRefW0, 0, N);
        return XRefW0;
    }
    
    
    public void test(){
        //loadMesh();
       // testHeatEq();
       testWaveEq();
       /*testWaveEq1dAnalytics();*/
       // testPhiEq();
       
  /* testWaveEq2d();
        testWaveEq2dAnalytics();*/
    }
    
    public void loadMesh(){
        MeshLoaderGmsh meshLoader = new MeshLoaderGmsh(false);
        Mesh mesh = meshLoader.loadMesh("/assets/test.msh");
        System.out.println(mesh.getNodesCount());
    }
    
    public void testWaveEq(){
        System.out.println("Test 1d wave equation");
        int spatElems= 20;
        int timeElems = 20;
        
        waveEquation1d waveTask = new waveEquation1d(spatElems, timeElems);
        
        long ms = System.nanoTime();
        double[][] X = waveTask.solveFDM();
        System.out.println((System.nanoTime() - ms)/1000000000.0);
        
        int timeNodes = waveTask.getTimeSolver().getTimeMesh().getNodesCount();
        double[][] XRef = getRefereneSolutionEx(waveTask.getMesh().getNodesCount(), timeNodes);
        
        double eps = SolutionPrecisionTester.getEnergyCriteriaPrecision(X, XRef, true);
        System.out.printf("Precision is %f", eps );
        System.out.println("Max value is " + MathUtils.arrMax(X));
        
        eps = SolutionPrecisionTester.getSummaryEnergyPrecision(X, XRef, false);
        System.out.printf("Full energy precision is %f", eps );
        
    }
     public void testHeatEq(){
        System.out.println("Test 1d heat equation");
        int spatElems= 10;
        
        
         heatEquation1d waveTask = new heatEquation1d(spatElems);
        
        long ms = System.nanoTime();
        double[] X = waveTask.solve();
        System.out.println(X);
        
    }
     public void testPhiEq(){
        System.out.println("Test 1d phi equation");
        int spatElems= 1;
 
        phiEquation1d1 task = new phiEquation1d1(spatElems);
        
      
        double[] X = task.solve();
        
        Mesh mesh = task.getMesh();
        
        double x = 1.0/3.0;
        int elemNum = mesh.findElement(x);
        Element elem = mesh.getElements().get(elemNum);
       
        System.out.println(elem.F(X, new double[]{x}));
    }
     
    public void testWaveEq1dAnalytics(){
        System.out.println("Test 1d wave equation Analytics Solution");
        int spatElems= 40;
        int timeElems = 40;
        
        waveEquation1d waveTask = new waveEquation1d(spatElems, timeElems);
        
        long ms = System.nanoTime();
        double[][] X = waveTask.solveAnalytics();
        System.out.println((System.nanoTime() - ms)/1000000000.0);
        
        //double[][] XRef = getRefereneSolutionEx(spatElems, timeElems);
        
         double[][] XRefW0 = getRefereneSolutionEx(spatElems, timeElems);
        double eps = SolutionPrecisionTester.getEnergyCriteriaPrecision(X, XRefW0, true);
        System.out.printf("Mean energy precision is %f", eps );
        
        eps = SolutionPrecisionTester.getSummaryEnergyPrecision(X, XRefW0, false);
        System.out.printf("Full energy precision is %f", eps );
        
        
    }
    
    protected double[][] getRefereneSolution2d(Mesh mesh, int timeSteps){
        ReferenceWave2dSolution refSol = new ReferenceWave2dSolution();
        
        double[] TRange = MathUtils.linSpace(0, 1, timeSteps+1);
        double[][] XRef = refSol.getReferenceSolution(TRange, mesh.getPoints(), 1);
        
        return XRef;
    }
     
    public void testWaveEq2d(){
        System.out.println("Test 2d wave equation");
        int spatElems= 20;
        int timeElems = 20;
        
        waveEquation2d waveTask = new waveEquation2d(spatElems, timeElems);
     
        
        long ms = System.nanoTime();
        double[][] X = waveTask.solve();
        System.out.println((System.nanoTime() - ms)/1000000000.0); 
        double[][] XRef = getRefereneSolution2d(waveTask.getMesh(), timeElems);
        double eps = SolutionPrecisionTester.getEnergyCriteriaPrecision(X, XRef, true);
        System.out.printf("Precision is %f", eps );
        
        System.out.println("Max value is " + MathUtils.arrMax(X));
        eps = SolutionPrecisionTester.getSummaryEnergyPrecision(X, XRef, false);
        System.out.printf("Full energy precision is %f", eps );
      }
    
       public void testWaveEq2dAnalytics(){
        System.out.println("Test 2d wave equation analytics");
        int spatElems= 20;
        int timeElems = 20;
        
        waveEquation2d waveTask = new waveEquation2d(spatElems, timeElems);
     
        
        long ms = System.nanoTime();
        double[][] X = waveTask.solveAnalytics();
        System.out.println((System.nanoTime() - ms)/1000000000.0); 
        double[][] XRef = getRefereneSolution2d(waveTask.getMesh(), timeElems);
       
        int N2 =  XRef[0].length;
        double[][] XRefW0 = new double[XRef.length - 1][N2];
        int N = XRef.length - 1;
        System.arraycopy(XRef,1, XRefW0, 0, N);
        
        double eps = SolutionPrecisionTester.getEnergyCriteriaPrecision(X, XRefW0, true);
        System.out.printf("Precision is %f", eps );
        System.out.println("Max value is " + MathUtils.arrMax(X));
        eps = SolutionPrecisionTester.getSummaryEnergyPrecision(X, XRefW0, false);
        System.out.printf("Full energy precision is %f", eps );
      }
}
