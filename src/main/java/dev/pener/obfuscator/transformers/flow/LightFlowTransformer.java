package dev.pener.obfuscator.transformers.flow;

import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.transformers.essentials.number.BasicNumberTransformer;
import dev.pener.obfuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class LightFlowTransformer extends Transformer {

    public LightFlowTransformer() {
        super("LightFlow",true  );
    }

    public void addSwitch(MethodNode mn, AbstractInsnNode first, AbstractInsnNode last){

        InsnList list = new InsnList();

        LabelNode bLabel = new LabelNode();
        LabelNode cLabel = new LabelNode();
        LabelNode end = new LabelNode();


        int a = nextInt(15000) + 10;
        int b = a - nextInt(15000) - 5;
        LookupSwitchInsnNode switchNode = new LookupSwitchInsnNode(end, Stream.of(b, a).mapToInt(i -> i).toArray(), Arrays.asList(bLabel, cLabel).toArray(new LabelNode[2]));
        list.add(new LabelNode());

        list.add(BasicNumberTransformer.getFastIntEncryption(a));
        list.add(switchNode);
        list.add(bLabel);
        list.add(new JumpInsnNode(Opcodes.GOTO, end));
        list.add(cLabel);

        mn.instructions.insertBefore(first, list);
        mn.instructions.insert(last, ASMUtils.getUselessFunctionOutput(mn.desc));
        mn.instructions.insert(last, end);

    }



    public InsnList getBytecodeVariant1(){
        InsnList output = new InsnList();
        output.add(new LdcInsnNode(nextString(20,40)));
        output.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
        output.add(new InsnNode(Opcodes.POP));
        return output;
    }

    public InsnList getBytecodeVariant2(){
        InsnList output = new InsnList();
        output.add(new LdcInsnNode(nextString(20,40)));
        output.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
        output.add(new LdcInsnNode(nextString(20,40)));
        output.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
        output.add(new InsnNode(Opcodes.POP2));
        return output;
    }

    public InsnList getBytecodeVariant3(){
        InsnList output = new InsnList();
        output.add(new LdcInsnNode(nextString(20,40)));
        output.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
        output.add(new InsnNode(Opcodes.I2L));
        output.add(new LdcInsnNode(nextLong()));
        output.add(new InsnNode(Opcodes.LXOR));
        output.add(new InsnNode(Opcodes.L2I));
        output.add(new LdcInsnNode(nextString(20,40)));
        output.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
        output.add(new InsnNode(Opcodes.IXOR));
        if(nextBoolean()){
            if(nextBoolean()){
                output.add(new InsnNode(Opcodes.DUP));
                output.add(new InsnNode(Opcodes.DUP));
                output.add(new InsnNode(Opcodes.POP2));
            }else{
                output.add(new InsnNode(Opcodes.DUP));
                output.add(new InsnNode(Opcodes.DUP));
                output.add(new InsnNode(Opcodes.POP));
                output.add(new InsnNode(Opcodes.POP));

            }
        }
        output.add(new InsnNode(Opcodes.POP));
        return output;
    }





    @Override
    public void run() {
        getClasses().forEach(cn -> {

            if (!getSettings().shouldTransform(cn.name)) return;
            cn.methods.forEach(mn -> {
                if(mn.name.contains("<"))
                    return;
                if(mn.instructions.size() < 2)
                    return;


               for(int i = 0; i < nextInt(5,10); i++){
                    addSwitch(mn, mn.instructions.getFirst(),  mn.instructions.getLast());
                }
                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if(insn.getOpcode() == Opcodes.GOTO || insn.getOpcode() == Opcodes.RETURN){
                        if(nextBoolean()){
                            for(int i = 0; i < nextInt(1,3); i++) {
                                addSwitch(mn, insn, insn);
                            }
                        }
                    }

                  /*  if(insn instanceof VarInsnNode || insn.getOpcode() == Opcodes.POP || insn instanceof MethodInsnNode){
                        int variant = nextInt(3);
                        InsnList output = null;
                        switch (variant){
                            case 0:
                                output = getBytecodeVariant1();
                                break;
                            case 1:
                                output = getBytecodeVariant2();
                                break;
                            case 2:
                                output = getBytecodeVariant3();
                                break;
                        }

                        if(output == null)
                            return;
                        mn.instructions.insertBefore(insn, output);
                    }*/


                });
            });
        });

    }


}