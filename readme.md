# Huffman File Compression in Java

This project implements **Huffman Coding**, a lossless data compression algorithm, in **Java**. It can compress and decompress text files, making it suitable for reducing file sizes while ensuring full data recovery.

---

## 📁 Files

- `Huffman.java`: Main logic for encoding and decoding using Huffman Trees
- `Encode.java`: Compresses an input text file
- `Decode.java`: Decompresses a previously compressed `.huf` file
- `inputFile.txt`: Example input file
- `compressedFile.huf`: Binary compressed output
- `outputFile.txt`: Final output (should match input exactly)

---

## 🚀 Features

✅ Compress any plain text file  
✅ Fully lossless decompression  
✅ Binary-safe compressed `.huf` format  
✅ Efficient Huffman Tree construction using a min-heap  
✅ Displays file sizes before and after operations for validation  

---

## 📦 How It Works

Huffman Coding assigns shorter binary codes to more frequent characters and longer codes to less frequent ones. It works in two major phases:

### 1. Compression
- Reads character frequencies
- Builds a binary Huffman Tree using a Min Heap
- Generates binary codes for each character
- Encodes input into binary and writes the encoded file with tree metadata

### 2. Decompression
- Reconstructs the Huffman Tree from metadata
- Reads the binary file and decodes the bits using the tree
- Writes the original text into a new output file

---

## 🖥️ How to Run

### 🧵 Compile
javac Huffman.java Encode.java Decode.java


📚 Compress a File
    java Encode inputFile.txt compressedFile.huf
    
    Sample Output:
        Compressed successfully.
        
        Input File Size     : 5120 bytes
        
        Compressed File Size: 2048 bytes

📂 Decompress a File
    java Decode compressedFile.huf outputFile.txt
    
    Sample Output:
        Decompressed successfully.
        
        Compressed File Size: 2048 bytes
        
        Output File Size    : 5120 bytes

✅ Verify Output
Ensure that outputFile.txt matches inputFile.txt exactly.

🧠 Core Concepts Used
      Huffman Tree: A binary tree with frequency-based node placement.
      
      Priority Queue (Min Heap): Used to build the tree with minimal cost.
      
      Binary Encoding: Characters are replaced with their respective binary codes.
      
      Bit Manipulation: Compressing into and reading from binary format.
      
      File I/O (Binary + Text): Reads/writes both binary and plain-text files.

📊 Example
    File	Size (Bytes)
    
    inputFile.txt	5120
    
    compressedFile.huf	2048
    
    outputFile.txt	5120

📚 Use Cases
    Text file compression (logs, data, documents)
    
    Learning Huffman algorithms
    
    Benchmarking custom compression
    
    Demonstrating lossless compression techniques in Java

📎 Future Enhancements
    Add support for all 256 ASCII characters
    
    GUI interface using JavaFX or Swing
    
    File checksum verification post-decompression
    
    Folder compression support
