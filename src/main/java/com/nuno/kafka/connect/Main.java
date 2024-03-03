package com.nuno.kafka.connect;

import org.apache.kafka.connect.cli.ConnectDistributed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Supplier;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger();
    static final String CONNECT_ENV_PREFIX = "CONNECT_";

    static Supplier<Map<String, String>> envSupplier = System::getenv;

    public static void main(final String[] args) {
        LOGGER.info("Starting Kafka Connect wrapper");
        try {
            ConnectDistributed.main(new String[]{createConnectProperties(envSupplier.get()).getAbsolutePath()});
        } catch (Exception e) {
            LOGGER.error("Error starting wrapper {}", ConnectDistributed.class.getSimpleName(), e);
            System.exit(1);
        }
    }

    static File createConnectProperties(Map<String, String> env) throws IOException {
        final File workerPropFile =
                File.createTempFile("tmp-connect-distributed", ".properties");
        workerPropFile.deleteOnExit();

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(workerPropFile))) {
            LOGGER.trace("Writing Kafka Connect worker properties to '{}'.", workerPropFile.getAbsolutePath());
            env.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey()
                            .startsWith(CONNECT_ENV_PREFIX))
                    .map(e -> new AbstractMap.SimpleEntry<>(connectEnvVarToProp(e.getKey()), e.getValue()))
                    .forEach(e -> {
                        final String k = e.getKey();
                        final String v = e.getValue();
                        LOGGER.info("Property defined: {}={}", k, v);
                        pw.printf("%s=%s%n", k, v);
                    });
            LOGGER.trace("Kafka Connect worker properties written.");
        }
        return workerPropFile;
    }

    private static String connectEnvVarToProp(String k) {
        return k.toLowerCase()
                .substring(CONNECT_ENV_PREFIX.length())
                .replace('_', '.');
    }
}
