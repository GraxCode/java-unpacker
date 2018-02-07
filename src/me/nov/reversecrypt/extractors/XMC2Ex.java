package me.nov.reversecrypt.extractors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.IOUtils;

import me.nov.reversecrypt.Extractor;

public class XMC2Ex extends Extractor {

  public XMC2Ex(JarFile file) {
    super(file);
  }

  @Override
  public byte[] extract() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      JarOutputStream jar = new JarOutputStream(baos);
      Enumeration<JarEntry> e = file.entries();
      while (e.hasMoreElements()) {
        JarEntry entry = (JarEntry) e.nextElement();
        if (entry.getName().startsWith("META-INF"))
          continue;
        byte[] bytes = IOUtils.toByteArray(file.getInputStream(entry));
        if (isClass(bytes)) {
          continue;
        }
        String name = new String(xor90(entry.getName().getBytes()));
        byte[] xorBytes = xor90(bytes);
        if (isClass(xorBytes)) {
          name += ".class";
        } else {
          System.err.println("Not a class: " + name);
        }
        JarEntry newFile = new JarEntry(name);
        jar.putNextEntry(newFile);
        jar.write(xorBytes);
        jar.closeEntry();
      }
      jar.close();
      baos.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return baos.toByteArray();
  }

  private static boolean isClass(byte[] bytes) {
    String cafebabe = String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
    if (cafebabe.toLowerCase().equals("cafebabe")) {
      return true;
    }
    return false;
  }

  private static byte[] xor90(byte[] var0) {
    final byte[] var = new byte[var0.length];
    for (int var2 = 0; var2 < var0.length; ++var2) {
      var[var2] = (var0[var2] ^= 0x5A);
    }
    return var;
  }

}
