

class LZ77:

    @staticmethod
    def validate_input(file_name: str) -> int:
        # validates file name
        if file_name.endswith("dcmp.txt"):
            return 2
        elif file_name.endswith("cmp.txt"):
            return 1
        else:
            return 0

    @staticmethod
    def read_txt(file_name: str) -> str:
        if LZ77.validate_input(file_name) != 1:
            raise ValueError(
                "The input file must be compressible (ends with cmp.txt)")
        extracted = ""
        with open(file_name, 'r') as file:
            extracted = file.read()
        return extracted

    @staticmethod
    def write_txt(file_name: str, decoded: str) -> bool:
        if LZ77.validate_input(file_name) != 1:
            raise ValueError(
                "The input file must be compressible (ends with cmp.txt)")

        with open(file_name, 'w') as file:
            file.write(decoded)
            return True
        return False

    @staticmethod
    def read_tags(file_name: str) -> list[tuple]:
        if LZ77.validate_input(file_name) != 2:
            raise ValueError(
                "The input file must be decompressible (ends with dcmp.txt)")

        # we will assume that tags are split by :
        with open(file_name, 'r') as file:
            tags_txt = file.read().split(":")
            print("Tags:\n", tags_txt)
            tags = [(int(tag[1]), int(tag[3]), tag[5]) if tag[5] != ")" else (
                int(tag[1]), int(tag[3]), '') for tag in tags_txt]
            return tags
        return None

    @staticmethod
    def write_tags(file_name: str, tags: list[tuple]) -> bool:
        if LZ77.validate_input(file_name) != 2:
            raise ValueError(
                "The input file must be decompressible (ends with dcmp.txt)")
        with open(file_name, 'w') as file:
            tag_list = [
                f"({tag[0]},{tag[1]},{tag[2] if tag[2] != '' else ''})" for tag in tags]
            text = ":".join(tag_list)
            file.write(text)
            return True
        return False

    # This is a function for encoding the first letters in the sequence (window_len)
    @staticmethod
    def encoder(read_file: str, write_file: str, search_len: int, look_ahead_len: int) -> None:
        """
            Some terminology before we proceed ahead

            sequence: Initial search buffer to be built. Acts as a look-ahead for now.
            temp_win: an empty list at first, but builds up as we move through initial window
        """

        tag_list = []

        sequence = LZ77.read_txt(read_file)
        search_start = 0
        look_ahead_start = 0
        # while look_ahead_start < search_start + search_len:
        # ----temp_win = []
        # ----(matching position (relative to window), length of the matching)
        while look_ahead_start < len(sequence):
            # we should be storing matchings of a single look-ahead instance in here
            # it was originally outside the while loop... A Huge Mistake It Was!
            matchings = []

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
                if (look_ahead_start+1) <= len(sequence):
                    look_ahead_start += 1
                else:
                    break
                print(tag_list[len(tag_list)-1])
            else:
                offset = longest_match[0]
                length = longest_match[1]
                next_char_pos = look_ahead_start + length
                if next_char_pos >= len(sequence):
                    tag_list.append((offset, length, ''))
                else:
                    tag_list.append((offset, length, sequence[next_char_pos]))

                # there is a minor issue with this logic:
                # it is possible that the de facto size
                # of the search buffer is increased than
                # what was originally intended (search_len).
                # If the de facto search size is still behind
                # search_len, then the look-ahead might increment
                # without incrementing the search_start, causing
                # the search buffer size to unintentionally expand.
                # Although it is a minor pet peeve, it's still
                # worth pointing out.
                if (look_ahead_start-search_start) > search_len:
                    search_start += length + 1
                if (look_ahead_start+length+1) <= len(sequence):
                    look_ahead_start += length + 1
                else:
                    break
                print(tag_list[len(tag_list)-1])
        LZ77.write_tags(write_file, tag_list)

    @staticmethod
    def decoder(read_file: str, write_file: str) -> bool:
        tag_list = LZ77.read_tags(read_file)
        decoded = []
        for tag in tag_list:
            start = len(decoded) - tag[0]
            length = tag[1]
            for i in range(start, start+length):
                decoded.append(decoded[i])
            decoded.append(tag[2])
        if decoded[len(decoded)-1] == None:
            decoded = decoded[:len(decoded)-1]
        print(decoded)
        sequence = ''.join(decoded)
        return LZ77.write_txt(write_file, sequence)
