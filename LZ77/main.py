from create_DNA import create_dna
from LZ77 import LZ77
import os


"""
Modify the encoder and decoder to deal with files instead of lists and strings
Remember to add interactive user choice of files to be compressed or decompressed
Add a marker to know whether a file is compressible or decompressible
"""

# We are going to set a convention here:
# any compressible file should end with cmp,
# any decompressible file ends with dcmp,
# any other file name would be invalid


def stats(original_file, encoded_file, decoded_file):
    original = LZ77.read_txt(original_file)
    decoded = LZ77.read_txt(decoded_file)
    encoded = LZ77.read_tags(encoded_file)

    original_size = os.path.getsize(original_file)
    encoded_size = os.path.getsize(encoded_file)

    print("\nDo they match?:", (original == decoded))
    print("\nCompression Ratio:", encoded_size/original_size)


def main():

    LENGTH = 1000
    create_dna("raw_cmp.txt", LENGTH)

    SEARCH_SIZE = 32
    LOOK_AHEAD_SIZE = 31

    while True:
        user_choice = input(
            "What would you like to do\n Compress File: 1\n Decompress File: 2\n Compare Files: 3\n")
        user_choice = int(user_choice)
        while user_choice not in (1, 2, 3):
            user_choice = input(
                "Please choose a valid option\n Compress File: 1\n Decompress File: 2\n Compare Files: 3\n")
        if user_choice == 1:
            user_input_1 = input("What file would you like to compress?:\n")
            user_input_2 = input("What file would you like to save to?:\n")
            LZ77.encoder(user_input_1, user_input_2,
                         SEARCH_SIZE, LOOK_AHEAD_SIZE)
        elif user_choice == 2:
            user_input_1 = input("What file would you like to decompress?:\n")
            user_input_2 = input("What file would you like to save to?:\n")
            LZ77.decoder(user_input_1, user_input_2)
        else:
            user_input_1 = input("What's the original file?:\n")
            user_input_2 = input("What's the encoded file?:\n")
            user_input_3 = input("What's the decoded file?:\n")
            stats(user_input_1, user_input_2, user_input_3)

    # creates a random file of a DNA sequence
    # RAW_NAME = "raw_cmp.txt"
    # ENCODED_NAME = "encoded_dcmp.txt"
    # DECODED_NAME = "decoded_cmp.txt"
    # LENGTH = 100
    # create_dna(RAW_NAME, LENGTH)

    # It is preferred that you experiment with
    # these parameters to see what works best.
    # SEARCH_SIZE = 8
    # LOOK_AHEAD_SIZE = 7

    # This is the list of tags or compressed representations
    # of the files recurring patterns (tuples <o,l,c>),
    # o: offset, l: length, c: character;
    # tag_list = []

    # reads the file into one long string
    # with open(READ_NAME, 'r') as dna:
    #     dna_sequence = dna.read()

    # LZ77.encoder(RAW_NAME, ENCODED_NAME, SEARCH_SIZE, LOOK_AHEAD_SIZE)

    # for i in tag_list:
    #     print(i)

    # print("\n\n Tag List Size: ", len(LZ77.read_tags(ENCODED_NAME)))

    #
    # LZ77.decoder(ENCODED_NAME, DECODED_NAME)

    # print("\nTag List:\n", LZ77.read_tags(ENCODED_NAME))
    # print("\nOriginal Sequence:", LZ77.read_txt(RAW_NAME))
    # print("Decoded Sequence: ", LZ77.read_txt(DECODED_NAME))
    # print("\nSize of Decoded Sequence:", len(LZ77.read_txt(DECODED_NAME)))
    # print("Do They Match? :", (LZ77.read_txt(
    #     RAW_NAME) == LZ77.read_txt(DECODED_NAME)))


if __name__ == "__main__":
    main()
