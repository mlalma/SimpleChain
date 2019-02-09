package com.simplechain.main;

import static com.google.common.base.Preconditions.checkNotNull;

import com.simplechain.node.SimpleChainNode;
import java.net.UnknownHostException;
import java.util.UUID;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
  private static Logger logger = LoggerFactory.getLogger(Main.class);

  private final InetAddress address;
  private final int port;
  private final String name;

  private Main(final InetAddress address, final int port, final String name) {
    this.address = address;
    this.port = port;
    this.name = name;
  }

  private void start() throws IOException {
    SimpleChainNode node = new SimpleChainNode(address, port, name);
  }

  private static Main createInstanceFromCommandLineArguments(String args[]) {
    Options options = new Options();
    options
        .addOption("h", "host", true, "Hostname to bind this server")
        .addOption("p", "port", true, "Port to use for this server")
        .addOption("n", "nodeName", true, "Name for the node");

    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);

      String hostName = null;
      int port = -1;
      String name = null;
      if (cmd.hasOption("h") && cmd.hasOption("p")) {
        hostName = cmd.getOptionValue("h");
        port = Integer.valueOf(cmd.getOptionValue("p"));
      } else {
        throw new IllegalArgumentException("Invalid command line arguments");
      }

      name = cmd.getOptionValue("n");

      return new Main.Builder(hostName, port).setName(name).build();
    } catch (Exception ex) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("SimpleChain", options);
      return null;
    }
  }

  // Entry point to the application
  public static void main(String args[]) {
    try {
      logger.info("Parsing command line arguments..");
      Main main = createInstanceFromCommandLineArguments(args);

      logger.info("Launching SimpleChain..");
      if (main != null) {
        main.start();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static class Builder {

    private final InetAddress address;
    private final int port;
    private String name = UUID.randomUUID().toString();

    public Builder(String host, int port) throws UnknownHostException {
      checkNotNull(host);
      this.address = InetAddress.getByName(host);
      this.port = port;
    }

    public Builder setName(String name) {
      if (name != null && name.length() > 0) {
        this.name = name;
      }
      return this;
    }

    public Main build() {
      return new Main(address, port, name);
    }
  }

  // Set up logging formatter
  static {
    System.setProperty(
        "java.util.logging.SimpleFormatter.format", "[%1$tc] %4$s: %2$s - %5$s %6$s%n");
  }
}
