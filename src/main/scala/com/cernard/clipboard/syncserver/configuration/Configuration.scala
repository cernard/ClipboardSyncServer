package com.cernard.clipboard.syncserver.configuration

import java.io.FileInputStream
import java.util.Properties

class Configuration {
  private[this] var _port: Int = 0

  def port: Int = _port

  def port_=(value: Int): Unit = {
    _port = value
  }
}
object Configuration {
  private val DEFAULT_PORT = "8888";

  def loadFromProperties(): Configuration = {
    val path = Thread.currentThread().getContextClassLoader.getResource("app.properties").getPath
    val properties = new Properties()

    properties.load(new FileInputStream(path))

    val port = properties.getProperty("port", DEFAULT_PORT);

    val configuration = new Configuration
    configuration.port = port.toInt

    configuration
  }
}
