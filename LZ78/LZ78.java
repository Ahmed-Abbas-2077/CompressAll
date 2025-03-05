package LZ78;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Scanner;

public class LZ78 {


    // This helper method compares all elements of the 
    // dictionary to see which of them has the longest 
    // match with the string starting from pos until the 
    // character just before c, where c is the character 
    // that makes the string unmatchable by any element 
    // in the dictionary
    public static Pair compare(Pos position, String sequence, ArrayList<String> dict){
        int dict_index = 0;
        char last_char = '\0';
        
        // initialize last_char to the first character at the current position
        if (position.start < sequence.length()) {
            last_char = sequence.charAt(position.start);
        }
        
        // base case: if we're at the end of the sequence
        if (position.end > sequence.length()) {
            position.end = sequence.length();
            System.out.println("End of sequence reached with position > length");
            return new Pair(dict_index, last_char);
        }
        
        boolean matched = true;

        String current = sequence.substring(position.start, position.end);

        while (matched){
            
            matched = false;

            if (position.end > sequence.length()){
                System.out.println("Reached end of Sequence");
                break;
            }
            // get the current substring
            current = sequence.substring(position.start, position.end);
            System.out.println("Comparing substring: '" + current + "' (pos " + position.start + "-" + position.end + ")");
            
            // first check if the current substring is in the dictionary
            for (int i = 1; i < dict.size(); i++) {
                if (dict.get(i).equals(current)) {
                    dict_index = i;
                    System.out.println("  Found match in dictionary at index " + i + ": '" + dict.get(i) + "'");
                    
                    // if we can, move to the next character
                    if (position.end < sequence.length()) {
                        last_char = sequence.charAt(position.end);
                        position.end++;
                        System.out.println("  Extending substring with char: '" + last_char + "', new end: " + position.end);
                    } else {
                        last_char = '\0';
                        position.end++;
                        System.out.println("  At end of sequence, extended with null char \\0");
                    }
                    matched = true;
                    break;
                }
            }
        }
        
        if (dict_index == 0) {
            System.out.println("  No match found in dictionary for: '" + current + "'");
        }
        
        return new Pair(dict_index, last_char);
    }


    public static boolean write_bool(byte[] bytes, String file_name) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file_name)) {
            fos.write(bytes);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return false;
        }
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
    public static boolean encoder(String raw_file, String encoded_file) throws IOException {
        ArrayList<String> dict = new ArrayList<>();
        ArrayList<Pair> tag_list = new ArrayList<>();
        
        Pos pos = new Pos(0, 1);
        String sequence = read_txt(raw_file);
        System.out.println("Encoding sequence of length: " + sequence.length());
        
        while (pos.end <= sequence.length()) {
            Pair tag = compare(pos, sequence, dict);
            tag_list.add(tag);
    
            if (tag.i == 0) {
                dict.add("" + tag.c);
            } else {
                dict.add(dict.get(tag.i) + tag.c);
            }
    
            // This is where the fix is needed
            if (pos.end >= sequence.length()) {
                // If we've already processed the last character, exit the loop
                System.out.println("Reached end of sequence, breaking out of loop");
                break;
            }
            
            // Update position for next iteration
            pos.start = pos.end;
            pos.end = pos.start + 1;
            System.out.println("Updated position to: start=" + pos.start + ", end=" + pos.end);
        }
    
        System.out.println("Encoding complete with " + dict.size() + " dictionary entries");
        byte[] bool_tags = booleanize(tag_list);
        write_bool(bool_tags, encoded_file);
    
        return true;
    }

    public static ArrayList<Pair> read_bool(String file_name) throws IOException {
        File file = new File(file_name);
        byte[] bytes = new byte[(int) file.length()];
        ArrayList<Pair> tags = new ArrayList<>();
    
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            return new ArrayList<>();  // Return empty list instead of null
        }
    
        BitSet bits = BitSet.valueOf(bytes);
        int bitPos = 0;
    
        // Read len_tags header
        int len_tags = 0;
        for (int i = 31; i >= 0; i--) {
            if (bits.get(bitPos++)) {
                len_tags |= (1 << i);
            }
        }
        System.out.println("Reading " + len_tags + " tags");
    
        // Read i_size header
        int i_size = 0;
        for (int i = 31; i >= 0; i--) {
            if (bits.get(bitPos++)) {
                i_size |= (1 << i);
            }
        }
        System.out.println("Tag index size: " + i_size + " bits");
    
        // Read tags
        int tagCount = 0;
        while (bitPos < bits.length() && tagCount < len_tags) {
            int dict_index = 0;
            for (int i = i_size - 1; i >= 0; i--) {
                if (bitPos < bits.length() && bits.get(bitPos++)) {
                    dict_index |= (1 << i);
                }
            }
    
            char c = 0;
            for (int i = 7; i >= 0; i--) {
                if (bitPos < bits.length() && bits.get(bitPos++)) {
                    c |= (1 << i);
                }
            }
            
            System.out.println("Read tag: index=" + dict_index + ", char='" + c + "'");
            tags.add(new Pair(dict_index, c));
            tagCount++;
        }
    
        return tags;
    }

    // moves through binary file, reading fixed size 
    // of bits, that is determined by (p + c), where p is
    // the number of bits it takes to store the length of
    // the dictionary, and c is the number of bits required
    // to store the character, which should be 8 bits (a byte)
    public static boolean decoder(String encoded_file, String decoded_file, ArrayList<String> dict) {
        ArrayList<Pair> tags = new ArrayList<>();
    
        try {
            tags = read_bool(encoded_file);
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
            return false;
        }
        
        // Clear the dictionary in case it has data
        // dict.clear();
        
        StringBuilder decoded = new StringBuilder();
        for (Pair tag : tags) {
            System.out.println("Processing tag: index=" + tag.i + ", char='" + tag.c + "'");
            
            String entry;
            if (tag.i == 0) {
                // For new characters not seen before
                entry = String.valueOf(tag.c);
            } else {
                // Check if index is valid
                if (tag.i >= dict.size()) {
                    System.err.println("Error: Invalid dictionary reference: " + tag.i + 
                                       " (dict size: " + dict.size() + ")");
                    return false;
                }
                entry = dict.get(tag.i) + tag.c;
            }
            
            // Add to decoded string
            decoded.append(entry);
            
            // Add to dictionary for future lookups
            dict.add(entry);
            System.out.println("Added to dictionary: '" + entry + "' at index " + (dict.size()-1));
        }
    
        try (FileOutputStream fos = new FileOutputStream(decoded_file)) {
            fos.write(decoded.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return false;
        }
    
        return true;
    }

    public static void stats(String raw_file, String encoded_file, String decoded_file) throws IOException{
        String raw = read_txt(raw_file);
        String decoded = read_txt(decoded_file);
        ArrayList<Pair> tags = read_bool(encoded_file);

        System.out.println("Raw file size: " + raw.length());
        System.out.println("Encoded file size: " + tags.size());
        System.out.println("Decoded file size: " + decoded.length());
        System.out.println("Compression ratio: " + (double) tags.size() / raw.length());
    }

    public static void main(String[] args) {
        String raw_file = "sample.txt";
        String encoded_file = "encoded.bin";
        String decoded_file = "decoded.txt";

        try {
            encoder(raw_file, encoded_file);
            ArrayList<String> dict = new ArrayList<>();
            decoder(encoded_file, decoded_file, dict);
            stats(raw_file, encoded_file, decoded_file);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
