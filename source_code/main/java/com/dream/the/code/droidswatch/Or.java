package com.dream.the.code.droidswatch;


/**
 * This class is taken from https://github.com/bennapp/forwardBackwardChaining
 *
 * @author Ben Nappier ben2113
 *
 */

public class Or extends Operator{
   public Or(Variable first, Variable second){
      this.first = first;
      this.second = second;
      this.token = "v";
   }

   public void op(){
      if(this.first.set){
         if(this.first.getValue()){
            this.second.set(true);
            this.second.setValue(true);
         }
      }
      if(this.second.set){
         if(this.second.getValue()){
            this.second.set(true); //redundant
         }
      }else{
         //this.second.set(false);
      }
   }
}