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
package at.or.reder.zcan20.stein.impl;

import at.or.reder.zcan20.stein.GAMode;
import at.or.reder.zcan20.stein.HLU;
import at.or.reder.zcan20.stein.ObjectClass;
import at.or.reder.zcan20.stein.StEinGA;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Wolfgang Reder
 */
public final class StEinGAImpl implements StEinGA
{

  private final Integer moduleNumber;
  private final String name;
  private final ObjectClass objectClass;
  private final StEinGA template;
  private final String templateName;
  private final Integer port;
  private final GAMode mode;
  private final HLU hluFix;
  private final List<HLU> hluSequenceFix;
  private final BitSet functionMaskFix;
  private final Integer positionFix;
  private final HLU gleinf;
  private final Integer detectionNormal;
  private final Integer detectionMoist;
  private final Integer detectionWet;
  private final Integer detectionMinTime;
  private final String detectionParam;
  private final Integer overcurrentLevelSlow;
  private final Integer overcurrentTimeSlow;
  private final Integer overcurrentRestartTimeSlow;
  private final Integer overcurrentRestartTrySlow;
  private final Integer overcurrentLevelFast;
  private final Integer overcurrentTimeFast;
  private final Integer overcurrentRestartTimeFast;
  private final Integer overcurrentRestartTryFast;
  private final Integer shortcutLevel;
  private final Integer shortcutTime;
  private final String ansprMX9;
  private final int outputPortModule;
  private final int outputPort;
  private final Integer prevOutputModule;
  private final Integer prevOutputPort;
  private final int input1Module;
  private final int input1;
  private final int input2Module;
  private final int input2;

  public StEinGAImpl(int moduleNumber,
                     String name,
                     ObjectClass objectClass,
                     StEinGA template,
                     String templateName,
                     Integer port,
                     GAMode mode,
                     HLU hluFix,
                     List<HLU> hluSequenceFix,
                     BitSet functionMaskFix,
                     Integer positionFix,
                     HLU gleinf,
                     Integer detectionNormal,
                     Integer detectionMoist,
                     Integer detectionWet,
                     Integer detectionMinTime,
                     String detectionParam,
                     Integer overcurrentLevelSlow,
                     Integer overcurrentTimeSlow,
                     Integer overcurrentRestartTimeSlow,
                     Integer overcurrentRestartTrySlow,
                     Integer overcurrentLevelFast,
                     Integer overcurrentTimeFast,
                     Integer overcurrentRestartTimeFast,
                     Integer overcurrentRestartTryFast,
                     Integer shortcutLevel,
                     Integer shortcutTime,
                     String ansprMX9,
                     int outputPortModule,
                     int outputPort,
                     Integer prevOutputModule,
                     Integer prevOutputPort,
                     int input1Module,
                     int input1,
                     int input2Module,
                     int input2)
  {
    this.moduleNumber = moduleNumber;
    this.name = name;
    this.objectClass = objectClass;
    this.template = template;
    this.templateName = templateName;
    this.port = port;
    this.mode = mode;
    this.hluFix = hluFix;
    this.hluSequenceFix = hluSequenceFix;
    this.functionMaskFix = (BitSet) functionMaskFix.clone();
    this.positionFix = positionFix;
    this.gleinf = gleinf;
    this.detectionNormal = detectionNormal;
    this.detectionMoist = detectionMoist;
    this.detectionWet = detectionWet;
    this.detectionMinTime = detectionMinTime;
    this.detectionParam = detectionParam;
    this.overcurrentLevelSlow = overcurrentLevelSlow;
    this.overcurrentTimeSlow = overcurrentTimeSlow;
    this.overcurrentRestartTimeSlow = overcurrentRestartTimeSlow;
    this.overcurrentRestartTrySlow = overcurrentRestartTrySlow;
    this.overcurrentLevelFast = overcurrentLevelFast;
    this.overcurrentTimeFast = overcurrentTimeFast;
    this.overcurrentRestartTimeFast = overcurrentRestartTimeFast;
    this.overcurrentRestartTryFast = overcurrentRestartTryFast;
    this.shortcutLevel = shortcutLevel;
    this.shortcutTime = shortcutTime;
    this.ansprMX9 = ansprMX9;
    this.outputPortModule = outputPortModule;
    this.outputPort = outputPort;
    this.prevOutputModule = prevOutputModule;
    this.prevOutputPort = prevOutputPort;
    this.input1Module = input1Module;
    this.input1 = input1;
    this.input2Module = input2Module;
    this.input2 = input2;
  }

  @Override
  public StEinGA getTemplate()
  {
    return template;
  }

