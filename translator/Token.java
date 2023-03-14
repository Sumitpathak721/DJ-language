package translator;

import java.util.ArrayList;
import java.util.HashMap;

public class Token {
    String type  = "";
    String value = "";
    Token(String type,String value){
        this.type = type;
        this.value = value;
    }
    Token(String type){
        this.type = type;
    }
    Token(){
    }
    public String toString(){
        if(value.length()>0){
            return String.format("%s",type);
        }else{
            return String.format("%s:%s",type,value);
        }
    }
}
//for function
//arg_data
class Function_data{
    ArrayList<String> F_args;
    String F_exp;
    Function_data(ArrayList<String> F_args,String exp){
        this.F_args = F_args;
        this.F_exp = exp;
    }
    void addArg(String F_arg){
        F_args.add(F_arg);
    }
    public String toString(){
        return String.format("[%s:'%s']",F_args,F_exp);
    }
}
class Function_token{
    HashMap<ArrayList<String>,Function_data> bucket = new HashMap<ArrayList<String>,Function_data>();
    void addMember(String F_name,ArrayList<String> args,String exp){
        //head
        ArrayList<String> head = new ArrayList<String>();
        head.add(F_name);
        head.add(String.valueOf(args.size()));
        
        //body
        bucket.put(head,new Function_data(args,exp));
    }
    public String toString(){
        return String.format("%s",bucket);
    }
}

//for data
class Data_token{
	HashMap<String,HashMap<String,String>> bucket;
    Boolean isError = false;

    Data_token(){
        this.bucket = new HashMap<String,HashMap<String,String>>();
    }

	void addMember(String name,HashMap<String,String> data_details){
        bucket.put(name,data_details);
	}
	public String toString(){
		return String.format("%s",this.bucket);
	}
}

class Error{
    Token err;
    Boolean isError;
    Error(){
        this.isError = false;
    }
    Error(Token err){
        this.err = err;
        this.isError = true;
    }
    public String toString(){
        return String.format("%s",err);
    }
}


//for final result
class Result_data{
    
    Data_token Variable_bucket;
    Function_token Function_bucket;
    Token return_val;
    Error err;
    
    Result_data(){
        this.Variable_bucket = new Data_token();
        this.Function_bucket = new Function_token();
        this.return_val = new Token("bool","false");
        this.err =  new Error();
    }
    
    public String toString(){
        if(err.isError){
            return String.format("%s",err.err);
        }else{
            return String.format("Varaibles: %s,Functions: %s",Variable_bucket,Function_bucket);
        }
    }

}

class expression_token{
    Token result;
    Error err;
    
    expression_token(Token token){
        this.result = token;
        this.err = new Error();
    }
    expression_token(Error err){
        this.err = err;
        this.result = new Token();
    }
    
    public String toString(){
        if(err.isError){
            return String.format("%s",err.err);
        }
        return String.format("Token: %s",result);
    }
}