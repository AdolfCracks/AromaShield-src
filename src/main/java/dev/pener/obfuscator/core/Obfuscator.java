package dev.pener.obfuscator.core;


import dev.pener.obfuscator.transformers.crasher.CFRCrasher;
import dev.pener.obfuscator.transformers.essentials.number.AdvancedNumberTransformer;
import dev.pener.obfuscator.transformers.essentials.number.BasicNumberTransformer;
import dev.pener.obfuscator.transformers.essentials.number.FunnyNumberTransformer;
import dev.pener.obfuscator.transformers.essentials.string.Case_StringTransformer;
import dev.pener.obfuscator.transformers.essentials.string.DES_StringTransformer;
import dev.pener.obfuscator.transformers.essentials.string.Stack_StringTransformer;
import dev.pener.obfuscator.transformers.essentials.string.XOR_StringTransformer;
import dev.pener.obfuscator.transformers.flow.AggresiveFlowTransformer;
import dev.pener.obfuscator.transformers.flow.LightFlowTransformer;
import dev.pener.obfuscator.transformers.flow.SwitchToIfTransformer;
import dev.pener.obfuscator.transformers.misc.AnnotationStringHider;
import dev.pener.obfuscator.transformers.misc.ReflectionTransformer;
import dev.pener.obfuscator.transformers.misc.StripTransformer;
import dev.pener.obfuscator.transformers.renamer.ClassRenamerTransformer;
import dev.pener.obfuscator.transformers.renamer.FieldRenamerTransformer;
import dev.pener.obfuscator.transformers.renamer.MethodRenamerTransformer;
import dev.pener.obfuscator.utils.JarLoader;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;

public class Obfuscator {

    public JarLoader loader;
    private static Obfuscator instance = new Obfuscator();
    public HashMap<String, String> maps = new HashMap<>();
    public Settings settings;

    // wait im making joint
    public static Obfuscator getInstance(){
        return instance;
    }
    public Random r = new Random();

    public void obfuscate(String inputFile, String outputFile, List<Transformer> transformers, Settings settings) throws Exception{
        loader = new JarLoader(inputFile, outputFile);
        System.out.println("Loading the jar file");
        loader.loadJar();
        maps.clear();
        this.settings = settings;

        Collections.shuffle(loader.classes);

        transformers.forEach(Transformer::run);

        System.out.println("Remapping the classes");
        SimpleRemapper remapper = new SimpleRemapper(maps);
        for(int i = 0; i < loader.classes.size(); i++){
            ClassNode cn = loader.classes.get(i);
            ClassNode remapped = new ClassNode();
            cn.accept(new ClassRemapper(remapped, remapper));
            loader.classes.set(i, remapped);
        }

        for(int i = 0; i < loader.newClasses.size(); i++){
            ClassNode cn = loader.newClasses.get(i);
            ClassNode remapped = new ClassNode();
            cn.accept(new ClassRemapper(remapped, remapper));
            loader.newClasses.set(i, remapped);
        }

        /* Transform Main-Class in the manifest */
        loader.files.forEach(file -> {
            if(file.name.equalsIgnoreCase("META-INF/MANIFEST.MF")){
                String manifest = new String(file.bytes);
                for(String v : maps.keySet()){
                    manifest = manifest.replace("Main-Class: " + v.replace("/", "."), "Main-Class: " + maps.get(v).replace("/", "."));
                }
                file.bytes = manifest.getBytes();
            }
        });


        System.out.println("Saving the jar file");
        loader.saveJar();
    }

    public static List<ClassNode> getClasses(){
        return getInstance().loader.classes;
    }

    public static ArrayList<Transformer> getTransformers() {
        ArrayList<Transformer> transformers = new ArrayList<>();
        transformers.add(new AnnotationStringHider());
        transformers.add(new ClassRenamerTransformer());
        transformers.add(new ReflectionTransformer());
        transformers.add(new Stack_StringTransformer());
        transformers.add(new XOR_StringTransformer());
        transformers.add(new Case_StringTransformer());
        transformers.add(new AdvancedNumberTransformer());
        transformers.add(new LightFlowTransformer());
        transformers.add(new SwitchToIfTransformer());
        transformers.add(new StripTransformer());
        transformers.add(new MethodRenamerTransformer());
        transformers.add(new FieldRenamerTransformer());
        transformers.add(new CFRCrasher());

        return transformers;
        }


}
