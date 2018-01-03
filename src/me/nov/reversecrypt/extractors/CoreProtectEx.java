package me.nov.reversecrypt.extractors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;

import me.nov.reversecrypt.Extractor;

public class CoreProtectEx extends Extractor {

	/**
	 * You'll find this somewhere in the JarFile decryption method 
	 */
	private String key = "3WEU1MMgXbBsXP5VAjCVZy3PQXovTW3WEU1MMgXbBsXP5VAjCVZy3PQXovTW";

	public CoreProtectEx(JarFile file) {
		super(file);
	}

	@Override
	public byte[] extract() {
		try {
			System.out.println("Decrypting CoreProtect using key " + key);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			Enumeration e = file.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = (JarEntry) e.nextElement();
				if (entry.getName().startsWith("META-INF"))
					continue;
				byte[] bytes = IOUtils.toByteArray(file.getInputStream(entry));
				String cafebabe = String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
				if (cafebabe.toLowerCase().equals("cafebabe")) {
					//entry is a class file, continue
					continue;
				}
				DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
				String utf = dis.readUTF();
				Engine rc4 = new Engine(utf.getBytes());
				int length = dis.readInt();
				byte[] array = new byte[length];

				for (int i = 0; i < length; ++i) {
					array[i] = dis.readByte();
				}

				byte[] decrypted = rc4.a(array);
				baos.write(new Engine(key.getBytes()).a(decrypted));
			}
			baos.close();
			String subKey = key.substring(0, 30); //warning: this could be something different, check jar
			return new Engine(subKey.getBytes()).a(baos.toByteArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private class Engine {
		//this is probably RC4 encryption but i'm not sure

		private int[] a = new int[256];
		private final int b;

		public Engine(byte[] var1) {
			if (var1.length >= 1 && var1.length <= 256) {
				this.b = var1.length;

				int var2;
				for (var2 = 0; var2 < 256; this.a[var2] = var2++) {
					;
				}

				var2 = 0;

				for (int var3 = 0; var3 < 256; ++var3) {
					var2 = (var2 + this.a[var3] + var1[var3 % this.b]) % 256;
					int var4 = this.a[var3];
					this.a[var3] = this.a[var2];
					this.a[var2] = var4;
				}

			} else {
				throw new IllegalArgumentException("key must be between 1 and 256 bytes");
			}
		}

		private int[] a(int[] var1) {
			int[] var2 = new int[var1.length];
			int[] var3 = new int[this.a.length];
			System.arraycopy(this.a, 0, var3, 0, var3.length);
			int var4 = 0;
			int var5 = 0;

			for (int var6 = 0; var6 < var1.length; ++var6) {
				var4 = var4 + 1 & 255;
				var5 = var5 + var3[var4] & 255;
				var3[var4] ^= var3[var5];
				var3[var5] ^= var3[var4];
				var3[var4] ^= var3[var5];
				int var13 = var3[var4] + var3[var5] & 255;
				int var14 = var3[var13];
				var2[var6] = var1[var6] ^ var14;
			}

			return var2;
		}

		private int[] c(byte[] var1) {
			int[] var2 = new int[var1.length];

			for (int var3 = 0; var3 < var1.length; ++var3) {
				var2[var3] = var1[var3];
			}

			return var2;
		}

		private byte[] b(int[] var1) {
			byte[] var2 = new byte[var1.length];

			for (int var3 = 0; var3 < var1.length; ++var3) {
				var2[var3] = (byte) var1[var3];
			}

			return var2;
		}

		public byte[] a(byte[] var1) {
			int[] var2 = this.c(var1);

			for (int var3 = 0; var3 < var2.length; ++var3) {
				var2[var3] &= 255;
			}

			byte[] var4 = this.b(this.a(var2));
			return var4;
		}

		public byte[] b(byte[] var1) {
			byte[] var2 = this.b(this.a(this.c(var1)));
			return var2;
		}
	}

}
