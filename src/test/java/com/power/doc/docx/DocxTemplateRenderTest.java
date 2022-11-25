package com.power.doc.docx;

import java.io.IOException;

import com.power.doc.builder.DocxApiDocBuilder;
import com.power.doc.model.ApiConfig;
import org.junit.jupiter.api.Test;


/**
 * Test Word(.dox) template render use beetl
 */
public class DocxTemplateRenderTest {


	@Test
	public void testSimpleRender() {
		ApiConfig apiConfig = new ApiConfig();
		try {
			apiConfig.setOutPath("target/doc");
			apiConfig.setAllInOne(true);
			apiConfig.setCoverOld(true);
			DocxApiDocBuilder.buildApiDoc(apiConfig);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
