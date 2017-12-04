package com.ie.common.utilities;

import java.io.InputStream;

/**
 * 文件表单 HTTP Message Info
 * 当对象移除时会自动释放stream流， 如果之后继续使用该stream， 自动设置该stream = null
 * @author bradly
 * @version 1.0
 *
 */
public class IEBasicFileValuePair implements IEIFileValuePair {

	/**
	 * 字段名称
	 */
	private String name;
	
	/**
	 * 文件名称，Server端获取到的文件名来源
	 */
	private String fileName;
	
	/**
	 * 文件类型 , 默认值 ： application/octet-stream
	 */
	private String contentType;
	
	/**
	 * 文件数据流
	 */
	private InputStream stream;
	
	public IEBasicFileValuePair() {
		super();
	}

	public IEBasicFileValuePair(String name, String fileName, String contentType, InputStream stream) {
		super();
		this.name = name;
		this.fileName = fileName;
		this.contentType = contentType;
		this.contentType = contentType;
		this.stream = stream;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public InputStream getStream() {
		return this.stream;
	}

	@Override
	public String getContentType() {
		if(this.contentType == null || this.contentType.trim().length() == 0){
			return "application/octet-stream";
		}
		return this.contentType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	@Override
	protected void finalize() throws Throwable {
		this.destroyStream();
	}

}