  @Override
  public String getTemplateName()
  {
    return templateName;
  }

  @Override
  public Integer getPort()
  {
    return port;
  }

  @Override
  public GAMode getMode()
  {
    return mode;
  }

  @Override
  public HLU getHLUFix()
  {
    return hluFix;
  }

  @Override
  public List<HLU> getHLUSequenceFix()
  {
    return hluSequenceFix;
  }

  @Override
  public BitSet getFunctionMaskFix()
  {
    return (BitSet) functionMaskFix.clone();
  }

  @Override
  public Integer getPositionFix()
  {
    return positionFix;
  }

  @Override
  public HLU getGleinf()
  {
    return gleinf;
  }

  @Override
  public Integer getDetectionNormal()
  {
    return detectionNormal;
  }

  @Override
  public Integer getDetectionMoist()
  {
    return detectionMoist;
  }

  @Override
  public Integer getDetectionWet()
  {
    return detectionWet;
  }

  @Override
  public Integer getDetectionMinTime()
  {
    return detectionMinTime;
  }

  @Override
  public String getDetectionParam()
  {
    return detectionParam;
  }

  @Override
  public Integer getOvercurrentLevelSlow()
  {
    return overcurrentLevelSlow;
  }

  @Override
  public Integer getOvercurrentTimeSlow()
  {
    return overcurrentTimeSlow;
  }

  @Override
  public Integer getOvercurrentRestartTimeSlow()
  {
    return overcurrentRestartTimeSlow;
  }

  @Override
  public Integer getOvercurrentRestartTrySlow()
  {
    return overcurrentRestartTrySlow;
  }

  @Override
  public Integer getOvercurrentLevelFast()
  {
    return overcurrentLevelFast;
  }

  @Override
  public Integer getOvercurrentTimeFast()
  {
    return overcurrentTimeFast;
  }

  @Override
  public Integer getOvercurrentRestartTimeFast()
  {
    return overcurrentRestartTimeFast;
  }

  @Override
  public Integer getOvercurrentRestartTryFast()
  {
    return overcurrentRestartTryFast;
  }

  @Override
  public Integer getShortcutLevel()
  {
    return shortcutLevel;
  }

  @Override
  public Integer getShortcutTime()
  {
    return shortcutTime;
  }

  @Override
  public String getAnsprMX9()
  {
    return ansprMX9;
  }

  @Override
  public int getOutputPortModule()
  {
    return outputPortModule;
  }

  @Override
  public int getOutputPort()
  {
    return outputPort;
  }

  @Override
  public Integer getPrevOutputPortModule()
  {
    return prevOutputModule;
  }

  @Override
  public int getPrevOutputPort()
  {
    return prevOutputPort;
  }

  @Override
  public int getInput1Module()
  {
    return input1Module;
  }

  @Override
  public int getInput1()
  {
    return input1;
  }

  @Override
  public int getInput2Module()
  {
    return input2Module;
  }

  @Override
  public int getInput2()
  {
    return input2;
  }

