package net.minecraftforge.fml.common;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    String modid();
    String value();
    String acceptableRemoteVersions();

    boolean clientSideOnly() default false;
    boolean serverSideOnly() default false;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface EventHandler{}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface EventBusSubscriber {
        Dist[] value() default {Dist.CLIENT, Dist.DEDICATED_SERVER};

        String modid() default "";

        Bus bus() default Mod.EventBusSubscriber.Bus.FORGE;

        public static enum Bus {
            FORGE(null),
            MOD(null);

            private final Supplier<IEventBus> busSupplier;

            private Bus(Supplier eventBusSupplier) {
                throw new UnsupportedOperationException("stub!");
            }

            public Supplier<IEventBus> bus() {
                throw new UnsupportedOperationException("stub!");
            }
        }
    }
}