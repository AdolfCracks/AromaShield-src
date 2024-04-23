package dev.pener.obfuscator.utils;


import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ASMUtils {

    public static boolean isNumber(AbstractInsnNode insn){
        return insn.getOpcode() == Opcodes.BIPUSH || insn.getOpcode() == Opcodes.SIPUSH || (insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode)insn).cst instanceof Number);
    }

    public static MethodNode getMethod(ClassNode node, String name, String desc) {
        return node.methods.stream()
                .filter(m -> m.name.equals(name))
                .filter(m -> m.desc.equals(desc))
                .findAny()
                .orElse(null);
    }

    public static FieldNode getField(ClassNode node, String name, String desc) {
        return node.fields.stream()

                .filter(m -> m.name.equals(name))
                .filter(m -> m.desc.equals(desc))
                .findAny()
                .orElse(null);
    }

    public static <T extends AbstractInsnNode> void forEach(InsnList instructions,
                                                            Class<T> type,
                                                            Consumer<T> consumer) {
        AbstractInsnNode[] array = instructions.toArray();
        for (AbstractInsnNode node : array) {
            if (node.getClass() == type) {
                //noinspection unchecked
                consumer.accept((T) node);
            }
        }
    }

    public static void forEach(InsnList instructions, Consumer<AbstractInsnNode> consumer) {
        forEach(instructions, AbstractInsnNode.class, consumer);
    }



    public static AbstractInsnNode newIntegerNode(int i) {
        if (i >= -1 && i <= 5) {
            return new InsnNode(Opcodes.ICONST_0 + i);
        } else if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
            return new IntInsnNode(Opcodes.BIPUSH, i);
        } else if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
            return new IntInsnNode(Opcodes.SIPUSH, i);
        } else {
            return new LdcInsnNode(i);
        }
    }

    public static AbstractInsnNode pushInt(int value){
        if(value >= -1 && value <= 5){
            return new InsnNode(value + 3);
        }else if(value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE){
            return new IntInsnNode(Opcodes.BIPUSH, value);
        }else if(value >= Short.MIN_VALUE && value <= Short.MAX_VALUE){
            return new IntInsnNode(Opcodes.SIPUSH, value);
        }else{
            return new LdcInsnNode(value);
        }
    }

    public static InsnList getUselessFunctionOutput(String desc) {
        Type type = Type.getReturnType(desc);
        InsnList l = new InsnList();
        switch(type.getClassName()){
            case "int":
                l.add(new InsnNode(Opcodes.ICONST_0));
                l.add(new InsnNode(Opcodes.IRETURN));
            case "void":
                l.add(new InsnNode(Opcodes.RETURN));
            case "double":
                l.add(new InsnNode(Opcodes.ICONST_0));
                l.add(new InsnNode(Opcodes.I2D));
                l.add(new InsnNode(Opcodes.DRETURN));
            case "float":
                l.add(new InsnNode(Opcodes.ICONST_0));
                l.add(new InsnNode(Opcodes.I2F));
                l.add(new InsnNode(Opcodes.FRETURN));
            case "long":
                l.add(new InsnNode(Opcodes.ICONST_0));
                l.add(new InsnNode(Opcodes.I2L));
                l.add(new InsnNode(Opcodes.LRETURN));
            case "boolean":
                l.add(new InsnNode(Opcodes.ICONST_0));
                l.add(new InsnNode(Opcodes.IRETURN));
            default:
                l.add(new InsnNode(Opcodes.ACONST_NULL));
                l.add(new InsnNode(Opcodes.ARETURN));

        }

        return l;
    }

    public static MethodNode getClinit(ClassNode cn){
        AtomicReference<MethodNode> clinit = new AtomicReference<>();

        cn.methods.forEach(mn -> {
            if(mn.name.equalsIgnoreCase("<clinit>"))
                clinit.set(mn);
        });
        if(clinit.get() != null){

            return clinit.get();

        }
        MethodNode mn = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", "()V", null);
        mn.instructions.insert(new InsnNode(Opcodes.RETURN));
        cn.methods.add(mn);
        return mn;
    }

    public static InsnList pushListInt(int val) {
        InsnList l = new InsnList();
        l.add(pushInt(val));
        return l;
    }
}