  @Override
  public ObjectClass getObjectClass()
  {
    return objectClass;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Integer getModuleNumber()
  {
    return moduleNumber;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 19 * hash + Objects.hashCode(this.moduleNumber);
    hash = 19 * hash + Objects.hashCode(this.name);
    hash = 19 * hash + Objects.hashCode(this.objectClass);
    hash = 19 * hash + Objects.hashCode(this.templateName);
    hash = 19 * hash + Objects.hashCode(this.port);
    hash = 19 * hash + Objects.hashCode(this.mode);
    hash = 19 * hash + Objects.hashCode(this.hluFix);
    hash = 19 * hash + Objects.hashCode(this.hluSequenceFix);
    hash = 19 * hash + Objects.hashCode(this.functionMaskFix);
    hash = 19 * hash + Objects.hashCode(this.positionFix);
    hash = 19 * hash + Objects.hashCode(this.gleinf);
    hash = 19 * hash + Objects.hashCode(this.detectionNormal);
    hash = 19 * hash + Objects.hashCode(this.detectionMoist);
    hash = 19 * hash + Objects.hashCode(this.detectionWet);
    hash = 19 * hash + Objects.hashCode(this.detectionMinTime);
    hash = 19 * hash + Objects.hashCode(this.detectionParam);
    hash = 19 * hash + Objects.hashCode(this.overcurrentLevelSlow);
    hash = 19 * hash + Objects.hashCode(this.overcurrentTimeSlow);
    hash = 19 * hash + Objects.hashCode(this.overcurrentRestartTimeSlow);
    hash = 19 * hash + Objects.hashCode(this.overcurrentRestartTrySlow);
    hash = 19 * hash + Objects.hashCode(this.overcurrentLevelFast);
    hash = 19 * hash + Objects.hashCode(this.overcurrentTimeFast);
    hash = 19 * hash + Objects.hashCode(this.overcurrentRestartTimeFast);
    hash = 19 * hash + Objects.hashCode(this.overcurrentRestartTryFast);
    hash = 19 * hash + Objects.hashCode(this.shortcutLevel);
    hash = 19 * hash + Objects.hashCode(this.shortcutTime);
    hash = 19 * hash + Objects.hashCode(this.ansprMX9);
    hash = 19 * hash + this.outputPortModule;
    hash = 19 * hash + this.outputPort;
    hash = 19 * hash + Objects.hashCode(this.prevOutputModule);
    hash = 19 * hash + Objects.hashCode(this.prevOutputPort);
    hash = 19 * hash + this.input1Module;
    hash = 19 * hash + this.input1;
    hash = 19 * hash + this.input2Module;
    hash = 19 * hash + this.input2;
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
    final StEinGAImpl other = (StEinGAImpl) obj;
    if (!Objects.equals(this.moduleNumber,
                        other.moduleNumber)) {
      return false;
    }
    if (this.outputPortModule != other.outputPortModule) {
      return false;
    }
    if (this.outputPort != other.outputPort) {
      return false;
    }
    if (this.input1Module != other.input1Module) {
      return false;
    }
    if (this.input1 != other.input1) {
      return false;
    }
    if (this.input2Module != other.input2Module) {
      return false;
    }
    if (this.input2 != other.input2) {
      return false;
    }
    if (!Objects.equals(this.name,
                        other.name)) {
      return false;
    }
    if (!Objects.equals(this.templateName,
                        other.templateName)) {
      return false;
    }
    if (!Objects.equals(this.detectionParam,
                        other.detectionParam)) {
      return false;
    }
    if (!Objects.equals(this.ansprMX9,
                        other.ansprMX9)) {
      return false;
    }
    if (this.objectClass != other.objectClass) {
      return false;
    }
    if (!Objects.equals(this.port,
                        other.port)) {
      return false;
    }
    if (this.mode != other.mode) {
      return false;
    }
    if (this.hluFix != other.hluFix) {
      return false;
    }
    if (!Objects.equals(this.hluSequenceFix,
                        other.hluSequenceFix)) {
      return false;
    }
    if (!Objects.equals(this.functionMaskFix,
                        other.functionMaskFix)) {
      return false;
    }
    if (!Objects.equals(this.positionFix,
                        other.positionFix)) {
      return false;
    }
    if (this.gleinf != other.gleinf) {
      return false;
    }
    if (!Objects.equals(this.detectionNormal,
                        other.detectionNormal)) {
      return false;
    }
    if (!Objects.equals(this.detectionMoist,
                        other.detectionMoist)) {
      return false;
    }
    if (!Objects.equals(this.detectionWet,
                        other.detectionWet)) {
      return false;
    }
    if (!Objects.equals(this.detectionMinTime,
                        other.detectionMinTime)) {
      return false;
    }
    if (!Objects.equals(this.overcurrentLevelSlow,
                        other.overcurrentLevelSlow)) {
      return false;
    }
    if (!Objects.equals(this.overcurrentTimeSlow,
                        other.overcurrentTimeSlow)) {
      return false;
    }
    if (!Objects.equals(this.overcurrentRestartTimeSlow,
                        other.overcurrentRestartTimeSlow)) {
      return false;
    }
    if (!Objects.equals(this.overcurrentRestartTrySlow,
                        other.overcurrentRestartTrySlow)) {
      return false;
    }
    if (!Objects.equals(this.overcurrentLevelFast,
                        other.overcurrentLevelFast)) {
      return false;
    }
    if (!Objects.equals(this.overcurrentTimeFast,
                        other.overcurrentTimeFast)) {
      return false;
    }
    if (!Objects.equals(this.overcurrentRestartTimeFast,
                        other.overcurrentRestartTimeFast)) {
      return false;
    }
    if (!Objects.equals(this.overcurrentRestartTryFast,
                        other.overcurrentRestartTryFast)) {
      return false;
    }
    if (!Objects.equals(this.shortcutLevel,
                        other.shortcutLevel)) {
      return false;
    }
    if (!Objects.equals(this.shortcutTime,
                        other.shortcutTime)) {
      return false;
    }
    if (!Objects.equals(this.prevOutputModule,
                        other.prevOutputModule)) {
      return false;
    }
    return Objects.equals(this.prevOutputPort,
                          other.prevOutputPort);
  }

}
