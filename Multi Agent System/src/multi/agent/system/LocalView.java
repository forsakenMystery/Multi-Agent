/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multi.agent.system;

/**
 *
 * @author Hamed Khashehchi
 */
class LocalView {

    private String color;
    private Agent agent;

    public LocalView(String color, Agent agent) {
        this.color = color;
        this.agent = agent;
    }

    public Agent getAgent() {
        return agent;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return this.getColor() + "," + this.agent.hashCode();
    }

}
