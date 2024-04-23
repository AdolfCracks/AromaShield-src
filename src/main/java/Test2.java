import sun.misc.Unsafe;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  Test2 {
    public static void main(String[] args) {
        short s = 69;
        dataTypes(2137,1.5F,1.5,10L,"xd",new Test(),s,true);
    }

    public static void callAutoReflection() {
        short s = 69;
        autoReflection(2137,1.5F,1.5,10L,"xd",new Test(),s,true);
    }

   public static void main2() throws Exception {

         long time = System.currentTimeMillis();
        Class<?> clazz = Class.forName("Test2");
        Method method = clazz.getMethod("dataTypes",int.class,float.class,double.class,long.class,String.class,Object.class,short.class,boolean.class);
       short s = 69;
        method.invoke(null,2137,1.5F,1.588888888766566,Long.MAX_VALUE,"xd",new Test(),s,true);
        System.out.println(System.currentTimeMillis() - time);

    }

    public void print() {
        System.out.println("penis");
    }
    public void print2() {
        PrintStream ps = new PrintStream(System.out);
        ps.println("penis");
    }
    public void TestFlow() {
        Object x;
        if (System.currentTimeMillis() < 1) {
            x = new byte[10];
        } else {
            x = new Test2();
        }
     //  x.
    }

    public static void autoReflection(int a,float b,double c,long l,String d,Object o,short sh,boolean bool) {
       try {
           Class<?> clazz = Class.forName("Test2");
           Method method = clazz.getMethod("dataTypes", int.class, float.class, double.class, long.class, String.class, Object.class, short.class, boolean.class);
         String s = "invoke starts here";
           method.invoke(null, a,b,c,l,d,o,sh,bool,"pener");
           s = "invoke ends here";
       } catch (Exception e) {

       }

    }
    public static void dataTypes(int a,float b,double c,long l,String d,Object o,short sh,boolean bool) {

    }
    public void arrayDataTypes(int[] a,float[] b,double[] c,long[] l,String[] d,Object[] o,short[] sh,boolean[] booleans) {

    }
}
