package SHuffman;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import Util.Util;

public class Test {
    public static void main(String[] args){
        // test write_txt
        String txt_file = "test.txt";
        String content = "haowifhwafiwaoeighapwifghpwaeifuhwaepighwpgiuehqoihgqowieurfgfawefewqfdwaedfadwq";
        Util.writeText(txt_file, content);

        // test read_txt
        String extracted = Util.readText(txt_file);
        System.out.println("The extracted content is: " + extracted);

        // test write_binary
        String bin_file = "test.bin";
        byte[] byted = content.getBytes(StandardCharsets.UTF_8);
        Util.writeBinary(bin_file, byted);

        Map<Character, String> encoding = new HashMap<>();
        // test the encoder function
        SHuff.encoder(txt_file, bin_file, encoding);
        
        // test read_binary
        byte[] bin = Util.readBinary(bin_file);
        String bin_str = Util.textify(bin);
        System.out.println("Binary Code: " + bin_str);

    }
}
