package dev.pener.obfuscator.transformers.essentials.string;

import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.utils.ASMUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DES_StringTransformer extends Transformer {

    private static final String FIELD_NAME = "string_store";
    private static final String CALL_NAME = "unscramble";
    private static final String CALL_DESC = "(ILjava/lang/String;)Ljava/lang/String;";

    private ClassNode unscrambleClass;
    private List<String> stringList;

    public DES_StringTransformer() {
        super("DES_StringEncryption",false);
    }


    public static String generateSecret()  {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] keyBytes = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }
    public static String encryptDes(String plaintext, String secretKeyString)  {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "DES");
        byte[] encryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();;
        }


        return Base64.getEncoder().encodeToString(encryptedData);
    }
    @Override
    public void run() {

        HashMap<String, ClassNode> classMap = new HashMap<>();

        getClasses().forEach(cn -> classMap.put(cn.name, cn));

        stringList = new ArrayList<>();
        do {
            unscrambleClass = (ClassNode) classMap.values().toArray()[nextInt(classMap.size())];
        } while ((unscrambleClass.access & Opcodes.ACC_INTERFACE) != 0);
        // Build string list

        classMap.values().stream().flatMap(cn -> cn.methods.stream()).forEach(this::buildStringList);
        Collections.shuffle(stringList);
        // Replace LDC constants with calls to unscramble


        classMap.values().forEach(cn -> cn.methods.forEach(mn -> scramble(cn, mn)));
        // Add unscrambling handler

        unscrambleClass.visitField(ACC_PUBLIC | ACC_STATIC, FIELD_NAME, "[Ljava/lang/String;", null, null);

        createUnscramble();
        try {
            createStaticConstructor(unscrambleClass);
        } catch (Exception ex) {

        }
    }

    private void buildStringList(MethodNode mn) {
        ASMUtils.forEach(mn.instructions, LdcInsnNode.class, ldc -> {
            if (ldc.cst instanceof String && !stringList.contains(ldc.cst)) {
                stringList.add((String) ldc.cst);
            }
        });
    }

    ArrayList<String> secretList = new ArrayList<>();
    private void scramble(ClassNode cn, MethodNode mn) {
        List<LdcInsnNode> ldcNodes = new LinkedList<>();
        ASMUtils.forEach(mn.instructions, LdcInsnNode.class, ldcNodes::add);
        for (LdcInsnNode node : ldcNodes) {
            if (node.cst instanceof String) {
                int index = stringList.indexOf(node.cst);
                if (index == -1)
                    continue;

                MethodInsnNode call = new MethodInsnNode(Opcodes.INVOKESTATIC, unscrambleClass.name, CALL_NAME, CALL_DESC, false);

                String secret = generateSecret();
                secretList.add(secret);
                String enc = encryptDes(stringList.get(index),secret);
                int subStringSize = (enc + secret).indexOf(secret);


                mn.visitFieldInsn(GETSTATIC, unscrambleClass.name, FIELD_NAME, "[Ljava/lang/String;");
                mn.visitVarInsn(ILOAD,index);
                mn.visitInsn(AALOAD);
                mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
                mn.instructions.insertBefore(call, ASMUtils.newIntegerNode(subStringSize));
                mn.instructions.set(node, call);
                mn.instructions.insertBefore(call, ASMUtils.newIntegerNode(index));


            }
        }
    }

    private void createUnscramble() {


        MethodVisitor methodVisitor = unscrambleClass.visitMethod(ACC_PUBLIC | ACC_STATIC, CALL_NAME, "(ILjava/lang/String;)Ljava/lang/String;", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
        Label label3 = new Label();
        methodVisitor.visitLabel(label3);
        methodVisitor.visitLineNumber(23, label3);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "(Ljava/lang/String;)[B", false);
        methodVisitor.visitVarInsn(ASTORE, 2);
        Label label4 = new Label();
        methodVisitor.visitLabel(label4);
        methodVisitor.visitLineNumber(26, label4);
        methodVisitor.visitTypeInsn(NEW, "javax/crypto/spec/SecretKeySpec");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitLdcInsn("DES");
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "javax/crypto/spec/SecretKeySpec", "<init>", "([BLjava/lang/String;)V", false);
        methodVisitor.visitVarInsn(ASTORE, 3);
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(31, label0);
        methodVisitor.visitLdcInsn("DES");
        methodVisitor.visitMethodInsn(INVOKESTATIC, "javax/crypto/Cipher", "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;", false);
        methodVisitor.visitVarInsn(ASTORE, 4);
        Label label5 = new Label();
        methodVisitor.visitLabel(label5);
        methodVisitor.visitLineNumber(32, label5);
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitInsn(ICONST_2);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "init", "(ILjava/security/Key;)V", false);
        Label label6 = new Label();
        methodVisitor.visitLabel(label6);
        methodVisitor.visitLineNumber(35, label6);
        methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        methodVisitor.visitVarInsn(ASTORE, 5);
        Label label7 = new Label();
        methodVisitor.visitLabel(label7);
        methodVisitor.visitLineNumber(39, label7);
        methodVisitor.visitFieldInsn(GETSTATIC, unscrambleClass.name, FIELD_NAME, "[Ljava/lang/String;");
        methodVisitor.visitVarInsn(ILOAD, 0);
        methodVisitor.visitInsn(AALOAD);
        methodVisitor.visitVarInsn(ASTORE, 6);
        Label label8 = new Label();
        methodVisitor.visitLabel(label8);
        methodVisitor.visitLineNumber(41, label8);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false);
        methodVisitor.visitVarInsn(ALOAD, 6);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "(Ljava/lang/String;)[B", false);
        methodVisitor.visitVarInsn(ASTORE, 7);
        Label label9 = new Label();
        methodVisitor.visitLabel(label9);
        methodVisitor.visitLineNumber(44, label9);
        methodVisitor.visitVarInsn(ALOAD, 4);
        methodVisitor.visitVarInsn(ALOAD, 7);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "doFinal", "([B)[B", false);
        methodVisitor.visitVarInsn(ASTORE, 8);
        Label label10 = new Label();
        methodVisitor.visitLabel(label10);
        methodVisitor.visitLineNumber(47, label10);
        methodVisitor.visitTypeInsn(NEW, "java/lang/String");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 8);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;");
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([BLjava/nio/charset/Charset;)V", false);
        methodVisitor.visitVarInsn(ASTORE, 9);
        Label label11 = new Label();
        methodVisitor.visitLabel(label11);
        methodVisitor.visitLineNumber(48, label11);
        methodVisitor.visitVarInsn(ALOAD, 5);
        methodVisitor.visitVarInsn(ALOAD, 9);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        methodVisitor.visitInsn(POP);
        Label label12 = new Label();
        methodVisitor.visitLabel(label12);
        methodVisitor.visitLineNumber(52, label12);
        methodVisitor.visitVarInsn(ALOAD, 5);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLineNumber(53, label2);
        methodVisitor.visitFrame(Opcodes.F_FULL, 4, new Object[]{Opcodes.INTEGER, "java/lang/String", "[B", "javax/crypto/spec/SecretKeySpec"}, 1, new Object[]{"java/lang/Exception"});
        methodVisitor.visitVarInsn(ASTORE, 5);
        Label label13 = new Label();
        methodVisitor.visitLabel(label13);
        methodVisitor.visitLineNumber(54, label13);
        methodVisitor.visitVarInsn(ALOAD, 5);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
        Label label14 = new Label();
        methodVisitor.visitLabel(label14);
        methodVisitor.visitLineNumber(55, label14);
        methodVisitor.visitLdcInsn("");
        methodVisitor.visitInsn(ARETURN);
        Label label15 = new Label();
        methodVisitor.visitLabel(label15);
        methodVisitor.visitLocalVariable("decryptedMessageBuilder", "Ljava/lang/StringBuilder;", null, label7, label2, 5);
        methodVisitor.visitLocalVariable("encryptedBase64", "Ljava/lang/String;", null, label8, label2, 6);
        methodVisitor.visitLocalVariable("encryptedData", "[B", null, label9, label2, 7);
        methodVisitor.visitLocalVariable("decryptedData", "[B", null, label10, label2, 8);
        methodVisitor.visitLocalVariable("decryptedText", "Ljava/lang/String;", null, label11, label2, 9);
        methodVisitor.visitLocalVariable("cipher", "Ljavax/crypto/Cipher;", null, label5, label2, 4);
        methodVisitor.visitLocalVariable("e", "Ljava/lang/Exception;", null, label13, label15, 5);
        methodVisitor.visitLocalVariable("count", "I", null, label3, label15, 0);
        methodVisitor.visitLocalVariable("secretKeyString", "Ljava/lang/String;", null, label3, label15, 1);
        methodVisitor.visitLocalVariable("keyBytes", "[B", null, label4, label15, 2);
        methodVisitor.visitLocalVariable("secretKeySpec", "Ljavax/crypto/spec/SecretKeySpec;", null, label0, label15, 3);
        methodVisitor.visitMaxs(4, 10);
        methodVisitor.visitEnd();
    }

    private void createStaticConstructor(ClassNode owner) throws UnsupportedEncodingException, UnsupportedEncodingException {
        MethodNode original = ASMUtils.getMethod(owner, "<clinit>", "()V");
        MethodVisitor mv = owner.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        // generate instructions
        InstructionAdapter builder = new InstructionAdapter(mv);

        builder.iconst(stringList.size());
        builder.newarray(Type.getType(String.class));
        for (int i = 0; i < stringList.size(); i++) {
            builder.dup();
            builder.iconst(i);
            String secret =  secretList.get(i);
            String enc = encryptDes(stringList.get(i),secret);
            int index = (enc + secret).indexOf(secret);
            //owner.visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL,"penis" + i,"I",null,index).visitEnd();
            
            builder.astore(InstructionAdapter.OBJECT_TYPE);
        }
        builder.putstatic(unscrambleClass.name, FIELD_NAME, "[Ljava/lang/String;");
        // merge with original if it exists
        if (original != null) {
            // original should already end with RETURN
            owner.methods.remove(original);
            original.instructions.accept(builder);
        } else {
            builder.areturn(Type.VOID_TYPE);
        }
    }

}
