package dev.pener.obfuscator.transformers.deobfuscation.qprotect;

import dev.pener.obfuscator.core.Transformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlowObfuscation extends Transformer {
    public FlowObfuscation() {
        super("deobf_qprotect_FlowObfuscation",false);
    }

    @Override
    public void run() {
        getClasses().forEach(cn -> {

            AtomicBoolean done = new AtomicBoolean(false);
            if (!getSettings().shouldTransform(cn.name)) return;
          /*  ArrayList<TableSwitchInsnNode> tableToRemove = new ArrayList<>();
            ArrayList<InsnList> */
            cn.methods.forEach(mn -> {
                if(mn.instructions.size() < 2)
                    return;
                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {
                    if(insn instanceof TableSwitchInsnNode){
                      TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode) insn;
                      if (cn.name.equalsIgnoreCase("hackclient/rise/aan")) {
                          boolean Flow  = true;
                           for (LabelNode labelNode : tableSwitchInsnNode.labels) {
                               if (labelNode != tableSwitchInsnNode.dflt) {
                                   if (labelNode.getNext().getNext().getOpcode() != GOTO) {
                                       Flow = false;
                                   }

                                   done.set(true);
                               }

                           }
                           if(tableSwitchInsnNode.dflt.getNext().getNext().getOpcode() == GOTO) Flow = false;
                           if (Flow) {
                               AbstractInsnNode next = tableSwitchInsnNode.getNext().getNext().getNext();
                           InsnList list = new InsnList();
                            while (next.getOpcode() != GOTO) {
                                System.out.print(insnToString(next));
                                list.add(next);
                                next = next.getNext();
                            }
                            if (list.size() != 0) {
                          //      mn.instructions.insertBefore(tableSwitchInsnNode, list);
                                mn.instructions.remove(tableSwitchInsnNode);
// TO DO MAKE THIS SHIT WORK XD

                                System.out.println("Removed flow! ");
                            }
                           }
                        }
                    }

                });
            });
        });
    }


    public static String insnToString(AbstractInsnNode insn){
        insn.accept(mp);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }

    private static Printer printer = new Textifier();
    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);
}
