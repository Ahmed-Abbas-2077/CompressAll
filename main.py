from create_DNA import create_dna
from LZ77 import LZ77


"""
Modify the encoder and decoder to deal with files instead of lists and strings
Remember to add interactive user choice of files to be compressed or decompressed
Add a marker to know whether a file is compressible or decompressible
"""


def main():
    # creates a random file of a DNA sequence
    FILE_NAME = "dna.txt"
    LENGTH = 10000
    create_dna(FILE_NAME, LENGTH)

    # It is preferred that you experiment with
    # these parameters to see what works best.
    SEARCH_SIZE = 8
    LOOK_AHEAD_SIZE = 7

    # This is the list of tags or compressed representations
    # of the files recurring patterns (tuples <o,l,c>),
    # o: offset, l: length, c: character;
    tag_list = []

    # reads the file into one long string
    with open(FILE_NAME, 'r') as dna:
        dna_sequence = dna.read()

    LZ77.encoder(dna_sequence, tag_list, SEARCH_SIZE, LOOK_AHEAD_SIZE)

    # for i in tag_list:
    #     print(i)

    print("\n\n Tag List Size: ", len(tag_list))

    # Encoder
    decoded_sequence = LZ77.decoder(tag_list)

    print("\nTag List:\n", tag_list)
    print("\nOriginal Sequence:", dna_sequence)
    print("Decoded Sequence: ", decoded_sequence)
    print("\nSize of Decoded Sequence:", len(decoded_sequence))
    print("Do They Match? :", (dna_sequence == decoded_sequence))


if __name__ == "__main__":
    main()
