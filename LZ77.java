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

    public static String read_txt(String file_name) throws IOException, IllegalArgumentException {
        /// There are two types of exceptions here,
        /// IOException: This is associated with the FileReader (e.g. file_name not found.) 
        
        if (validate_input(file_name) != 1) {
            throw new IllegalArgumentException("The input file must be compressible (ends with cmp.txt)");
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

    public static Boolean write_txt(String file_name, String decoded) throws IllegalArgumentException {
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

    public static Boolean write_tags(String file_name, ArrayList<Tuple> tags) throws IOException, IllegalArgumentException {
        if (validate_input(file_name) != 2) {
            throw new IllegalArgumentException("The output file must be decompressible (ends with dcmp.txt)");
        }

        FileWriter writer = new FileWriter(file_name);
        StringBuilder text_tags = new StringBuilder();
        for(Integer i=0; i<tags.size(); i++){
            String line = "(" + tags.get(i).first + "," + tags.get(i).second + "," + tags.get(i).third + "):";
            text_tags.append(line);
            if (i<tags.size()-1){
                text_tags.append(":");
            }
        }
        writer.write(text_tags.toString());
        writer.close();
        return true;
    }


    public static void encoder(String read_file, String write_file, Integer search_len, Integer look_ahead_len) throws IOException, IllegalArgumentException {
        ArrayList<Tuple> tag_list = new ArrayList<Tuple>();

        String sequence;
        String trial = new String();
        try{
            trial = read_txt(read_file);
        }
        finally{
            sequence = trial;
        }

        Integer search_start = 0;
        Integer look_ahead_start = 0;

        while (look_ahead_start < sequence.length()) {
            ArrayList<Tuple> matchings = new ArrayList<Tuple>();

            for (Integer j=search_start; j<look_ahead_start; j++){
                if (sequence.charAt(look_ahead_start) == sequence.charAt(j)){
                    Integer look_pos = look_ahead_start;
                    Integer srch_pos = j; 
                    Integer matching_length = 0;

                    Integer look_ahead_limit = Math.min(sequence.length(), look_ahead_start+look_ahead_len);
                    while ((look_pos < look_ahead_limit) && (sequence.charAt(look_pos) == sequence.charAt(srch_pos))){
                        matching_length++;
                        look_pos++;
                        srch_pos++;
                    }
                    matchings.add(new Tuple(look_ahead_start-j, matching_length));
                }
            }

            Tuple longest_match = null;
            for (Tuple t : matchings) {
                if (longest_match == null || t.second > longest_match.second) {
                    longest_match = t;
                }
            }

            if (longest_match == null){
                tag_list.add(new Tuple(0, 0, sequence.charAt(look_ahead_start)));
                if ((look_ahead_start-search_start) > search_len){
                    search_start++;
                }
                if ((look_ahead_start+1) <= sequence.length()){
                    look_ahead_start++;
                }
                else{
                    break;
                }
            }
            else {
                Integer offset = longest_match.first;
                Integer length = longest_match.second;
                Integer next_char_pos = look_ahead_start + length;
                if (next_char_pos >= sequence.length()){
                    tag_list.add(new Tuple(offset, length, ' '));
                }
                else{
                    tag_list.add(new Tuple(offset, length, sequence.charAt(next_char_pos)));
                }

                if ((look_ahead_start-search_start) > search_len){
                    search_start = search_start + length + 1;
                }
                if ((look_ahead_start+length+1) <= sequence.length()){
                    look_ahead_start = look_ahead_start + length + 1;
                }
                else{
                    break;
                }
            }

        }
        write_tags(write_file, tag_list);
    }

    


}

