package dev.pener.obfuscator.transformers.flow;

import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.utils.ASMUtils;
import dev.pener.obfuscator.utils.VariableUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.List;

public class SwitchToIfTransformer extends Transformer {
    public SwitchToIfTransformer() {
        super("Switch_To_IF",true);
    }

    public void magic(ClassNode classNode) {
        for(MethodNode node : classNode.methods) {
            if (Modifier.isAbstract(node.access) || Modifier.isNative(node.access))
                return;

            VariableUtil provider = new VariableUtil(node);
            int resultSlot = provider.allocateVar();

            for (AbstractInsnNode abstractInsnNode : node.instructions.toArray()) {
                if (Obfuscator.getInstance().r.nextBoolean()) {
                    if (abstractInsnNode instanceof TableSwitchInsnNode) {
                        TableSwitchInsnNode switchInsnNode = (TableSwitchInsnNode) abstractInsnNode;

                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ISTORE, resultSlot));

                        int j = 0;

                        for (int i = switchInsnNode.min; i <= switchInsnNode.max; i++) {
                            insnList.add(new VarInsnNode(Opcodes.ILOAD, resultSlot));
                            insnList.add(ASMUtils.pushInt(i));
                            insnList.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, switchInsnNode.labels.get(j)));

                            j++;
                        }
                        insnList.add(new JumpInsnNode(Opcodes.GOTO, switchInsnNode.dflt));


                        node.instructions.insert(abstractInsnNode, insnList);
                        node.instructions.remove(abstractInsnNode);
                    }
                    if (abstractInsnNode instanceof LookupSwitchInsnNode) {
                        LookupSwitchInsnNode switchInsnNode = (LookupSwitchInsnNode) abstractInsnNode;

                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ISTORE, resultSlot));

                        List<Integer> keys = switchInsnNode.keys;
                        for (int i = 0; i < keys.size(); i++) {
                            Integer key = keys.get(i);
                            insnList.add(new VarInsnNode(Opcodes.ILOAD, resultSlot));
                            insnList.add(ASMUtils.pushInt(key));
                            insnList.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, switchInsnNode.labels.get(i)));

                        }

                        insnList.add(new JumpInsnNode(Opcodes.GOTO, switchInsnNode.dflt));


                        node.instructions.insert(abstractInsnNode, insnList);
                        node.instructions.remove(abstractInsnNode);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        getClasses().forEach(this::magic);
    }
}
