package LZ78;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Scanner;

public class LZ78 {

    // This method receives two strings and returns
    // the length of their match, with a constraint 
    // that the match shall be a prefix of either one
    // (i.e. neither string could be represented as a 
    // (prefix + theOtherString + suffix), rather, either 
    // they can be represented as (str, pre+str) or not.
    // public static int subMatch(String a, String b){
    //     int i = 0;
    //     int j = 0;
    //     while ((i < a.length()) && (j < b.length()))
    //     {
    //         if(a.charAt(i) == b.charAt(j)){
    //             i++;
    //             j++;
    //         }
    //         else
    //         {
    //             break;
    //         }
    //     }
    //     int matchLength = Math.min(i, j);
    //     return matchLength;
    // }


    // This method compares all elements of the 
    // dictionary to see which of them has the longest 
    // match with the string starting from pos until the 
    // character just before c, where c is the character 
    // that makes the string unmatchable by any element 
    // in the dictionary
    public static Pair compare(Pos position, String sequence, ArrayList<String> dict){
        int dict_index = -1;
        char last_char = sequence.charAt(position.end-1);
        boolean inDict = true;

        while (inDict)
        {
            inDict = false;
            for (int i=0; i<dict.size(); i++){
                String sub = sequence.substring(position.start, position.end);
                System.out.println("SubString: " + sub + ", EndPos " + position.end);
                if (dict.get(i).equals(sub))
                {
                    inDict = true;
                    dict_index = i;
                    last_char = sequence.charAt(position.end);
                    position.end++;
                    break;
                }
            }
            if (!inDict){break;}
        }
        Pair new_Pair = new Pair(dict_index, last_char);
        return new_Pair;
    }


        public static String read_txt(String file_name) throws IOException{
        
        StringBuilder extracted = new StringBuilder();
        File text_File = new File(file_name);
        Scanner reader = new Scanner(text_File);

        while(reader.hasNextLine()){
            extracted.append(reader.nextLine());
        
        }
        reader.close();

        return extracted.toString();
    }

    public static byte[] booleanize(ArrayList<Pair> tags)
    {
        int len_tags = tags.size();
        int i_size = (int) Math.ceil(Math.log(len_tags) / Math.log(2));
        int c_size = 8;
        
        int totalBits = (len_tags * (i_size + c_size)) + 64;

        BitSet bits = new BitSet(totalBits);
        int bitPos = 0;

        // len_tags header: read first thing in the boolean set,
        // which should give the decoder a heads-up on how many
        // tags there are to read.
        for (int i=31; i>=0; i--)
        {
            bits.set(bitPos++, ((len_tags >> i) & 1) == 1);
        }

        // tag_size header: read after the len_tags header,
        // indicates the size (number of bits) of each tag.
        for (int i=31; i>=0; i--) {
            bits.set(bitPos++, ((i_size >> i) & 1) == 1);
        }

            // write tags
            for (Pair tag : tags) {
                //write dictionary index using i_size bits
                for (int i = i_size - 1; i >= 0; i--) {
                    bits.set(bitPos++, ((tag.i >> i) & 1) == 1);
                }
                
                // write character using 8 bits
                for (int i = 7; i >= 0; i--) {
                    bits.set(bitPos++, ((tag.c >> i) & 1) == 1);
                }
            } 
            return bits.toByteArray();
    }


    // implements the LZ78 algorithm as follows:
    // reads the sequence stream, performs a compare 
    // operation on all dictionary members, appends new
    // sequences accordingly, and finally stores all the 
    // dictionary tags in a binary file (.bin)
    public static ArrayList<Pair> encoder(String raw_file, String encoded_file) throws IOException{
        
        ArrayList<String> dict = new ArrayList<>();
        ArrayList<Pair> tag_list = new ArrayList<>();
        
        Pos pos = new Pos(0, 1);

        String sequence = read_txt(raw_file);
        
        while (pos.end <= sequence.length())
        {
            Pair tag = compare(pos, sequence, dict);
            tag_list.add(tag);

            if (tag.i == -1){
                dict.add("" + tag.c);
            }
            else{
                dict.add(dict.get(tag.i) + tag.c);
            }

            pos.start = pos.end - 1;
        }

        return tag_list;
    }

    // moves through binary file, reading fixed size 
    // of bits, that is determined by (p + c), where p is
    // the number of bits it takes to store the length of
    // the dictionary, and c is the number of bits required
    // to store the character, which should be 8 bits (a byte)
    public static boolean decoder(String encoded_file, String decoded_file, ArrayList<String> dict){
        return true;
    }

    public static void main(String[] args) {
        // Test the compare method
        System.out.println("Testing compare method:");
        ArrayList<String> testDict = new ArrayList<>();
        testDict.add("a");
        testDict.add("ab");
        testDict.add("bac");
        
        Pos position = new Pos(0, 1);
        String testSequence = "abacus";
        Pair result = compare(position, testSequence, testDict);
        System.out.println("Result for 'abacus': (dict_index=" + result.i + ", last_char=" + result.c + ")");
        System.out.println("End: " + position.end);
        System.out.println("Matched String: " + testDict.get(result.i) + "\n\n");
        
        // Test with different starting position
        position.start = position.end-1;
        result = compare(position, testSequence, testDict);
        System.out.println("Result starting from position 2: (dict_index=" + result.i + ", last_char=" + result.c + ")");
        System.out.println("End: " + position.end);
        System.out.println("Matched String: " + testDict.get(result.i));
    }
}
