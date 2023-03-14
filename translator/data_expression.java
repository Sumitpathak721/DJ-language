package translator;

import java.util.ArrayList;
import java.util.HashMap;

public class data_expression {
    static expression_token evaluate(String text,Data_token Variable_bucket,Function_token Function_bucket){
        

        ArrayList<Token> values = new ArrayList<Token>();
        ArrayList<Token> ops = new ArrayList<Token>();

        // Precidence for operation

        HashMap<String, Integer> precidence = new HashMap<String, Integer>();
        //logical operation:->
        precidence.put("||",1);
        precidence.put("&&",2);
        precidence.put("<", 4);
        precidence.put(">", 4);
        precidence.put(">=",4);
        precidence.put("<=",4);
        precidence.put("!=",5);
        precidence.put("==",5);

        //Arithemeatic operation
        precidence.put("+", 6);
        precidence.put("-", 6);
        precidence.put("*", 7);
        precidence.put("/", 7);
        precidence.put("**",8);


        // type and value use for store temp_variable types and values to store in
                // values and ops list
        String expression_type = "";
        String expression_value = "";
        /****Iteration variable*****/

        String current_variable="";
        
        Position pos = new Position(text);
        while(pos.idx<pos.text.length()){
            // Declear String from expression
            if ("'".contains("" + pos.current_char) && expression_value.length() == 0) {
                expression_type = "String";
                pos.IncrementChar();
                Boolean open_string = true;
                while (pos.idx < text.length() && open_string) {
                    
                    if ("'".contains("" + pos.current_char)) {
                        open_string = false;

                    } else {
                        expression_value += pos.current_char;
                        pos.IncrementChar();
                    }
                }
                if (open_string) {
                    expression_token Error_result = new expression_token(new Error(new Token("Operational Error","Missing ' in string")));
                    return Error_result;
                    
                } else {
                    values.add(new Token(expression_type, expression_value));
                    expression_type = "";
                    expression_value = "";
                }
                pos.IncrementChar();//"'"->next char
            }else if (" \n\t".contains("" + pos.current_char)) {
                pos.IncrementChar();
            } else if (CONSTANTS.DIGITS.contains("" + pos.current_char) || (pos.current_char=='-' && (values.size()==0 || ops.size()>0 && ops.get(ops.size()-1).type.equals("(")) && CONSTANTS.DIGITS.contains(""+pos.text.charAt(pos.idx+1))) ) {
                // declear float and int number
                Boolean dotCount = false;

                while (pos.idx < pos.text.length()) {
                    
                    if (pos.current_char == '.') {
                        if (dotCount == false) {
                            dotCount = true;
                            expression_value += pos.current_char;
                        } else {
                            // throw error of number don't have more then one '.'
                            return new expression_token(new Error(new Token("InvaildNumber","Invaild data type with more then one '.' symbol")));
                        }
                        
                    } else if (CONSTANTS.DIGITS.contains("" + pos.current_char) || (pos.current_char=='-' && expression_value.length()==0 && (values.size()==0 || ops.size()>0 && ops.get(ops.size()-1).type.equals("(")) && CONSTANTS.DIGITS.contains(""+pos.text.charAt(pos.idx+1)))) {
                        expression_value += pos.current_char;
                    }else {
                        break;
                    }
                    pos.IncrementChar();
                }
                if (dotCount) {
                    expression_type = "float";
                }else {
                    expression_type = "int";
                }
                values.add(new Token(expression_type, expression_value));
                expression_value = "";
                expression_type = "";
            } else if ('(' == pos.current_char) {
                ops.add(new Token("" + pos.current_char));
                pos.IncrementChar();
            } else if (pos.current_char == ')') {
                while (ops.size() != 0 && !ops.get(ops.size() - 1).type.equals("(")) {
                    Token val2 = values.get(values.size() - 1);
                    values.remove(values.size() - 1);
                    Token val1 = values.get(values.size() - 1);
                    values.remove(values.size() - 1);
                    Token op = ops.get(ops.size() - 1);
                    ops.remove(ops.size() - 1);
                    expression_token result =  Operation.performOp(val1, val2, op);
                    if(result.err.isError){
                        return new expression_token(result.err);
                    }
                    values.add(result.result);
                }
                ops.remove(ops.size() - 1);
                pos.IncrementChar();
            } else if ("+-/*%><=|&!".contains("" + pos.current_char)) {
                String cur_op = "";
                while(pos.idx<pos.text.length() && CONSTANTS.OPERATIONS.contains(""+pos.current_char)){
                    cur_op+=pos.current_char;
                    pos.IncrementChar();
                }
                if(precidence.containsKey(cur_op)){
                    while (ops.size() != 0 && !ops.get(ops.size() - 1).type.equals("(") && precidence
                            .get(ops.get(ops.size() - 1).type) >= precidence.get(cur_op) && values.size()>1) {
                        Token val2 = values.get(values.size() - 1);
                        values.remove(values.size() - 1);
                        Token val1 = values.get(values.size() - 1);
                        values.remove(values.size() - 1);
                        Token op = ops.get(ops.size() - 1);
                        ops.remove(ops.size() - 1);
                        expression_token resultOP =  Operation.performOp(val1, val2, op);
                        if(resultOP.err.isError){
                            return new expression_token(resultOP.err);
                        }
                        values.add(resultOP.result);
                    }
                    ops.add(new Token(cur_op));
                    
                }else{
                    return new expression_token(new Error(new Token("UNDEFINE OPERATION",String.format("Operation '%s' is not define",cur_op))));
                }
            } else if (CONSTANTS.CHARACTERS.contains("" + pos.current_char)) {
                // in case of noting match then check for define_variable
                current_variable = "";
                while (pos.idx < pos.text.length()) {
                    
                    if (CONSTANTS.CHARACTERS.contains("" + pos.current_char) || (current_variable.length() > 0
                            && CONSTANTS.DIGITS.contains("" + pos.current_char))) {
                        current_variable += pos.current_char;
                        pos.IncrementChar();
                    } else {
                        break;
                    }
                }
                if (current_variable.equals("true") || current_variable.equals("false")) {

                    values.add(new Token("bool", current_variable));
                } else if (Variable_bucket.bucket.containsKey(current_variable)) {

                    values.add(new Token(Variable_bucket.bucket.get(current_variable).get("type"),Variable_bucket.bucket.get(current_variable).get("value")));
                }
            }else{
                return new expression_token(new Error(new Token("SYNTAX ERROR",String.format("Illegal character '%s' used",pos.current_char))));
            }
            
            
        }
        while(values.size()!=1 || ops.size()!=0){
            Token val2 = values.get(values.size()-1);
            values.remove(values.size()-1);
            Token val1 = values.get(values.size()-1);
            values.remove(values.size()-1);
            Token op = ops.get(ops.size()-1);
            ops.remove(ops.size()-1);
            expression_token OP_result=Operation.performOp(val1, val2, op);
            if(OP_result.err.isError){
                return new expression_token(OP_result.err);
            }
            values.add(OP_result.result);
        }
        return new expression_token(values.get(0));
    }
}