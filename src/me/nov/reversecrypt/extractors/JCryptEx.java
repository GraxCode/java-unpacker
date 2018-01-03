package me.nov.reversecrypt.extractors;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;

import me.nov.reversecrypt.Extractor;
import me.nov.reversecrypt.exception.ExtractException;

public class JCryptEx extends Extractor {

	private String path = "jar.dat";

	public JCryptEx(JarFile file) {
		super(file);
	}

	public JCryptEx(JarFile file, String jarInside) {
		super(file);
		this.path = jarInside;
	}

	@SuppressWarnings("unused")
  @Override
	public byte[] extract() {
		JarEntry e = file.getJarEntry(path);
		if (e == null) {
			throw new ExtractException(path + " inside jar not found");
		}
		byte[] extra = e.getExtra();
		byte[] key = new byte[16];
		System.arraycopy(extra, 0, key, 0, 16);
		byte[] iv = new byte[16];
		System.arraycopy(extra, 16, iv, 0, 16);
		boolean encryptedResources = extra[32] == 1;
		byte[] bMainClass = new byte[extra.length - 33];
		System.arraycopy(extra, 33, bMainClass, 0, extra.length - 33);
		String mainClass = new String(bMainClass);

		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
			SecretKeySpec sks = new SecretKeySpec(key, "AES");
			cipher.init(2, sks, new IvParameterSpec(iv));
			CipherInputStream cis = new CipherInputStream(file.getInputStream(e), cipher);
			return IOUtils.toByteArray(cis);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
