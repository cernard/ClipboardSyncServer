package com.cernard.clipboard.syncserver.server

import java.net.{ServerSocket, Socket}
import java.util.concurrent.ConcurrentHashMap

import com.cernard.clipboard.syncserver.configuration.Configuration
import org.apache.log4j.Logger

object Server {
  private val logger = Logger.getLogger(getClass)
  private val clintList = new ConcurrentHashMap[String, Socket]()

  def start(config: Configuration): Unit = {
    val serverSocket = new ServerSocket(config.port)
    logger.info("Start server in port " + config.port)

    while (true) {
      logger.info("Waiting for connection")
      val socket = serverSocket.accept()

      val processor = new Processor(socket, clintList)
      processor.start()

      println(clintList.toString)
    }

    serverSocket.close()
  }
}
