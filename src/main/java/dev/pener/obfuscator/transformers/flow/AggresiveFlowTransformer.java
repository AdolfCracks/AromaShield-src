package dev.pener.obfuscator.transformers.flow;

import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;

public class AggresiveFlowTransformer extends Transformer {


    public AggresiveFlowTransformer() {
        super("AggresiveFlow",false);
    }

    public InsnList getBytecodeVariant1(){
        int variant = nextInt(2);
        InsnList output = new InsnList();
        output.add(new LdcInsnNode(nextString(5,60)));
        switch (variant) {
            case 0:
                output.add(new MethodInsnNode(INVOKEVIRTUAL,"java/lang/String","getBytes","()[B", false));
                break;
            case 1:
                output.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
                break;
        }

        output.add(new InsnNode(Opcodes.POP));
        return output;
    }
    @Override
    public void run() {
        boolean pener = true;
        getClasses().forEach(cn -> {
            cn.methods.forEach(mn -> {
                if(mn.name.contains("<"))
                    return;
                if(mn.instructions.size() < 2)
                    return;
                if (pener) return;

                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if(insn instanceof LdcInsnNode || insn.getOpcode() == Opcodes.POP || insn instanceof MethodInsnNode){
                        int variant =0;
                        InsnList output = null;
                        switch (variant){
                            case 0:
                                output = getBytecodeVariant1();
                                break;
                            case 1:
                               // output = getBytecodeVariant2();
                                break;
                            case 2:
                               // output = getBytecodeVariant3();
                                break;
                        }

                        if(output == null)
                            return;
                        mn.instructions.insertBefore(insn, output);
                    }
                });
            });
        });
            }
    }

