package com.power.doc.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.power.doc.builder.DocxApiDocBuilder;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.TemplateDataBind;
import com.power.doc.utils.DocxStampTemplateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


/**
 *  tests for docx-stamper render .docx file
 */

public class DocxStamperTemplateUtilsTest {

	@BeforeAll
	static void clearTempFile() {
		File tempFile = new File("target/temp");
		try {
			File[] files = tempFile.listFiles();

			for (File file : files) {
				file.delete();
			}
		}
		catch (Exception e) {
			System.out.printf("Delete temp test file:%s failed%n", tempFile);
		}
	}


	@ParameterizedTest
	@MethodSource("renderParamsProvider")
	public <T> void renderEachDocx(String templateFilePath, String outDic, String testDataFile, Class<T> classOfT) throws IOException {
		InputStream templateSteam = DocxApiDocBuilder.class.getResourceAsStream(templateFilePath);
		String outFile = outDic + UUID.randomUUID() + ".docx";
		URL testDataFileURL = this.getClass().getClassLoader().getResource(testDataFile);
		Assertions.assertNotNull(testDataFileURL);
		T t = new Gson().fromJson(new FileReader(testDataFileURL.getFile()), classOfT);
		Assertions.assertNotNull(t);
		DocxStampTemplateUtil.stamp(templateSteam, outFile, t);
		Assertions.assertTrue(new File(outFile).exists(), String.format("Failed to create file %s", outFile));
	}

	private static Stream<Arguments> renderParamsProvider() {
		return Stream.of(
				// Arguments.of("/template/AllInOne.docx", "target/temp/allInOne-", "all-in-one-apis.json", TemplateDataBind.class),
				// Arguments.of("/template/ApiDoc.docx", "target/temp/ApiDoc-Apis-", "apis.json", ApiDoc.class),
				// Arguments.of("/template/ErrorCodeList.docx", "target/temp/ApiDoc-Error-", "error-code-list.json", TemplateDataBind.class),
				// Arguments.of("/template/Dictionary.docx", "target/temp/ApiDoc-Dictionary-", "dictionary-list.json", TemplateDataBind.class),
				Arguments.of("/template/dubbo/DubboAllInOne.docx", "target/temp/dubbo-all-in-one-", "all-in-one-dubbo-apis.json", TemplateDataBind.class)

		);
	}
}
