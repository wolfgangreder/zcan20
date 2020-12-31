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
package at.or.reder.zcan20.stein;

import at.or.reder.zcan20.stein.impl.StEinGAImpl;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 *
 * @author Wolfgang Reder
 */
public final class StEinGABuilder
{

  private Integer moduleNumber;
  private String name;
  private ObjectClass objectClass;
  private StEinGA template;
  private String templateName;
  private Integer port;
  private GAMode mode;
  private HLU hluFix;
  private final List<HLU> hluSequenceFix = new ArrayList<>(2);
  private BitSet functionMaskFix;
  private Integer positionFix;
  private HLU gleinf;
  private Integer detectionNormal;
  private Integer detectionMoist;
  private Integer detectionWet;
  private Integer detectionMinTime;
  private String detectionParam;
  private Integer overcurrentLevelSlow;
  private Integer overcurrentTimeSlow;
  private Integer overcurrentRestartTimeSlow;
  private Integer overcurrentRestartTrySlow;
  private Integer overcurrentLevelFast;
  private Integer overcurrentTimeFast;
  private Integer overcurrentRestartTimeFast;
  private Integer overcurrentRestartTryFast;
  private Integer shortcutLevel;
  private Integer shortcutTime;
  private String ansprMX9;
  private int outputPortModule;
  private int outputPort;
  private Integer prevOutputModule;
  private Integer prevOutputPort;
  private int input1Module;
  private int input1;
  private int input2Module;
  private int input2;

  StEinGABuilder copy(StEinGA ga)
  {
    this.moduleNumber = ga.getModuleNumber();
    this.name = ga.getName();
    this.objectClass = ga.getObjectClass();
    this.template = ga.getTemplate();
    this.templateName = ga.getTemplateName();
    this.port = ga.getPort();
    this.mode = ga.getMode();
    this.hluFix = ga.getHLUFix();
    this.hluSequenceFix.clear();
    this.hluSequenceFix.addAll(ga.getHLUSequenceFix());
    this.functionMaskFix = ga.getFunctionMaskFix();
    this.positionFix = ga.getPositionFix();
    this.gleinf = ga.getGleinf();
    this.detectionNormal = ga.getDetectionNormal();
    this.detectionMoist = ga.getDetectionMoist();
    this.detectionWet = ga.getDetectionWet();
    this.detectionMinTime = ga.getDetectionMinTime();
    this.detectionParam = ga.getDetectionParam();
    this.overcurrentLevelSlow = ga.getOvercurrentLevelSlow();
    this.overcurrentTimeSlow = ga.getOvercurrentTimeSlow();
    this.overcurrentRestartTimeSlow = ga.getOvercurrentRestartTimeSlow();
    this.overcurrentRestartTrySlow = ga.getOvercurrentRestartTrySlow();
    this.overcurrentLevelFast = ga.getOvercurrentLevelFast();
    this.overcurrentTimeFast = ga.getOvercurrentTimeFast();
    this.overcurrentRestartTimeFast = ga.getOvercurrentRestartTimeFast();
    this.overcurrentRestartTryFast = ga.getOvercurrentRestartTryFast();
    this.shortcutLevel = ga.getShortcutLevel();
    this.shortcutTime = ga.getShortcutTime();
    this.ansprMX9 = ga.getAnsprMX9();
    this.outputPortModule = ga.getOutputPortModule();
    this.outputPort = ga.getOutputPort();
    this.prevOutputModule = ga.getPrevOutputPortModule();
    this.prevOutputPort = ga.getPrevOutputPort();
    this.input1Module = ga.getInput1Module();
    this.input1 = ga.getInput1();
    this.input2Module = ga.getInput2Module();
    this.input2 = ga.getInput2();
    return this;
  }

  public StEinGABuilder template(StEinGA template)
  {
    this.template = template;
    return this;
  }

  public StEinGABuilder templateName(String templateName)
  {
    this.templateName = templateName;
    return this;
  }

  public StEinGABuilder port(Integer port)
  {
    this.port = port;
    return this;
  }

  public StEinGABuilder mode(GAMode mode)
  {
    this.mode = mode;
    return this;
  }

  public StEinGABuilder hLUFix(HLU hlu)
  {
    this.hluFix = hlu;
    return this;
  }

  public StEinGABuilder hLUSequenceFix(List<HLU> seq)
  {
    this.hluSequenceFix.clear();
    this.hluSequenceFix.addAll(seq);
    return this;
  }

  public StEinGABuilder functionMaskFix(BitSet functions)
  {
    this.functionMaskFix = (BitSet) functions.clone();
    return this;
  }

