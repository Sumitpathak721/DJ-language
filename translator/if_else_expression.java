package translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class if_else_expression {
    static Result_data evaluate(Data_token Variable_bucket,Function_token Function_bucket,Position pos){
        
        
        while(" \n\t".equals(""+pos.current_char) && pos.idx<pos.text.length()){
            pos.IncrementChar();
        }
        if(pos.current_char=='('){
            int count_braces = 1;
            String exp = "";
            pos.IncrementChar();//"("->next char
            while(pos.idx<pos.text.length() && count_braces!=0){
                if(pos.current_char=='('){
                    count_braces++;
                }else if(pos.current_char==')'){
                    count_braces--;
                }else if(pos.current_char=='\''){
                    exp+=pos.current_char;
                    pos.IncrementChar();
                    while(pos.current_char!='\'' && pos.idx<pos.text.length()){
                        exp+=pos.current_char;
                    }
                }
                if(count_braces!=0){
                    exp+=pos.current_char;
                }
                pos.IncrementChar();
            }
            if(count_braces!=0){
                Result_data Error_result = new Result_data();
                Error_result.err = new Error(new Token("SYNTAX ERROR","Invalid no of paranthesis in if statement"));
                return Error_result;
            }
            
            expression_token evaluated_condition = data_expression.evaluate(exp,Variable_bucket,Function_bucket);
            if(evaluated_condition.err.isError){
                Result_data Error_result = new Result_data();
                Error_result.err = evaluated_condition.err;
                return Error_result;
            }
            while(" \n\t".equals(""+pos.current_char)){
                pos.IncrementChar();
            }
            if(pos.current_char=='{'){
                pos.IncrementChar();//'{'->expression
                Result_data if_else_result = new Result_data();
                /* 
                        for if_else ladder we have to state
                        1. if(){

                            }else{

                            }
                            next expression;
                        2. if(){

                            }
                            next expression;
                */
                if(Boolean.parseBoolean(evaluated_condition.result.value)){
                    
                    if_else_result = translator.Output(pos,"}", Variable_bucket, Function_bucket);
                    
                    pos.IncrementChar();//'}'->next expression
                    while(" \n\t".equals(""+pos.current_char ) && pos.idx<pos.text.length()){
                        pos.IncrementChar();
                    }
                    if(pos.idx<pos.text.length()){
                        String keyword = "";
                        while(pos.idx<pos.text.length() && keyword.length()<4){
                            keyword+=pos.current_char;
                            pos.IncrementChar();
                        }
                        if(keyword.equals("else")){
                            while(" \n\t".equals(""+pos.current_char)){
                                pos.IncrementChar();
                            }
                            if(pos.current_char=='{'){
                                count_braces = 1;
                                pos.IncrementChar();//'{'->next char
                                while(pos.idx<pos.text.length() && count_braces>0){
                                    if(pos.current_char=='{'){
                                        count_braces++;
                                    }else if(pos.current_char=='}'){
                                        count_braces--;
                                    }
                                    pos.IncrementChar();
                                }
                                if(count_braces>0){
                                    Result_data Error_result = new Result_data();
                                    Error_result.err = new Error(new Token("SYNTAX ERROR","Invalid no of paranthesis in else statement"));
                                    return Error_result;
                                }
                            }else{
                                Result_data Error_result = new Result_data();
                                Error_result.err = new Error(new Token("SYNTAX ERROR", String.format("Invalid else format at line %s",pos.ln)));
                                return Error_result;
                                
                            }
                        }else{
                            for(int i=0;i<4;i++){
                                pos.DecrementChar();
                            }
                        }
                    }
                }else{
                    //first skipping if statement
                    count_braces = 1;
                    while(pos.idx<pos.text.length() && count_braces>0){
                        if(pos.current_char=='{'){
                            count_braces++;
                        }else if(pos.current_char=='}'){
                            count_braces--;
                        }
                        pos.IncrementChar();
                    }
                    if(count_braces!=0){
                        //throw error
                        Result_data Error_Result = new Result_data();
                        Error_Result.err = new Error(new Token("SYNTAX ERROR", "Invalid no of curle braces in if statement"));
                        return Error_Result;
                    }else{
                        while(" \n\t".equals(""+pos.current_char)){
                            pos.IncrementChar();
                        }
                        String Keyword = "";
                        for(int i=0;i<4;i++){
                            Keyword+=pos.current_char;
                            pos.IncrementChar();
                        }
                        if(Keyword.equals("else")){
                            while(" \n\t".equals(""+pos.current_char)){
                                pos.IncrementChar();
                            }
                            if(pos.current_char=='{'){
                                pos.IncrementChar();//'{'->next_char
                                if_else_result = translator.Output(pos, "}", Variable_bucket, Function_bucket);
                                
                                pos.IncrementChar();//'}'->next_char


                            }else{
                                //throw error
                                Result_data Error_Result = new Result_data();
                                Error_Result.err = new Error(new Token("SYNTAX ERROR", String.format("Invalid else statement at line %s",pos.ln)));
                                return Error_Result;
                                
                            }
                        }else{
                            for(int i=0;i<4;i++){
                                pos.DecrementChar();
                            }
                        }
                    }

                }

                //throw error from if_else_result
                
                if(if_else_result.err.isError){
                    Result_data Error_Result = new Result_data();
                    Error_Result.err = if_else_result.err;
                    return Error_Result;
                }
                

                //Updation of Variables
                for(Map.Entry<String,HashMap<String,String>> newVaraible:if_else_result.Variable_bucket.bucket.entrySet()){
                    if(Variable_bucket.bucket.containsKey(newVaraible.getKey()) && Variable_bucket.bucket.get(newVaraible.getKey()).get("mutable")=="true"){
                        Variable_bucket.bucket.put(newVaraible.getKey(),newVaraible.getValue());
                    }
                }
                
                //Updation of Functions

                for(Map.Entry<ArrayList<String>,Function_data> newFunction:if_else_result.Function_bucket.bucket.entrySet()){
                    if(Function_bucket.bucket.containsKey(newFunction.getKey())){
                        Function_bucket.bucket.put(newFunction.getKey(),newFunction.getValue());
                    }
                }
                

            }else{
                Result_data Error_Result = new Result_data();
                Error_Result.err = new Error(new Token("SYNTAX ERROR",String.format("Invalid '%s' use for if_else ladder",pos.current_char)));
                return Error_Result;
                
            }
            
        

        }else{
            Result_data Error_Result = new Result_data();
            Error_Result.err = new Error(new Token("SYNTAX ERROR",String.format("Invalid character %s in ln %s",pos.current_char,pos.ln)));
            return Error_Result;
            
        }
        Result_data result = new Result_data();
        result.Variable_bucket = Variable_bucket;
        result.Function_bucket = Function_bucket;
        return result;
    }
}
