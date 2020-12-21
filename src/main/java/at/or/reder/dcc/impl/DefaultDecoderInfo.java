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
package at.or.reder.dcc.impl;

import at.or.reder.dcc.DecoderClass;
import at.or.reder.dcc.DecoderInfo;
import at.or.reder.dcc.SpeedstepSystem;
import java.util.Objects;
import org.openide.util.Lookup;

public class DefaultDecoderInfo implements DecoderInfo
{

  private final String manufacturerName;
  private final int address;
  private final int consistsAddress;
  private final SpeedstepSystem speedSteps;
  private final DecoderClass decoderType;
  private final String decoderName;
  private final String swVersion;
  private final String serial;
  private final String soundCode;
  private final Lookup lookup;

  public DefaultDecoderInfo(String manufacturerName,
                            int address,
                            int consistsAddress,
                            DecoderClass decoderType,
                            String decoderName,
                            String swVersion,
                            String serial,
                            String soundCode,
                            SpeedstepSystem speedSteps)
  {
    this(manufacturerName,
         address,
         consistsAddress,
         decoderType,
         decoderName,
         swVersion,
         serial,
         soundCode,
         speedSteps,
         null);
  }

  public DefaultDecoderInfo(String manufacturerName,
                            int address,
                            int consistsAddress,
                            DecoderClass decoderType,
                            String decoderName,
                            String swVersion,
                            String serial,
                            String soundCode,
                            SpeedstepSystem speedSteps,
                            Lookup lookup)
  {
    this.manufacturerName = manufacturerName;
    this.address = address;
    this.consistsAddress = consistsAddress;
    this.speedSteps = speedSteps;
    this.decoderType = decoderType;
    this.decoderName = decoderName;
    this.swVersion = swVersion;
    this.serial = serial;
    this.soundCode = soundCode;
    this.lookup = lookup != null ? lookup : Lookup.EMPTY;
  }

  @Override
  public String getManufacturerName()
  {
    return manufacturerName;
  }

  @Override
  public int getAddress()
  {
    return address;
  }

  @Override
  public int getConsistsAddress()
  {
    return consistsAddress;
  }

  @Override
  public SpeedstepSystem getSpeedSteps()
  {
    return speedSteps;
  }

  @Override
  public DecoderClass getDecoderType()
  {
    return decoderType;
  }

  @Override
  public String getDecoderName()
  {
    return decoderName;
  }

  @Override
  public String getSWVersion()
  {
    return swVersion;
  }

  @Override
  public String getSerial()
  {
    return serial;
  }

  @Override
  public String getSoundcode()
  {
    return soundCode;
  }

  @Override
  public Lookup getLookup()
  {
    return lookup;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 71 * hash + Objects.hashCode(this.manufacturerName);
    hash = 71 * hash + this.address;
    hash = 71 * hash + this.consistsAddress;
    hash = 71 * hash + Objects.hashCode(this.speedSteps);
    hash = 71 * hash + Objects.hashCode(this.decoderType);
    hash = 71 * hash + Objects.hashCode(this.decoderName);
    hash = 71 * hash + Objects.hashCode(this.swVersion);
    hash = 71 * hash + Objects.hashCode(this.serial);
    hash = 71 * hash + Objects.hashCode(this.soundCode);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DefaultDecoderInfo other = (DefaultDecoderInfo) obj;
    if (this.address != other.address) {
      return false;
    }
    if (this.consistsAddress != other.consistsAddress) {
      return false;
    }
    if (this.speedSteps != other.speedSteps) {
      return false;
    }
    if (!Objects.equals(this.manufacturerName,
                        other.manufacturerName)) {
      return false;
    }
    if (!Objects.equals(this.decoderName,
                        other.decoderName)) {
      return false;
    }
    if (!Objects.equals(this.swVersion,
                        other.swVersion)) {
      return false;
    }
    if (!Objects.equals(this.serial,
                        other.serial)) {
      return false;
    }
    if (!Objects.equals(this.soundCode,
                        other.soundCode)) {
      return false;
    }
    return this.decoderType == other.decoderType;
  }

  @Override
  public String toString()
  {
    return "DefaultDecoderInfo{" + "manufacturerName=" + manufacturerName + ", address=" + address + ", consistsAddress=" + consistsAddress + ", speedSteps=" + speedSteps + ", decoderType=" + decoderType + ", decoderName=" + decoderName + ", swVersion=" + swVersion + ", serial=" + serial + ", soundCode=" + soundCode + ", lookup=" + lookup + '}';
  }

}
