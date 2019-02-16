package com.tests;

import com.simplechain.node.SimpleChainNode;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;

public class NodeDiscoveryTest extends NodeTestBase {

  @Test
  public void testPlainNodeDiscoveryRequest() throws Exception {
    lock = new CountDownLatch(1);

    SimpleChainNode node =
        new SimpleChainNode(TEST_NODE_IP_ADDR, TEST_NODE_PORT_NUM, TEST_NODE_NAME);

    AtomicBoolean nodeDiscoveryRequestMsgReceived = new AtomicBoolean(false);
    bootstrap.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(BOOTSTRAP_NODE_NAME)
              && message.equals(
                  "Received node discovery request from "
                      + TEST_NODE_IP_ADDR.getHostAddress()
                      + ":"
                      + TEST_NODE_PORT_NUM)) {
            nodeDiscoveryRequestMsgReceived.set(true);
            lock.countDown();
          }
        });

    node.sendNodeDiscoveryQuery(BOOTSTRAP_NODE_IP_ADDR, BOOTSTRAP_NODE_PORT_NUM);

    lock.await(250, TimeUnit.MILLISECONDS);
    node.closeNode();

    Assert.assertTrue(nodeDiscoveryRequestMsgReceived.get());
  }

  @Test
  public void testEmptyNodeDiscoveryResponseRequest() throws Exception {
    lock = new CountDownLatch(2);

    SimpleChainNode node =
        new SimpleChainNode(TEST_NODE_IP_ADDR, TEST_NODE_PORT_NUM, TEST_NODE_NAME);

    AtomicBoolean nodeDiscoveryRequestMsgReceived = new AtomicBoolean(false);
    bootstrap.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(BOOTSTRAP_NODE_NAME)
              && message.equals(
                  "Received node discovery request from "
                      + TEST_NODE_IP_ADDR.getHostAddress()
                      + ":"
                      + TEST_NODE_PORT_NUM)) {
            nodeDiscoveryRequestMsgReceived.set(true);
            lock.countDown();
          }
        });

    AtomicBoolean nodeDiscoveryResponseMsgReceived = new AtomicBoolean(false);
    node.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(TEST_NODE_NAME)
              && message.equals(
                  "Received node discovery reply from "
                      + TEST_NODE_IP_ADDR.getHostAddress()
                      + ":"
                      + TEST_NODE_PORT_NUM)) {
            nodeDiscoveryResponseMsgReceived.set(true);
            lock.countDown();
          }
        });

    node.sendNodeDiscoveryQuery(BOOTSTRAP_NODE_IP_ADDR, BOOTSTRAP_NODE_PORT_NUM);

    lock.await(250, TimeUnit.MILLISECONDS);
    node.closeNode();

    Assert.assertTrue(nodeDiscoveryRequestMsgReceived.get());
  }

  // TODO: Add test to get a list of nodes back - also test for maxcount
}
