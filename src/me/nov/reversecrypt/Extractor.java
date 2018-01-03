package me.nov.reversecrypt;

import java.util.jar.JarFile;

public abstract class Extractor {
	protected JarFile file;

	public Extractor(JarFile file) {
		super();
		this.file = file;
	}

	public abstract byte[] extract();
}
