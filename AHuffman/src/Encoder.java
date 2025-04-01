package AHuffman.src;

import Util.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


public class Encoder {

    public Map<Character, String> getVocab(String text) {
        Map<Character, String> vocab = new HashMap<>();
        Set<Character> chars = new HashSet<>();

        for (char c : text.toCharArray()) {
            chars.add(c);
        }

        int length = (int) Math.ceil(Math.log(chars.size()) / Math.log(2));
        int index = 0;

        for (char c : chars) {
            StringBuilder code = new StringBuilder(Integer.toBinaryString(index++));
            while (code.length() < length) {
                code.insert(0, '0');
            }
            vocab.put(c, code.toString());
        }

        return vocab;
    }

    public Boolean encode(String textPath, String binPath) {
        String text = Util.readText(textPath);
        if (text == null) {
            return false;
        }

        Map<Character, String> vocab = getVocab(text);
        StringBuilder encodedText = new StringBuilder();
        AHuffman.src.HuffmanTree tree = new AHuffman.src.HuffmanTree(vocab);

        for (char c : text.toCharArray()) {
            if (tree.encounter.get(c) == null) {
                encodedText.append(tree.vocabulary.get(c));
            } else {
                encodedText.append(tree.search(tree.root, c).code);
            }

            tree.updateTree(c);
        }
        byte[] binaryData = Util.binarize(encodedText.toString());
        Util.writeBinary(binPath, binaryData);
        return true;
    }
}
