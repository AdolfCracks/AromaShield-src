import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Base64;

public class Test {

    public Test() {

    }
    public static void test(int e) {
        System.out.println("spenizer " + e);
    }
    public static void test2() {
        System.out.println("spenizer");
    }
    static boolean penis = false;
    public static void main(String[] args) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\Admin\\Downloads\\decode.txt"));
            String str = new String(Base64.getDecoder().decode(bufferedReader.readLine()));
            File file = new File("C:\\Users\\Admin\\Downloads\\decoded.txt");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(str.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {

        }
        if (!penis) return;
        try {
            PrintStream ps = System.out;
            Class<?> c = ps.getClass();
            Method m = c.getDeclaredMethod("println", String.class);
            m.invoke(ps, "e");

        } catch (NoSuchMethodException x) {
            x.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (!penis) return;
        String inputJarFilePath = "C:\\Users\\Admin\\Desktop\\WSZYSTKO\\obfuscator\\obf.jar";
        String outputBase64FilePath = "C:\\Users\\Admin\\Desktop\\WSZYSTKO\\obfuscator\\obf.txt";

        try {
            // Read the JAR file into a byte array
            byte[] jarFileBytes = Files.readAllBytes(new File(inputJarFilePath).toPath());

            // Encode the byte array into a base64 string
            String base64String = Base64.getEncoder().encodeToString(jarFileBytes);

            // Write the base64 string to the output file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputBase64FilePath))) {
                writer.write(base64String);
            }

            System.out.println("JAR file successfully converted to base64 and saved to " + outputBase64FilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void getDecrypt(int number) {
        System.out.println("buffered reader called in udp");
    }
}
