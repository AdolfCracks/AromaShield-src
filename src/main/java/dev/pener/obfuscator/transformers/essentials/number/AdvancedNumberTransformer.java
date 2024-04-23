package dev.pener.obfuscator.transformers.essentials.number;

import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedNumberTransformer extends Transformer {
    public AdvancedNumberTransformer() {
        super("AdvancedNumberEncryption",true);
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {
            if (!getSettings().shouldTransform(cn.name)) return;
            if((cn.access & Opcodes.ACC_ANNOTATION) == Opcodes.ACC_ANNOTATION)
                return;
            if((cn.access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE)
                return;

            MethodNode clinit = ASMUtils.getClinit(cn);

            // int opcode, String owner, String name, String descriptor

            List<InsnList> initializers = new ArrayList<>();
            List<InsnList> transformers = new ArrayList<>();

            String name = getRandomJVMString(10);


            FieldNode fn = new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "AromaShield" + name , "[I", null, null);
            cn.fields.add(fn);


            cn.methods.forEach(mn -> {
                if(mn.instructions.size() == 0)
                    return;

                if(mn.name.equalsIgnoreCase("<clinit>"))
                    return;
                if(Arrays.stream(mn.instructions.toArray()).filter(insn -> insn.getOpcode() == Opcodes.BIPUSH || insn.getOpcode() == Opcodes.SIPUSH).toArray().length == 0)
                    return;

                final int index = initializers.size();

                AtomicInteger seed = new AtomicInteger(nextInt(100) + 10);


                InsnList init = new InsnList();
                int iseed = nextInt(100) + 10;
                init.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, fn.name, fn.desc));
                init.add(new InsnNode(Opcodes.DUP));
                //init.add(ASMUtils.pushInt(index));
                init.add(BasicNumberTransformer.getFastIntEncryption(index));
                init.add(new IntInsnNode(Opcodes.BIPUSH, seed.get()));
                init.add(new IntInsnNode(Opcodes.BIPUSH, iseed));
                init.add(new InsnNode(Opcodes.IXOR));
                init.add(new InsnNode(Opcodes.IASTORE));

                init.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, fn.name, fn.desc));
                initializers.add(init);
                seed.getAndSet(seed.get() ^ iseed);



                for(int i = 0; i < nextInt(4) + 4; i++){
                    InsnList transform = new InsnList();
                    int aseed = nextInt(100) + 10;


                    seed.getAndSet(seed.get() ^ aseed);
                    transform.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, fn.name, fn.desc));
                    transform.add(BasicNumberTransformer.getFastIntEncryption(index));
                    transform.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, fn.name, fn.desc));
                    transform.add(BasicNumberTransformer.getFastIntEncryption(index));
                    transform.add(new InsnNode(Opcodes.IALOAD));
                    transform.add(nextBoolean() ? ASMUtils.pushListInt(aseed) :  BasicNumberTransformer.getFastIntEncryption(aseed));
                    transform.add(new InsnNode(Opcodes.IXOR));
                    transform.add(new InsnNode(Opcodes.IASTORE));

                    transformers.add(transform);
                }



                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {



                    if(insn.getOpcode() == Opcodes.BIPUSH || insn.getOpcode() == Opcodes.SIPUSH){


                        IntInsnNode integer = (IntInsnNode) insn;

                        InsnList l = new InsnList();
                        l.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, fn.name, fn.desc));
                        l.add(BasicNumberTransformer.getFastIntEncryption(index));
                        l.add(new InsnNode(Opcodes.IALOAD));
                        l.add(BasicNumberTransformer.getFastIntEncryption(integer.operand ^ seed.get()));
                        l.add(new InsnNode(Opcodes.IXOR));


                        mn.instructions.insert(integer,l);
                        mn.instructions.remove(integer);


                    }
                });






            });



            InsnList output = new InsnList();
            Collections.shuffle(initializers);
            Collections.shuffle(transformers);

            output.add(BasicNumberTransformer.getFastIntEncryption(initializers.size()));
            output.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_INT));
            output.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, fn.name, fn.desc));
            initializers.forEach(output::add);
            transformers.forEach(output::add);

            clinit.instructions.insertBefore(clinit.instructions.getFirst(), output);
        });
    }
}
