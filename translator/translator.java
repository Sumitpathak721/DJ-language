package translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class translator {

    static Result_data Output(Position pos, String endKey, Data_token Variable_bucket, Function_token Function_bucket) {

        try {

            String current_variable = "";
            String name = "";
            String type = "";
            String value = "";
            String mutabilty = "true";
            ArrayList<String> args = new ArrayList<String>();
            while (pos.idx < pos.text.length() && !endKey.equals("" + pos.current_char)) {
                if (" \n\t(".contains("" + pos.current_char)) {
                    if (current_variable.length() > 0) {
                        if (isKeyword(current_variable)) {

                            if (current_variable.equals("const") && type.length() == 0
                                    && mutabilty.equals("true")) {
                                mutabilty = "false";
                            } else if (current_variable.equals("if")) {

                                Result_data if_else_result_bucket = if_else_expression.evaluate(Variable_bucket,
                                        Function_bucket, pos);

                                // returning error;
                                if (if_else_result_bucket.err.isError) {
                                    Result_data Error_result = new Result_data();
                                    Error_result.err = if_else_result_bucket.err;
                                    return Error_result;

                                }

                                // Updating values of Variables_bucket and Function_bucket which is present in
                                // global variable

                                Variable_bucket = if_else_result_bucket.Variable_bucket;
                                Function_bucket = if_else_result_bucket.Function_bucket;

                            } else if (current_variable.equals("func")) {
                                if (mutabilty.equals("true") && type.length() == 0 && name.length() == 0) {
                                    type = "func";
                                } else {
                                    Result_data Error_result = new Result_data();
                                    Error_result.err = new Error(new Token("SYNTAX ERROR",
                                            String.format("Invalid Function creation format at line %s", pos.ln)));
                                    return Error_result;
                                }
                                // for function

                                /*
                                 * function creation->
                                 * func func_name(){
                                 * expression;
                                 * }
                                 */
                            } else {
                                Result_data Error_result = new Result_data();
                                Error_result.err = new Error(new Token("SYNTAX ERROR", "Invalid Syntax format"));
                                return Error_result;

                            }

                        } else if (pos.current_char == '(') {
                            if (name.length() == 0) {
                                if (current_variable.length() > 0
                                        && !(isDataType(current_variable) || isKeyword(current_variable))) {
                                    name = current_variable;
                                } else {
                                    Result_data Error_result = new Result_data();
                                    Error_result.err = new Error(new Token("SYNTAX ERROR",
                                            "Invalid syntax formation for function decleration"));
                                    return Error_result;
                                }
                            }
                            // to check is it present or not in function

                            // check In case if args already define
                            if (args.size() == 0) {
                                
                                if (type.equals("func")) {
                                    // It means we are creating function
                                    args = function.creation_args_evaluate(pos);
                                    if (args.get(0).equals("true")) {
                                        Result_data Error_result = new Result_data();
                                        Error_result.err = new Error(new Token(args.get(1), args.get(2)));
                                        return Error_result;
                                    }

                                } else if (type.length() == 0) {
                                    // calling a function
                                    ArrayList<HashMap<String, String>> func_args = function
                                            .calling_args_evaluation(pos, Variable_bucket, Function_bucket);
                                    if (func_args.get(0).containsKey("isError")
                                            && func_args.get(0).get("isError").equals("true")) {
                                        Result_data Error_result = new Result_data();
                                        Error_result.err = new Error(new Token(func_args.get(0).get("name"),
                                                func_args.get(0).get("value")));
                                        return Error_result;
                                    }
                                    ArrayList<String> f_head = new ArrayList<String>();
                                    f_head.add(name);
                                    f_head.add(String.valueOf(func_args.size()));
                                    if(isBuild_func(f_head.get(0))){
                                        if(f_head.size()-1==1){
                                            if(f_head.get(0).equals("println")){
                                                System.out.println(func_args.get(0).get("value"));
                                            }else if(f_head.get(0).equals("print")){
                                                System.out.print(func_args.get(0).get("value"));
                                            }else{
                                                Result_data Error_result = new Result_data();
                                                Error_result.err = new Error(
                                                    new Token("SYNTAX ERROR", "Undefine buildin func with different args size"));
                                                return Error_result;    
                                            }
                                        }else{
                                            Result_data Error_result = new Result_data();
                                            Error_result.err = new Error(
                                                new Token("SYNTAX ERROR", "Undefine buildin func with different args size"));
                                            return Error_result;
                                        }
                                        
                                    }
                                    else if (Function_bucket.bucket.containsKey(f_head)) {
                                        // remaining work
                                        Data_token call_variables = new Data_token();
                                        for (int i = 0; i < func_args.size(); i++) {
                                            call_variables.addMember(
                                                    Function_bucket.bucket.get(f_head).F_args.get(i),
                                                    func_args.get(i));
                                        }
                                        // Result_data
                                        translator.Output(new Position(Function_bucket.bucket.get(f_head).F_exp),
                                                endKey, call_variables, Function_bucket);
                                        

                                    } else {
                                        Result_data Error_result = new Result_data();
                                        Error_result.err = new Error(
                                                new Token("UNDEFINE FUNCTION", "Function is not define"));
                                        return Error_result;
                                    }
                                    name = "";
                                    value = "";
                                    args = new ArrayList<String>();
                                    current_variable = "";
                                    type = "";

                                } else {
                                    Result_data Error_result = new Result_data();
                                    Error_result.err = new Error(
                                            new Token("UNDEFINE FUNCTION", "Function is not define"));
                                    return Error_result;
                                }

                            } else {
                                Result_data Error_result = new Result_data();
                                Error_result.err = new Error(
                                        new Token("SYNTAX ERROR", "Argumnets of function is already define"));
                                return Error_result;
                            }

                        } else if (isDataType(current_variable) && type.length() == 0) {
                            type = current_variable;
                        } else if (name.length() == 0) {
                            name = current_variable;
                        } else {
                            // throw error result
                            Result_data Error_result = new Result_data();
                            Error_result.err = new Error(new Token("SYNTAX ERROR",
                                    String.format("Invalid Keyword found at line %s", pos.ln)));
                            return Error_result;
                        }

                        current_variable = "";
                    }
                } else if (CONSTANTS.CHARACTERS.contains("" + pos.current_char)
                        || (current_variable.length() > 0 && CONSTANTS.DIGITS.contains("" + pos.current_char))) {
                    current_variable += pos.current_char;
                } else if (pos.current_char == '{') {
                    if (type.equals("func") && args.size() > 0 && name.length() > 0) {
                        // function expression store
                        int count_braces = 1;
                        pos.IncrementChar();// "{"->next char
                        current_variable = "";
                        while (pos.idx < pos.text.length() && count_braces > 0) {
                            if (pos.current_char == '}') {
                                count_braces--;
                            } else if (pos.current_char == '{') {
                                count_braces++;
                            }
                            if (count_braces != 0) {
                                current_variable += pos.current_char;
                                pos.IncrementChar();
                            }
                        }
                        if (count_braces == 0) {
                            value = current_variable;
                            args.remove(0);// remove isError from args;
                            Function_bucket.addMember(name, args, value);

                            // reset all values;
                            current_variable = "";
                            value = "";
                            args = new ArrayList<String>();
                            type = "";
                            name = "";
                        } else {
                            Result_data Error_result = new Result_data();
                            Error_result.err = new Error(new Token("SYNTAX ERROR",
                                    String.format("function '%s' not have close brackets'}'", name)));
                            return Error_result;
                        }

                    } else {
                        Result_data Error_result = new Result_data();
                        Error_result.err = new Error(new Token("SYNTAX ERROR",
                                String.format("Invalid Fuction decleration format of function '%s'", name)));
                        return Error_result;
                    }

                } else if (pos.current_char == '=') {
                    pos.IncrementChar();// '='->next character

                    if (name.length() > 0) {
                        if (current_variable.length() > 0) {
                            Result_data Error_result = new Result_data();
                            Error_result.err = new Error(
                                    new Token("SYNTAX ERROR", String.format("Invalid format to assign value")));
                            return Error_result;
                        }
                    } else if (name.length() == 0 && (!(isDataType(current_variable) || isKeyword(current_variable))
                            && current_variable.length() > 0)) {
                        name = current_variable;
                    }
                    if (name.length() > 0) {
                        if (Variable_bucket.bucket.containsKey(name)) {
                            if (Variable_bucket.bucket.get(name).get("mutable").equals("false")) {
                                Result_data Error_result = new Result_data();
                                Error_result.err = new Error(
                                        new Token("ILLEGAL ACCESS", String.format("Can't overwrite const variable")));
                                return Error_result;
                            }
                        }
                        while (pos.idx < pos.text.length() && pos.current_char != ';') {
                            value += pos.current_char;
                            pos.IncrementChar();
                        }
                        if (pos.current_char != ';') {
                            Result_data Error_result = new Result_data();
                            Error_result.err = new Error(
                                    new Token("SYNTAX ERROR", String.format("Variable decleration end with ';'")));
                            return Error_result;
                        }

                        expression_token exp_res = data_expression.evaluate(value, Variable_bucket, Function_bucket);
                        if (exp_res.err.isError) {
                            Result_data Error_result = new Result_data();
                            Error_result.err = exp_res.err;
                            return Error_result;
                        }
                        if (type.length() > 0) {
                            // Casting value in case if type is define
                            expression_token castedValue = Operation.typeCast(exp_res.result, type);
                            if (castedValue.err.isError) {
                                Result_data Error_result = new Result_data();
                                Error_result.err = castedValue.err;
                                return Error_result;
                            }
                            exp_res.result = castedValue.result;
                        }
                        HashMap<String, String> data_details = new HashMap<String, String>();
                        data_details.put("mutable", mutabilty);
                        data_details.put("value", exp_res.result.value);
                        data_details.put("type", exp_res.result.type);
                        Variable_bucket.addMember(name, data_details);
                        // reset values
                        mutabilty = "true";
                        value = "";
                        type = "";
                        name = "";
                        current_variable = "";

                    } else {
                        Result_data Error_result = new Result_data();
                        Error_result.err = new Error(
                                new Token("SYNTAX ERROR", String.format("Variable name is not define", pos.ln)));
                        return Error_result;
                    }
                }
                pos.IncrementChar();
            }
            // returning result;
            Result_data result = new Result_data();
            result.err = new Error();
            result.Variable_bucket = Variable_bucket;
            result.Function_bucket = Function_bucket;
            return result;
        } catch (Exception e) {
            Result_data Error_result = new Result_data();
            System.out.println(e);
            Error_result.err = new Error(
                    new Token("SYSTEM ERROR", String.format("Internal system get some error in line %s", pos.ln)));
            return Error_result;
        }
    }

    static Boolean isDataType(String variable) {
        for (int i = 0; i < BUILD_INs.Data_types.length; i++) {
            if (BUILD_INs.Data_types[i].equals(variable)) {
                return true;
            }
        }
        return false;
    }

    static Boolean isKeyword(String variable) {
        for (int i = 0; i < CONSTANTS.KEYWORDS.length; i++) {
            if (CONSTANTS.KEYWORDS[i].equals(variable)) {
                return true;
            }
        }
        return false;
    }

    static Boolean isBuild_func(String name){
        for(int i=0;i<BUILD_INs.functions.length;i++){
            if(BUILD_INs.functions[i].equals(name)){
                return true;
            }
        }
        return false;
    }
}