package com.power.doc.model;

import java.util.List;

import com.power.doc.model.rpc.RpcApiDependency;
import com.power.doc.model.rpc.RpcApiDoc;

/**
 * template data bind object
 */
public class TemplateDataBind {
	private String desc;
	private String name;
	private List<ApiMethodDoc> list;
	private List<ApiDoc> apiDocList;
	private Boolean apiDocListOnlyHasDefaultGroup;
	private List<ApiErrorCode> errorCodeList;
	private Integer errorCodeListOrder;
	private List<RevisionLog> revisionLogList;
	private List<RpcApiDependency> dependencyList;
	private String homePage;
	private String html;
	private String title;
	private String style;
	private String background;
	private String errorListTitle;
	private String createTime;
	private String projectName;
	private List<ApiDocDict> dictList;
	private String dictListTitle;
	private Integer dictListOrder;
	private String version;
	private String protocol;
	private String author;
	private String uri;
	private String consumerConfigExample;
	private Boolean isRequestExample;
	private Boolean isResponseExample;
	private Boolean displayRequestParams;
	private Boolean displayResponseParams;
	private Integer order;
	private String alias;
	private List<RpcApiDoc> directoryTree;
	private String highlightCssLink;
	private String rpcConfigContent;

	private String css_cdn;

	public String getRpcConfigContent() {
		return rpcConfigContent;
	}

	public void setRpcConfigContent(String rpcConfigContent) {
		this.rpcConfigContent = rpcConfigContent;
	}
	/**
	 * api list for rpc ;
	 */
	private List<RpcApiDoc> rpcApiDocList;

	public List<RpcApiDoc> getRpcApiDocList() {
		return rpcApiDocList;
	}

	public TemplateDataBind() {
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ApiMethodDoc> getList() {
		return list;
	}

	public void setList(List<ApiMethodDoc> list) {
		this.list = list;
	}

	public List<ApiDoc> getApiDocList() {
		return apiDocList;
	}

	public void setApiDocList(List<ApiDoc> apiDocList) {
		this.apiDocList = apiDocList;
	}

	public void setRpcApiDocList(List<RpcApiDoc> rpcApiDocList) {
		this.rpcApiDocList = rpcApiDocList;
	}

	public Boolean getApiDocListOnlyHasDefaultGroup() {
		return apiDocListOnlyHasDefaultGroup;
	}

	public void setApiDocListOnlyHasDefaultGroup(Boolean apiDocListOnlyHasDefaultGroup) {
		this.apiDocListOnlyHasDefaultGroup = apiDocListOnlyHasDefaultGroup;
	}

	public List<ApiErrorCode> getErrorCodeList() {
		return errorCodeList;
	}

	public void setErrorCodeList(List<ApiErrorCode> errorCodeList) {
		this.errorCodeList = errorCodeList;
	}

	public Integer getErrorCodeListOrder() {
		return errorCodeListOrder;
	}

	public void setErrorCodeListOrder(Integer errorCodeListOrder) {
		this.errorCodeListOrder = errorCodeListOrder;
	}

	public List<RevisionLog> getRevisionLogList() {
		return revisionLogList;
	}

	public void setRevisionLogList(List<RevisionLog> revisionLogList) {
		this.revisionLogList = revisionLogList;
	}

	public List<RpcApiDependency> getDependencyList() {
		return dependencyList;
	}

	public void setDependencyList(List<RpcApiDependency> dependencyList) {
		this.dependencyList = dependencyList;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getErrorListTitle() {
		return errorListTitle;
	}

	public void setErrorListTitle(String errorListTitle) {
		this.errorListTitle = errorListTitle;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<ApiDocDict> getDictList() {
		return dictList;
	}

	public void setDictList(List<ApiDocDict> dictList) {
		this.dictList = dictList;
	}

	public String getDictListTitle() {
		return dictListTitle;
	}

	public void setDictListTitle(String dictListTitle) {
		this.dictListTitle = dictListTitle;
	}

	public Integer getDictListOrder() {
		return dictListOrder;
	}

	public void setDictListOrder(Integer dictListOrder) {
		this.dictListOrder = dictListOrder;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getConsumerConfigExample() {
		return consumerConfigExample;
	}

	public void setConsumerConfigExample(String consumerConfigExample) {
		this.consumerConfigExample = consumerConfigExample;
	}

	public Boolean getRequestExample() {
		return isRequestExample;
	}

	public void setIsRequestExample(Boolean isRequestExample) {
		isRequestExample = isRequestExample;
	}

	public Boolean getIsResponseExample() {
		return isResponseExample;
	}

	public void setResponseExample(Boolean responseExample) {
		isResponseExample = responseExample;
	}

	public Boolean getDisplayRequestParams() {
		return displayRequestParams;
	}

	public void setDisplayRequestParams(Boolean displayRequestParams) {
		this.displayRequestParams = displayRequestParams;
	}

	public Boolean getDisplayResponseParams() {
		return displayResponseParams;
	}

	public void setDisplayResponseParams(Boolean displayResponseParams) {
		this.displayResponseParams = displayResponseParams;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<RpcApiDoc> getDirectoryTree() {
		return directoryTree;
	}

	public void setDirectoryTree(List<RpcApiDoc> directoryTree) {
		this.directoryTree = directoryTree;
	}

	public String getHighlightCssLink() {
		return highlightCssLink;
	}

	public void setHighlightCssLink(String highlightCssLink) {
		this.highlightCssLink = highlightCssLink;
	}

	public String getCss_cdn() {
		return css_cdn;
	}

	public void setCss_cdn(String css_cdn) {
		this.css_cdn = css_cdn;
	}
}
