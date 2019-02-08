package com.tests;

import com.simplechain.node.SimpleChainNode;
import java.io.IOException;
import java.net.InetAddress;
import org.junit.Before;
import org.junit.Test;

public class PingTest {

  private InetAddress TEST_DOORMAN_IP_ADDR;
  private final int TEST_DOORMAN_PORT_NUM = 4444;

  private SimpleChainNode doorman;

  @Before
  public void initialize() throws IOException {
    TEST_DOORMAN_IP_ADDR = InetAddress.getLocalHost();
    SimpleChainNode doorman = new SimpleChainNode(TEST_DOORMAN_IP_ADDR, TEST_DOORMAN_PORT_NUM, "Doorman");
  }

  @Test
  public void testDoorman() throws IOException, InterruptedException {
    SimpleChainNode node = new SimpleChainNode(InetAddress.getLocalHost(), 5000, "Node1");
    SimpleChainNode node2 = new SimpleChainNode(InetAddress.getLocalHost(), 5001, "Node2");
    SimpleChainNode node3 = new SimpleChainNode(InetAddress.getLocalHost(), 5002, "Node3");

    Thread.sleep(100);

    node.sendPing(TEST_DOORMAN_IP_ADDR, TEST_DOORMAN_PORT_NUM);
    node2.sendPing(TEST_DOORMAN_IP_ADDR, TEST_DOORMAN_PORT_NUM);
    node3.sendPing(TEST_DOORMAN_IP_ADDR, TEST_DOORMAN_PORT_NUM);
  }
}
