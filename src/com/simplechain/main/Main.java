package com.simplechain.main;

import com.simplechain.node.SimpleChainNode;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
  private static Logger logger = LoggerFactory.getLogger(Main.class);


  private void doMain() throws Exception {

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


  public class Builder {

    private final InetAddress address;
    private final int port;

    public Builder(String host, int port) throws UnknownHostException {
      this.address = InetAddress.getByName(host);
      this.port = port;
    }
  }

  // Set up logging formatter
  static {
    System.setProperty(
        "java.util.logging.SimpleFormatter.format", "[%1$tc] %4$s: %2$s - %5$s %6$s%n");
  }
}
