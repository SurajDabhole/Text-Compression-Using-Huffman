import java.io.File;
import java.io.IOException;

public class Encode {
    public static void main(String[] args) throws IOException{
        if (args.length != 2) {
            System.out.println("Failed to detect Files");
            System.exit(1);
        }

        Huffman h = new Huffman(args[0], args[1]);
        h.compress();

        File inputFile = new File(args[0]);
        File compressedFile = new File(args[1]);

        System.out.println("Compressed successfully");
        System.out.println("Input File Size     : " + inputFile.length() + " bytes");
        System.out.println("Compressed File Size: " + compressedFile.length() + " bytes");
    }
}
