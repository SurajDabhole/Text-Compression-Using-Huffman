// Huffman.java
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
    private final List<Node> arr = new ArrayList<>();
    private final String inFileName, outFileName;
    private Node root;
    private final PriorityQueue<Node> minHeap = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
    private final Map<Character, String> huffmanCodes = new HashMap<>();

    public Huffman(String inFileName, String outFileName) {
        this.inFileName = inFileName;
        this.outFileName = outFileName;
        createArr();
    }

    private void createArr() {
        for (int i = 0; i < 128; i++) {
            Node node = new Node();
            node.data = (char) i;
            node.freq = 0;
            arr.add(node);
        }
    }

    private void traverse(Node node, String str) {
        if (node.left == null && node.right == null) {
            node.code = str;
            huffmanCodes.put(node.data, str);
            return;
        }
        traverse(node.left, str + "0");
        traverse(node.right, str + "1");
    }

    private int binToDec(String str) {
        return Integer.parseInt(str, 2);
    }

    private String decToBin(int num) {
        String bin = Integer.toBinaryString(num);
        return "0".repeat(8 - bin.length()) + bin;
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

    private void createMinHeap() throws IOException {
        FileInputStream fis = new FileInputStream(inFileName);
        int b;
        while ((b = fis.read()) != -1) {
            arr.get(b).freq++;
        }
        fis.close();
        for (Node node : arr) {
            if (node.freq > 0) minHeap.add(node);
        }
    }

    private void createTree() {
        PriorityQueue<Node> tempPQ = new PriorityQueue<>(minHeap);
        while (tempPQ.size() > 1) {
            Node left = tempPQ.poll();
            Node right = tempPQ.poll();
            Node parent = new Node();
            parent.freq = left.freq + right.freq;
            parent.left = left;
            parent.right = right;
            tempPQ.add(parent);
        }
        root = tempPQ.peek();
    }

    private void createCodes() {
        traverse(root, "");
    }

    private void saveEncodedFile() throws IOException {
        FileInputStream fis = new FileInputStream(inFileName);
        FileOutputStream fos = new FileOutputStream(outFileName);
        StringBuilder result = new StringBuilder();

        fos.write(minHeap.size());
        PriorityQueue<Node> tempPQ = new PriorityQueue<>(minHeap);
        while (!tempPQ.isEmpty()) {
            Node node = tempPQ.poll();
            fos.write(node.data);
            StringBuilder s = new StringBuilder("0".repeat(127 - node.code.length()) + "1" + node.code);
            for (int i = 0; i < 16; i++) {
                fos.write(binToDec(s.substring(0, 8)));
                s = new StringBuilder(s.substring(8));
            }
        }

        int b;
        while ((b = fis.read()) != -1) {
            result.append(huffmanCodes.get((char) b));
            while (result.length() > 8) {
                fos.write(binToDec(result.substring(0, 8)));
                result = new StringBuilder(result.substring(8));
            }
        }
        int pad = 8 - result.length();
        if (result.length() < 8) result.append("0".repeat(pad));
        fos.write(binToDec(result.toString()));
        fos.write(pad);
        fis.close();
        fos.close();
    }

    private void saveDecodedFile() throws IOException {
        FileInputStream fis = new FileInputStream(inFileName);
        FileOutputStream fos = new FileOutputStream(outFileName);
        int size = fis.read();
        fis.skip(17L * size);
        fis.skip(fis.available() - 1);
        int padding = fis.read();
        fis.close();

        fis = new FileInputStream(inFileName);
        fis.skip(1 + 17L * size);
        byte[] bytes = fis.readAllBytes();
        StringBuilder bitStream = new StringBuilder();
        for (int i = 0; i < bytes.length - 1; i++) {
            bitStream.append(decToBin(bytes[i] & 0xFF));
        }
        if (padding > 0) {
            int len = bitStream.length();
            bitStream.setLength(len - padding);
        }

        Node curr = root;
        for (int i = 0; i < bitStream.length(); i++) {
            curr = (bitStream.charAt(i) == '0') ? curr.left : curr.right;
            if (curr.left == null && curr.right == null) {
                fos.write(curr.data);
                curr = root;
            }
        }
        fis.close();
        fos.close();
    }

    private void getTree() throws IOException {
        FileInputStream fis = new FileInputStream(inFileName);
        int size = fis.read();
        root = new Node();
        for (int i = 0; i < size; i++) {
            char ch = (char) fis.read();
            byte[] codeBytes = fis.readNBytes(16);
            StringBuilder code = new StringBuilder();
            for (byte b : codeBytes) code.append(decToBin(b & 0xFF));
            int j = 0;
            while (code.charAt(j) == '0') j++;
            code = new StringBuilder(code.substring(j + 1));
            buildTree(ch, code.toString());
        }
        fis.close();
    }

    public void compress() throws IOException {
        createMinHeap();
        createTree();
        createCodes();
        saveEncodedFile();
    }

    public void decompress() throws IOException {
        getTree();
        saveDecodedFile();
    }
}
