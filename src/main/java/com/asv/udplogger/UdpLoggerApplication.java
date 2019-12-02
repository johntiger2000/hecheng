package com.asv.udplogger;

import com.asv.udplogger.model.Hecheng;
import com.asv.udplogger.model.Jiurun;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
public class UdpLoggerApplication implements CommandLineRunner {

  protected static String forwardHost = "106.14.46.105";
  protected static int forwardPort = 10066;

  public static void main(String[] args) {
    SpringApplication.run(UdpLoggerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    DatagramSocket receiveSocket = new DatagramSocket(17086);
    Socket forwardSocket = new Socket(forwardHost, forwardPort);

    while (true) {

      byte[] buf = new byte[1024];
      DatagramPacket dp = new DatagramPacket(buf, buf.length);
      receiveSocket.receive(dp);

      try {
        String data = new String(dp.getData(), 0, dp.getLength());
        log.info("received data: {}", data);

        Hecheng hecheng = Hecheng.fromString(data);

        Jiurun jiurun = new JiurunConverter().convert(hecheng);
        byte[] bytes = jiurun.toBytes();
        log.info("converted data to: {}", Hex.encodeHexString(bytes));

        try {
          forwardSocket.getOutputStream().write(bytes);
        } catch (Exception e) {
          forwardSocket = new Socket(forwardHost, forwardPort);
          forwardSocket.getOutputStream().write(bytes);
        }
        int len = forwardSocket.getInputStream().read(buf);
        byte[] ret = Arrays.copyOf(buf, len);
        if (len > 9 && ret[9] == 5) {
          log.info("forwarded data. response: {}", Hex.encodeHexString(ret));
        } else {
          log.warn("forwarded data error. response: {}", Hex.encodeHexString(ret));
        }

      } catch (Exception e) {
        log.error("forward one message error", e);
      }
    }

    //forwardSocket.close();
  }
}
