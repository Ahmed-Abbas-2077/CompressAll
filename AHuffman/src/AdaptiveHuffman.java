package AHuffman.src;

import Util.Util;

// - [ ] AdaptiveHuffman.java
// 	- Test three strings (First Amendment (US Bill of Rights), DNA, "Wealth of Nations" excerpt)
// 	- vocabularies (attr, a list of vocabularies, which is a newly defined data structure, containing the original file path, the compressed/encoded file path, and the vocabulary of the text)
// 	- Allow user to dynamically specify which text file they would like to compress.
// 	- Display compression statistics w. timing.

public class AdaptiveHuffman {
    public static void main(String[] args) {
        String textFile1 = "test/DNA.txt";
        String textFile2 = "test/FA.txt";
        String textFile3 = "test/WoN.txt";

        String binFile1 = "test/DNA.bin";
        String binFile2 = "test/FA.bin";
        String binFile3 = "test/WoN.bin";

        String decodedFile1 = "test/DNA_decoded.txt";
        String decodedFile2 = "test/FA_decoded.txt";
        String decodedFile3 = "test/WoN_decoded.txt";

        // encode
        Encoder encoder = new Encoder();
        encoder.encode(textFile1, binFile1);
        encoder.encode(textFile2, binFile2);
        encoder.encode(textFile3, binFile3);

        // decode
        Decoder decoder = new Decoder();
        decoder.decode(binFile1, decodedFile1, Util.getVocab(Util.readText(textFile1)));
        decoder.decode(binFile2, decodedFile2, Util.getVocab(Util.readText(textFile2)));
        decoder.decode(binFile3, decodedFile3, Util.getVocab(Util.readText(textFile3)));

        // display compression statistics
        Util.printCompressionStatistics(textFile1, binFile1, decodedFile1);
        Util.printCompressionStatistics(textFile2, binFile2, decodedFile2);
        Util.printCompressionStatistics(textFile3, binFile3, decodedFile3);
    }
}
