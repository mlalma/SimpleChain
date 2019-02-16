package com.tests;

import com.simplechain.node.SimpleChainNode;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;

public class PingTest extends NodeTestBase {

  @Test
  public void testPing() throws IOException, InterruptedException {
    lock = new CountDownLatch(2);
    SimpleChainNode node =
        new SimpleChainNode(TEST_NODE_IP_ADDR, TEST_NODE_PORT_NUM, TEST_NODE_NAME);

    AtomicBoolean pingMessageReceived = new AtomicBoolean(false);
    bootstrap.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(BOOTSTRAP_NODE_NAME)
              && message.equals(
                  "Received ping message from "
                      + TEST_NODE_IP_ADDR.getHostAddress()
                      + ":"
                      + TEST_NODE_PORT_NUM)) {
            pingMessageReceived.set(true);
            lock.countDown();
          }
        });

    AtomicBoolean pongMessageReceived = new AtomicBoolean(false);
    node.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(TEST_NODE_NAME)
              && message.equals(
                  "Received pong message from "
                      + BOOTSTRAP_NODE_IP_ADDR.getHostAddress()
                      + ":"
                      + BOOTSTRAP_NODE_PORT_NUM)) {
            pongMessageReceived.set(true);
            lock.countDown();
          }
        });

    node.sendPing(BOOTSTRAP_NODE_IP_ADDR, BOOTSTRAP_NODE_PORT_NUM, "testNonce");
    lock.await(250, TimeUnit.MILLISECONDS);
    node.closeNode();

    Assert.assertTrue(pingMessageReceived.get());
    Assert.assertTrue(pongMessageReceived.get());
  }

  @Test
  public void testPong() throws IOException, InterruptedException {
    lock = new CountDownLatch(1);
    SimpleChainNode node =
        new SimpleChainNode(TEST_NODE_IP_ADDR, TEST_NODE_PORT_NUM, TEST_NODE_NAME);

    AtomicBoolean pongMessageReceived = new AtomicBoolean(false);
    bootstrap.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(BOOTSTRAP_NODE_NAME)
              && message.equals(
                  "Received pong message from "
                      + TEST_NODE_IP_ADDR.getHostAddress()
                      + ":"
                      + TEST_NODE_PORT_NUM)) {
            pongMessageReceived.set(true);
            lock.countDown();
          }
        });

    bootstrap.sendPing(TEST_NODE_IP_ADDR, TEST_NODE_PORT_NUM, "testNonce");
    lock.await(250, TimeUnit.MILLISECONDS);
    node.closeNode();

    Assert.assertTrue(pongMessageReceived.get());
  }
}
