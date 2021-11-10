/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multi.agent.system;

import java.io.IOException;

/**
 *
 * @author Hamed Khashehchi
 */
public class program {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Agent n1 = new Agent(1);
        Agent n2 = new Agent(2);
        Agent n3 = new Agent(3);
        n1.addNeighbors(n3);
        n1.addNeighbors(n2);
        n1.addColor("red");
        n1.addColor("black");
        n3.addColor("black");
        n2.addColor("red");
        n2.addColor("black");
        System.out.println("n3 = " + n3);
        System.out.println("n2 = " + n2);
        System.out.println("n1 = " + n1);
        n1.start();
        n2.start();
        n3.start();
        
    }
    
}
