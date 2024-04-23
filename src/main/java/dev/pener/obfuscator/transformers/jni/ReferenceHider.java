package dev.pener.obfuscator.transformers.jni;

import dev.pener.obfuscator.core.Transformer;
import java.util.Arrays;
import org.objectweb.asm.tree.*;

public class ReferenceHider extends Transformer {
    public ReferenceHider() {
        super("ReferenceHider",false);
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {
            if (!getSettings().shouldTransform(cn.name)) return;
            cn.methods.forEach(mn -> {
                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if (insn instanceof MethodInsnNode) {

                    }
                });
               });
            });
        }

}
