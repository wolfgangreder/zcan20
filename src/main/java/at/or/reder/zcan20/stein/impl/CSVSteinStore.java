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

import at.or.reder.zcan20.stein.ConfigFormat;
import at.or.reder.zcan20.stein.ObjectClass;
import at.or.reder.zcan20.stein.StEin;
import at.or.reder.zcan20.stein.StEinConfigStoreProvider;
import at.or.reder.zcan20.stein.StEinGA;
import at.or.reder.zcan20.stein.StEinObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = StEinConfigStoreProvider.class)
@Messages({
  "# {0} - sep",
  "CSVSteinStore_GA_Captions=NAME{0}MODULNR{0}OBJKL{0}GATYP{0}GASYSNR{0}BEFORM{0}HLUFIX{0}PUFFIX{0}FUNFIX{0}POSFIX{0}GLEINF{0}BESMNOR{0}BESMFEU{0}BESMNAS{0}GKMINZT{0}GKPARAM{0}UESLAMP{0}UESLAZT{0}UESLEZT{0}UESLEAZ{0}UESSAMP{0}UESSAZT{0}UESSEZT{0}UESSEAZ{0}KUSAMP{0}KUSEZT{0}ANSPRMX9{0}APUGA{0}APUGAV{0}APUGK1{0}APUGK2"})
public class CSVSteinStore implements StEinConfigStoreProvider
{

  public static final String KEY_NEWLINE = "CSVSteinStore.newline";
  public static final String KEY_CHARSET = "CSVSteinStore.charset";
  public static final String KEY_COLUMN_SEPARATOR = "CSVSteinStore.columnSeparator";
  public static final Charset CP1252 = Charset.forName("CP1252");
  public static final String NL = "\r\n";
  public static final String SEP = ";";
  public static final UUID ID = UUID.fromString("04ba621d-6bf0-4ca1-8740-24e935505525");

  @Override
  public UUID getId()
  {
    return ID;
  }

  @Override
  public Set<Features> getSupportedFeatures()
  {
    return EnumSet.of(Features.LOAD,
                      Features.STORE);
  }

  @Override
  public StEin load(Object is,
                    Map<String, Object> params) throws IOException
  {
    if (is == null) {
      throw new NullPointerException("input is null");
    }
    if (is instanceof LineNumberReader) {
      return load((LineNumberReader) is);
    }
    Charset charset = CP1252;
    String sep = SEP;
    if (params != null) {
      Object tmp = params.get(KEY_CHARSET);
      if (tmp != null) {
        charset = Charset.forName(tmp.toString());
      }
      tmp = params.get(KEY_COLUMN_SEPARATOR);
      if (tmp != null && !tmp.toString().isBlank()) {
        sep = tmp.toString();
      }
    }
    LineNumberReader reader = null;
    if (is instanceof InputStream) {
      reader = new LineNumberReader(new InputStreamReader((InputStream) is,
                                                          charset));
    } else if (is instanceof ReadableByteChannel) {
      reader = new LineNumberReader(Channels.newReader((ReadableByteChannel) is,
                                                       charset));
    } else if (is instanceof Reader) {
      reader = new LineNumberReader((Reader) is);
    } else {
      throw new IllegalArgumentException("invalid input source");
    }
    return load(reader,
                sep);
  }

  public StEin load(LineNumberReader reader,
                    String sep) throws IOException
  {
    String line;
    Map<Integer, List<StEinObject>> objectList = new HashMap<>();
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split(sep);
      if (parts.length > 3) {
        String strClazz = parts[3];
        ObjectClass objectClass = ObjectClass.valueOfMagic(strClazz);

        if (objectClass != null) {
          switch (objectClass) {
            case GA:
            case GATYP:

              break;
            default:

          }

        }
      }
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public void store(StEin stein,
                    Object os,
                    Map<String, Object> params) throws IOException
  {
    if (os == null) {
      throw new NullPointerException("output is null");
    }
    if (stein == null) {
      throw new NullPointerException("stein is null");
    }
    String nl = NL;
    String sep = SEP;
    boolean addCaptions = false;
    if (params != null) {
      Object tmp = params.getOrDefault(KEY_NEWLINE,
                                       NL);
      nl = tmp != null ? tmp.toString() : NL;
      tmp = params.getOrDefault(KEY_WRITECAPTIONS,
                                "false");
      addCaptions = Boolean.parseBoolean(tmp != null ? tmp.toString() : "false");
      tmp = params.getOrDefault(KEY_COLUMN_SEPARATOR,
                                SEP);
      if (tmp != null && !tmp.toString().isBlank()) {
        sep = tmp.toString();
      }
    }
    if (os instanceof Writer) {
      store(stein,
            (Writer) os,
            nl,
            addCaptions,
            sep);
    } else {
      Charset ch = CP1252;
      if (params != null) {
        Object tmp = params.getOrDefault(KEY_CHARSET,
                                         CP1252);
        if (tmp instanceof Charset) {
          ch = (Charset) tmp;
        } else if (tmp != null) {
          ch = Charset.forName(tmp.toString());
        }
      }
      if (os instanceof OutputStream) {
        try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream) os,
                                                                ch)) {
          store(stein,
                writer,
                nl,
                addCaptions,
                sep);
        }
      } else if (os instanceof WritableByteChannel) {
        try (Writer writer = Channels.newWriter((WritableByteChannel) os,
                                                ch)) {
          store(stein,
                writer,
                nl,
                addCaptions,
                sep);
        }
      }
      throw new IllegalArgumentException("invalid output sink");
    }
  }

  private <C extends StEinObject> void writeObjectList(List<? extends StEinGA> list,
                                                       String newLine,
                                                       String sep,
                                                       Writer writer) throws IOException
  {
    for (StEinGA ga : list) {
      writer.write(ga.getName());
      writer.write(sep);
      if (ga.getModuleNumber() != 0) {
        writer.write(Integer.toString(ga.getModuleNumber()));
      }
      writer.write(sep);
      writer.write(ga.getObjectClass().getMagic());
      writer.write(sep);
      writer.write(ga.getTemplateName());
      writer.write(sep);
      if (ga.getModuleNumber() != 0) {
        writer.write(Integer.toString(ga.getModuleNumber()));
      } else {
        writer.write('M');
      }
      writer.write('-');
      writer.write(Integer.toString(ga.getPort()));
      writer.write(sep);
      if (ga.getMode() != null) {
        writer.write(Integer.toString(ga.getMode().getMagic()));
      } else {
        writer.write(Integer.toString(ga.getTemplate().getMode().getMagic()));
      }
      writer.write(sep);
      if (ga.getHLUFix() != null) {
        writer.write(ga.getHLUFix().getMagic());
      } else {
        writer.write(ga.getTemplate().getHLUFix().getMagic());
      }
      writer.write(sep);
      if (ga.getPositionFix() != 0) {

      }
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(sep);
      writer.write(newLine);
    }
  }

  public void store(StEin stein,
                    Writer writer,
                    String newLine,
                    boolean addCaptions,
                    String sep) throws IOException
  {
    if (addCaptions) {
      writer.write(Bundle.CSVSteinStore_GA_Captions(sep));
    }
    writeObjectList(stein.getTrackTemplates(),
                    newLine,
                    sep,
                    writer);
    writeObjectList(stein.getTracks(),
                    newLine,
                    sep,
                    writer);
  }

  @Override
  public ConfigFormat getSupportedFormat()
  {
    return ConfigFormat.CSV;
  }

}
