package dev.pener.obfuscator.transformers.crasher;

import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class CFRCrasher extends Transformer {

    public CFRCrasher() {
        super("CFRCrasher", true);
    }

    // Compare crasher
    public InsnList variant1() {
        InsnList list = new InsnList();


        LabelNode label1 = new LabelNode();
        LabelNode label2 = new LabelNode();
        LabelNode label3 = new LabelNode();
        list.add(new InsnNode(Opcodes.ICONST_5));
        list.add(new InsnNode(Opcodes.ICONST_5));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label1));
        list.add(new LdcInsnNode(nextInt(99999)));
        list.add(new LdcInsnNode(32));
        list.add(new VarInsnNode(BIPUSH,20));
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label2));
        list.add(new LdcInsnNode(nextInt(99999)));
        list.add(label2);
        list.add(new JumpInsnNode(Opcodes.GOTO, label3));
        list.add(label1);
        list.add(new LdcInsnNode(nextInt(99999)));
        list.add(label3);
        return list;
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
                mn.instructions.insertBefore(mn.instructions.getFirst(), variant1());

            });
        });
    }
    }

