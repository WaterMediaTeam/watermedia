package me.srrapero720.watermedia.minecraft;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import me.srrapero720.watermedia.IMediaLoader;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.util.ThreadUtil;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CpwLoader implements ITransformationService, IMediaLoader {
    public static final Marker IT = MarkerFactory.getMarker("ServiceLoader");
    public static final String NAME = "Forge";
    public static WaterMedia instance;

    public CpwLoader() {
        instance = WaterMedia.getInstance(this);
    }

    @Override
    public String name() {
        return "watermedia";
    }

    @Override
    public void initialize(IEnvironment environment) {}

    @Override
    public void beginScanning(IEnvironment environment) {}

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {
        instance.init();
        instance.crash();
        ThreadUtil.thread(() -> {
            final CpwLoader self = this;
            while (true) {
               ThreadUtil.trySimple(() -> Thread.sleep(10000));
               WaterMedia.LOGGER.warn("HEARTBEAT, self is null: {} and instannce is null: {}", self == null, this == null);
           }
        });
    }

    @Override
    public List<ITransformer> transformers() {
        return new ArrayList<>();
    }

    // WATERMEDIA STUFF
    @Override
    public ClassLoader getModuleClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Path getProcessDirectory() {
        return new File("").toPath();
    }

    @Override
    public Path getTmpDirectory() {
        return new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    }
//    private static class EventSubclassTransformerTransformer implements ITransformer<ClassNode> {
//        @Override
//        public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
//            if (input.name.equals("net/minecraftforge/eventbus/EventSubclassTransformer")) {
//                for (MethodNode method : input.methods) {
//                    if (method.name.equals("buildEvents")) {
//                        InsnList instructions = method.instructions;
//                        for (AbstractInsnNode insnNode = instructions.getFirst(); insnNode != null; insnNode = insnNode.getNext()) {
//                            if (insnNode.getOpcode() == INVOKESPECIAL &&
//                                    "java/lang/Thread".equals(((MethodInsnNode) insnNode).owner) &&
//                                    "getContextClassLoader".equals(((MethodInsnNode) insnNode).name)) {
//
//                                // Replace the getContextClassLoader call
//                                instructions.insertBefore(insnNode, new MethodInsnNode(
//                                        INVOKEVIRTUAL,
//                                        "java/lang/Thread",
//                                        "getContextClassLoader",
//                                        "()Ljava/lang/ClassLoader;",
//                                        false
//                                ));
//                                instructions.insertBefore(insnNode, new InsnNode(DUP));
//                                LabelNode label = new LabelNode();
//                                instructions.insertBefore(insnNode, new JumpInsnNode(IFNULL, label));
//                                instructions.insertBefore(insnNode, new InsnNode(POP));
//                                instructions.insertBefore(insnNode, new MethodInsnNode(
//                                        INVOKEVIRTUAL,
//                                        "java/lang/Object",
//                                        "getClass",
//                                        "()Ljava/lang/Class;",
//                                        false
//                                ));
//                                instructions.insertBefore(insnNode, new MethodInsnNode(
//                                        INVOKEVIRTUAL,
//                                        "java/lang/Class",
//                                        "getClassLoader",
//                                        "()Ljava/lang/ClassLoader;",
//                                        false
//                                ));
//                                instructions.insertBefore(insnNode, label);
//                                instructions.remove(insnNode); // Remove the original instruction
//                            }
//                        }
//                        // Add System.out.println() to verify the change
//                        instructions.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
//                        instructions.add(new LdcInsnNode("Modified buildEvents in EventSubclassTransformer"));
//                        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
//                    }
//                }
//            }
//            return input;
//        }
//
//        @Override
//        public TransformerVoteResult castVote(ITransformerVotingContext context) {
//            return TransformerVoteResult.YES;
//        }
//
//        @Override
//        public Set<Target> targets() {
//            return Collections.singleton(Target.targetClass("net.minecraftforge.eventbus.EventSubclassTransformer"));
//        }
//    }
}
