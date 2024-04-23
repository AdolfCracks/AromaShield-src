package dev.pener.obfuscator.transformers.misc;

import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationStringHider extends Transformer {

    public AnnotationStringHider() {
        super("AnnonationStringEncryption",true);
    }

    @Override
    public void run() {

        HashMap<String,String> strings = new HashMap<>();
        List<String> calls = new ArrayList<>();
        String hashName = getRandomJVMString(10);

        getClasses().forEach(cn -> {
            if (!getSettings().shouldTransform(cn.name)) return;
            if(cn.visibleAnnotations == null)
                return;
            cn.visibleAnnotations.forEach(ann -> {

                if(ann == null || ann.values == null)
                    return;

                for(int i = 0; i < ann.values.size(); i++){
                    String key = (String) ann.values.get(i);
                    Object value = ann.values.get(++i);
                    if(value instanceof String){
                        calls.add(ann.desc.split("L")[1].split(";")[0] + "." + key);
                        String r = getRandomUTFString(15);
                        strings.put(r, (String)value);
                        ann.values.set(i, r);
                    }
                }
            });
        });

        getClasses().forEach(cn -> {
            AtomicBoolean found = new AtomicBoolean(false);
            cn.methods.forEach(mn -> {
                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if(insn instanceof MethodInsnNode){
                        MethodInsnNode min = (MethodInsnNode) insn;
                        if(!calls.contains(min.owner + "." + min.name))
                            return;

                        mn.instructions.insert(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, hashName, "(Ljava/lang/String;)Ljava/lang/String;", false));

                        found.set(true);
                    }
                });


            });
            if(!found.get())
                return;
            FieldNode fn = new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, hashName, "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", null);
            cn.fields.add(fn);
            InsnList il = new InsnList();
            il.add(new TypeInsnNode(Opcodes.NEW, "java/util/HashMap"));
            il.add(new InsnNode(Opcodes.DUP));
            il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false));
            il.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, hashName, "Ljava/util/HashMap;"));


            strings.forEach((b, k) -> {
                il.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, hashName, "Ljava/util/HashMap;"));
                il.add(new LdcInsnNode(b));
                il.add(new LdcInsnNode(k));
                il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
                il.add(new InsnNode(Opcodes.POP));
            });

            MethodNode clinit = ASMUtils.getClinit(cn);
            clinit.instructions.insertBefore(clinit.instructions.getFirst(), il);

            MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, hashName, "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            mn.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, hashName, "Ljava/util/HashMap;"));
            mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false));
            mn.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/String"));
            mn.instructions.add(new InsnNode(Opcodes.ARETURN));
            cn.methods.add(mn);

            System.out.println("added in " + cn.name);

        });






    }
}