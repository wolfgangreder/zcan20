/*
 * Copyright 2020 Wolfgang Reder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.or.reder.mx1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Wolfgang Reder
 */
public enum MX1Command
{
  RESET(0,
        0),
  NACK(1,
       0),
  TRACK_CONTROL(2,
                1),
  LOCO_CONTROL(3,
               6),
  INVERT_FUNCTION(4,
                  5),
  ACCEL(5,
        3),
  SHUTTLE_TRAIN(6,
                3),
  ACCESSORY(7,
            3),
  QUERY_CS_LOCO(8,
                2),
  QUERY_CS_DECODER(9,
                   2),
  ADDRESS_CONTROL(10,
                  4),
  READ_CS_IO(11,
             1),
  RW_CS_CV(12,
           3),
  CS_EQ_QUERY(13,
              1),
  SERIAL_INFO(17,
              2),
  RW_DECODER_CV(19,
                5),
  CURRENT_LOCO_MEM(255,
                   8),
  CURRENT_DECODER_MEM(254,
                      4);
  private final int cmd;

  private final int payload;

  private MX1Command(int cmd,
                     int payload)
  {
    this.cmd = cmd;
    this.payload = payload;
  }

  private static final Map<Integer, MX1Command> commandMap = new ConcurrentHashMap<>();

  public static MX1Command getCommand(byte cmdByte)
  {
    int tmp = cmdByte & 0xff;
    return commandMap.computeIfAbsent(tmp,
                                      (c) -> {
                                        for (MX1Command cand : MX1Command.values()) {
                                          if (cand.cmd == c) {
                                            return cand;
                                          }
                                        }
                                        return null;
                                      });
  }

  public int getCmd()
  {
    return cmd;
  }

  public int getPayload()
  {
    return payload;
  }

}
