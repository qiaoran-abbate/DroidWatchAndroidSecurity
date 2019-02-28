package com.dream.the.code.droidswatch;


/**
 * This class is taken from https://github.com/bennapp/forwardBackwardChaining
 *
 * @author Ben Nappier ben2113
 *
 */

public abstract class Operator{
    public Variable first;
    public Variable second;
    public String token;

    abstract void op();
}
