package dev.pener.obfuscator.transformers.misc;

import dev.pener.obfuscator.core.Transformer;

import java.util.Arrays;

public class SystemCallHider extends Transformer {
    public SystemCallHider() {
        super("SystemCallHider",true);
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {

            if (!getSettings().shouldTransform(cn.name)) return;
            cn.methods.forEach(mn -> {
                if (mn.name.contains("<"))
                    return;
                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {

                });
            });
        });
    }
}
