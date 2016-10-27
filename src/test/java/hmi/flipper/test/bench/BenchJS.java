/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmi.flipper.test.bench;

import hmi.flipper.behaviourselection.TemplateController;
import hmi.flipper.defaultInformationstate.DefaultRecord;

/**
 *
 * @author Siewart
 */
public class BenchJS {
    long time;
    public BenchJS(String benchmarkFile){
        TemplateController controller = new TemplateController();
        DefaultRecord is = new DefaultRecord();
        is.set("is.test_run", 0);
        is.set("is.test_me", 3);
        ping();
        controller.processTemplateFile(benchmarkFile);
        pong("Process Templates");
        ping();
        controller.checkTemplates(is);
        pong("Inital Check");
        ping();
        for(int i= 0; i < 100; i++){
            controller.checkTemplates(is);
        }
        pong("10 Additional Checks");
    }
    
    private void ping(){
        time = System.nanoTime();
    }
    
    private long pong(String testName){
        long time2 = System.nanoTime();
        System.out.println(testName + " took " + (time2-time) + " nanoseconds. (" + (time2-time)/1000000000.0 + " seconds)");
        return (time2-time);
    }
    
    public static void main(String[] args){
       new BenchJS("C:\\FlipperBenchmark\\bench2.xml");
    }
}
