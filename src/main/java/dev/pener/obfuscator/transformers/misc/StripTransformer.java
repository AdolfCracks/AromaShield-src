package dev.pener.obfuscator.transformers.misc;

import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.Opcodes;

public class StripTransformer extends Transformer {
    public StripTransformer() {
        super("StripDebugInfo",true);
    }

    @Override
    public void run() {
        Obfuscator.getClasses().forEach(cn -> {
            cn.sourceFile = "protected by AromaShield";
            cn.sourceDebug = "protected by AromaShield";
            cn.methods.forEach(mn -> {
                mn.localVariables = null;
                mn.parameters = null;
                mn.instructions.forEach(insn -> {
                    if(insn.getOpcode() == Opcodes.NOP)
                        mn.instructions.remove(insn);
                });
            });
        });
    }
}
