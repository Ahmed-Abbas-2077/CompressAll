import random


# DNA is made of Adenine, Thymine, Cytosine, and Guanine.
# I chose DNA bases as a vocabulary cause they are only 4 letters.
# As such, there is a lot of redundancy and recurring patterns, and those
# occur frequently enough that they can be efficiently compressed using LZ77.

FILE_NAME = "dna.txt"

# We can experiment a bit with this number to see
# which value makes for a more compressible file (higher compression rate).
LENGTH = 10000


# I have modularized the previous functionality into a
def create_dna(file_name, len):
    bases = ['A', 'T', 'C', 'G']
    with open(file_name, 'w') as file:
        dna_sequence = ''.join(random.choices(bases, k=len))
        file.write(dna_sequence)


# This call is merely for illustration; the function is imported and called by LZ77.py
# create_dna(FILE_NAME, LENGTH)

# Nice! By now, we should have a text file with a DNA sequence
# that we can accordingly compress using LZ77.
