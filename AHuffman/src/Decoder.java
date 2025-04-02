package AHuffman.src;

import java.util.HashMap;
import java.util.Map;

import Util.Util;

// - [ ] Decoder.java
// 	- bool Decode(String binPath, String textPath) (reads binary string, decodes it by incrementally reading the binary string and decoding the characters within, returns true if decoding finished correctly, raises error o.w.)

public class Decoder {
    
    public Boolean decode(String binPath, String textPath, Map<Character, String> vocab) {
        byte[] binaryData = Util.readBinary(binPath);
        if (binaryData == null) {
            System.err.println("Error reading binary data.");
            return false;
        }
        String binaryString = Util.textify(binaryData);

        // System.out.println("Binary string read from file: " + binaryString);
        // System.out.println("Vocabulary: ");
        // for (Map.Entry<Character, String> entry : vocab.entrySet()) {
        //     System.out.println(entry.getKey() + ": " + entry.getValue());
        // }

        AHuffman.src.HuffmanTree tree = new AHuffman.src.HuffmanTree(vocab);
        int e = vocab.values().iterator().next().length();
        
        Map<String, Character> reverseVocab = new HashMap<>();
        for (Map.Entry<Character, String> entry : vocab.entrySet()) {
            reverseVocab.put(entry.getValue(), entry.getKey());
        }

        System.out.println("Reverse vocabulary: ");
        for (Map.Entry<String, Character> entry : reverseVocab.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        StringBuilder decodedText = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();
        int index = 0;

        while (index < binaryString.length()) {

            System.out.println("Current code: " + currentCode.toString());
            Node decodedNode = tree.searchCode(tree.root, currentCode.toString());
            System.out.println("Decoded node: " + decodedNode);
            if (decodedNode != null) {
                if (decodedNode.character != null) {
                    decodedText.append(decodedNode.character);
                    tree.updateTree(decodedNode.character); // update the tree with the new character   
                    currentCode.setLength(0); // reset current code
                } else if (decodedNode == tree.NYT) {
                    String charCode = binaryString.substring(index, index + e);
                    index += e; // move index forward by e bits
                    
                    Character newChar = reverseVocab.get(charCode);
                    decodedText.append(newChar);
                    tree.updateTree(newChar); // update the tree with the new character
                    
                    currentCode.setLength(0);
                } 
            }
            currentCode.append(binaryString.charAt(index));
            index++;
        }
        Util.writeText(textPath, decodedText.toString());
        return true;
    }
}
