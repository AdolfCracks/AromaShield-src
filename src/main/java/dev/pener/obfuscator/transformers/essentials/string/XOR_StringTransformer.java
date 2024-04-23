package dev.pener.obfuscator.transformers.essentials.string;

import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;

public class XOR_StringTransformer extends Transformer {

    public XOR_StringTransformer() {
        super("XOR_StringEncryption",true);
    }

    public static String encrypt(String v, String k, int salt){
        StringBuilder o = new StringBuilder();
        int i = 0;
        for(char c : v.toCharArray()){
            o.append((char) (salt ^ (int)c ^ (int) k.toCharArray()[i++]));
            if(i >= k.length())
                i = 0;
        }
        return o.toString();
    }

    public MethodNode createDecryptMethod(String name, int salt){
        MethodNode mn = new MethodNode(ACC_STATIC | ACC_PUBLIC, name, "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", null, null);
        mn.visitCode();
        Label label0 = new Label();
        mn.visitLabel(label0);
        mn.visitLineNumber(18, label0);
        mn.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mn.visitInsn(DUP);
        mn.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mn.visitVarInsn(ASTORE, 2);
        Label label1 = new Label();
        mn.visitLabel(label1);
        mn.visitLineNumber(19, label1);
        mn.visitInsn(ICONST_0);
        mn.visitVarInsn(ISTORE, 3);
        Label label2 = new Label();
        mn.visitLabel(label2);
        mn.visitLineNumber(20, label2);
        mn.visitVarInsn(ALOAD, 0);
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
        mn.visitVarInsn(ASTORE, 4);
        mn.visitVarInsn(ALOAD, 4);
        mn.visitInsn(ARRAYLENGTH);
        mn.visitVarInsn(ISTORE, 5);
        mn.visitInsn(ICONST_0);
        mn.visitVarInsn(ISTORE, 6);
        Label label3 = new Label();
        mn.visitLabel(label3);
        mn.visitFrame(Opcodes.F_FULL, 7, new Object[]{"java/lang/String", "java/lang/String", "java/lang/StringBuilder", Opcodes.INTEGER, "[C", Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[]{});
        mn.visitVarInsn(ILOAD, 6);
        mn.visitVarInsn(ILOAD, 5);
        Label label4 = new Label();
        mn.visitJumpInsn(IF_ICMPGE, label4);
        mn.visitVarInsn(ALOAD, 4);
        mn.visitVarInsn(ILOAD, 6);
        mn.visitInsn(CALOAD);
        mn.visitVarInsn(ISTORE, 7);
        Label label5 = new Label();
        mn.visitLabel(label5);
        mn.visitLineNumber(21, label5);
        mn.visitVarInsn(ALOAD, 2);
        mn.visitIntInsn(BIPUSH, salt);
        mn.visitVarInsn(ILOAD, 7);
        mn.visitInsn(IXOR);
        mn.visitVarInsn(ALOAD, 1);
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
        mn.visitVarInsn(ILOAD, 3);
        mn.visitIincInsn(3, 1);
        mn.visitInsn(CALOAD);
        mn.visitInsn(IXOR);
        mn.visitInsn(I2C);
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
        mn.visitInsn(POP);
        Label label6 = new Label();
        mn.visitLabel(label6);
        mn.visitLineNumber(22, label6);
        mn.visitVarInsn(ILOAD, 3);
        mn.visitVarInsn(ALOAD, 1);
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
        Label label7 = new Label();
        mn.visitJumpInsn(IF_ICMPLT, label7);
        Label label8 = new Label();
        mn.visitLabel(label8);
        mn.visitLineNumber(23, label8);
        mn.visitInsn(ICONST_0);
        mn.visitVarInsn(ISTORE, 3);
        mn.visitLabel(label7);
        mn.visitLineNumber(20, label7);
        mn.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mn.visitIincInsn(6, 1);
        mn.visitJumpInsn(GOTO, label3);
        mn.visitLabel(label4);
        mn.visitLineNumber(25, label4);
        mn.visitFrame(Opcodes.F_CHOP, 3, null, 0, null);
        mn.visitVarInsn(ALOAD, 2);
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mn.visitInsn(ARETURN);
        Label label9 = new Label();
        mn.visitLabel(label9);
        mn.visitLocalVariable("c", "C", null, label5, label7, 7);
        mn.visitLocalVariable("v", "Ljava/lang/String;", null, label0, label9, 0);
        mn.visitLocalVariable("k", "Ljava/lang/String;", null, label0, label9, 1);
        mn.visitLocalVariable("o", "Ljava/lang/StringBuilder;", null, label1, label9, 2);
        mn.visitLocalVariable("i", "I", null, label2, label9, 3);
        mn.visitMaxs(4, 8);
        mn.visitEnd();
        return mn;
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {
            if (!getSettings().shouldTransform(cn.name)) return;
            if(cn.version < 52)
                return;
            int salt = nextInt(100) + 10;
            MethodNode decrypt = createDecryptMethod(getRandomJVMString(10), salt);
            cn.methods.forEach(mn -> {

                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if(insn.getOpcode() == Opcodes.LDC){
                        LdcInsnNode ldc = (LdcInsnNode) insn;
                        if(ldc.cst instanceof String){
                            if(((String) ldc.cst).length() > 250){
                                // large
                            }else{
                                String key = getRandomUTFString(2);
                                ldc.cst = encrypt((String)ldc.cst, key, salt);
                                mn.instructions.insert(ldc, new LdcInsnNode(key));
                                mn.instructions.insert(ldc.getNext(), new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, decrypt.name, decrypt.desc, false));
                            }

                        }
                    }
                });
            });
            cn.methods.add(decrypt);
        });
    }
}
