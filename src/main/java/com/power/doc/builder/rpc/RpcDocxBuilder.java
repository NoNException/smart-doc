package com.power.doc.builder.rpc;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.gson.Gson;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.doc.builder.AbstractDocxBuilder;
import com.power.doc.builder.BaseDocBuilderTemplate;
import com.power.doc.constants.FrameworkEnum;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDocDict;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.Doc;
import com.power.doc.model.TemplateDataBind;
import com.power.doc.model.rpc.RpcApiDoc;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.DocxStampTemplateUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;

/**
 * Rpc Docx Builder that can handle rpc docs
 * @author zongzi
 */
public class RpcDocxBuilder extends AbstractDocxBuilder {
	private static final Logger log = LoggerFactory.getLogger(RpcDocxBuilder.class);

	public RpcDocxBuilder(JavaProjectBuilder javaProjectBuilder, ApiConfig config) {
		super(javaProjectBuilder, config);
	}


	@Override
	protected BaseDocBuilderTemplate provideDocBuilderTemplate() {
		return new RpcDocBuilderTemplate();
	}

	@Override
	public List<? extends Doc> doAnalyzeSourceCode() {
		// rewrite the framework enum
		getApiConfig().setFramework(FrameworkEnum.DUBBO.getFramework());
		return ((RpcDocBuilderTemplate) getDocBuilderTemplate()).getRpcApiDoc(getApiConfig(), getJavaProjectBuilder());
	}


	@Override
	public void renderAllInOne(List<? extends Doc> docs) {
		try {
			ApiConfig config = getApiConfig();
			JavaProjectBuilder javaProjectBuilder = getJavaProjectBuilder();
			long now = System.currentTimeMillis();

			String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
			String rpcConfig = config.getRpcConsumerConfig();
			String rpcConfigContent = null;
			if (Objects.nonNull(rpcConfig)) {
				rpcConfigContent = FileUtil.getFileContent(rpcConfig);
			}
			List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
			TemplateDataBind templateDataBind = new TemplateDataBind();
			templateDataBind.setRpcApiDocList((List<RpcApiDoc>) docs);
			templateDataBind.setErrorCodeList(errorCodeList);
			templateDataBind.setRevisionLogList(config.getRevisionLogs());
			templateDataBind.setDependencyList(config.getRpcApiDependencies());
			templateDataBind.setVersion(String.valueOf(now));
			templateDataBind.setCreateTime(strTime);
			templateDataBind.setProjectName(config.getProjectName());
			templateDataBind.setRpcConfigContent(rpcConfigContent);
			setDirectoryLanguageVariable(templateDataBind, config);
			List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
			templateDataBind.setDictList(apiDocDictList);


			String outputFilePath = config.getOutPath() + FILE_SEPARATOR + allInOneDocName("rpc-");
			DocxStampTemplateUtil.stamp("/template/dubbo/DubboAllInOne.docx", outputFilePath, formatData(templateDataBind));
		}
		catch (IOException e) {
			log.warn("Error happens when try to build all doc in one", e);
		}
	}

	@Override
	protected String provideNameForEachDoc(Doc doc) {
		RpcApiDoc rpcApiDoc = (RpcApiDoc) doc;
		String realVersion = Optional.ofNullable(rpcApiDoc.getVersion())
				.orElse(String.valueOf(System.currentTimeMillis()));
		rpcApiDoc.setVersion(realVersion);
		return rpcApiDoc.getName();
	}

	@Override
	protected String provideTemplateFile() {
		return "/template/dubbo/Dubbo.docx";
	}

}
