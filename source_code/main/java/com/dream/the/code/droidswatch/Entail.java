package com.dream.the.code.droidswatch;

import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class is taken from https://github.com/bennapp/forwardBackwardChaining
 *
 * @author Ben Nappier ben2113
 *
 */

public class Entail{
    public static BufferedReader br;
    public static String currentLine;
    public static String sentencesS = "";
    public static String[] sentencesA;
    public static LinkedList<String> facts = new LinkedList<String>();
    public static Hashtable<String, Boolean> factsTable = new Hashtable<String, Boolean>();
    public static LinkedList<String> notfacts = new LinkedList<String>();
    public static LinkedList<Sentence> sentences = new LinkedList<Sentence>();
    public static LinkedList<String> agenda = new LinkedList<String>();
    public static Hashtable<String, Boolean> entailed = new Hashtable<String, Boolean>();
    public static Hashtable<String, Boolean> setSymbol = new Hashtable<String, Boolean>();
    public static Hashtable<String, Boolean> valSymbol = new Hashtable<String, Boolean>();
    public static LinkedList<String> toPush = new LinkedList<String>();
    public static LinkedList<String> bcSentence = new LinkedList<String>();
    public static LinkedList<String> bcFacts = new LinkedList<String>();



    public static void print(Object stringOrMore){
        System.out.println(stringOrMore);
    }

    public static boolean notOp(String check){
        return ((!check.equals("^")) && (!check.equals("v")) && (!check.equals("=>")));
    }

    public static boolean notOpAtAll(String check){
        return ((!check.contains("^")) && (!check.contains("v")) && (!check.contains("=>")));
    }


    public static void addSentences(LinkedList<String> sentencesList){
        Iterator<String> sentenceIt = sentencesList.iterator();
        while(sentenceIt.hasNext()){
            String sent = sentenceIt.next();
            // print(sent);
            LinkedList<Operator> ops = new LinkedList<Operator>();
            if(notOpAtAll(sent)){
                if(sent.charAt(0)=='~'){
                    notfacts.add(sent);
                    factsTable.put(sent, true);
                }else{
                    facts.add(sent);
                    factsTable.put(sent, true);
                }
            } else {
                String[] tokens = sent.split(" ");
                Variable[] variables = new Variable[tokens.length];
                for(int j=0;j<tokens.length;j++){
                    if(notOp(tokens[j])){
                        if(tokens[j].charAt(0) == '~'){
                            variables[j] = new Variable(tokens[j], false, true);
                            if(tokens[j].equals("false")){
                                variables[j] = new Variable("true");
                            }
                            if(tokens[j].equals("true")){
                                variables[j] = new Variable("false");
                            }
                        }else{
                            variables[j] = new Variable(tokens[j], false, false);
                            if(tokens[j].equals("false")){
                                variables[j] = new Variable("false");
                            }
                            if(tokens[j].equals("true")){
                                variables[j] = new Variable("true");
                            }
                        }
                    }
                }
                for(int j=0;j<tokens.length;j++){
                    if(tokens[j].equals("^")){
                        ops.add(new And(variables[j-1], variables[j+1]));
                    }
                    if(tokens[j].equals("v")){
                        ops.add(new Or(variables[j-1], variables[j+1]));
                    }
                    if(tokens[j].equals("=>")){
                        ops.add(new Imp(variables[j-1], variables[j+1]));
                    }
                }
                sentences.add(new Sentence(ops, false));
            }
        }
    }
    public static void deParenSentence(String sentence, int depth, LinkedList<String> toPush, String build, boolean ready){
        if(sentence.length()>0 && sentence.charAt(0)=='('){
            deParenSentence(sentence.substring(1), (depth +1), toPush, build, true);
        } else if(sentence.length()>0 && sentence.charAt(0)==')'){
            deParenSentence(sentence.substring(1), (depth -1), toPush, build, true);
        } else if(depth == 0 && ready){
            String rest = "";
            String[] splitC = sentence.split(" \\^ ");
            if(splitC.length > 1){
                int i;
                for (i =1;i<splitC.length-1;i++){
                    rest += splitC[i]+" ^ ";
                }
                rest += splitC[i];
                toPush.push(build);
                deParenSentence(rest, depth, toPush, "", false);
            } else{
                toPush.push(build);
            }
        } else {
            build += sentence.charAt(0);
            deParenSentence(sentence.substring(1), depth, toPush, build, true);
        }
    }
    public static LinkedList<String> removeParens(String[] sentencesA){
        LinkedList<String> sentenceList = new LinkedList<String>();
        for(int i=0;i<sentencesA.length;i++){
            if(sentencesA[i].contains("(")){
                toPush.clear();
                deParenSentence(sentencesA[i], 0, toPush, "", false);
                Iterator<String> toPushi = toPush.iterator();
                while(toPushi.hasNext()){
                    sentenceList.push(toPushi.next());
                }
            } else {
                sentenceList.push(sentencesA[i]);
            }
        }
        return sentenceList;
    }

