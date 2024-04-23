package dev.pener.obfuscator.transformers.essentials.string;

import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class Stack_StringTransformer extends Transformer {

    public Stack_StringTransformer() {
        super("StackStringEncryption",true);
    }

    public static String encrypt(String value, String key, int seed){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < value.length(); i++){
            result.append((char) (value.charAt(i) ^ key.charAt(i % key.length()) ^ seed));
        }
        return result.toString();
    }

    public static String decrypt(String value){
        String key = Arrays.stream(new Exception().getStackTrace()).collect(Collectors.toList()).get(0).getClassName();
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < value.length(); i++){
            result.append((char) (value.charAt(i) ^ key.charAt(i % key.length()) ^ 7));
        }
        return result.toString();
    }

    public MethodNode createDecryptMethod(int seed){
        MethodNode decrypt = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, getRandomJVMString(10), "(Ljava/lang/String;)Ljava/lang/String;", null, null);
        decrypt.visitCode();
        Label label0 = new Label();
        decrypt.visitLabel(label0);
        decrypt.visitLineNumber(20, label0);
        decrypt.visitTypeInsn(NEW, "java/lang/Exception");
        decrypt.visitInsn(DUP);
        decrypt.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", "<init>", "()V", false);
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
        decrypt.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "stream", "([Ljava/lang/Object;)Ljava/util/stream/Stream;", false);
        decrypt.visitMethodInsn(INVOKESTATIC, "java/util/stream/Collectors", "toList", "()Ljava/util/stream/Collector;", false);
        decrypt.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;", true);
        decrypt.visitTypeInsn(CHECKCAST, "java/util/List");
        decrypt.visitInsn(ICONST_0);
        decrypt.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true);
        decrypt.visitTypeInsn(CHECKCAST, "java/lang/StackTraceElement");
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
        decrypt.visitVarInsn(ASTORE, 1);
        Label label1 = new Label();
        decrypt.visitLabel(label1);
        decrypt.visitLineNumber(21, label1);
        decrypt.visitTypeInsn(NEW, "java/lang/StringBuilder");
        decrypt.visitInsn(DUP);
        decrypt.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        decrypt.visitVarInsn(ASTORE, 2);
        Label label2 = new Label();
        decrypt.visitLabel(label2);
        decrypt.visitLineNumber(22, label2);
        decrypt.visitInsn(ICONST_0);
        decrypt.visitVarInsn(ISTORE, 3);
        Label label3 = new Label();
        decrypt.visitLabel(label3);
        decrypt.visitFrame(Opcodes.F_APPEND, 3, new Object[]{"java/lang/String", "java/lang/StringBuilder", Opcodes.INTEGER}, 0, null);
        decrypt.visitVarInsn(ILOAD, 3);
        decrypt.visitVarInsn(ALOAD, 0);
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        Label label4 = new Label();
        decrypt.visitJumpInsn(IF_ICMPGE, label4);
        Label label5 = new Label();
        decrypt.visitLabel(label5);
        decrypt.visitLineNumber(23, label5);
        decrypt.visitVarInsn(ALOAD, 2);
        decrypt.visitVarInsn(ALOAD, 0);
        decrypt.visitVarInsn(ILOAD, 3);
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
        decrypt.visitVarInsn(ALOAD, 1);
        decrypt.visitVarInsn(ILOAD, 3);
        decrypt.visitVarInsn(ALOAD, 1);
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        decrypt.visitInsn(IREM);
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
        decrypt.visitInsn(IXOR);

        decrypt.visitIntInsn(BIPUSH, seed);


        decrypt.visitInsn(IXOR);
        decrypt.visitInsn(I2C);
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
        decrypt.visitInsn(POP);
        Label label6 = new Label();
        decrypt.visitLabel(label6);
        decrypt.visitLineNumber(22, label6);
        decrypt.visitIincInsn(3, 1);
        decrypt.visitJumpInsn(GOTO, label3);
        decrypt.visitLabel(label4);
        decrypt.visitLineNumber(25, label4);
        decrypt.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        decrypt.visitVarInsn(ALOAD, 2);
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        decrypt.visitInsn(ARETURN);
        Label label7 = new Label();
        decrypt.visitLabel(label7);
        decrypt.visitLocalVariable("i", "I", null, label3, label4, 3);
        decrypt.visitLocalVariable("value", "Ljava/lang/String;", null, label0, label7, 0);
        decrypt.visitLocalVariable("key", "Ljava/lang/String;", null, label1, label7, 1);
        decrypt.visitLocalVariable("result", "Ljava/lang/StringBuilder;", null, label2, label7, 2);
        decrypt.visitMaxs(5, 4);
        decrypt.visitEnd();
        return decrypt;
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {
            if (!getSettings().shouldTransform(cn.name)) return;

            final MethodNode[] decrypt = {null};
            final int seed = nextInt(5000) + 10;

            Arrays.stream(cn.methods.toArray(new MethodNode[0])).forEach(mn -> {

                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if(insn.getOpcode() == Opcodes.LDC){
                        LdcInsnNode ldc = (LdcInsnNode) insn;
                        if(ldc.cst instanceof String){

                            if(((String)ldc.cst).length() > 100) return;

                            String key = Obfuscator.getInstance().maps.getOrDefault(cn.name, cn.name).replace("/", ".");
                            String value = (String)ldc.cst;
                            ldc.cst = encrypt(value, key, seed);
                            if(decrypt[0] == null){
                                decrypt[0] = createDecryptMethod(seed);
                                cn.methods.add(decrypt[0]);
                            }
                            mn.instructions.insert(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, decrypt[0].name, decrypt[0].desc, false));
                        }
                    }
                });
            });
        });
    }
}
