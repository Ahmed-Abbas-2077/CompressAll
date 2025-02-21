

# This is a function for encoding the first letters in the sequence (window_len)
def encoder(sequence: str, tag_list: list[tuple], search_len: int, look_ahead_len: int):
    """
        Some terminology before we proceed ahead

        sequence: Initial search buffer to be built. Acts as a look-ahead for now.
        temp_win: an empty list at first, but builds up as we move through initial window
    """

    search_start = 0
    look_ahead_start = 0
    # while look_ahead_start < search_start + search_len:
    # ----temp_win = []
    # ----(matching position (relative to window), length of the matching)
    matchings = []
    while look_ahead_start < len(sequence):
        print("\n---------------------------")
        # printing the look-ahead buffer
        print("look-ahead buffer from", look_ahead_start,
              "to", look_ahead_start+look_ahead_len, "\n[", end="")
        for i in range(look_ahead_start, min(look_ahead_start+look_ahead_len, len(sequence))):
            print(sequence[i], end="")
        print("]")
        # end of printing

        # printing search buffer
        print("\nsearch buffer from", search_start,
              "to", look_ahead_start, "\n[", end="")
        for i in range(search_start, look_ahead_start):
            print(sequence[i], end="")
        print("]")

        for j in range(search_start, look_ahead_start):
            if sequence[look_ahead_start] == sequence[j]:
                print("Matching Positions:")
                print("LA:", look_ahead_start, "SB:", j)
                look_pos = look_ahead_start  # matching position at the buffer
                srch_pos = j  # matching position at the window
                matching_length = 0
                look_ahead_limit = min(
                    len(sequence), look_ahead_start + look_ahead_len)
                while ((look_pos < look_ahead_limit) and (sequence[look_pos] == sequence[srch_pos])):
                    matching_length += 1
                    look_pos += 1
                    srch_pos += 1

                print("\nMatch(o,l):", (look_ahead_start -
                      j, matching_length), "\n [", end="")
                for k in range(j, j+matching_length):
                    print(sequence[k], end="")
                print("]\n")
                # [abbaababa]|abaabbbb|
                matchings.append((look_ahead_start-j, matching_length))
                # j = srch_pos-1

                print("look_pos:", look_pos)
                print("srch_pos:", srch_pos)

        # Looking for the longest matching
        longest_match = max(matchings, key=lambda x: x[1], default=None)
        if not longest_match:
            tag_list.append((0, 0, sequence[look_ahead_start]))
            if (look_ahead_start-search_start) > search_len:
                search_start += 1
            if (look_ahead_start+1) < len(sequence):
                look_ahead_start += 1
            else:
                break
            print(tag_list[len(tag_list)-1])
        else:
            offset = longest_match[0]
            length = longest_match[1]
            next_char_pos = look_ahead_start + length
            if next_char_pos >= len(sequence):
                tag_list.append((offset, length, None))
            else:
                tag_list.append((offset, length, sequence[next_char_pos]))
            if (look_ahead_start-search_start) > search_len:
                search_start += length + 1
            if (look_ahead_start+length+1) < len(sequence):
                look_ahead_start += length + 1
            else:
                break
            print(tag_list[len(tag_list)-1])
