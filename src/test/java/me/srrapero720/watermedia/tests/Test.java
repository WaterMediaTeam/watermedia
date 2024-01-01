package me.srrapero720.watermedia.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ServiceLoader;

public abstract class Test {
    protected static final Logger LOGGER = LogManager.getLogger("watermedia");
    protected static final Marker IT = MarkerManager.getMarker("Test");

    protected abstract void prepare() throws Exception;
    protected abstract void run() throws Exception;
    protected abstract void release() throws Exception;

    public static void main(String[] args) {
        ServiceLoader<Test> services = ServiceLoader.load(Test.class);

        for (Test test: services) {
            try {
                LOGGER.info(IT, "Preparing test for {}", test.getClass().getSimpleName());
                test.prepare();

                LOGGER.info(IT, "Executing testing for {}", test.getClass().getSimpleName());
                test.run();

                LOGGER.info(IT, "Cleaning test resources for {}", test.getClass().getSimpleName());
                test.release();
            } catch (Exception e) {
                LOGGER.fatal(IT, "Exception occurred running test for {}", test.getClass().getSimpleName(), e);
                System.exit(-1);
            }
        }

        LOGGER.info(IT, "Test successfully, good work!");
        System.exit(0);
    }
}