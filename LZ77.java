import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


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

    public static String read_txt(String file_name) throws IOException, IllegalAccessException {
        /// There are two types of exceptions here,
        /// IOException: This is associated with the FileReader (e.g. file_name not found.) 
        
        if (validate_input(file_name) != 1) {
            throw new IllegalAccessException("The input file must be compressible (ends with cmp.txt)");
        }
        
        StringBuilder extracted = new StringBuilder();
        
        File text_File = new File(file_name);
        Scanner reader = new Scanner(text_File);
        while(reader.hasNextLine()){
            extracted.append(reader.nextLine());
        }
        reader.close();

        return extracted.toString();
    }

    public static Boolean write_txt(String file_name, String decoded) throws IllegalStateException {
        if (validate_input(file_name) != 1) {
            throw new IllegalArgumentException("The output file must be compressible (ends with cmp.txt)");
        }
        try{
            FileWriter writer = new FileWriter(file_name);
            writer.write(decoded);
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static ArrayList<Tuple> read_tags(String file_name) throws IOException, IllegalArgumentException {
        if (validate_input(file_name) != 2) {
            throw new IllegalArgumentException("The output file must be decompressible (ends with dcmp.txt)");
        }

        ArrayList<Tuple> tags = new ArrayList<Tuple>();
        File tag_File = new File(file_name);
        Scanner reader = new Scanner(tag_File);
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            ArrayList<String> tags_txt = new ArrayList<>(Arrays.asList(line.split(":")));
            for (String tag : tags_txt) {
                int first = Integer.parseInt(tag.substring(1, 2));
                int second = Integer.parseInt(tag.substring(3, 4));
                char third = tag.charAt(5);
                tags.add(new Tuple(first, second, third));
            }
        }
        reader.close();

        return tags;
    }


}

