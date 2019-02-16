package com.tests;

import com.simplechain.node.SimpleChainNode;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import org.junit.After;
import org.junit.Before;

public class NodeTestBase {

  InetAddress BOOTSTRAP_NODE_IP_ADDR;
  final int BOOTSTRAP_NODE_PORT_NUM = 4444;
  final String BOOTSTRAP_NODE_NAME = "Bootstrap";

  InetAddress TEST_NODE_IP_ADDR;
  final int TEST_NODE_PORT_NUM = 5000;
  final String TEST_NODE_NAME = "Node1";

  CountDownLatch lock;
  SimpleChainNode bootstrap;

  @Before
  public void initialize() throws IOException {
    System.setProperty(
        "java.util.logging.SimpleFormatter.format", "[%1$tc] %4$s: %2$s - %5$s %6$s%n");
    BOOTSTRAP_NODE_IP_ADDR = InetAddress.getLocalHost();
    TEST_NODE_IP_ADDR = InetAddress.getLocalHost();
    bootstrap =
        new SimpleChainNode(BOOTSTRAP_NODE_IP_ADDR, BOOTSTRAP_NODE_PORT_NUM, BOOTSTRAP_NODE_NAME);
  }

  @After
  public void deInitialize() throws IOException {
    bootstrap.closeNode();
  }
}
