package me.nov.reversecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.jar.JarFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import me.nov.reversecrypt.extractors.JCryptEx;

public class ReverseCrypt {

	public static void main(String[] args) throws Exception {

		Options options = new Options();
		options.addOption("i", "input", true, "The crypted input file to use");
		options.addOption("o", "output", true, "The decrypted output file");
		options.addOption("ex", "extractor", true, "The extractor to use");
		options.addOption("help", false, "Prints this help");

		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(options, args);
		} catch (org.apache.commons.cli.ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("An error occurred while parsing the commandline ");
		}
		if (line.hasOption("help") || !line.hasOption("i") || !line.hasOption("o") || !line.hasOption("ex")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ReverseCrypter", options);
			return;
		}
		File input = new File(line.getOptionValue("i"));
		File output = new File(line.getOptionValue("o"));
		if (!input.exists()) {
			throw new FileNotFoundException(input.getAbsolutePath());
		}
		if (output.exists()) {
			System.err.println("Warning: Output already exists, renaming existing file");
			File existing = new File(line.getOptionValue("o"));
			File newName = new File(line.getOptionValue("o") + "-BAK");
			if (newName.exists()) {
				newName.delete();
			}
			existing.renameTo(newName);
		}
		Class ex = null;
		try {
			ex = Class.forName(line.getOptionValue("ex"));
		} catch (ClassNotFoundException e) {
			try {
				ex = Class.forName("me.nov.reversecrypt.extractors." + line.getOptionValue("ex"));
			} catch (ClassNotFoundException e1) {
				throw new RuntimeException("Extractor not found");
			}
		}
		Extractor e = (Extractor) ex.getConstructor(JarFile.class).newInstance(new JarFile(input));
		Files.write(output.toPath(), e.extract());
	}

}
