import random


# DNA is made of Adenine, Thymine, Cytosine, and Guanine
# I choose DNA bases as vocabulary as it is only composed of 4 letters
# meaning that there is a lot of redundancy and recurring patterns, and those
# occur frequently enough that they can be efficiently enough using LZ77

BASES = ['A', 'T', 'C', 'G']
FILE_NAME = "dna.txt"
# We can experiment a bit with this number to see which value makes for a better compressible file
LENGTH = 10000


with open(FILE_NAME, 'w') as file:
    dna_sequence = ''.join(random.choices(BASES, k=LENGTH))
    file.write(dna_sequence)
