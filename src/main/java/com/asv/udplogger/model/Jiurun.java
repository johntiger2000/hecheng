package com.asv.udplogger.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Setter
@Getter
public class Jiurun {

  Integer number;
  Integer type;
  String id;
  Instant timestamp;
  Integer len;
  Integer cmd;
  List<Data> data;
  Integer check;

  @Setter
  @Getter
  public static class Data {
    Integer type;
    Integer status;
    Integer unit;
    Integer len;
    Double content;
  }

  public byte[] toBytes() {

    byte[] appBytes = appBytes(data);
    len = appBytes.length;

    byte[] controlBytes = controlBytes();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write("@@".getBytes(), 0, 2);

    //控制单元
    baos.write(controlBytes, 0, controlBytes.length);

    //应用数据单元
    baos.write(appBytes, 0, appBytes.length);

    check = 0;
    for (byte b : controlBytes) check += b;
    for (byte b : appBytes) check += b;
    //校验和
    baos.write(check);

    baos.write("##".getBytes(), 0, 2);
    return baos.toByteArray();
  }

  protected byte[] controlBytes() {

    ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.of("Asia/Shanghai"));
    int year = zonedDateTime.getYear() % 100;
    int month = zonedDateTime.getMonth().getValue();
    int day = zonedDateTime.getDayOfMonth();
    int hour = zonedDateTime.getHour();
    int min = zonedDateTime.getMinute();
    int sec = zonedDateTime.getSecond();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //业务流水号
    baos.write("**".getBytes(), 0, 2);
    //设备类型
    baos.write(type);
    //设备编码
    baos.write(19);
    baos.write(("hecheng--" + StringUtils.leftPad(id, 10, " ")).getBytes(), 0, 19);
    baos.write(sec);
    baos.write(min);
    baos.write(hour);
    baos.write(day);
    baos.write(month);
    baos.write(year);

    //应用数据单元长度
    baos.write(len);
    //命令字节
    baos.write(cmd);
    return baos.toByteArray();
  }

  protected byte[] appBytes(List<Data> data) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (Data d : data) {
      d.setLen(4);
      baos.write(d.getType());
      baos.write(d.getStatus());
      baos.write(d.getUnit());
      baos.write(d.getLen());
      int decimal = (int) (d.getContent() * 10000) % 10000;
      int integer = d.getContent().intValue();
      baos.write(decimal);
      baos.write(decimal >> 8);
      baos.write(integer);
      baos.write(integer >> 8);
    }
    return baos.toByteArray();
  }

}
