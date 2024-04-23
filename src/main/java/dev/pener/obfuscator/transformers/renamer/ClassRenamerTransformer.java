package dev.pener.obfuscator.transformers.renamer;

import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.utils.StringUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassRenamerTransformer extends Transformer {
    public ClassRenamerTransformer() {
        super("ClassRenamer",true);
    }

    @Override
    public void run() {
        StringBuilder prefix = new StringBuilder();

        if(getSettings().renameTo.equalsIgnoreCase("short")){
            prefix.append("a/");
        } else if (getSettings().renameTo.equalsIgnoreCase("long")) {
            for(int a = 0; a < 20; a++){
                prefix.append("AromaShield/");
            }
        }else{
            throw new RuntimeException("Invalid renameTo option: " + getSettings().renameTo);
        }

        /*AtomicInteger index = new AtomicInteger(0);
        getClasses().forEach(cn -> {
            System.out.println(cn.name + " - " + getSettings().shouldRename(cn.name));
            if(!getSettings().shouldRename(cn.name)) return;
            String newName = name + getAlphabetical(index.incrementAndGet());
            addMap(cn.name, newName);
            cn.name = newName;
            int inner = 0;
            for (InnerClassNode ic : cn.innerClasses) {
                String n = ic.name.replace(cn.name, "");
                if (!n.startsWith("$")) continue;
                n = "$" + getAlphabetical(++inner);
                addMap(ic.name,  newName + n);
            }
        });*/

        //fixed renamer (now works with mcp)

        List<String> nonSubclass = new ArrayList<>();
        List<String> subclass = new ArrayList<>();
        for(ClassNode cn : getClasses()) {
            if(cn.name.contains("$")) {
                subclass.add(cn.name);
            } else {
                nonSubclass.add(cn.name);
            }
        }
        List<String> allNames = new ArrayList<>();
        allNames.addAll(nonSubclass);
        allNames.addAll(subclass);

        int index = 0;
        for(String name : allNames) {
            if(getSettings().shouldRename(name)) {
                if(name.contains("$")) {
                    String base = Obfuscator.getInstance().maps.getOrDefault(StringUtil.split(name,"$")[0],StringUtil.split(name,"$")[0]);
                    StringBuilder rest = new StringBuilder();
                    for(int i = 1;i<StringUtil.split(name,"$").length;i++) {
                        rest.append("$").append(getAlphabetical(++index));
                    }
                    addMap(name, base+rest);
                }else {
                    addMap(name, prefix+getAlphabetical(++index));
                }
            }
        }

    }


}
