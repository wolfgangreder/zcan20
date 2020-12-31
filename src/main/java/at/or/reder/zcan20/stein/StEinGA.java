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

import java.util.BitSet;
import java.util.List;

public interface StEinGA extends StEinObject
{

  public StEinGA getTemplate();

  public String getTemplateName();

  public Integer getPort();

  public GAMode getMode();

  public HLU getHLUFix();

  public List<HLU> getHLUSequenceFix();

  public BitSet getFunctionMaskFix();

  public Integer getPositionFix();

  public HLU getGleinf();

  public Integer getDetectionNormal();

  public Integer getDetectionMoist();

  public Integer getDetectionWet();

  public Integer getDetectionMinTime();

  public String getDetectionParam();

  public Integer getOvercurrentLevelSlow();

  public Integer getOvercurrentTimeSlow();

  public Integer getOvercurrentRestartTimeSlow();

  public Integer getOvercurrentRestartTrySlow();

  public Integer getOvercurrentLevelFast();

  public Integer getOvercurrentTimeFast();

  public Integer getOvercurrentRestartTimeFast();

  public Integer getOvercurrentRestartTryFast();

  public Integer getShortcutLevel();

  public Integer getShortcutTime();

  public String getAnsprMX9();

  public int getOutputPortModule();

  public int getOutputPort();

  public Integer getPrevOutputPortModule();

  public int getPrevOutputPort();

  public int getInput1Module();

  public int getInput1();

  public int getInput2Module();

  public int getInput2();

}
