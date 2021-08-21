package com.cernard.clipboard.syncserver.server

import java.net.{Socket, SocketException}
import java.util.concurrent.ConcurrentHashMap

import org.apache.log4j.Logger

class Processor(client: Socket, clientList:  ConcurrentHashMap[String, Socket]) extends Thread{
  private val remoteAddress = client.getRemoteSocketAddress.toString
  private val logger = Logger.getLogger("Processor for " + remoteAddress)

  override def run(): Unit = {
    logger.info("Process connection from " + remoteAddress)
    clientList.put(remoteAddress, client)

    while (!client.isClosed && client.isConnected) {
      val inputStream = client.getInputStream
      val bytes:Array[Byte] = new Array[Byte](1024)
      var len: Int = 0;
      val sb = new StringBuilder;

      while (inputStream.available() > 0 && (len = inputStream.read(bytes)) != -1) {
        sb.append(new String(bytes, 0, len, "UTF-8"));
      }
      if (sb.nonEmpty) {
        logger.info("Get message from " + remoteAddress + ": " + sb)

        sendDataToAll(sb.toString().getBytes())
      }

      // Sleep 500ms
      Thread.sleep(500)

//      checkHeatBeat()
    }

    clientList.remove(remoteAddress)
  }

  // 向客户端发送心跳检测
  def checkHeatBeat(): Boolean = {
    val outputStream = client.getOutputStream
    try {
      outputStream.write(toSystemMessage("heart_beat").getBytes)
      outputStream.flush()
    } catch {
      case ex: SocketException => {
        logger.info("Client " + remoteAddress +" was closed! ")
        client.close()
      }
    }
    true
  }

  def toSystemMessage(str: String): String = {
    s"""[SYSTEM]$str"""
  }

  // 将消息转发给所有客户端，如果发送的时候错误就将客户端移除
  def sendDataToAll(byte: Array[Byte]): Unit = {
    clientList.forEach((clientIp, clientSocket) => {
      if (clientIp != remoteAddress) {
        try {
          val outputStream = clientSocket.getOutputStream
          outputStream.write(byte)
          outputStream.flush()
        } catch {
              // 可能多线程扫描不及时，导致从ip列表找到的socket有已关闭的
          case ex: SocketException => {
            clientList.remove(clientIp)
          }
        }
      }
    })
  }
}
