package SHuffman;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SHuff {
    
    public static String read_txt(String txt_file) {
        try{
            return Files.readString(Paths.get(txt_file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Sorry, Error reding file: " + e.getMessage());
            return "";
        }
    }


    public static void write_txt (String bin_file){

    }

}
