package com.power.doc.builder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDocDict;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.Doc;
import com.power.doc.model.TemplateDataBind;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.DocxStampTemplateUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;

/**
 * Abstract method of create .docx template file
 * @author zongzi
 */
public abstract class AbstractDocxBuilder {

	private static final Logger log = LoggerFactory.getLogger(AbstractDocxBuilder.class);
	private static final String API_EXTENSION = "Api.docx";
	private static final String DATE_FORMAT = "yyyyMMddHHmm";
	private BaseDocBuilderTemplate builderTemplate;
	private final JavaProjectBuilder javaProjectBuilder;
	private final ApiConfig config;

	public AbstractDocxBuilder(JavaProjectBuilder javaProjectBuilder, ApiConfig config) {
		this.javaProjectBuilder = javaProjectBuilder;
		this.config = config;
	}

	public BaseDocBuilderTemplate getDocBuilderTemplate() {
		if (this.builderTemplate == null) {
			this.builderTemplate = provideDocBuilderTemplate();
		}
		return this.builderTemplate;
	}

	public JavaProjectBuilder getJavaProjectBuilder() {
		return this.javaProjectBuilder;
	}

	public ApiConfig getApiConfig() {
		return this.config;
	}

	/**
	 * build controller api and write in word file(.docx)
	 *
	 */
	public void buildApiDoc() throws IOException {
		config.setAdoc(false);
		config.setParamsDataToTree(false);
		getDocBuilderTemplate().checkAndInit(config, Boolean.FALSE);
		List<? extends Doc> docs = doAnalyzeSourceCode();
		if (config.isAllInOne()) {
			renderAllInOne(docs);
		}
		else {
			rendApis(docs);
			rendBothErrorAndDictionary();
		}
	}

	private void rendBothErrorAndDictionary() throws IOException {
		List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
		List<ApiDocDict> directories = DocUtil.buildDictionary(config, javaProjectBuilder);
		TemplateDataBind templateDataBind = new TemplateDataBind();
		setDirectoryLanguageVariable(templateDataBind, config);
		// handle directories
		if (CollectionUtil.isNotEmpty(directories)) {
			log.debug("dictionary:" + new Gson().toJson(errorCodeList));
			String outputFilePath = config.getOutPath() + FILE_SEPARATOR + "Dictionary.docx";
			templateDataBind.setDictList(directories);
			DocxStampTemplateUtil.stamp("/template/Dictionary.docx", outputFilePath, formatData(templateDataBind));
		}
		// handle error lists
		if (CollectionUtil.isNotEmpty(errorCodeList)) {
			log.debug("errorCode:" + new Gson().toJson(errorCodeList));
			templateDataBind.setErrorCodeList(errorCodeList);
			String outputFilePath = config.getOutPath() + FILE_SEPARATOR + "ErrorCodeList.docx";
			DocxStampTemplateUtil.stamp("/template/ErrorCodeList.docx", outputFilePath, formatData(templateDataBind));
		}
	}

	/**
	 * create one file for each doc object
	 * @param docs api lists
	 */
	private void rendApis(List<? extends Doc> docs) {
		log.debug("apis:" + new Gson().toJson(docs));
		for (Doc doc : docs) {
			String outPutFileName = provideNameForEachDoc(doc);
			try {
				String outputFilePath = config.getOutPath() + FILE_SEPARATOR + outPutFileName + API_EXTENSION;
				String templateFile = provideTemplateFile();
				DocxStampTemplateUtil.stamp(templateFile, outputFilePath, formatData(doc));
			}
			catch (IOException e) {
				log.warn(String.format("[Error] when create render api:%s！", outPutFileName), e);
			}
		}
	}


	public static void setDirectoryLanguageVariable(TemplateDataBind templateDataBind, ApiConfig config) {
		if (Objects.isNull(config.getLanguage())) {
			config.setLanguage(DocLanguage.ENGLISH);
		}
		boolean isChineseTitle = DocLanguage.CHINESE.code.equals(config.getLanguage().getCode());
		Map<Boolean, List<String>> hashMap = new HashMap<Boolean, List<String>>(2) {{
			put(true, Arrays.asList(DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE, DocGlobalConstants.DICT_CN_TITLE));
			put(false, Arrays.asList(DocGlobalConstants.ERROR_CODE_LIST_EN_TITLE, DocGlobalConstants.DICT_EN_TITLE));
		}};
		templateDataBind.setErrorListTitle(hashMap.get(isChineseTitle).get(0));
		templateDataBind.setDictListTitle(hashMap.get(isChineseTitle).get(1));
	}

	protected String allInOneDocName(String prefix) {
		String version = config.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(), DATE_FORMAT);
		return prefix + getDocBuilderTemplate().allInOneDocName(config, "all" + version, ".docx");
	}

	protected <T> T formatData(T t) {
		Gson gson = new Gson();
		String s = gson.toJson(t);
		s = s.replace("\\n", "\\u000D").
				replace("\\u0026nbsp;", "\\u00A0").
				replace("\\r", "\\u000A").
				// 将<br>替换成换行符
						replace("\\u003cbr\\u003e", "\\u000D").
				replace("\\u003cbr/\\u003e", "\\u000D");
		return (T) gson.fromJson(s, t.getClass());
	}

	/**
	 * subClass provided the templates;
	 * @return subClass provided the templates;
	 */
	protected abstract BaseDocBuilderTemplate provideDocBuilderTemplate();

	/**
	 * really job of analyze source code
	 * @return List of docs
	 */
	protected abstract List<? extends Doc> doAnalyzeSourceCode();

	/**
	 * rend all the files according to the api config and apiDocs;
	 * @param docs     docs which created by {@link #doAnalyzeSourceCode()}
	 */
	protected abstract void renderAllInOne(List<? extends Doc> docs);

	/**
	 * subClass decides what kinds of doc it should be
	 * @param doc doc object
	 * @return doc file's name
	 */
	protected abstract String provideNameForEachDoc(Doc doc);

	/**
	 * subClass decides what kinds of template it should be when rend one .docx for each api
	 * @return template name
	 */
	protected abstract String provideTemplateFile();
}
