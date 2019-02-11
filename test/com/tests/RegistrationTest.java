import com.simplechain.node.SimpleChainNode;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegistrationTest {

  private InetAddress BOOTSTRAP_NODE_IP_ADDR;
  private final int BOOTSTRAP_NODE_PORT_NUM = 4444;
  private final String BOOTSTRAP_NODE_NAME = "Bootstrap";

  private InetAddress TEST_NODE_IP_ADDR;
  private final int TEST_NODE_PORT_NUM = 5000;
  private final String TEST_NODE_NAME = "Node1";

  private CountDownLatch lock;
  private SimpleChainNode bootstrap;

  @Before
  public void initialize() throws IOException {
    System.setProperty(
        "java.util.logging.SimpleFormatter.format", "[%1$tc] %4$s: %2$s - %5$s %6$s%n");
    BOOTSTRAP_NODE_IP_ADDR = InetAddress.getLocalHost();
    TEST_NODE_IP_ADDR = InetAddress.getLocalHost();
    bootstrap = new SimpleChainNode(BOOTSTRAP_NODE_IP_ADDR, BOOTSTRAP_NODE_PORT_NUM,
        BOOTSTRAP_NODE_NAME);
  }

  @After
  public void deInitialize() throws IOException {
    bootstrap.closeNode();
  }

  @Test
  public void testSendRegistration() throws Exception {
    lock = new CountDownLatch(1);

    SimpleChainNode node =
        new SimpleChainNode(TEST_NODE_IP_ADDR, TEST_NODE_PORT_NUM, TEST_NODE_NAME);

    AtomicBoolean regMessageReceived = new AtomicBoolean(false);
    bootstrap.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(BOOTSTRAP_NODE_NAME)
              && message.equals(
              "Received registration message from "
                  + TEST_NODE_IP_ADDR.getHostAddress()
                  + ":"
                  + TEST_NODE_PORT_NUM)) {
            regMessageReceived.set(true);
            lock.countDown();
          }
        });

    node.sendRegistration(BOOTSTRAP_NODE_IP_ADDR, BOOTSTRAP_NODE_PORT_NUM);

    lock.await(250, TimeUnit.MILLISECONDS);
    node.closeNode();

    Assert.assertTrue(regMessageReceived.get());
  }

  @Test
  public void testSendRegistrationACKBack() throws Exception {
    lock = new CountDownLatch(2);

    SimpleChainNode node =
        new SimpleChainNode(TEST_NODE_IP_ADDR, TEST_NODE_PORT_NUM, TEST_NODE_NAME);

    AtomicBoolean regMessageReceived = new AtomicBoolean(false);
    bootstrap.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(BOOTSTRAP_NODE_NAME)
              && message.equals(
              "Received registration message from "
                  + TEST_NODE_IP_ADDR.getHostAddress()
                  + ":"
                  + TEST_NODE_PORT_NUM)) {
            regMessageReceived.set(true);
            lock.countDown();
          }
        });
    node.sendRegistration(BOOTSTRAP_NODE_IP_ADDR, BOOTSTRAP_NODE_PORT_NUM);

    AtomicBoolean regMessageACKReceived = new AtomicBoolean(false);
    node.addJournalListener(
        (nodeName, message) -> {
          if (nodeName.equals(TEST_NODE_NAME)
              && message.equals(
              "Received registration ack message from "
                  + BOOTSTRAP_NODE_IP_ADDR.getHostAddress()
                  + ":"
                  + BOOTSTRAP_NODE_PORT_NUM)) {
            regMessageACKReceived.set(true);
            lock.countDown();
          }
        });

    lock.await(250, TimeUnit.MILLISECONDS);
    node.closeNode();

    Assert.assertTrue(regMessageReceived.get());
    Assert.assertTrue(regMessageACKReceived.get());
  }
}