    public boolean ruleEngineProcess(String allRules,String QueryTobeFound){
        /*if(args.length != 3){
            print("Invalid arguments");
            print("Try:");
            print("$./entail <algorithm> <kBFile> <querySymbol>");
            print("eg. backward KB.txt Q");
            print("eg. forward KB.txt L");
            System.exit(1);
        }*/

        try{
            //String alg = args[0];
            String alg = "forward";
            //String kBFile = args[1];
            String kBFile = allRules;
            String querySymbol = QueryTobeFound;

            //br = new BufferedReader(new FileReader(kBFile));
            //while ((currentLine = br.readLine()) != null) {
            //     sentencesS += currentLine.trim().replaceAll(" +", " ") + "@";
            //}
            sentencesS = kBFile;
            sentencesA = sentencesS.split("@");
            LinkedList<String> sentenceList = new LinkedList<String>();
            sentenceList = removeParens(sentencesA);
            addSentences(sentenceList);

            //convert the sentences to implicative form
            LinkedList<Sentence> toRemove = new LinkedList<Sentence>();
            LinkedList<Sentence> toAdd = new LinkedList<Sentence>();
            Iterator<Sentence> i = sentences.iterator();
            while(i.hasNext()){
                Sentence temp = i.next();
                if(temp.opList.getLast().token.equals("=>")){
                    temp.impForm = true;
                }

                Variable head = null;
                Operator opHead = null;
                Operator opHeadFirst = null;
                boolean allOr = true;
                boolean noHead = true;
                Iterator<Operator> j = temp.opList.iterator();
                while(j.hasNext()){
                    Operator tempOp = j.next();
                    if(!tempOp.token.equals("v")){
                        allOr = false;
                    }
                    if(!(tempOp.first.not)){
                        head = tempOp.first;
                        opHeadFirst = tempOp;
                        noHead = false;
                    }
                    if(!tempOp.second.not){
                        head = tempOp.second;
                        opHead = tempOp;
                        noHead = false;
                    }
                }

                if(noHead){
                    Or addFalse = new Or(temp.opList.getLast().second, new Variable("false"));
                    opHead = addFalse;
                    temp.opList.add(addFalse);
                }

                //convert to imp
                Iterator<Operator> k = temp.opList.iterator();
                if(allOr){
                    Sentence impSentence = new Sentence();

                    Variable impFirst = new Variable();
                    Variable impSecond = new Variable();
                    Operator impOp = new And();

                    LinkedList<Operator> impOps = new LinkedList<Operator>();
                    toRemove.add(temp);
                    while(k.hasNext()){
                        Operator tempOp = k.next();
                        if(tempOp != opHead){
                            impFirst = new Variable();
                            impSecond = new Variable();

                            impFirst.name = tempOp.first.name.substring(1);
                            impFirst.value = tempOp.first.value;
                            impFirst.set = tempOp.first.set;
                            impFirst.not = false;
                            //
                            impSecond.name = tempOp.second.name.substring(1);
                            impSecond.value = tempOp.second.value;
                            impSecond.set = tempOp.second.set;
                            impSecond.not = false;

                            impOp = new And(impFirst, impSecond);
                            impOp.token = "^";
                            if(impOp.first.name.equals("")){
                            } else {
                                impOps.add(impOp);
                            }

                        } else{
                            if(opHeadFirst != null){
                                impFirst = new Variable();
                                impSecond = new Variable();
                                impFirst.name = tempOp.first.name.substring(1);
                                impFirst.value = tempOp.first.value;
                                impFirst.set = tempOp.first.set;
                                impFirst.not = false;
                                impSecond.name = opHeadFirst.second.name.substring(1);
                                impSecond.value = opHeadFirst.second.value;
                                impSecond.set = opHeadFirst.second.set;
                                impSecond.not = false;
                                impOp = new And(impFirst, impSecond);
                                impOp.token = "^";
                                impOps.add(impOp);
                            }
                        }
                    }

                    impSentence.opList = impOps;

                    if(noHead){
                        impFirst = impOps.getLast().second;
                        Operator nImp = new Imp(impFirst, new Variable("false"));
                        impOps.add(nImp);
                        impSentence.impForm = true;
                    } else {
                        impFirst = impOps.getLast().second;
                        impSecond = head;
                        Operator nImp = new Imp(impFirst, impSecond);
                        impOps.add(nImp);
                        impSentence.impForm = true;
                    }
                    toAdd.add(impSentence);
                }
            }
            Iterator<Sentence> removeSentences = toRemove.iterator();
            while(removeSentences.hasNext()){
                Sentence remove = removeSentences.next();
                sentences.remove(remove);
            }
            Iterator<Sentence> addSentences = toAdd.iterator();
            while(addSentences.hasNext()){
                Sentence add = addSentences.next();
                sentences.add(add);
            }

            agenda = facts;
            Iterator<String> a = agenda.iterator();
            while(a.hasNext()){
                String symbol = a.next();
                setSymbol.put(symbol, true);
                valSymbol.put(symbol, true);
            }
            Iterator<String> b = notfacts.iterator();
            while(b.hasNext()){
                String symbol = b.next();
                setSymbol.put(symbol, true);
                valSymbol.put(symbol.substring(1), false);
            }

            if(alg.equals("forward")){
                Iterator<String> factsIt = facts.iterator();
                while(factsIt.hasNext()){
                    print(factsIt.next());
                }

                while(!agenda.isEmpty()){
                    LinkedList<Sentence> clauses = new LinkedList<Sentence>();
                    Iterator<Sentence> clauseInit = sentences.iterator();
                    while(clauseInit.hasNext()){
                        clauses.push(clauseInit.next());
                    }

                    String fact = agenda.pop();
                    if(!fact.equals("false")){
                        entailed.put(fact, true);
                    }
                    Iterator<Sentence> clauseI = clauses.iterator();
                    while(clauseI.hasNext()){
                        Sentence clause = clauseI.next();

                        Iterator<Operator> oi = clause.opList.iterator();
                        while(oi.hasNext()){
                            Operator operator = oi.next();
                            if(setSymbol.containsKey(operator.first.name)){
                                operator.first.set(true);
                            }
                            if(setSymbol.containsKey(operator.second.name)){
                                operator.second.set(true);
                            }
                            if(valSymbol.containsKey(operator.first.name)){
                                operator.first.setValue(valSymbol.get(operator.first.name));
                            }
                            if(valSymbol.containsKey(operator.second.name)){
                                operator.second.setValue(valSymbol.get(operator.second.name));
                            }
                        }

                        oi = clause.opList.iterator();
                        Operator headOp = null;
                        while(oi.hasNext()){
                            Operator operator = oi.next();
                            operator.op();
                            if(operator.token.equals("=>")){
                                headOp = operator;
                            }
                        }
                        if(headOp!= null){
                            if(headOp.first.getValue()){
                                setSymbol.put(headOp.second.name, true);
                                valSymbol.put(headOp.second.name, true);

                                sentences.remove(clause);
                                agenda.add(headOp.second.name);
                                printSentence(clause);
                            }

                        }
                    }
                }
                if(entailed.containsKey(querySymbol)){
                    //print("--> True");
                    return true;
                } else{
                    //print("--> False");
                    return false;
                }
                ///////////////////////////////////
            } else if(alg.equals("backward")) {
                boolean output = false;

                LinkedList<Sentence> sentenceTable = new LinkedList<Sentence>();

                Iterator<Sentence> isentences = sentences.iterator();
                while(isentences.hasNext()){
                    Sentence s = isentences.next();
                    if(s.opList.getLast().token.equals("=>")){
                        sentenceTable.push(s);
                    }
                }
                Hashtable<String, Boolean> inQ = new Hashtable<String, Boolean>();

                prove(querySymbol, sentenceTable, inQ);

                while(!bcSentence.isEmpty()){
                    print(bcSentence.pop());
                }
                while(!facts.isEmpty()){
                    String fact = facts.pop();
                    Iterator bcFactIt = bcFacts.iterator();
                    while(bcFactIt.hasNext()){
                        if(fact.equals(bcFactIt.next())){
                            print(fact);
                            break;
                        }
                    }
                }


                if(factsTable.get(querySymbol) != null){
                    //print("--> True");
                    return true;
                } else {
                    //print("--> False");
                    return false;
                }
            }

            //implement alg
            //implment alg 2
            //deal with parens

            //Converting works
            // Iterator<Sentence> test = sentences.iterator();
            // while(test.hasNext()){
            // 	Sentence testS = test.next();
            // 	Iterator<Operator> opTest = testS.opList.iterator();
            // 	String sentenceTest = "";
            // 	while(opTest.hasNext()){
            // 		Operator testOP = opTest.next();

            // 		sentenceTest += testOP.first.name + " " + testOP.token + " " + testOP.second.name + ".";
            // 	}
            // 	print(sentenceTest);
            // }

            //AND works
            //OR works?? except if it is not set but the value is true?? then it will pass on true eval
            //operating on an op changes its second var which will alter the next ops first var
            // sentences.pop();
            // sentences.pop();
            // Sentence testS = sentences.pop();
            // int i = 0;
            // while(!testS.opList.isEmpty()){
            // 	Operator temp = testS.opList.pop();
            // 	if(i==0){
            // 		temp.first.set = true;
            // 		temp.first.setValue(false);
            // 		temp.second.set = true;
            // 		temp.second.setValue(true);
            // 		print(temp.first.name);
            // 		print(temp.token);
            // 		print(temp.second.name);
            // 		temp.op();
            // 	}
            // 	if(i==1){
            // 		print(temp.first.getValue());
            // 	}
            // 	i++;
            // }

            //while(!facts.isEmpty()){//checks out
            //	print(facts.pop());
            //}

        } catch (Exception e){
            System.err.println("Try <algorithm> <knowledgeBaseFile.txt> <querySybol>");
            e.printStackTrace();
        }
        return false;
    }

