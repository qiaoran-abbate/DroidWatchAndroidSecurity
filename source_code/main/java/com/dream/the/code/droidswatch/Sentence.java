package com.dream.the.code.droidswatch;

import java.util.LinkedList;

/**
 * This class is taken from https://github.com/bennapp/forwardBackwardChaining
 *
 * @author Ben Nappier ben2113
 *
 */

public class Sentence{
    public LinkedList<Operator> opList;
    public boolean impForm;

    public Sentence(LinkedList<Operator> opList, boolean impForm){
        this.opList = opList;
        this.impForm = impForm;
    }

    public Sentence(){
        this.opList = null;
        this.impForm = false;
    }

}
