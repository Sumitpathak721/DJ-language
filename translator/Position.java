package translator;



public class Position {
    String text;
    char current_char;
    int ln,idx;
    
    Position(String text){
        this.text = text;
        this.current_char = Character.MIN_VALUE;
        this.ln = 0;
        this.idx = -1;
        IncrementChar();
    }
    void IncrementChar(){
        idx++;
        if(idx<text.length()){
            current_char = text.charAt(idx);
        }else{
            current_char = Character.MIN_VALUE;
        }
        if(current_char=='\n'){
            ln++;
        }
    }
    void DecrementChar(){
        idx--;
        if(0<idx){
            current_char = text.charAt(idx);
        }else{
            current_char = Character.MIN_VALUE;
        }
        if(current_char=='\n'){
            ln--;
        }
    }
}
