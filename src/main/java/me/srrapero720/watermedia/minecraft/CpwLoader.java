package me.srrapero720.watermedia.minecraft;

import cpw.mods.modlauncher.api.*;
import me.srrapero720.watermedia.IMediaLoader;
import me.srrapero720.watermedia.WaterMedia;
import org.objectweb.asm.tree.*;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class CpwLoader implements ITransformationService, IMediaLoader {
    public static final Marker IT = MarkerFactory.getMarker("cpw-TransformationServices");

    @Nonnull
    @Override
    public String name() {
        return "a-watermedia";
    }

    @Override
    public void initialize(IEnvironment environment) {
        WaterMedia.LOGGER.info(IT, "Starting...");
    }

    @Override
    public void beginScanning(IEnvironment environment) {
        WaterMedia.LOGGER.info(IT, "beginscanned...");
    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
        WaterMedia.LOGGER.info(IT, "OnLoad...");
    }

    @Nonnull
    @Override
    public List<ITransformer> transformers() {
        List<ITransformer> transformers = new ArrayList<>();
        transformers.add(new EventSubclassTransformerTransformer());
        return transformers;
    }

    // WATERMEDIA STUFF

    @Override
    public boolean isDev() {
        return false;
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public boolean isInstalled(String modId) {
        return false;
    }

    @Override
    public ClassLoader getJarClassLoader() {
        return null;
    }

    @Override
    public String getLoaderName() {
        return null;
    }

    @Override
    public Path getWorkingDir() {
        return null;
    }

    @Override
    public Path getTempDir() {
        return null;
    }

    @Override
    public boolean isTLauncher() {
        return false;
    }

    private static class EventSubclassTransformerTransformer implements ITransformer<ClassNode> {
        @Override
        public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
            if (input.name.equals("net/minecraftforge/eventbus/EventSubclassTransformer")) {
                for (MethodNode method : input.methods) {
                    if (method.name.equals("buildEvents")) {
                        InsnList instructions = method.instructions;
                        for (AbstractInsnNode insnNode = instructions.getFirst(); insnNode != null; insnNode = insnNode.getNext()) {
                            if (insnNode.getOpcode() == INVOKESPECIAL &&
                                    "java/lang/Thread".equals(((MethodInsnNode) insnNode).owner) &&
                                    "getContextClassLoader".equals(((MethodInsnNode) insnNode).name)) {

                                // Replace the getContextClassLoader call
                                instructions.insertBefore(insnNode, new MethodInsnNode(
                                        INVOKEVIRTUAL,
                                        "java/lang/Thread",
                                        "getContextClassLoader",
                                        "()Ljava/lang/ClassLoader;",
                                        false
                                ));
                                instructions.insertBefore(insnNode, new InsnNode(DUP));
                                LabelNode label = new LabelNode();
                                instructions.insertBefore(insnNode, new JumpInsnNode(IFNULL, label));
                                instructions.insertBefore(insnNode, new InsnNode(POP));
                                instructions.insertBefore(insnNode, new MethodInsnNode(
                                        INVOKEVIRTUAL,
                                        "java/lang/Object",
                                        "getClass",
                                        "()Ljava/lang/Class;",
                                        false
                                ));
                                instructions.insertBefore(insnNode, new MethodInsnNode(
                                        INVOKEVIRTUAL,
                                        "java/lang/Class",
                                        "getClassLoader",
                                        "()Ljava/lang/ClassLoader;",
                                        false
                                ));
                                instructions.insertBefore(insnNode, label);
                                instructions.remove(insnNode); // Remove the original instruction
                            }
                        }
                        // Add System.out.println() to verify the change
                        instructions.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                        instructions.add(new LdcInsnNode("Modified buildEvents in EventSubclassTransformer"));
                        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                    }
                }
            }
            return input;
        }

        @Override
        public TransformerVoteResult castVote(ITransformerVotingContext context) {
            return TransformerVoteResult.YES;
        }

        @Override
        public Set<Target> targets() {
            return Collections.singleton(Target.targetClass("net.minecraftforge.eventbus.EventSubclassTransformer"));
        }
    }
}
