// Huffman.java (Updated for full Unicode character support)
import java.io.*;
import java.util.*;

class Node {
    char data;
    int freq;
    String code = "";
    Node left, right;

    Node() {}
    Node(char data, int freq) {
        this.data = data;
        this.freq = freq;
        left = right = null;
    }
}

public class Huffman {
    private final Map<Character, Integer> freqMap = new HashMap<>();
    private final String inFileName, outFileName;
    private Node root;
    private final PriorityQueue<Node> minHeap = new PriorityQueue<>(new Comparator<Node>() {
        @Override
        public int compare(Node n1, Node n2) {
            return Integer.compare(n1.freq, n2.freq);
        }
    });
    private final Map<Character, String> huffmanCodes = new HashMap<>();

    public Huffman(String inFileName, String outFileName) {
        this.inFileName = inFileName;
        this.outFileName = outFileName;
    }

    private void createMinHeap() throws IOException {
        FileInputStream fis = new FileInputStream(inFileName);      // Reads bytes from the input file one at a time (binary mode)
        int b;
        while ((b = fis.read()) != -1) {    // read() returns the byte value (0–255); if file is text, it maps to ASCII/UTF-8 char 
            char ch = (char) b;             // explicit conversion from int -> char , ex. 97 => 'a'
            freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);       
        }
        fis.close();

        // Creating the minHeap (priority queue) using character frequencies
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            minHeap.add(new Node(entry.getKey(), entry.getValue()));
        }
    }

    /*
     * Constructs the Huffman Tree using the min-heap of frequency nodes.
     * 
     * Process:
     * 1. Copy the existing minHeap into a temporary priority queue.
     * 2. While more than one node remains:
     *    a. Remove the two nodes with the lowest frequencies.
     *    b. Create a new parent node with frequency = sum of both nodes.
     *    c. Set the left and right children of the parent to these two nodes.
     *    d. Add the parent node back into the priority queue.
     * 3. The remaining node in the queue is the root of the Huffman Tree.
    */
    private void createTree() {
        PriorityQueue<Node> tempPQ = new PriorityQueue<>(minHeap);

        while (tempPQ.size() > 1) {
            Node left = tempPQ.poll();   // lowest frequency node
            Node right = tempPQ.poll();  // second lowest

            Node parent = new Node();    // internal node (no character, only frequency)
            parent.freq = left.freq + right.freq;
            parent.left = left;
            parent.right = right;

            tempPQ.add(parent);          // insert new parent back into the queue
        }

        root = tempPQ.peek();            // final node is the root of the Huffman Tree
    }

    /*
     * Traverse the entire tree recursively and generate updated binary code representation for each character
     * Start with empty string from root
     *      when traversing left, append 0
     *      when traversing right, append 1
     * When leaf node is reached, the character and its binary code are then stored in the huffmanCodes map.
     */
    private void createCodes() {
        traverse(root, new StringBuilder(""));
    }

    private void traverse(Node node, StringBuilder sb) {
        if (node.left == null && node.right == null) {  // It's a leaf node
            node.code = sb.toString();                  // Set the binary code
            huffmanCodes.put(node.data, node.code);     // Map character to its code
            return;
        }

        // Traverse left with '0' appended
        traverse(node.left, new StringBuilder(sb).append('0'));

        // Traverse right with '1' appended
        traverse(node.right, new StringBuilder(sb).append('1'));
    }

    private int binToDec(String str) {
        return Integer.parseInt(str, 2);
    }

    private String decToBin(int num) {
        String bin = Integer.toBinaryString(num);
        return "0".repeat(8 - bin.length()) + bin;
    }

    private void saveEncodedFile() throws IOException {
        FileInputStream fis = new FileInputStream(inFileName);
        FileOutputStream fos = new FileOutputStream(outFileName);
        DataOutputStream dos = new DataOutputStream(fos);

        dos.writeInt(minHeap.size());
        PriorityQueue<Node> tempPQ = new PriorityQueue<>(minHeap);
        while (!tempPQ.isEmpty()) {
            Node node = tempPQ.poll();
            dos.writeChar(node.data);
            StringBuilder sb = new StringBuilder("0".repeat(127 - node.code.length()) + "1" + node.code);
            for (int i = 0; i < 16; i++) {
                dos.writeByte(binToDec(sb.substring(0, 8)));
                sb = new StringBuilder(sb.substring(8));
            }
        }

        StringBuilder result = new StringBuilder();
        int b;
        while ((b = fis.read()) != -1) {
            result.append(huffmanCodes.get((char) b));
            while (result.length() > 8) {
                dos.writeByte(binToDec(result.substring(0, 8)));
                result = new StringBuilder(result.substring(8));
            }
        }
        int pad = 8 - result.length();
        if (result.length() < 8) result.append("0".repeat(pad));
        dos.writeByte(binToDec(result.toString()));
        dos.writeByte(pad);
        fis.close();
        dos.close();
    }

    private void buildTree(char ch, String code) {
        Node curr = root;
        for (char c : code.toCharArray()) {
            if (c == '0') {
                if (curr.left == null) curr.left = new Node();
                curr = curr.left;
            } else {
                if (curr.right == null) curr.right = new Node();
                curr = curr.right;
            }
        }
        curr.data = ch;
    }

    private void saveDecodedFile() throws IOException {
        FileInputStream fis = new FileInputStream(inFileName);
        DataInputStream dis = new DataInputStream(fis);
        FileOutputStream fos = new FileOutputStream(outFileName);

        int size = dis.readInt();
        root = new Node();
        for (int i = 0; i < size; i++) {
            char ch = dis.readChar();
            byte[] codeBytes = new byte[16];
            dis.readFully(codeBytes);
            StringBuilder code = new StringBuilder();
            for (byte b : codeBytes) code.append(decToBin(b & 0xFF));
            int j = 0;
            while (code.charAt(j) == '0') j++;
            code = new StringBuilder(code.substring(j + 1));
            buildTree(ch, code.toString());
        }

        List<Byte> data = new ArrayList<>();
        while (dis.available() > 1) {
            data.add(dis.readByte());
        }
        int padding = dis.readByte();

        StringBuilder bitStream = new StringBuilder();
        for (byte d : data) {
            bitStream.append(decToBin(d & 0xFF));
        }
        if (padding > 0) {
            bitStream.setLength(bitStream.length() - padding);
        }

        Node curr = root;
        for (int i = 0; i < bitStream.length(); i++) {
            curr = (bitStream.charAt(i) == '0') ? curr.left : curr.right;
            if (curr.left == null && curr.right == null) {
                fos.write(curr.data);
                curr = root;
            }
        }
        dis.close();
        fos.close();
    }

    public void compress() throws IOException {
        createMinHeap();
        createTree();
        createCodes();
        saveEncodedFile();
    }

    public void decompress() throws IOException {
        saveDecodedFile();
    }
}
