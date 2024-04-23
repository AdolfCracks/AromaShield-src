package dev.pener.obfuscator.core;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public abstract class Transformer implements Opcodes {
   private String name;
   private boolean visible;

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    public Transformer(String name, boolean visible) {
        this.name = name;
        this.visible = visible;
    }
    public abstract void run();
    public List<ClassNode> getClasses(){
        return Obfuscator.getClasses();
    }
    public void addMap(String key, String value){
        Obfuscator.getInstance().maps.put(key, value);
    }

    public static Settings getSettings(){
        return Obfuscator.getInstance().settings;
    }

    public static int nextInt(int val){
        return Obfuscator.getInstance().r.nextInt(val);
    }
    public static long nextLong(){
        return Obfuscator.getInstance().r.nextLong();
    }

    public static String getRandomUTFString(int length){
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < length; i++){
            output.append((char)(nextInt(600) + 1));
        }
        return output.toString();
    }

    public static boolean nextBoolean() {
        return Obfuscator.getInstance().r.nextBoolean();
    }

    public static Random r = Obfuscator.getInstance().r;

    public static String nextString(int minLength, int maxLength) {
        if (minLength < 0 || maxLength < 0 || minLength > maxLength) {
            throw new IllegalArgumentException("Invalid range");
        }

        int length = nextInt(minLength, maxLength);
        byte[] randomBytes = new byte[length];
        r.nextBytes(randomBytes);
        return new String(randomBytes);
    }
    public static String nextAlphaString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid length");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = (char) (r.nextInt(26) + 'a');
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static String nextAlphaString(int minLength, int maxLength) {
        if (minLength < 0 || maxLength < 0 || minLength > maxLength) {
            throw new IllegalArgumentException("Invalid range");
        }

        int length = nextInt(minLength, maxLength);

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = (char) (r.nextInt(26) + 'a');
            sb.append(randomChar);
        }
        return sb.toString();
    }
    public static String nextString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Invalid length");
        }

        byte[] randomBytes = new byte[length];
        r.nextBytes(randomBytes);
        return new String(randomBytes, java.nio.charset.StandardCharsets.UTF_8);
    }
    public static int nextInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }
        return r.nextInt(max - min + 1) + min;
    }
    public static String getAlphabetical(int number) {
        char baseChar = 'a';
        StringBuilder letters = new StringBuilder();

        do {
            number -= 1;
            letters.insert(0, (char)(baseChar + (number % 26)));
            number = (int)Math.floor(number / 26);
        } while(number > 0);

        return letters.toString();
    }

    public static String getRandomJVMString(int length){
        StringBuilder output = new StringBuilder();
        String chars = "ɶʲʷɦ˶ʑɢʤ̿ͱ̙ʟ͟ʏ̴̝̍̀ɵʼͮomheaguTASLCrkvsdpIfVb0jwFPyOxzURGBYDZXEqMHW2KQN3J45ͦͯʻː̵˫ɰʬʡ˴ˀɞ˺ɳ˷˟ʳ̌˧ɭʓɱ_6798ɜ͚ɩ̛ʀʎɹɼʫʔͥ˯ˋˠ̼̃˙ʅ̲˛ʴ˓ɚ͇ˏ͙ʪͺ̯ʠ˩͞ʁ͠ʋ̥ͪ̉ɷ̫ʾʊˎ̭̈ˉ̏\u0379ɪʍ̇ˌɫ˥ɡ͵ɥɤ̡̠̹̒ə̗ͅʸ̨̑͊̈́ʦɾʢʱɝ̧͍ɘ̳̓˸;͓̕ʚ͐˪̔͋˾ʽ˻ɲͬɛͣ˲̜̊ˁ˨ͩͽˍͫ˭ʨ̄ʃͿˊ̂ʯʭ͝ˑʹ̬˄ʶ̀ʥʜ͔̖ʈ̺̱͗ɠʉ̽́˼̓ʝɨ˝˜ɽ˅˱\u0380ɮʵ͌͛̋ʺ̣̮ɣͶʇ˃ʕɺ͏ˢ͜Ͱʂ͢ʛ̩ͳʐʙ˦ˤ\u0383ˣʄ̶̰̆ͨ̚˒̎ͼ͑ˬͷ˕\u0381˰͎ɻ˖ʮ̾ʆʌ˞̐ʣͤ͆˔̤ˈ͖ͭ˿͒͡˘ʹ̷ɧʧʒͲ̅́ʰ͘ʩˆ̢ʘɸʖ̻̦̟ͧʞˮ\u0378ɯ˽ɴˇ˵͕ˡ̸͉̞ɟ˗ɿ˚˹̪͂ɬʿ\u0382̘͈ͻʗ-";
        for(int i = 0; i < length; i++){
            output.append(chars.charAt(nextInt(chars.length())));
        }
        return output.toString();
    }

}
