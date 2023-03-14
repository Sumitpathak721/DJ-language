package translator;

public class Operation {
    static expression_token typeCast(Token value,String type){
        
        value.type = type;
        
        if(type.equals("bool")){
            
            value.value = String.format("%s",Boolean.parseBoolean(value.value));
        }else if(type.equals("int")){
            value.value = String.format("%s",(int)Float.parseFloat(value.value));
        }else if(type.equals("float")){
            value.value = String.format("%s",Float.parseFloat(value.value));
        }else if(type.equals("String")){
            //do nothing
        }else{
            expression_token Result = new expression_token(new Token());
            Result.err = new Error(new Token("INVALID TYPECASTING",String.format("Invalid typecasting for %s to %s",value.type,type)));
            return Result;
        }
        expression_token Result = new expression_token(value);
        return Result;
    }
    static expression_token performOp(Token val1,Token val2,Token op){
        
        if(op.type.equals("+")){
            if(val1.type.equals("String") && val2.type.equals("String")){
                return new expression_token(new Token("String",String.format("%s%s",val1.value,val2.value)));
            }else if(val1.type.equals("float") || val2.type.equals("float")){
                return new expression_token(new Token("float",String.valueOf(Float.parseFloat(val1.value)+Float.parseFloat(val2.value))));
            }else if(val1.type.equals("int") && val2.type.equals("int")){
                return new expression_token(new Token("int",String.valueOf(Integer.parseInt(val1.value)+Integer.parseInt(val2.value))));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Undefine type for '%s' operation",op.type))));
            }
        }else if(op.type.equals("-")){
            if(val1.type.equals("float") || val2.type.equals("float")){
                return new expression_token(new Token("float",String.valueOf(Float.parseFloat(val1.value)-Float.parseFloat(val2.value))));
            }else if(val1.type.equals("int") && val2.type.equals("int")){
                return new expression_token(new Token("int",String.valueOf(Integer.parseInt(val1.value)-Integer.parseInt(val2.value))));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Undefine type for '%s' operation",op.type))));
            }
        }else if(op.type.equals("/")){
            if((val1.type.equals("float") || val1.type.equals("int")) && (val2.type.equals("int") || val2.type.equals("float"))){
                return new expression_token(new Token("float",String.valueOf(Float.parseFloat(val1.value)/Float.parseFloat(val2.value))));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Undefine type for '%s' operation",op.type))));
            }
        }else if(op.type.equals("*")){
            if(val1.type.equals("float") || val2.type.equals("float")){
                return new expression_token(new Token("float",String.valueOf(Float.parseFloat(val1.value)*Float.parseFloat(val2.value))));
            }else if(val1.type.equals("int") && val2.type.equals("int")){
                return new expression_token(new Token("int",String.valueOf(Integer.parseInt(val1.value)*Integer.parseInt(val2.value))));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Undefine type for '%s' operation",op.type))));
            }
        }else if(op.type.equals("**")){
            if((val1.type.equals("int") || val1.type.equals("float") && val2.type.equals("float"))){
                float v1 = Float.valueOf(val1.value);
                int v2 = Integer.parseInt(val2.value);
                float res = 1f;
                for(int i=0;i<v2;i++){
                    res*=v1;
                }
                if(val1.type.equals("int")){
                    
                    return new expression_token(new Token("int",String.valueOf((int)(res))));
                }
                return new expression_token(new Token("float",String.valueOf(res)));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Undefine type for '%s' operation",op.type))));
            }
        }else if(op.type.equals("==")){
            if((val1.type.equals("float") || val1.type.equals("int")) && (val2.type.equals("float") || val2.type.equals("int"))){
                float v1 = Float.parseFloat(val1.value);
                float v2 = Float.parseFloat(val2.value);
                return new expression_token(new Token("bool",String.valueOf(v1==v2)));
            }else if(val1.type.equals(val2.type)){
                return new expression_token(new Token("bool",String.valueOf(val1.value.equals(val2.value))));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Invalid type for '%s'",op.type))));
            }
        }else if(op.type.equals("!=")){
            if((val1.type.equals("float") || val1.type.equals("int")) && (val2.type.equals("float") || val2.type.equals("int"))){
                float v1 = Float.parseFloat(val1.value);
                float v2 = Float.parseFloat(val2.value);
                return new expression_token(new Token("bool",String.valueOf(v1!=v2)));
            }else if(val1.type.equals(val2.type)){
                return new expression_token(new Token("bool",String.valueOf(!val1.value.equals(val2.value))));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Invalid type for '%s'",op.type))));
            }
        }else if(op.type.equals(">")){
            if((val1.type.equals("float") || val1.type.equals("int")) && (val2.type.equals("float") || val2.type.equals("int"))){
                float v1 = Float.parseFloat(val1.value);
                float v2 = Float.parseFloat(val2.value);
                return new expression_token(new Token("bool",String.valueOf(v1>v2)));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Invalid type for '%s'",op.type))));
            }
        }else if(op.type.equals("<")){
            if((val1.type.equals("float") || val1.type.equals("int")) && (val2.type.equals("float") || val2.type.equals("int"))){
                float v1 = Float.parseFloat(val1.value);
                float v2 = Float.parseFloat(val2.value);
                return new expression_token(new Token("bool",String.valueOf(v1<v2)));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Invalid type for '%s'",op.type))));
            }
        }else if(op.type.equals("<=")){
            if((val1.type.equals("float") || val1.type.equals("int")) && (val2.type.equals("float") || val2.type.equals("int"))){
                float v1 = Float.parseFloat(val1.value);
                float v2 = Float.parseFloat(val2.value);
                return new expression_token(new Token("bool",String.valueOf(v1<=v2)));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Invalid type for '%s'",op.type))));
            }
        }else if(op.type.equals(">=")){
            if((val1.type.equals("float") || val1.type.equals("int")) && (val2.type.equals("float") || val2.type.equals("int"))){
                float v1 = Float.parseFloat(val1.value);
                float v2 = Float.parseFloat(val2.value);
                return new expression_token(new Token("bool",String.valueOf(v1>=v2)));
            }else{
                return new expression_token(new Error(new Token("TYPE ERROR",String.format("Invalid type for '%s'",op.type))));
            }
        }else if(op.type.equals("&&")){
            Boolean v1 = Boolean.parseBoolean(val1.value);
            Boolean v2 = Boolean.parseBoolean(val2.value);
            return new expression_token(new Token("bool",String.valueOf(v1 && v2)));
        }else if(op.type.equals("||")){
            Boolean v1 = Boolean.parseBoolean(val1.value);
            Boolean v2 = Boolean.parseBoolean(val2.value);
            return new expression_token(new Token("bool",String.valueOf(v1 || v2)));
        }else{
            return new expression_token(new Error(new Token("INVALID OPERATION","Invalid Operation type")));
        }
    }
}