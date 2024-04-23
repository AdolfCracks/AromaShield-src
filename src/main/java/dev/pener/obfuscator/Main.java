package dev.pener.obfuscator;

import dev.pener.obfuscator.core.Obfuscator;
import dev.pener.obfuscator.core.Settings;
import dev.pener.obfuscator.transformers.crasher.CFRCrasher;
import dev.pener.obfuscator.transformers.deobfuscation.general.NopRemover;
import dev.pener.obfuscator.transformers.deobfuscation.qprotect.FlowObfuscation;
import dev.pener.obfuscator.transformers.essentials.number.AdvancedNumberTransformer;
import dev.pener.obfuscator.transformers.essentials.number.BasicNumberTransformer;
import dev.pener.obfuscator.transformers.essentials.number.FunnyNumberTransformer;
import dev.pener.obfuscator.transformers.essentials.string.Case_StringTransformer;
import dev.pener.obfuscator.transformers.essentials.string.DES_StringTransformer;
import dev.pener.obfuscator.transformers.essentials.string.Stack_StringTransformer;
import dev.pener.obfuscator.transformers.essentials.string.XOR_StringTransformer;
import dev.pener.obfuscator.transformers.flow.LightFlowTransformer;
import dev.pener.obfuscator.transformers.flow.SwitchToIfTransformer;
import dev.pener.obfuscator.transformers.misc.AnnotationStringHider;
import dev.pener.obfuscator.transformers.misc.ReflectionTransformer;
import dev.pener.obfuscator.transformers.misc.StripTransformer;
import dev.pener.obfuscator.transformers.renamer.ClassRenamerTransformer;
import dev.pener.obfuscator.transformers.renamer.FieldRenamerTransformer;
import dev.pener.obfuscator.transformers.renamer.MethodRenamerTransformer;
import dev.pener.obfuscator.utils.Configs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Main {

    static boolean dev = true;
    public static void main(String[] args){

      //  GuiMain guiMain = new GuiMain();
       if (dev) {
           long time = System.currentTimeMillis();
           try {
/*
    Transformers that have issues with performance
    STACK TRANSFORMER_STRING - +900ms
    LIGHT FLOW TRANSFORMER - + 200ms
    FUNNY NUMBER TRANSFORMER - +2000ms

    GENERAL ISSUES

    XOR STRING TRANSFORMER - DOSENT WORK WITH MCP
  */

               //  Obfuscator.getInstance().obfuscate("/home/server/.minecraft/versions/Test/TestOr.jar", "/home/server/.minecraft/versions/Test/Test.jar", Arrays.asList(
               Obfuscator.getInstance().obfuscate("C:\\Users\\Admin\\Downloads\\Rise v6\\files\\RiseCompressedOriginal.jar", "C:\\Users\\Admin\\Downloads\\Rise v6\\files\\RiseCompressed.jar", Arrays.asList(
/*                               //   Obfuscator.getInstance().obfuscate("C:\\Users\\Admin\\AppData\\Roaming\\.minecraft\\versions\\North\\Client.jar", "C:\\Users\\Admin\\AppData\\Roaming\\.minecraft\\versions\\North\\North.jar", Arrays.asList(
                              new AnnotationStringHider(),
                          //     new ClassRenamerTransformer(),
                                         new Stack_StringTransformer(),
                               new XOR_StringTransformer(),
                                  new Case_StringTransformer(),
                                  new AdvancedNumberTransformer(),
                               //  new FunnyNumberTransformer(),
                                 new LightFlowTransformer(),
                                new SwitchToIfTransformer(),
                            new StripTransformer(),
                               new ReflectionTransformer(),
                          //       new MethodRenamerTransformer(),
                               new FieldRenamerTransformer()
                               //  new CFRCrasher()*/
                       new NopRemover(),
                       new FlowObfuscation()
                       ),
                       new Settings()
                             //  .addPackagesToRename("never/", "AromaShield")
                               .renameTo("long")
                               .fullExclude("com/")
                               .fullExclude("us/")
                               .fullExclude("net/java/")
                               .fullExclude("org/")
                               .fullExclude("io/")
                               .fullExclude("paulscode/")
                               .fullExclude("by/")
                               .fullExclude("okio/")
                               .fullExclude("okhttp3/")
                               .fullExclude("mozilla/")
                               .fullExclude("linux/")
                               .fullExclude("kotlin/")
                               .fullExclude("javax/")
                               .fullExclude("darwin/")
                               .fullExclude("joptsimple/")
                               .fullExclude("sun/")
                               .fullExclude("schema/")
                               .fullExclude("tv/")
                               .fullExclude("oshi/")
                               .excludeFromRenaming("net/minecraft/client/main/")
               );
               System.out.println(System.currentTimeMillis() - time + "ms");
           } catch (Exception e) {
               System.out.println("Something went wrong while obfuscating the jar file!");
               System.out.println("Error: " + e.getMessage());
               e.printStackTrace();
           }
           return;
       }
       if (args.length != 1) {
           System.err.println("Invalid arguments expected : java -jar obfuscator.jar [config name]");
           return;
       }
        String dateString = "2023-11-29";
        LocalDate targetDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Check if the current date is greater than the target date
        if (currentDate.isAfter(targetDate)) {
            return;
        }

       String fileName = args[0];
       Configs configs = new Configs(fileName);

    }

}
