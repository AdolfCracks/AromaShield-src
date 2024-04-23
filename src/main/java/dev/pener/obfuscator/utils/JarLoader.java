package dev.pener.obfuscator.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * This class is a utility for working with JAR files.
 */
public class JarLoader {

    /**
     * A list of ClassNode objects that have been parsed from the input JAR file.
     */
    public List<ClassNode> classes = new ArrayList<ClassNode>();

    public List<ClassNode> newClasses = new ArrayList<ClassNode>();

    public List<UnknownFile> files = new ArrayList<UnknownFile>();

    public String input;
    public String output;
    public boolean xorOutputName = false;


    public JarLoader(String input,String output) {
        this.input = input;
        this.output = output;
    }


    /**
     * Loads the input JAR file and parses its contents into a list of ClassNode objects and a list of UnknownFile objects.
     *
     * @throws Exception If an error occurs while reading or parsing the input JAR file.
     */


    public void loadJar() throws Exception{
        File inputFile = new File(input);
        try (ZipInputStream jarInputStream = new ZipInputStream(Files.newInputStream(inputFile.toPath()))) {
            ZipEntry zipEntry;
            while ((zipEntry = jarInputStream.getNextEntry()) != null) {


                if (zipEntry.getName().endsWith(".class") || zipEntry.getName().endsWith(".class/")) {
                    try{
                        ClassReader reader = new ClassReader(jarInputStream);
                        ClassNode classNode = new ClassNode();
                        reader.accept(classNode, 0);
                        classes.add(classNode);
                    }catch (Exception e){
                        e.printStackTrace();
                        files.add(new UnknownFile(zipEntry.getName(), jarInputStream));

                    }

                }else {
                    files.add(new UnknownFile(zipEntry.getName(), jarInputStream));files.add(new UnknownFile(zipEntry.getName(), jarInputStream));

                }
            }
        }

    }

    /**
     * Saves the contents of the input JAR file, along with any additional ClassNode objects and UnknownFile objects that have been added, to the output JAR file.
     *
     * @throws Exception If an error occurs while writing to the output JAR file.
     */








    public void saveJar() throws Exception {
        File outputFile = new File(output);
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(outputFile.toPath()))) {
            out.setComment("Obfusucated by AromaShield\n2023 - All rights reserved.");
            for (ClassNode classNode : classes) {
              //  if (Obfuscator.getInstance().settings.shouldTransform(classNode.name)) {
                    try {
                        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES); // ClassWriter.COMPUTE_FRAMES |
                        classNode.accept(writer);
                        JarEntry entry = new JarEntry(classNode.name + ".class");
                        out.putNextEntry(entry);
                        out.write(writer.toByteArray());
                    } catch (Exception e) {
                        //   e.printStackTrace();
                        System.out.println("couldnt write entry so using Compute.MAXS: " + classNode.name);
                        try {
                            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // ClassWriter.COMPUTE_FRAMES |
                            classNode.accept(writer);
                            out.putNextEntry(new JarEntry(classNode.name + ".class"));
                            out.write(writer.toByteArray());
                        } catch (Exception xd) {
                           xd.printStackTrace();
                        }
                   // }
                }
            }
            for (ClassNode classNode : newClasses) {
                //if (Obfuscator.getInstance().settings.shouldTransform(classNode.name)) {
                    try {
                        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES); // ClassWriter.COMPUTE_FRAMES |
                        classNode.accept(writer);
                        out.putNextEntry(new JarEntry(classNode.name + ".class"));
                        out.write(writer.toByteArray());
                    } catch (Exception e) {
                     //   e.printStackTrace();
                        System.out.println("couldnt write entry so using Compute.MAXS: " + classNode.name);
                        try {
                            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // ClassWriter.COMPUTE_FRAMES |
                            classNode.accept(writer);
                            out.putNextEntry(new JarEntry(classNode.name + ".class"));
                            out.write(writer.toByteArray());
                        } catch (Exception xd) {
                            xd.printStackTrace();
                        }
                    }
                //}
            }
            for(UnknownFile file : files) {
                if(file.name.endsWith("/"))
                    continue;
                if(file.name.endsWith(".md"))
                    continue;
                System.out.println("Writing entry " + file.name);
                try{
                    out.putNextEntry(new JarEntry(file.name));
                    out.write(file.bytes);
                }catch (Exception e){
                }
            }

        }

    }

    /**
     * Adds a ClassNode object to the list of ClassNode objects.
     *
     * @param cn The ClassNode object to add.
     */
    public void addClass(ClassNode cn) {
        this.classes.add(cn);
    }
}