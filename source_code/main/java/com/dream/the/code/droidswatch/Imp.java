package com.dream.the.code.droidswatch;

/**
 * This class is taken from https://github.com/bennapp/forwardBackwardChaining
 *
 * @author Ben Nappier ben2113
 *
 */

public class Imp extends Operator{
    public Imp(Variable first, Variable second){
        this.first = first;
        this.second = second;
        this.token = "=>";
    }

    public void op(){
        // System.out.println("um what?");

    }
}
