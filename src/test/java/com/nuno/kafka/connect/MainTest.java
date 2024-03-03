package com.nuno.kafka.connect;

import org.apache.kafka.connect.cli.ConnectDistributed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MainTest {
    private static final String CONNECT_VAR = "CONNECT_VAR";
    private static final String VAR = "var";
    private static final String OTHER_VAR = "OTHER_VAR";
    private static final String VALUE = "VALUE";

    private final Map<String, String> env = new HashMap<>();
    private Supplier<Map<String, String>> oldEnvSupplier;

    @BeforeEach
    void setUpEnvSupplier() {
        oldEnvSupplier = Main.envSupplier;
        Main.envSupplier = () -> env;
    }

    @AfterEach
    void restoreEnvSupplier() {
        Main.envSupplier = oldEnvSupplier;
    }

    @Test
    void mainCallsConnectWithConvertedEnvironmentVariables() throws IOException {
        env.put(CONNECT_VAR, VALUE);
        env.put(OTHER_VAR, VALUE);

        ArgumentCaptor<String[]> argumentCaptor = ArgumentCaptor.forClass(String[].class);
        try (MockedStatic<ConnectDistributed> connectDistributed = Mockito.mockStatic(ConnectDistributed.class)) {
            Main.main(new String[0]);

            connectDistributed.verify(() -> ConnectDistributed.main(argumentCaptor.capture()));
        }

        String[] args = argumentCaptor.getValue();
        assertEquals(1, args.length);
        Properties props = readPropsFile(args[0]);

        assertNull(props.getProperty(OTHER_VAR));
        assertEquals(VALUE, props.getProperty(VAR));
    }

    private static Properties readPropsFile(String propsFile) throws IOException {
        Properties props = new Properties();
        try (FileReader propsReader = new FileReader(propsFile)) {
            props.load(propsReader);
        }
        return props;
    }
}