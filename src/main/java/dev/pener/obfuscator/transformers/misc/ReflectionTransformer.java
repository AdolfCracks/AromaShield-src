package dev.pener.obfuscator.transformers.misc;

import dev.pener.obfuscator.core.Transformer;
import dev.pener.obfuscator.utils.ASMUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectionTransformer extends Transformer {

    public ReflectionTransformer() {
        super("Reflection",true);
    }

    static ArrayList<MethodNode> methodNodes = new ArrayList<>();
    public InsnList InvokeStaticVariant(MethodInsnNode node, MethodNode mn) {
        String owner = node.owner;
        String name = node.name;
        String desc = node.desc;
        InsnList list = new InsnList();

        if ("()V".equals(desc)) {
                list.add(new LdcInsnNode(owner));
                list.add(new MethodInsnNode(
                        INVOKESTATIC,
                        "java/lang/Class",
                        "forName",
                        "(Ljava/lang/String;)Ljava/lang/Class;",
                        false
                ));
                list.add(new VarInsnNode(ASTORE, 1));

                LabelNode label1 = new LabelNode(new Label());
                list.add(label1);
                list.add(new LineNumberNode(12, label1));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new LdcInsnNode(name));
                list.add(new InsnNode(ICONST_0));
                list.add(new TypeInsnNode(ANEWARRAY, "java/lang/Class"));
                list.add(new MethodInsnNode(
                        INVOKEVIRTUAL,
                        "java/lang/Class",
                        "getMethod",
                        "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                        false
                ));
                list.add(new VarInsnNode(ASTORE, 2));

                LabelNode label2 = new LabelNode(new Label());
                list.add(label2);
                list.add(new LineNumberNode(13, label2));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new InsnNode(ACONST_NULL));
                list.add(new InsnNode(ICONST_0));
                list.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));
                list.add(new MethodInsnNode(
                        INVOKEVIRTUAL,
                        "java/lang/reflect/Method",
                        "invoke",
                        "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
                        false
                ));
                list.add(new InsnNode(POP));

            } else if (desc.startsWith("(") && desc.endsWith(")V")) {

            // Remove leading "(" and trailing ")V"
            String descriptor = desc.replaceAll("^\\(|\\)V$", "");


            String regex = "\\[*(?:[BCDFIJSZ]|L[^;]+;)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(descriptor);

            List<String> descriptorElements = new ArrayList<>();
            while (matcher.find()) {
                descriptorElements.add(matcher.group());
            }

            if (descriptor.equals(String.join("", descriptorElements))) {
                System.out.println(descriptorElements );
            } else {
                // unknown data type
                System.out.println(desc + " invalid!");
               return new InsnList();
            }
            MethodNode reflection = new MethodNode(ACC_PUBLIC | ACC_STATIC, nextAlphaString(5,15) , node.desc, null, null);
            reflection.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            reflection.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
            reflection.visitLabel(label0);
            reflection.visitLineNumber(54, label0);
            reflection.visitLdcInsn("Test2");
            reflection.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            reflection.visitVarInsn(ASTORE, 10);
            Label label3 = new Label();
            reflection.visitLabel(label3);
            reflection.visitLineNumber(55, label3);
            reflection.visitVarInsn(ALOAD, 10);
            reflection.visitLdcInsn("dataTypes");
            reflection.visitIntInsn(BIPUSH, 8);
            reflection.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            reflection.visitInsn(DUP);
          //  reflection.visitInsn(ICONST_0);
         /*   reflection.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_1);
            reflection.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_2);
            reflection.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_3);
            reflection.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_4);
            reflection.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_5);
            reflection.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitIntInsn(BIPUSH, 6);
            reflection.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitIntInsn(BIPUSH, 7);
            reflection.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            reflection.visitInsn(AASTORE);*/
            int i = 0;
            for (String s : descriptorElements) {
                switch (i) {
                    case 0:
                        reflection.visitInsn(ICONST_0);
                        break;
                    case 1:
                        reflection.visitInsn(ICONST_1);
                        break;
                    case 2:
                        reflection.visitInsn(ICONST_2);
                        break;
                    case 3:
                        reflection.visitInsn(ICONST_3);
                        break;
                    case 4:
                        reflection.visitInsn(ICONST_4);
                        break;
                    case 5:
                        reflection.visitInsn(ICONST_5);
                        break;
                    default:
                        reflection.visitVarInsn(BIPUSH,i);
                }

                switch (s) {
                    case "I": // int
                        reflection.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
                        break;
                    case "F": // float
                        reflection.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
                        break;
                    case "D": // double
                        reflection.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
                        break;
                    case "J": // long
                        reflection.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
                        break;
                    case "Ljava/lang/String;": // String
                        reflection.visitLdcInsn(Type.getType("Ljava/lang/String;"));
                        break;
                    case "Ljava/lang/Object;": // Object
                        reflection.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
                        break;
                    case "S": // Short
                        reflection.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
                        break;
                    case "Z": // Boolean
                        reflection.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
                        break;

                }
                reflection.visitInsn(AASTORE);
                if ((i - 1) != descriptorElements.size()) {
                    reflection.visitInsn(DUP);

                }
            }

            reflection.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            reflection.visitVarInsn(ASTORE, 11);
            Label label4 = new Label();
            reflection.visitLabel(label4);
            reflection.visitLineNumber(56, label4);
            reflection.visitLdcInsn("invoke starts here");
            reflection.visitVarInsn(ASTORE, 12);
            Label label5 = new Label();
            reflection.visitLabel(label5);
            reflection.visitLineNumber(57, label5);
            reflection.visitVarInsn(ALOAD, 11);
            reflection.visitInsn(ACONST_NULL);
            reflection.visitIntInsn(BIPUSH, 8);
            reflection.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_0);
            reflection.visitVarInsn(ILOAD, 0);
            reflection.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_1);
            reflection.visitVarInsn(FLOAD, 1);
            reflection.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_2);
            reflection.visitVarInsn(DLOAD, 2);
            reflection.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_3);
            reflection.visitVarInsn(LLOAD, 4);
            reflection.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_4);
            reflection.visitVarInsn(ALOAD, 6);
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitInsn(ICONST_5);
            reflection.visitVarInsn(ALOAD, 7);
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitIntInsn(BIPUSH, 6);
            reflection.visitVarInsn(ILOAD, 8);
            reflection.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            reflection.visitInsn(AASTORE);
            reflection.visitInsn(DUP);
            reflection.visitIntInsn(BIPUSH, 7);
            reflection.visitVarInsn(ILOAD, 9);
            reflection.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            reflection.visitInsn(AASTORE);
            reflection.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            reflection.visitInsn(POP);
            Label label6 = new Label();
            reflection.visitLabel(label6);
            reflection.visitLineNumber(58, label6);
            reflection.visitLdcInsn("invoke ends here");
            reflection.visitVarInsn(ASTORE, 12);
            reflection.visitLabel(label1);
            reflection.visitLineNumber(61, label1);
            Label label7 = new Label();
            reflection.visitJumpInsn(GOTO, label7);
            reflection.visitLabel(label2);
            reflection.visitLineNumber(59, label2);
            reflection.visitFrame(F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
            reflection.visitVarInsn(ASTORE, 10);
            reflection.visitLabel(label7);
            reflection.visitLineNumber(63, label7);
            reflection.visitFrame(F_SAME, 0, null, 0, null);
            reflection.visitInsn(RETURN);
            Label label8 = new Label();
            reflection.visitLabel(label8);
            methodNodes.add(reflection);


              /*  int operand = 0;
                boolean can = false;

                if (node.getPrevious() instanceof LdcInsnNode) {
                    LdcInsnNode n1 = (LdcInsnNode) node.getPrevious();
                    if (n1.cst instanceof Integer) {
                        operand = (int) n1.cst;
                        can = true;
                    }
                } else if (node.getPrevious() instanceof IntInsnNode) {
                    operand = ((IntInsnNode) node.getPrevious()).operand;
                    can = true;
                } else if (node.getPrevious() instanceof InsnNode) {
                    can = true;
                    switch (node.getPrevious().getOpcode()) {
                        case ICONST_0:
                            operand = 0;
                            break;
                        case ICONST_1:
                            operand = 1;
                            break;
                        case ICONST_2:
                            operand = 2;
                            break;
                        case ICONST_3:
                            operand = 3;
                            break;
                        case ICONST_4:
                            operand = 4;
                            break;
                        case ICONST_5:
                            operand = 5;
                            break;
                        default:
                            can = false;
                            break;
                    }
                }
                if (can) {
                    System.out.println("operand " + operand);
                    mn.instructions.remove(node.getPrevious());
                   *//* TO DO
                      - MULTIPLE ARGUMENETS AT ONE TIME
                      - MAKE WORKING INT REFLECTION
                    *//*
                }*/
            }


        return list;
    }
    @Override
    public void run() {
        getClasses().forEach(cn -> {

            if (!getSettings().shouldTransform(cn.name)) return;
            cn.methods.forEach(mn -> {
                if (mn.name.contains("<"))
                    return;
                Arrays.stream(mn.instructions.toArray()).forEach(insn -> {

                    if (insn instanceof MethodInsnNode) {
                        MethodInsnNode node = (MethodInsnNode) insn;
                        if (node.getOpcode() == INVOKESTATIC) {
                            //    System.out.println("Found invokestatic " + node.owner + "  |  " + node.desc + "   |   " + node.name + "  |  " + node.itf);
                               InsnList list = InvokeStaticVariant(node,mn);
                                if (list.size() != 0) {
                                    mn.instructions.insertBefore(node, list);
                                    mn.instructions.remove(node);

                                }

                        }
                    }
                });
            });
            for(MethodNode methodNode : methodNodes) {
                cn.methods.add(methodNode);
            }
        });
    }
}
