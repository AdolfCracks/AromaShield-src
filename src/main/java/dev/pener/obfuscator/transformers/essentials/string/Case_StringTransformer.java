package dev.pener.obfuscator.transformers.essentials.string;

import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.transformers.essentials.number.BasicNumberTransformer;
import dev.pener.obfuscator.utils.ASMUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import java.util.ArrayList;
import java.util.Arrays;

public class Case_StringTransformer extends Transformer {


    public Case_StringTransformer() {
        super("CaseStringEncryption",true);
    }

    public MethodNode getDecrypt(String s) {
        MethodNode decrypt = new MethodNode(ACC_PUBLIC | ACC_STATIC, nextAlphaString(5,15) , "(I)Ljava/lang/String;", null, null);
        decrypt.visitCode();
        Label label0 = new Label();
        decrypt.visitLabel(label0);
        decrypt.visitLineNumber(8, label0);
        decrypt.visitTypeInsn(NEW, "java/lang/StringBuilder");
        decrypt.visitInsn(DUP);
        decrypt.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        decrypt.visitVarInsn(ASTORE, 1);

        Label label1 = new Label();
        decrypt.visitLabel(label1);
        decrypt.visitLineNumber(9, label1);
        decrypt.visitVarInsn(ILOAD, 0);

        Label defaultLabel = new Label();
        Label[] labels = new Label[s.length()];


        // Create labels for each character
        ArrayList<Integer> list = new ArrayList<>();
        boolean first = true;
        for (int i = 0; i < s.length(); i++) {

            labels[i] = new Label();
            list.add(i);
        }

        int[] arr = list.stream().mapToInt(i -> i).toArray();

        decrypt.visitLookupSwitchInsn(defaultLabel, arr, labels);


        int caseNumber = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // Create a label for the character

            Label charLabel = labels[i];
            decrypt.visitLabel(charLabel);

            decrypt.visitIntInsn(BIPUSH,i);
            decrypt.visitVarInsn(ISTORE,0);
            decrypt.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/StringBuilder"}, 0, null);
            decrypt.visitVarInsn(ALOAD, 1);
            decrypt.visitLdcInsn(String.valueOf(c));
            decrypt.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            decrypt.visitInsn(Opcodes.POP);

            // Increment the case number
            caseNumber++;
        }


        // Default case
        decrypt.visitLabel(defaultLabel);
        decrypt.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

        Label label2 = new Label();
        decrypt.visitLabel(label2);
        decrypt.visitLineNumber(21, label2);
        decrypt.visitVarInsn(ALOAD, 1);
        decrypt.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        decrypt.visitInsn(ARETURN);

        Label label3 = new Label();
        decrypt.visitLabel(label3);
        decrypt.visitLocalVariable("number", "I", null, label0, label3, 0);
        decrypt.visitLocalVariable("string", "Ljava/lang/StringBuilder;", null, label1, label3, 1);
        decrypt.visitMaxs(2, 2);
        decrypt.visitEnd();

        return decrypt;
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {
            if (!getSettings().shouldTransform(cn.name)) return;
            final int seed = nextInt(5000) + 10;

            Arrays.stream(cn.methods.toArray(new MethodNode[0])).forEach(mn -> {

                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if(insn.getOpcode() == Opcodes.LDC){
                        LdcInsnNode ldc = (LdcInsnNode) insn;
                        if(ldc.cst instanceof String){
                            if(((String)ldc.cst).length() > 100) return;
                            String key = Obfuscator.getInstance().maps.getOrDefault(cn.name, cn.name).replace("/", ".");
                            String value = (String)ldc.cst;
                            ldc.cst = 0;
                            MethodNode decrypt = getDecrypt(value);
                             cn.methods.add(decrypt);

                            mn.instructions.insert(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, decrypt.name, decrypt.desc, false));
                        }

                    }
                });
            });
        });
    }
}
