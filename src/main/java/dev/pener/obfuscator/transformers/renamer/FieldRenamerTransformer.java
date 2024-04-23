package dev.pener.obfuscator.transformers.renamer;

import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.tree.ClassNode;

import java.util.concurrent.atomic.AtomicInteger;

public class FieldRenamerTransformer extends Transformer {


    public FieldRenamerTransformer() {
        super("FieldRenamer",true);
    }

    public void renameExtendingClassesObject(ClassNode cn, String from, String to, String mn){

        addMap(cn.name + "." + from, to);
        getClasses().forEach(ec -> {
            if(ec.superName == null)
                return;
            if(ec.superName.equalsIgnoreCase(cn.name))
                renameExtendingClassesObject(ec, from, to, mn);
        });
    }

    public String strip(String desc){
        return desc.substring(0, desc.lastIndexOf(")"));
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {
            if(cn.superName == null)
                return;
            if(!cn.superName.equals("java/lang/Object") )
                return;
            if(cn.name.contains("$"))
                return;
            if(!getSettings().shouldRename(cn.name))
                return;
            if((cn.access & ACC_ANNOTATION) == ACC_ANNOTATION)
                return;
            AtomicInteger n = new AtomicInteger();
            cn.fields.forEach(fd -> {
                String name = getAlphabetical(n.incrementAndGet());
                renameExtendingClassesObject(cn, fd.name, name, fd.name);


            });
        });

        getClasses().forEach(cn -> {
            if(cn.superName == null)
                return;
            if(cn.superName.equals("java/lang/Object") )
                return;
            if(cn.name.contains("$"))
                return;
            if(!getSettings().shouldRename(cn.name))
                return;
            AtomicInteger n = new AtomicInteger();
            cn.fields.forEach(fd -> {
                int index = n.incrementAndGet();
                String name = getAlphabetical(index);
                if(!Obfuscator.getInstance().maps.containsKey(cn.name + "." + fd.name))
                    renameExtendingClassesObject(cn, fd.name, name, fd.name);
            });
        });
    }
}