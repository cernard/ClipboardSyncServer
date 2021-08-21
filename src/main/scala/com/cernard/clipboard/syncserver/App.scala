package com.cernard.clipboard.syncserver

import com.cernard.clipboard.syncserver.configuration.Configuration
import com.cernard.clipboard.syncserver.server.Server

object App {
  def main(args: Array[String]): Unit = {
    val configuration = Configuration.loadFromProperties()

    Server.start(configuration)
  }
}
