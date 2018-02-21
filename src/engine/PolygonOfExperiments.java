/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import engine.meshloader.MeshLoaderGmsh;
import engine.utils.common.MathUtils;
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
        
        double[] TRange = MathUtils.linSpace(0, 1, timeSteps+1);
        double[] XRange = MathUtils.linSpace(0, 1, spatSteps+1);
        
        double[][] XRef =  refSol.getReferenceSolution(TRange, XRange, 1);
        
        return XRef;
    }
    
    
    public void test(){
        //loadMesh();
        testWaveEq();
    }
    
    public void loadMesh(){
        MeshLoaderGmsh meshLoader = new MeshLoaderGmsh(false);
        Mesh mesh = meshLoader.loadMesh("/assets/test.msh");
        System.out.println(mesh.getNodesCount());
    }
    
    public void testWaveEq(){
        
        int spatElems= 10;
        int timeElems = 100;
        
        waveEquation1d waveTask = new waveEquation1d(spatElems, timeElems);
        
        long ms = System.nanoTime();
        double[][] X = waveTask.solve();
        System.out.println((System.nanoTime() - ms)/1000000000.0);
        
        double[][] XRef = getRefereneSolution(spatElems, timeElems);
        
        double eps = SolutionPrecisionTester.getEnergyCriteriaPrecision(X, XRef, true);
        System.out.printf("Precision is %f", eps );
        
    }
}
