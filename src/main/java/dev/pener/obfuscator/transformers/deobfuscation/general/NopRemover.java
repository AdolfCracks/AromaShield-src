package dev.pener.obfuscator.transformers.deobfuscation.general;

import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import java.util.Arrays;

public class NopRemover extends Transformer {

    public NopRemover() {
        super("deobf_general_NopRemover",false);
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {

            if (!getSettings().shouldTransform(cn.name)) return;
            cn.methods.forEach(mn -> {
                if(mn.instructions.size() < 2)
                    return;

                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if(insn.getOpcode() == Opcodes.NOP){
                       mn.instructions.remove(insn);
                    }

                });
            });
        });

    }
}
