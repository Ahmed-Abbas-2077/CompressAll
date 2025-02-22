

public class LZ77{
    public static int validate_input(String file_name){
        if (file_name.endsWith("dcmp.txt")){
            return 2;
        } else if (file_name.endsWith("cmp.txt")){
            return 1;
        } else {
            return 0;
        }
    }
}

