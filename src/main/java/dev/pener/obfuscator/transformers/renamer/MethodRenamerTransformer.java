package dev.pener.obfuscator.transformers.renamer;

import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodRenamerTransformer extends Transformer {

    public MethodRenamerTransformer() {
        super("MethodRenamer",true);
    }

    public boolean shouldIgnore(String name){
        if(name.equalsIgnoreCase("<clinit>")) return true;
        if(name.equalsIgnoreCase("<init>")) return true;
        if(name.equalsIgnoreCase("main")) return true;
        if(name.contains("$")) return true;
        return false;

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
            cn.methods.forEach(mn -> {
                if(!shouldIgnore(mn.name)){
                    int index = n.incrementAndGet();
                    String name = getAlphabetical(index);


                    renameExtendingClassesObject(cn, mn.name + mn.desc, name, mn.name);

                }
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
            cn.methods.forEach(mn -> {
                if(!shouldIgnore(mn.name)){
                    int index = n.incrementAndGet();
                    String name = getAlphabetical(index);
                    if(!Obfuscator.getInstance().maps.containsKey(cn.name + "." + mn.name + mn.desc))
                        renameExtendingClassesObject(cn, mn.name + mn.desc, name, mn.name);

                }
            });
        });
    }
}