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

    public static byte[] read_binary(String bin_file){
        try {
            return Files.readAllBytes(Paths.get(bin_file));
        } catch (IOException e){
            System.err.println("Error reading bin file: " + e.getMessage());
            return new byte[0];
        }
    }


    public static void write_txt (String bin_file){
        
    }

}
