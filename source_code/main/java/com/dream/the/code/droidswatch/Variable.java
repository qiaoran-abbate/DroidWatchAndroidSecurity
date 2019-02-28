package com.dream.the.code.droidswatch;


/**
 * This class is taken from https://github.com/bennapp/forwardBackwardChaining
 *
 * @author Ben Nappier ben2113
 *
 */

public class Variable{
    public String name;
    public boolean value;
    public boolean set;
    public boolean not;

    public Variable(){
        this.name = "";
        this.value = false;
        this.set = false;
    }

    public Variable(String name){
        this.name = name;
        if(this.name.equalsIgnoreCase("false")){
            this.value = false;
        }
        if(this.name.equalsIgnoreCase("true")){
            this.value = true;
        }
        this.not = false;
    }

    public Variable(String name, boolean value, boolean not){
        this.name = name;
        this.value = value;
        this.not = not;
        this.set = false;

        //if(this.name.equalsIgnoreCase("false")){
        //	this.value = false;
        //}
        //if(this.name.equalsIgnoreCase("true")){
        //	this.value = true;
        //}
    }

    public boolean getValue(){
        return not ? !value : value;
    }

    public void setValue(boolean value){
        this.value = value;
    }
    public void set(boolean set){
        this.set = set;
    }

}
