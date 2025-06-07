import java.io.IOException;
import java.io.File;

public class Decode {
    public static void main(String[] args) throws IOException{
        if (args.length != 2) {
            System.out.println("Failed to detect Files");
            System.exit(1);
        }

        Huffman h = new Huffman(args[0], args[1]);
        h.decompress();

        File compressedFile = new File(args[0]);
        File outputFile = new File(args[1]);

        System.out.println("Decompressed successfully.");
        System.out.println("Compressed File Size: " + compressedFile.length() + " bytes");
        System.out.println("Output File Size    : " + outputFile.length() + " bytes");
    }
}
