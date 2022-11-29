package com.power.doc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.power.doc.builder.DocxApiDocBuilder;
import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;

/**
 * load Java Object to .docx template
 * @author zongzi
 */
public class DocxStampTemplateUtil {

	public static <T> void stamp(String template, String outputFilePath, T contextRoot) throws IOException {
		InputStream templateSteam = DocxApiDocBuilder.class.getResourceAsStream(template);
		stamp(templateSteam,outputFilePath,contextRoot);
	}
	public static <T> void stamp(InputStream template, String outputFilePath, T contextRoot) throws IOException {
		OutputStream out = getOutputStream(outputFilePath);
		DocxStamper<T> stamper = new DocxStamper<>(new DocxStamperConfiguration());
		stamper.stamp(template, contextRoot, out);
	}

	protected static OutputStream getOutputStream(String outputFilePath) throws IOException {
		OutputStream out;
		Path path = Paths.get(outputFilePath);
		try {
			Files.createDirectories(path.getParent());
			if (!Files.exists(path, new LinkOption[0])) {
				Files.createFile(path);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		out = Files.newOutputStream(path);
		return out;
	}

}
