package com.asv.udplogger;

import com.asv.udplogger.model.Hecheng;
import com.asv.udplogger.model.Jiurun;

import java.util.ArrayList;
import java.util.List;

public class JiurunConverter {

  public Jiurun convert(Hecheng hecheng) {

    Jiurun jiurun = new Jiurun();
    jiurun.setId(hecheng.getId());
    jiurun.setNumber(hecheng.getIndex());
    jiurun.setType(4);
    jiurun.setCmd(4);

    List<Jiurun.Data> dataList = new ArrayList<>();
    if (Hecheng.Cmd.UPLOAD_CURRENT == hecheng.getCmd()) {
      for (Double value : hecheng.getData()) {
        Jiurun.Data data = new Jiurun.Data();
        data.setType(30);
        data.setStatus(1);
        data.setUnit(128);
        data.setContent(value);
        if (value > 800) {
          jiurun.setCmd(2);
          data.setStatus(3);
        }
        dataList.add(data);
      }
    } else if (Hecheng.Cmd.UPLOAD_TEMP == hecheng.getCmd()) {
      for (Double value : hecheng.getData()) {
        Jiurun.Data data = new Jiurun.Data();
        data.setType(23);
        data.setStatus(1);
        data.setUnit(3);
        data.setContent(value);
        if (value > 50) {
          jiurun.setCmd(2);
          data.setStatus(3);
        }
        dataList.add(data);
      }
    } else {
      {
        Jiurun.Data data = new Jiurun.Data();
        data.setType(24);
        data.setStatus(1);
        data.setUnit(8);
        data.setContent(hecheng.getData().get(0));
        dataList.add(data);
      }
      {
        Jiurun.Data data = new Jiurun.Data();
        data.setType(30);
        data.setStatus(1);
        data.setUnit(3);
        data.setContent(hecheng.getData().get(1));
        dataList.add(data);
      }
    }
    jiurun.setData(dataList);

    return jiurun;
  }
}
