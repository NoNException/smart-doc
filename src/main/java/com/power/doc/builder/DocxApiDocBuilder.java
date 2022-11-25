package com.power.doc.builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.constants.TornaConstants;
import com.power.doc.factory.BuildTemplateFactory;
import com.power.doc.helper.JavaProjectBuilderHelper;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiDocDict;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.TemplateDataBind;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.DocxStampTemplateUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;


import static com.power.doc.constants.DocGlobalConstants.ALL_IN_ONE_WORD_TPL;
import static com.power.doc.constants.DocGlobalConstants.API_DOC_WORD_TPL;
import static com.power.doc.constants.DocGlobalConstants.DICT_LIST_WORD;
import static com.power.doc.constants.DocGlobalConstants.DICT_LIST_WORD_TPL;
import static com.power.doc.constants.DocGlobalConstants.ERROR_CODE_LIST_WORD;
import static com.power.doc.constants.DocGlobalConstants.ERROR_CODE_LIST_WORD_TPL;
import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;

/**
 * Doc Builder for Microsoft Word fild
 * @author zongzi
 * @since 2.6.1+
 */
public class DocxApiDocBuilder {


	private static final String API_EXTENSION = "Api.docx";
	private static final String DATE_FORMAT = "yyyyMMddHHmm";

	/**
	 * build controller api and write in word file(.docx)
	 *
	 * @param config config
	 */
	public static void buildApiDoc(ApiConfig config) throws IOException {
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		buildApiDoc(config, javaProjectBuilder);

	}

	public static void buildApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) throws IOException {
		config.setAdoc(false);
		config.setParamsDataToTree(false);
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
		DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
		builderTemplate.checkAndInit(config, Boolean.FALSE);
		IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
		List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
		if (config.isAllInOne()) {
			String version = config.isCoverOld() ? "" : "-V" + DateTimeUtil.long2Str(System.currentTimeMillis(), DATE_FORMAT);
			String docName = builderTemplate.allInOneDocName(config, "AllInOne" + version + ".docx", ".docx");
			// handle the api group infos
			apiDocList = docBuildTemplate.handleApiGroup(apiDocList, config);
			buildAllInOne(apiDocList, config, javaProjectBuilder, ALL_IN_ONE_WORD_TPL, docName, null, null, builderTemplate);
			return;
		}
		builderTemplate.buildApiDoc(apiDocList, config, API_DOC_WORD_TPL, API_EXTENSION);
		builderTemplate.buildErrorCodeDoc(config, ERROR_CODE_LIST_WORD_TPL, ERROR_CODE_LIST_WORD, javaProjectBuilder);
		builderTemplate.buildDirectoryDataDoc(config, javaProjectBuilder, DICT_LIST_WORD_TPL, DICT_LIST_WORD);

	}

	/**
	 * Merge all api doc into one document
	 *
	 * @param apiDocList         list  data of Api doc
	 * @param config             api config
	 * @param javaProjectBuilder JavaProjectBuilder
	 * @param template           template
	 * @param outPutFileName     output file
	 */
	public static void buildAllInOne(List<ApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder,
			String template, String outPutFileName, ApiDoc apiDoc, String index, DocBuilderTemplate builderTemplate) throws IOException {
		// 输出路径
		String outPath = config.getOutPath();
		String strTime = DateTimeUtil.long2Str(System.currentTimeMillis(), DateTimeUtil.DATE_FORMAT_SECOND);
		// 创建输出文件夹
		FileUtil.mkdirs(outPath);

		List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);

		TemplateDataBind templateDataBind = new TemplateDataBind();
		templateDataBind.setApiDocList(apiDocList);
		templateDataBind.setErrorCodeList(errorCodeList);
		templateDataBind.setRevisionLogList(config.getRevisionLogs());
		templateDataBind.setVersion(strTime);
		templateDataBind.setAlias(index);
		templateDataBind.setCreateTime(strTime);
		templateDataBind.setProjectName(config.getProjectName());
		templateDataBind.setIsRequestExample(config.isRequestExample());
		templateDataBind.setResponseExample(config.isResponseExample());
		templateDataBind.setDisplayRequestParams(config.isRequestParamsTable());
		templateDataBind.setDisplayResponseParams(config.isResponseParamsTable());
		setDirectoryLanguageVariable(templateDataBind, config);

		List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
		templateDataBind.setDictList(apiDocDictList);

		boolean onlyHasDefaultGroup = apiDocList.stream()
				.allMatch(doc -> Objects.equals(TornaConstants.DEFAULT_GROUP_CODE, doc.getGroup()));
		int codeIndex = apiDocList.size();
		if (onlyHasDefaultGroup && apiDocList.size() > 0) {
			codeIndex = apiDocList.get(0).getChildrenApiDocs().size();
		}
		templateDataBind.setApiDocListOnlyHasDefaultGroup(onlyHasDefaultGroup);

		if (CollectionUtil.isNotEmpty(errorCodeList)) {
			templateDataBind.setErrorCodeListOrder(++codeIndex);
		}
		if (CollectionUtil.isNotEmpty(apiDocDictList)) {
			templateDataBind.setDictListOrder(++codeIndex);
		}

		if (Objects.nonNull(apiDoc)) {
			templateDataBind.setDesc(apiDoc.getDesc());
			templateDataBind.setOrder(apiDoc.getOrder());
			templateDataBind.setList(apiDoc.getList());
		}
		InputStream templateSteam = DocxApiDocBuilder.class.getResourceAsStream("/template/" + template);

		System.out.println(new Gson().toJson(templateDataBind));

		DocxStampTemplateUtil.stamp(templateSteam, config.getOutPath() + FILE_SEPARATOR + outPutFileName, templateDataBind);

	}


	static void setDirectoryLanguageVariable(TemplateDataBind templateDataBind, ApiConfig config) {
		if (Objects.isNull(config.getLanguage())) {
			return;
		}
		boolean chineseTitle = DocLanguage.CHINESE.code.equals(config.getLanguage().getCode());
		String errorListTitle = chineseTitle ? DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE : DocGlobalConstants.ERROR_CODE_LIST_EN_TITLE;
		String dictListTitle = chineseTitle ? DocGlobalConstants.DICT_CN_TITLE : DocGlobalConstants.DICT_EN_TITLE;
		templateDataBind.setErrorListTitle(errorListTitle);
		templateDataBind.setDictListTitle(dictListTitle);
	}

}
