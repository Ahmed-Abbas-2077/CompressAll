package AHuffman.src;

import Util.Util;
import java.util.Map;



public class Encoder {

    

    public Boolean encode(String textPath, String binPath) {
        String text = Util.readText(textPath);
        if (text == null) {
            return false;
        }

        Map<Character, String> vocab = Util.getVocab(text);
        StringBuilder encodedText = new StringBuilder();
        AHuffman.src.HuffmanTree tree = new AHuffman.src.HuffmanTree(vocab);

        for (char c : text.toCharArray()) {
            if (tree.encounter.get(c) == null) {
                encodedText.append(tree.NYT.code);
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