  public StEinGABuilder positionFix(Integer posFix)
  {
    this.positionFix = posFix;
    return this;
  }

  public StEinGABuilder gleinf(HLU gleinf)
  {
    this.gleinf = gleinf;
    return this;
  }

  public StEinGABuilder detectionNormal(Integer detNormal)
  {
    this.detectionNormal = detNormal;
    return this;
  }

  public StEinGABuilder detectionMoist(Integer detMoist)
  {
    this.detectionMoist = detMoist;
    return this;
  }

  public StEinGABuilder detectionWet(Integer detWet)
  {
    this.detectionWet = detWet;
    return this;
  }

  public StEinGABuilder detectionMinTime(Integer detMin)
  {
    this.detectionMinTime = detMin;
    return this;
  }

  public StEinGABuilder detectionParam(String param)
  {
    this.detectionParam = param;
    return this;
  }

  public StEinGABuilder overcurrentLevelSlow(Integer current)
  {
    this.overcurrentLevelSlow = current;
    return this;
  }

  public StEinGABuilder overcurrentTimeSlow(Integer time)
  {
    this.overcurrentTimeSlow = time;
    return this;
  }

  public StEinGABuilder overcurrentRestartTimeSlow(Integer time)
  {
    this.overcurrentRestartTimeSlow = time;
    return this;
  }

  public StEinGABuilder overrcurrentRestartTrySlow(Integer retry)
  {
    this.overcurrentRestartTrySlow = retry;
    return this;
  }

  public StEinGABuilder overcurrentLevelFast(Integer current)
  {
    this.overcurrentLevelFast = current;
    return this;
  }

  public StEinGABuilder overcurrentTimeFast(Integer time)
  {
    this.overcurrentTimeFast = time;
    return this;
  }

  public StEinGABuilder overcurrentRestartTimeFast(Integer time)
  {
    this.overcurrentRestartTimeFast = time;
    return this;
  }

  public StEinGABuilder overrcurrentRestartTryFast(Integer retry)
  {
    this.overcurrentRestartTryFast = retry;
    return this;
  }

  public StEinGABuilder shortcutLevel(Integer current)
  {
    this.shortcutLevel = current;
    return this;
  }

  public StEinGABuilder shortcutTime(Integer time)
  {
    this.shortcutTime = time;
    return this;
  }

  public StEinGABuilder ansprMX9(String anspr)
  {
    this.ansprMX9 = anspr;
    return this;
  }

  public StEinGABuilder outputPortModule(int module)
  {
    this.outputPortModule = module;
    return this;
  }

  public StEinGABuilder outputPort(int port)
  {
    this.outputPort = port;
    return this;
  }

  public StEinGABuilder prevOutputPortModule(Integer portModule)
  {
    this.prevOutputModule = portModule;
    return this;
  }

  public StEinGABuilder prevOutputPort(Integer port)
  {
    this.prevOutputPort = port;
    return this;
  }

  public StEinGABuilder input1Module(int module)
  {
    this.input1Module = module;
    return this;
  }

  public StEinGABuilder input1(int input)
  {
    this.input1 = input;
    return this;
  }

  public StEinGABuilder input2Module(int module)
  {
    this.input2Module = module;
    return this;
  }

  public StEinGABuilder input2(int input)
  {
    this.input2 = input;
    return this;
  }

  public StEinGABuilder objectClass(ObjectClass oc)
  {
    this.objectClass = oc;
    return this;
  }

  public StEinGABuilder name(String name)
  {
    this.name = name;
    return this;
  }

  public StEinGABuilder moduleNumber(int number)
  {
    this.moduleNumber = number;
    return this;
  }

  public StEinGA build()
  {
    return new StEinGAImpl(moduleNumber,
                           name,
                           objectClass,
                           template,
                           templateName,
                           port,
                           mode,
                           hluFix,
                           hluSequenceFix,
                           functionMaskFix,
                           positionFix,
                           gleinf,
                           detectionNormal,
                           detectionMoist,
                           detectionWet,
                           detectionMinTime,
                           detectionParam,
                           overcurrentLevelSlow,
                           overcurrentTimeSlow,
                           overcurrentRestartTimeSlow,
                           overcurrentRestartTrySlow,
                           overcurrentLevelFast,
                           overcurrentTimeFast,
                           overcurrentRestartTimeFast,
                           overcurrentRestartTryFast,
                           shortcutLevel,
                           shortcutTime,
                           ansprMX9,
                           outputPortModule,
                           outputPort,
                           prevOutputModule,
                           prevOutputPort,
                           input1Module,
                           input1,
                           input2Module,
                           input2);
  }

}
