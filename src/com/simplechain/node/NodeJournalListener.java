package com.simplechain.node;

// Journal listeners get the important messages from SimpleChain node for keeping e.g. append log in place
public interface NodeJournalListener {

  // Message to journal listeners
  void message(String nodeName, String message);
}
