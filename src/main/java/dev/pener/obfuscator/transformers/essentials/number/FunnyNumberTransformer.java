package dev.pener.obfuscator.transformers.essentials.number;


import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;

public class FunnyNumberTransformer extends Transformer {


    public FunnyNumberTransformer() {
        super("FunnyNumberTransformer",false);
    }

    public static String getWord() {
       ArrayList<String> words = new ArrayList<>();
       words.add("kys");
       words.add("fuck niggers");
       words.add("go fuck yourself");
       words.add("you little bitch");
      words.add("yo mama's so fat that recaf crashes java.lang.OutOfMemoryError");
     words.add("Exception in thread YourDad java.lang.NullPointerException");
       SecureRandom random = new SecureRandom();
       int i = random.nextInt(words.size());
       return words.get(i);
   }

    public static InsnList getFastIntEncryption(int value){
        InsnList list = new InsnList();

        SecureRandom random = new SecureRandom();
        boolean xd = random.nextBoolean();


        final String word = getWord();
            list.add(new LdcInsnNode(word + "" + value ));


        list.add(new LdcInsnNode(word));
        list.add(new LdcInsnNode(""));
/*        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
        list.add(new LdcInsnNode("\\s"));
        list.add(new LdcInsnNode(""));*/
        list.add(new MethodInsnNode(INVOKEVIRTUAL,"java/lang/String", "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;)Ljava/lang/Integer;", false));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));
        return list;
    }
    @Override
    public void run() {
        getClasses().forEach(cn -> {
            if (!getSettings().shouldTransform(cn.name)) return;
            if((cn.access & Opcodes.ACC_ANNOTATION) == Opcodes.ACC_ANNOTATION)
                return;
            if((cn.access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE)
                return;
            cn.methods.forEach(mn -> {
                mn.instructions.forEach(insn -> {

                    if(ASMUtils.isNumber(insn)){
                        if(insn instanceof LdcInsnNode){
                            LdcInsnNode ldc = (LdcInsnNode) insn;
                            if(ldc.cst instanceof Integer){
                                mn.instructions.insertBefore(ldc, getFastIntEncryption((int) ldc.cst));
                                mn.instructions.remove(ldc);
                            }
                        }
                        if(insn instanceof IntInsnNode){
                            IntInsnNode iinsn = (IntInsnNode) insn;
                            mn.instructions.insertBefore(iinsn, getFastIntEncryption(iinsn.operand));
                            mn.instructions.remove(iinsn);
                        }
                    }
                });
            });
        });

    }
}
