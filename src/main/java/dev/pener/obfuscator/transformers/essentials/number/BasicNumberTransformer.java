package dev.pener.obfuscator.transformers.essentials.number;

import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BasicNumberTransformer extends Transformer {

    public BasicNumberTransformer() {
        super("BasicNumberEncryption",true);
    }

    public static InsnList getFastIntEncryption(int value){
        InsnList list = new InsnList();
        long[] val = new long[nextInt(1) + 1];
        for(int i = 0; i < val.length; i++){
            val[i] = nextLong();
            value ^= val[i];
        }
        list.add(ASMUtils.pushInt(value));
        list.add(new InsnNode(Opcodes.I2L));
        for(int i = 0; i < val.length; i++){
            list.add(new LdcInsnNode(val[i]));
            list.add(new InsnNode(Opcodes.LXOR));
        }
        list.add(new InsnNode(Opcodes.L2I));

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

                    if(insn.getOpcode() >= Opcodes.ICONST_0 && insn.getOpcode() <= Opcodes.ICONST_5) {
                        InsnList list = new InsnList();

                        list.add(new LdcInsnNode(getRandomUTFString(insn.getOpcode() - Opcodes.ICONST_0)));

                        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));

                        mn.instructions.insertBefore(insn, list);
                        mn.instructions.remove(insn);
                    }

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
