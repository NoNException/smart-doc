package com.power.doc.builder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.doc.constants.TornaConstants;
import com.power.doc.factory.BuildTemplateFactory;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiDocDict;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.Doc;
import com.power.doc.model.TemplateDataBind;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.DocxStampTemplateUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;

/**
 * Doc Builder for Microsoft Word fild
 * @author zongzi
 * @since 2.6.1+
 */
public class DocxApiDocBuilder extends AbstractDocxBuilder {

	private static final Logger log = LoggerFactory.getLogger(DocxApiDocBuilder.class);
	private IDocBuildTemplate<ApiDoc> docBuildTemplate = null;

	public DocxApiDocBuilder(JavaProjectBuilder javaProjectBuilder, ApiConfig config) {
		super(javaProjectBuilder, config);
	}

	@Override
	protected BaseDocBuilderTemplate provideDocBuilderTemplate() {
		return new DocBuilderTemplate();
	}

	@Override
	public List<? extends Doc> doAnalyzeSourceCode() {
		ApiConfig apiConfig = getApiConfig();
		JavaProjectBuilder javaProjectBuilder = getJavaProjectBuilder();
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(apiConfig, javaProjectBuilder);
		this.docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(apiConfig.getFramework());
		return docBuildTemplate.getApiData(configBuilder);
	}


	@Override
	public void renderAllInOne(List<? extends Doc> docs) {
		// handle the api group infos
		List<ApiDoc> apiDocList = docBuildTemplate.handleApiGroup((List<ApiDoc>) docs, getApiConfig());
		try {
			ApiConfig config = getApiConfig();
			JavaProjectBuilder javaProjectBuilder = getJavaProjectBuilder();
			String strTime = DateTimeUtil.long2Str(System.currentTimeMillis(), DateTimeUtil.DATE_FORMAT_SECOND);
			// 创建输出文件夹
			List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);

			TemplateDataBind templateDataBind = new TemplateDataBind();
			templateDataBind.setApiDocList(apiDocList);
			templateDataBind.setErrorCodeList(errorCodeList);
			templateDataBind.setRevisionLogList(config.getRevisionLogs());
			templateDataBind.setVersion(strTime);
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

			log.debug(new Gson().toJson(templateDataBind));
			String outputFilePath = config.getOutPath() + FILE_SEPARATOR + allInOneDocName("html-");
			DocxStampTemplateUtil.stamp("/template/AllInOne.docx", outputFilePath, templateDataBind);
		}
		catch (IOException e) {
			log.warn("[Error] when create render all in one apis！", e);
		}
	}

	@Override
	protected String provideNameForEachDoc(Doc doc) {
		return ((ApiDoc) doc).getName();
	}

}
