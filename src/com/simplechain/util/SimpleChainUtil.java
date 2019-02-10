package com.simplechain.util;

import com.simplechain.node.SimpleChainNode;
import java.util.TimerTask;

// Utility methods
public final class SimpleChainUtil {

  // Wrap timer task to runnable
  public static TimerTask wrap(Runnable r) {
    return new TimerTask() {
      @Override
      public void run() {
        r.run();
      }
    };
  }

  // Private constructor
  private SimpleChainUtil() {
  }
}
