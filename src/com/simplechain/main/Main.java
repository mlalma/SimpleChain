package com.simplechain.main;

import com.simplechain.node.SimpleChainNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private final InetAddress TEST_DOORMAN_IP_ADDR = InetAddress.getLocalHost();
    private final int TEST_DOORMAN_PORT_NUM = 4444;

    private final SimpleChainNode doorman;

    private Main() throws IOException {
        doorman = new SimpleChainNode(TEST_DOORMAN_IP_ADDR, TEST_DOORMAN_PORT_NUM, "Doorman");
    }

    private void doMain() throws Exception {
        SimpleChainNode node = new SimpleChainNode(InetAddress.getLocalHost(), 5000, "Node1");
        SimpleChainNode node2 = new SimpleChainNode(InetAddress.getLocalHost(), 5001, "Node2");
        SimpleChainNode node3 = new SimpleChainNode(InetAddress.getLocalHost(), 5002, "Node3");

        Thread.sleep(500);

        node.sendPing(TEST_DOORMAN_IP_ADDR, TEST_DOORMAN_PORT_NUM);
        node2.sendPing(TEST_DOORMAN_IP_ADDR, TEST_DOORMAN_PORT_NUM);
        node3.sendPing(TEST_DOORMAN_IP_ADDR, TEST_DOORMAN_PORT_NUM);
    }

    // Entry point to the application
    public static void main(String args[]) {
        try {
            logger.info("Launching SimpleChain..");
            Main main = new Main();
            main.doMain();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Set up logging formatter
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tc] %4$s: %2$s - %5$s %6$s%n");
    }
}
