package org.watermedia.app;

import org.watermedia.WaterMedia;
import org.watermedia.api.image.ImageAPI;
import org.watermedia.api.image.ImageCache;
import org.watermedia.api.image.ImageRenderer;
import org.watermedia.api.player.videolan.VideoPlayer;
import org.watermedia.loaders.ILoader;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.net.URI;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TestOpenGLApp implements Executor {
    private final Queue<Runnable> executor = new LinkedList<>();
    private ImageCache cache;
    private ImageRenderer renderer;
    private VideoPlayer player;

    // The window handle
    private long window;

    // The media loader
    private static final String NAME = "WATERMeDIA: Multimedia API";

    public void run(URI url) {
        try {
            WaterMedia.prepare(ILoader.DEFAULT).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load WATERMeDIA", e);
        }

        // enable slavism
        WaterMedia.setSlavismMode(true);

        renderer = ImageAPI.loadingGif();
        cache = ImageAPI.getCache(url, this);
        cache.load();

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).close();
        System.exit(0);
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(1280, 720, NAME, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (key == GLFW_KEY_P) {
                player.start(URI.create("https://www.youtube.com/watch?v=LlNCDSz5BeE"));
            }
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        ARBDebugOutput.glDebugMessageCallbackARB((source, type, id, severity, length, message, userParam) -> {
            System.out.println(MemoryUtil.memASCII(message));
        }, 0);

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if (cache.getStatus() == ImageCache.Status.READY) {
              if (player == null && this.cache.isVideo()) {
                  player = new VideoPlayer(this);
                  player.start(cache.uri);
              } else {
                  renderer = cache.getRenderer();
              }
              if (player != null && player.isSafeUse() && player.isEnded()) {
                  glfwSetWindowShouldClose(window, true);
              }
            } else if (cache.getStatus() == ImageCache.Status.FAILED) {
                throw new RuntimeException("Cannot load specified media");
            }

            if (cache.isVideo()) {
                player.preRender();
                glBindTexture(GL_TEXTURE_2D, player.texture());
            } else if (renderer.duration == 0) {
                glBindTexture(GL_TEXTURE_2D, renderer.texture(0));
            } else {
                glBindTexture(GL_TEXTURE_2D, renderer.texture(System.currentTimeMillis() % renderer.duration));
            }


            glColor4f(1, 1, 1, 1);

            glBegin(GL_QUADS); {
                glTexCoord2f(0, 1); glVertex2f(-1, -1);
                glTexCoord2f(0, 0); glVertex2f(-1, 1);
                glTexCoord2f(1, 0); glVertex2f(1, 1);
                glTexCoord2f(1, 1); glVertex2f(1, -1);
            }
            glEnd();

            if (player != null && (!player.isSafeUse() || player.isBuffering() || player.isLoading() || player.isPaused())) {
                glBindTexture(GL_TEXTURE_2D, ImageAPI.loadingGif().texture(System.currentTimeMillis() % renderer.duration));
                glBegin(GL_QUADS); {
                    glTexCoord2f(0, 1); glVertex2f(-1, -1);
                    glTexCoord2f(0, 0); glVertex2f(-1, 1);
                    glTexCoord2f(1, 0); glVertex2f(1, 1);
                    glTexCoord2f(1, 1); glVertex2f(1, -1);
                }
                glEnd();
            }

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            if (!executor.isEmpty()) executor.remove().run();
        }
    }

    public static void main(String[] args) {
        String url = args.length == 0 ? "https://www.mediafire.com/file/f23l3csbeeap9jo/TU_QLO.mp4/file" : args[0];
        new TestOpenGLApp().run(URI.create(url));
    }

    @Override
    public void execute(Runnable command)
    {
        executor.add(command);
    }
}
