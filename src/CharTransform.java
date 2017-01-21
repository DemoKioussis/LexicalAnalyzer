/**
 * Created by Demok on 2017-01-20.
 */
public class CharTransform {
    char low,high;
    char[] result = new char[2];
    public CharTransform(char l, char h,char ident,char res){
        low = l;
        high = h;
        result[0] = ident;
        result[1] = res;
    }
    public char[] transform(char c){
        if(hasTransfomr(c))
            return result;
        else
            return null;

    }
    public boolean hasTransfomr(char c){
        return(c>= low && c<=high);
    }
}
