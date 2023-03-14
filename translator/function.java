package translator;

import java.util.ArrayList;
import java.util.HashMap;

public class function {

    static ArrayList<String> creation_args_evaluate(Position pos){


        //return format is {array{isError=='False',arg1,arg2,arg3}};
        //array{isError=='True',err_name,err_details}

        int count_braces = 1;
        pos.IncrementChar();//"("->next char
        ArrayList<String> args = new ArrayList<String>();
        while(count_braces>0 && pos.idx<pos.text.length()){
            if(" \n\t".equals(""+pos.current_char)){
                //then skip spaces
            }else if(pos.current_char==')'){
                count_braces--;
            }else if(CONSTANTS.CHARACTERS.contains(""+pos.current_char)){
                String curr_variable = "";
                while(CONSTANTS.CHARACTERS.contains(""+pos.current_char) || (CONSTANTS.DIGITS.contains(""+pos.current_char) && curr_variable.length()>0)){
                    curr_variable+=pos.current_char;
                    pos.IncrementChar();
                }
                if(pos.current_char==','){
                    args.add(curr_variable);
                }else if(pos.current_char==')'){
                    args.add(curr_variable);
                    count_braces--;
                }else{
                    ArrayList<String> Error_result = new ArrayList<String>();
                    Error_result.add("true");
                    Error_result.add("SYNTAX ERROR");
                    Error_result.add(String.format("Define invalid function argument format in line %s",pos.ln));
                    
                    return Error_result;
                }
            }else{
                
                ArrayList<String> Error_result = new ArrayList<String>();
                Error_result.add("true");
                Error_result.add("SYNTAX ERROR");
                Error_result.add(String.format("Define invalid function argument format in line %s",pos.ln));
                return Error_result;

            }
            if(count_braces!=0){
               pos.IncrementChar();
            }
        }
        if(count_braces==0){
            ArrayList<String> Result = new ArrayList<String>();
            Result.add("false");
            for(String arg:args){
                Result.add(arg);
            }
            return Result;
        }else{

            ArrayList<String> Error_result = new ArrayList<String>();
            Error_result.add("true");
            Error_result.add("SYNTAX ERROR");
            Error_result.add(String.format("Define invalid function argument format in line %s",pos.ln));
            
            return Error_result;
        }
    }

    static ArrayList<HashMap<String,String>> calling_args_evaluation(Position pos,Data_token Variable_bucket,Function_token Function_bucket){

        pos.IncrementChar();//"("->next char
        int count_braces = 1;
        String curr_variable = "";
        ArrayList<HashMap<String,String>> args_data = new ArrayList<HashMap<String,String>>();
        while(pos.idx<pos.text.length() && count_braces>0){

            if(" \n\t".equals(""+pos.current_char)){
                //do nothing
            }else if(pos.current_char==','){
                if(curr_variable.length()>0){
                    expression_token exp_res = data_expression.evaluate(curr_variable,Variable_bucket,Function_bucket);
                    if(exp_res.err.isError){
                        //error throw
                        ArrayList<HashMap<String,String>> Error_result = new ArrayList<HashMap<String,String>>();
                        HashMap<String,String> Error_data = new HashMap<String,String>();
                        Error_data.put("isError","true");
                        Error_data.put("name",exp_res.err.err.type);
                        Error_data.put("value",exp_res.err.err.value);
                        Error_result.add(Error_data);
                        return Error_result;
                    }else{
                        HashMap<String,String>value=new HashMap<String,String>();
                        value.put("type",exp_res.result.type);
                        value.put("value",exp_res.result.value);
                        args_data.add(value);
                        curr_variable = "";
                    }
                    
                }else{
                    //error throw
                    ArrayList<HashMap<String,String>> Error_result = new ArrayList<HashMap<String,String>>();
                    HashMap<String,String> Error_data = new HashMap<String,String>();
                    Error_data.put("isError","true");
                    Error_data.put("name","SYNTAX ERROR");
                    Error_data.put("value","Extra ',' in function callling agrumnet");
                    Error_result.add(Error_data);
                    return Error_result;
                }
            }else if(pos.current_char=='('){
                int count_exp_braces = 1;
                pos.IncrementChar();
                curr_variable+='(';
                while(count_exp_braces!=0 && pos.idx<pos.text.length()){
                    
                    if(pos.current_char=='('){
                        count_exp_braces++;
                    }else if(pos.current_char==')'){
                        count_exp_braces--;
                    }
                    curr_variable+=pos.current_char;
                    if(count_exp_braces!=0){
                        pos.IncrementChar();
                    }
                    
                }
                if(count_exp_braces!=0){
                    //error throw
                    ArrayList<HashMap<String,String>> Error_result = new ArrayList<HashMap<String,String>>();
                    HashMap<String,String> Error_data = new HashMap<String,String>();
                    Error_data.put("isError","true");
                    Error_data.put("name","SYNTAX ERROR");
                    Error_data.put("value","Not have close bracket in function argument");
                    Error_result.add(Error_data);
                    return Error_result;
                }
            }else if(pos.current_char==')'){
                if(curr_variable.length()>0){
                    expression_token exp_res = data_expression.evaluate(curr_variable,Variable_bucket,Function_bucket);
                    if(exp_res.err.isError){
                        //error throw
                        ArrayList<HashMap<String,String>> Error_result = new ArrayList<HashMap<String,String>>();
                        HashMap<String,String> Error_data = new HashMap<String,String>();
                        Error_data.put("isError","true");
                        Error_data.put("name",exp_res.err.err.type);
                        Error_data.put("value",exp_res.err.err.value);
                        Error_result.add(Error_data);
                        return Error_result;
                    }else{
                        HashMap<String,String>value=new HashMap<String,String>();
                        value.put("type",exp_res.result.type);
                        value.put("value",exp_res.result.value);
                        args_data.add(value);
                    }

                }
                count_braces--;
            }else if(pos.current_char=='\''){
                //String Value
                curr_variable+=pos.current_char;
                pos.IncrementChar();
                while(pos.current_char!='\'' && pos.idx<pos.text.length()){
                    curr_variable+=pos.current_char;
                    pos.IncrementChar();
                }
                curr_variable+=pos.current_char;
            }else{
                curr_variable+=pos.current_char;
            }
            pos.IncrementChar();
        }
        if(count_braces==0){
            return args_data;
        }else{
            //error throw
            ArrayList<HashMap<String,String>> Error_result = new ArrayList<HashMap<String,String>>();
            HashMap<String,String> Error_data = new HashMap<String,String>();
            Error_data.put("isError","true");
            Error_data.put("name","SYNTAX ERROR");
            Error_data.put("value","Not have close bracket in calling function");
            Error_result.add(Error_data);
            return Error_result;
        }
    }
}