    public static void prove(String q, LinkedList<Sentence> sList, Hashtable<String, Boolean> inQ){
        Iterator<Sentence> sentIt = sList.iterator();
        LinkedList<Sentence> inQuestionList = new LinkedList<Sentence>();
        while(sentIt.hasNext()){
            Sentence s = sentIt.next();
            if(s.opList.getLast().second.name.equals(q)){
                inQuestionList.add(s);
            }
        }
        Iterator<Sentence> inQuestionListIt = inQuestionList.iterator();
        while(inQuestionListIt.hasNext()){
            Sentence inQuestion = inQuestionListIt.next();

            LinkedList<Sentence> sListNext = new LinkedList<Sentence>();
            Iterator<Sentence> sentItNext = sList.iterator();
            while(sentItNext.hasNext()){
                Sentence next = sentItNext.next();
                if(inQuestion != next){
                    sListNext.add(next);
                }
            }
            if(inQuestion != null){
                boolean bcContain = false;
                Iterator<String> bcIt = bcSentence.iterator();
                while(bcIt.hasNext()){
                    if(sentenceToString(inQuestion).equals(bcIt.next())){
                        bcContain = true;
                    }
                }
                if(!bcContain){
                    bcSentence.add(sentenceToString(inQuestion));
                }
                inQ.put(inQuestion.opList.getLast().second.name, true);
                Iterator<Operator> opIt = inQuestion.opList.iterator();
                while(opIt.hasNext()){
                    String operatorName = opIt.next().first.name;
                    if(factsTable.get(operatorName) != null){
                        bcFacts.add(operatorName);
                    }
                    if(inQ.get(operatorName) != null){
                        if(inQ.get(operatorName)){
                            break;
                        }
                    } else {
                        prove(operatorName, sListNext, inQ);
                    }
                }
                boolean allFacts = true;
                Iterator<Operator> opItFacts = inQuestion.opList.iterator();
                while(opItFacts.hasNext()){
                    String operatorNameFacts = opItFacts.next().first.name;
                    if(factsTable.get(operatorNameFacts) == null){
                        allFacts = false;
                    }
                }
                if(allFacts){
                    inQ.remove(q);
                    factsTable.put(q, true);
                }
            }
        }
    }

    public static void printSentence(Sentence clause){
        Iterator<Operator> opTest = clause.opList.iterator();
        String sentenceTest = "";
        int i = 0;
        while(opTest.hasNext()){
            Operator testOP = opTest.next();
            if(i == 0){
                sentenceTest += testOP.first.name + " " + testOP.token + " " + testOP.second.name;
            } else {
                sentenceTest += " " + testOP.token + " " + testOP.second.name;
            }
            i++;
        }
        print(sentenceTest);
    }
    public static String sentenceToString(Sentence clause){
        Iterator<Operator> opTest = clause.opList.iterator();
        String sentenceTest = "";
        int i = 0;
        while(opTest.hasNext()){
            Operator testOP = opTest.next();
            if(i == 0){
                sentenceTest += testOP.first.name + " " + testOP.token + " " + testOP.second.name;
            } else {
                sentenceTest += " " + testOP.token + " " + testOP.second.name;
            }
            i++;
        }
        return sentenceTest;
    }
}
