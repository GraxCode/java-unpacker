package me.nov.reversecrypt.extractors;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

import me.nov.reversecrypt.Extractor;
import sun.misc.BASE64Decoder;

public class JarProtectorEx extends Extractor {

  private String configPath = "c.dat";

  public JarProtectorEx(JarFile file) {
    super(file);
  }

  public JarProtectorEx(JarFile file, String config) {
    super(file);
    this.configPath = config;
  }

  @Override
  public byte[] extract() {
    try {
      String[] config = new String(getConfig(), "UTF-8").split("\n");
      String path = config[1];
      boolean compressed = Boolean.parseBoolean(config[4]);
      String key = config[5];
      JarEntry e = file.getJarEntry(path);
      if(compressed) {
        //TODO test for compressed
        return decrypt(IOUtils.toByteArray(new GZIPInputStream(file.getInputStream(e))), key.getBytes());
      }
      return decrypt(IOUtils.toByteArray(file.getInputStream(e)), key.getBytes());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private byte[] decrypt(byte[] enc, byte[] keybytes) {
    byte[] decrypted = (byte[])enc.clone();
    int i = 0;

    for(int keyIndex = 0; i < decrypted.length; ++i) {
       decrypted[i] ^= keybytes[keyIndex++];
       if(keyIndex == keybytes.length) {
          keyIndex = 0;
       }
    }
    return decrypted;
 }

  private byte[] getConfig() throws IOException {
    InputStream bytes = file.getInputStream(file.getJarEntry(configPath));
    byte[] configBytes = (new BASE64Decoder()).decodeBuffer(bytes);

    for (int i = 0; i < configBytes.length; ++i) {
      configBytes[i] ^= -35;
    }

    return configBytes;
  }
}
