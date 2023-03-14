package translator;


import java.io.FileReader;

class improperExtension extends Exception{
    String msg;
    public improperExtension(String msg){
        super(msg);
        this.msg = msg;
    }
    @Override
    public String getMessage() {
        return this.msg;
    }

}

public class shell {
    public static void  main(String[] args){
        try{
        String filename = "D:\\Program Files\\project\\Compiler\\test_DJ_FIles\\test1.dj";
        int idx = filename.lastIndexOf('.');
        if(idx>0){
            if(!filename.substring(idx+1).equals("dj")){
                throw new improperExtension("DJ runs only file with .dj extension");
            }
        }
        FileReader fs = new FileReader(filename);
        String text = "";
        int i=0;
        while((i=fs.read())!=-1){
            text+=(char)i;
        }
        fs.close();
        Result_data result = translator.Output(new Position(text), "global_varaible", new Data_token(), new Function_token());
        if(result.err.isError){
            System.out.println(result.err);
        }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
}
