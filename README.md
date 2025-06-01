# CompressAll

CompressAll is a Java library that provides a simple and efficient way to compress and decompress files using various compression algorithms. We implement LZ77 and LZ78 dictionary-based compression algorithms, as well as standard and adaptive Huffman coding for lossless data compression. Moreover, we include vector and tensor quantization with YUV translation and chrominance sub-sampling, and differential pulse code modulation (DPCM) for image compression.

# Features
- **LZ77 and LZ78 Compression**: Efficient dictionary-based compression algorithms.
- **Huffman Coding**: Standard and adaptive Huffman coding for lossless data compression.
- **Vector and Tensor Quantization**: Includes YUV translation and chrominance sub-sampling.
- **Differential Pulse Code Modulation (DPCM)**: For predictive image compression.
- **File Handling**: Supports reading and writing files in various formats.
- **Java Implementation**: Pure Java implementation for easy integration into Java applications.


# Usage

Download the repo as follows:
```bash
git clone https://github.com/Ahmed-Abbas-2077/CompressAll.git
```

Then you can find the implementation of each algorithm in its respective folder. For example, to use the LZ77 compression algorithm, you can refer to the `LZ77` folder and its corresponding Java files.


# Example
```java
java VecQuant/main.java <input_file> <output_file>
```

# Requirements
- Java 8+
- Maven for dependency management

# Notes
- I use non-uniform histogram quantization for vector quantization.
- Some implementations, such as that of the Multi_VQ may consume a lot of memory, so be cautious with large files. I know it needs optimization, but I haven't had the time to do so yet.
- The code is designed to be modular, so you can easily extend it with additional compression algorithms or features as needed-- checkout the Util folder if your interested.

# License
No license is specified, so please check the repository for any updates regarding licensing, or don't and use it at your own risk ;).