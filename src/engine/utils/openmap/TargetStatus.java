/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils.openmap;

import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class TargetStatus {
    protected ArrayList<Integer> boundIndexes;
    protected int targetValue;
    protected int targetInd;
    protected int offset;
    protected boolean statusChanged;

    
    public TargetStatus(ArrayList<Integer> boundIndexes) {
        this.boundIndexes = boundIndexes;
        targetValue = boundIndexes.get(0);
    }
    
    public TargetCompareResult compare(int value){
        if(value == targetValue){
            
            statusChanged = true;
            if(targetInd == boundIndexes.size() - 1){
                offset = boundIndexes.size()-1;
                return TargetCompareResult.AllTagetsReached;
            } else{
               targetInd++;
               targetValue = boundIndexes.get(targetInd);
               offset = targetInd - 1;
            }
            return TargetCompareResult.TargetReached;
        } else{
            if(statusChanged && value != boundIndexes.get(targetInd - 1)){
                offset++;
                statusChanged = false;
            }
            return TargetCompareResult.OrdinaryItem;
        }
    }

    public int getOffset() {
        return offset;
    }
    
    public void reset(){
        targetValue = boundIndexes.get(0);
        targetInd = 0;
        offset = 0;
        statusChanged = false;
    }
}
