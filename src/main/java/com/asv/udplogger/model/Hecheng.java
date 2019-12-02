package com.asv.udplogger.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Hecheng {

  Integer len;
  Integer index;
  Cmd cmd;
  String id;
  List<Double> data;

  public enum Cmd {
    UPLOAD_CURRENT,
    UPLOAD_TEMP,
    UPLOAD_VOLTAGE;
  }

  public static Hecheng fromString(byte[] in) {
    String input = new String(in);
    return fromString(input);
  }

  public static Hecheng fromString(String input) {
    Hecheng hecheng = new Hecheng();
    String[] tokens = input.split(",");
    hecheng.len = Integer.parseInt(tokens[0].split(":")[1]);
    hecheng.index = Integer.parseInt(tokens[1].split(":")[1]);
    hecheng.cmd = Cmd.valueOf(tokens[2].split(":")[1]);
    hecheng.id = tokens[3].split(":")[1];
    String[] contents = tokens[4].split(":")[1].split("&");
    hecheng.data = new ArrayList<>();
    for (String content : contents) hecheng.data.add(Double.parseDouble(content));
    return hecheng;
  }

}
