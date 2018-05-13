package com.thecodewarrior.nodenet.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

// Boilerplate code taken with love from Vazkii's Quark mod and JamiesWhiteShirt's Clothesline
// Quark is distributed at https://github.com/Vazkii/Quark
// Clothesline is distributed at https://github.com/JamiesWhiteShirt/clothesline

@SuppressWarnings({"Duplicates", "WeakerAccess", "unused"})
public class NodeNetTransformer implements IClassTransformer, Opcodes {

    private static final String ASM_HOOKS = "com/thecodewarrior/nodenet/asm/NodeNetAsmHooks";
    private static final Map<String, Transformer> transformers = new HashMap<>();

    static {
        transformers.put("net.minecraft.client.Minecraft", NodeNetTransformer::transformMinecraft);
        transformers.put("net.minecraft.client.renderer.EntityRenderer", NodeNetTransformer::transformEntityRenderer);
    }

    private static byte[] transformMinecraft(byte[] basicClass) {
        MethodSignature sig1 = new MethodSignature("processKeyBinds", "func_184117_aA", "()V");

        MethodSignature branchStart = new MethodSignature("isHandActive", "func_184587_cr", "()Z");
        MethodSignature branchEnd = new MethodSignature("middleClickMouse", "func_147112_ai", "()V");

        return transform(basicClass, sig1, "Process keybinds hook", (MethodNode method) -> {
            AbstractInsnNode[] nodes = method.instructions.toArray();

            Iterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

//            Label label = new Label();
            LabelNode labelNode = null;// = new LabelNode(label);

            // find the first call to `Minecraft.middleClickMouse`, which is the last of the loops in the else branch
            // once we find that we search for the next label, which will be at the end of the else branch. We will use
            // this label later.
            boolean added = false;
            boolean foundMarker = false;
            while (iterator.hasNext()) {
                AbstractInsnNode anode = iterator.next();
                if (foundMarker) {
                    if (anode instanceof LabelNode) {
                        labelNode = (LabelNode)anode;
                        added = true;
                        break;
                    }
                } else {
                    if((anode.getOpcode() == INVOKESPECIAL || anode.getOpcode() == INVOKEVIRTUAL) &&
                            branchEnd.matches((MethodInsnNode) anode)) {
                        foundMarker = true;
                    }
                }
            }
            if(!added) return false;

            iterator = new InsnArrayIterator(nodes);

            // find the first call to `isHandActive`, which is used in the main if/else surrounding the mouse code
            // once we find it search up until we find the instruction loading `this`. that instruction is the first
            // step in the statement `this.player.isHandActive()` and is the start of the part we want to skip.
            int pos = 0;
            while (iterator.hasNext()) {
                pos++;
                AbstractInsnNode anode = iterator.next();
                if((anode.getOpcode() == INVOKESPECIAL || anode.getOpcode() == INVOKEVIRTUAL) &&
                        branchStart.matches((MethodInsnNode) anode)) {

                    ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                    while (internal.hasPrevious()) {
                        AbstractInsnNode anode2 = internal.previous();
                        if (anode2.getOpcode() == ALOAD) {

                            InsnList newInstructions = new InsnList();

                            // Once we have found the beginning of the block we want to skip and have the label at the
                            // end we can finally add our check. We call our own `processKeyBinds`, and if it returns
                            // true we will jump past the block.
                            newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "processKeyBinds",
                                    "()Z", false));
                            newInstructions.add(new JumpInsnNode(IFNE, labelNode));

                            method.instructions.insertBefore(anode2, newInstructions);
                            return true;
                        }
                    }
                }
            }

            return false;
        });
    }

    private static byte[] transformEntityRenderer(byte[] basicClass) {
        MethodSignature sig1 = new MethodSignature("getMouseOver", "func_78473_a", "(F)V");

        return transform(basicClass, sig1, "Get mouseover hook", (MethodNode method) -> {
            AbstractInsnNode[] nodes = method.instructions.toArray();

            InsnList newInstructions = new InsnList();
            newInstructions.add(new InsnNode(FCONST_0));
            newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "getMouseOver", "(F)V", false ));
            method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);

            Iterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

            int count = 0;
            while (iterator.hasNext()) {
                AbstractInsnNode anode = iterator.next();
                if(anode.getOpcode() == PUTFIELD) {
                    FieldInsnNode node = (FieldInsnNode)anode;
                    if(node.owner.equals("net/minecraft/client/Minecraft") &&
                            (node.name.equals("objectMouseOver") || node.name.equals("field_71476_x"))) {
                        count++;

                        // instructions starting with > are inserted

                        // get the instance of Minecraft and put it on the stack
                        // generate a value to put in objectMouseOver and put it on the stack
                        // > check if setting the value should be skipped                                            (1)
                        // > if it should continue, jump to PRE                                                      (2)
                        // > pop the value that was destined to go in objectMouseOver off the stack                  (3)
                        // > pop the instance of Minecraft off the stack                                             (3)
                        // > jump to POST                                                                            (4)
                        // > PRE label                                                                               (5)
                        // store that value in `mc.objectMouseOver`
                        // > POST label                                                                              (6)

                        Label preLabel = new Label();
                        LabelNode preLabelNode = new LabelNode(preLabel);

                        Label postLabel = new Label();
                        LabelNode postLabelNode = new LabelNode(postLabel);

                        newInstructions = new InsnList();

                        // check if we should skip setting the value, and put the result on the stack
                        /* (1) */ newInstructions.add(new VarInsnNode(FLOAD, 1));
                        /* (1) */ newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS,
                                "shouldSkipSettingObjectMouseOver", "(F)Z", false));

                        // If we should skip then continue on and remove the unused stuff from the stack
                        // otherwise, jump to the instruction where the field is set using the stuff from the stack
                        // (jump if value == 0, 0 is false in the JVM, false means we should go and set the field)
                        /* (2) */ newInstructions.add(new JumpInsnNode(IFEQ, preLabelNode));

                        // pop the top two values from the stack
                        /* (3) */ newInstructions.add(new InsnNode(POP2));

                        // jump to the point right after the field would be set
                        /* (4) */ newInstructions.add(new JumpInsnNode(GOTO, postLabelNode));

                        // the point we will jump to if we don't want to skip
                        /* (5) */ newInstructions.add(preLabelNode);

                        method.instructions.insertBefore(anode, newInstructions);

                        // the point we will jump to if we do want to skip
                        /* (6) */ method.instructions.insert(anode, postLabelNode);
                    }
                }
            }

            return count > 0;
        });
    }

    // BOILERPLATE =====================================================================================================

    public static byte[] transform(byte[] basicClass, MethodSignature sig, String simpleDesc, MethodAction action) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        log("Applying Transformation to method (" + sig + ")");
        log("Attempting to insert: " + simpleDesc);
        boolean didAnything = findMethodAndTransform(node, sig, action);

        if (didAnything) {
            ClassWriter writer = new SafeClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            byte[] transformed = writer.toByteArray();
            verify(transformed);
            return transformed;
        }

        return basicClass;
    }

    public static void verify(byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        ClassVisitor tracer = new TraceClassVisitor(new PrintWriter(System.out));
        ClassVisitor checker = new CheckClassAdapter(tracer, true);
        reader.accept(checker, 0);
    }

    public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction pred) {
        for (MethodNode method : node.methods) {
            if (sig.matches(method)) {

                boolean finish = pred.test(method);
                log("Patch result: " + (finish ? "Success" : "!!!!!!! Failure !!!!!!!"));

                return finish;
            }
        }

        log("Patch result: !!!!!!! Couldn't locate method! !!!!!!!");

        return false;
    }

    public static MethodAction combine(NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNode(node, filter, action);
    }

    public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        Iterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

        boolean didAny = false;
        while (iterator.hasNext()) {
            AbstractInsnNode anode = iterator.next();
            if (filter.test(anode)) {
                didAny = true;
                if (action.test(method, anode))
                    break;
            }
        }

        return didAny;
    }

    public static MethodAction combineByLast(NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeByLast(node, filter, action);
    }

    public static boolean applyOnNodeByLast(MethodNode method, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

        boolean didAny = false;
        while (iterator.hasPrevious()) {
            AbstractInsnNode anode = iterator.previous();
            if (filter.test(anode)) {
                didAny = true;
                if (action.test(method, anode))
                    break;
            }
        }

        return didAny;
    }

    public static MethodAction combineFrontPivot(NodeFilter pivot, NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeFrontPivot(node, pivot, filter, action);
    }

    public static boolean applyOnNodeFrontPivot(MethodNode method, NodeFilter pivot, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

        int pos = 0;

        boolean didAny = false;
        while (iterator.hasNext()) {
            pos++;
            AbstractInsnNode pivotTest = iterator.next();
            if (pivot.test(pivotTest)) {
                ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                while (internal.hasPrevious()) {
                    AbstractInsnNode anode = internal.previous();
                    if (filter.test(anode)) {
                        didAny = true;
                        if (action.test(method, anode))
                            break;
                    }
                }
            }
        }

        return didAny;
    }

    public static MethodAction combineBackPivot(NodeFilter pivot, NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeBackPivot(node, pivot, filter, action);
    }

    public static boolean applyOnNodeBackPivot(MethodNode method, NodeFilter pivot, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

        int pos = method.instructions.size();

        boolean didAny = false;
        while (iterator.hasPrevious()) {
            pos--;
            AbstractInsnNode pivotTest = iterator.previous();
            if (pivot.test(pivotTest)) {
                ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                while (internal.hasNext()) {
                    AbstractInsnNode anode = internal.next();
                    if (filter.test(anode)) {
                        didAny = true;
                        if (action.test(method, anode))
                            break;
                    }
                }
            }
        }

        return didAny;
    }

    public static MethodAction combineFrontFocus(NodeFilter focus, NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeFrontFocus(node, focus, filter, action);
    }

    public static boolean applyOnNodeFrontFocus(MethodNode method, NodeFilter focus, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

        int pos = method.instructions.size();

        boolean didAny = false;
        while (iterator.hasNext()) {
            pos++;
            AbstractInsnNode focusTest = iterator.next();
            if (focus.test(focusTest)) {
                ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                while (internal.hasNext()) {
                    AbstractInsnNode anode = internal.next();
                    if (filter.test(anode)) {
                        didAny = true;
                        if (action.test(method, anode))
                            break;
                    }
                }
            }
        }

        return didAny;
    }

    public static MethodAction combineBackFocus(NodeFilter focus, NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeBackFocus(node, focus, filter, action);
    }

    public static boolean applyOnNodeBackFocus(MethodNode method, NodeFilter focus, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

        int pos = method.instructions.size();

        boolean didAny = false;
        while (iterator.hasPrevious()) {
            pos--;
            AbstractInsnNode focusTest = iterator.previous();
            if (focus.test(focusTest)) {
                ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                while (internal.hasPrevious()) {
                    AbstractInsnNode anode = internal.previous();
                    if (filter.test(anode)) {
                        didAny = true;
                        if (action.test(method, anode))
                            break;
                    }
                }
            }
        }

        return didAny;
    }

    public static void log(String str) {
        LogManager.getLogger("LibrarianLib ASM").info(str);
    }

    public static void prettyPrint(MethodNode node) {
        Printer printer = new Textifier();

        TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
        node.accept(visitor);

        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();

        log(sw.toString());
    }

    public static void prettyPrint(AbstractInsnNode node) {
        Printer printer = new Textifier();

        TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
        node.accept(visitor);

        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();

        log(sw.toString());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformers.containsKey(transformedName)) {
            String[] arr = transformedName.split("\\.");
            log("Transforming " + arr[arr.length - 1]);
            return transformers.get(transformedName).apply(basicClass);
        }

        return basicClass;
    }

    public interface Transformer extends Function<byte[], byte[]> {
        // NO-OP
    }

    public interface MethodAction extends Predicate<MethodNode> {
        // NO-OP
    }

    // Basic interface aliases to not have to clutter up the code with generics over and over again

    public interface NodeFilter extends Predicate<AbstractInsnNode> {
        // NO-OP
    }

    public interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
        // NO-OP
    }

    private static class InsnArrayIterator implements ListIterator<AbstractInsnNode> {

        private final AbstractInsnNode[] array;
        private int index;

        public InsnArrayIterator(AbstractInsnNode[] array) {
            this(array, 0);
        }

        public InsnArrayIterator(AbstractInsnNode[] array, int index) {
            this.array = array;
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return array.length > index + 1 && index >= 0;
        }

        @Override
        public AbstractInsnNode next() {
            if (hasNext())
                return array[++index];
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0 && index <= array.length;
        }

        @Override
        public AbstractInsnNode previous() {
            if (hasPrevious())
                return array[--index];
            return null;
        }

        @Override
        public int nextIndex() {
            return hasNext() ? index + 1 : array.length;
        }

        @Override
        public int previousIndex() {
            return hasPrevious() ? index - 1 : 0;
        }

        @Override
        public void remove() {
            throw new Error("Unimplemented");
        }

        @Override
        public void set(AbstractInsnNode e) {
            throw new Error("Unimplemented");
        }

        @Override
        public void add(AbstractInsnNode e) {
            throw new Error("Unimplemented");
        }
    }

    public static class MethodSignature {
        private final String funcName, srgName, funcDesc;

        public MethodSignature(String funcName, String srgName, String funcDesc) {
            this.funcName = funcName;
            this.srgName = srgName;
            this.funcDesc = funcDesc;
        }

        @Override
        public String toString() {
            return "Names [" + funcName + ", " + srgName + "] Descriptor " + funcDesc;
        }

        public boolean matches(String methodName, String methodDesc) {
            return (methodName.equals(funcName) || methodName.equals(srgName))
                    && (methodDesc.equals(funcDesc));
        }

        public boolean matches(MethodNode method) {
            return matches(method.name, method.desc);
        }

        public boolean matches(MethodInsnNode method) {
            return matches(method.name, method.desc);
        }

    }

    /**
     * Safe class writer.
     * The way COMPUTE_FRAMES works may require loading additional classes. This can cause ClassCircularityErrors.
     * The override for getCommonSuperClass will ensure that COMPUTE_FRAMES works properly by using the right ClassLoader.
     *
     * Code from: https://github.com/JamiesWhiteShirt/clothesline/blob/master/src/core/java/com/jamieswhiteshirt/clothesline/core/SafeClassWriter.java
     */
    public static class SafeClassWriter extends ClassWriter {
        public SafeClassWriter(int flags) {
            super(flags);
        }

        public SafeClassWriter(ClassReader classReader, int flags) {
            super(classReader, flags);
        }

        @Override
        protected String getCommonSuperClass(String type1, String type2) {
            Class<?> c, d;
            ClassLoader classLoader = Launch.classLoader;
            try {
                c = Class.forName(type1.replace('/', '.'), false, classLoader);
                d = Class.forName(type2.replace('/', '.'), false, classLoader);
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
            if (c.isAssignableFrom(d)) {
                return type1;
            }
            if (d.isAssignableFrom(c)) {
                return type2;
            }
            if (c.isInterface() || d.isInterface()) {
                return "java/lang/Object";
            } else {
                do {
                    c = c.getSuperclass();
                } while (!c.isAssignableFrom(d));
                return c.getName().replace('.', '/');
            }
        }
    }
}
