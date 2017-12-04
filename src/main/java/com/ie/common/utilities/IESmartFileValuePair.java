package com.ie.common.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 增强版文件对模型，通过文件全路径自动获取文件名与文件流
 * @author bradly
 * @version 1.0
 *
 */
public class IESmartFileValuePair extends IEBasicFileValuePair {

	/**
	 * 文件全路径
	 */
	private String filePath;

	public IESmartFileValuePair(String name, String filePath, String contentType) throws FileNotFoundException {
		super(name,getFilename(filePath),contentType, new FileInputStream(filePath));
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) throws FileNotFoundException {
		this.filePath = filePath;
		super.setFileName(getFilename(filePath));
		super.setStream(new FileInputStream(filePath));
	}
	
	private static String getFilename(String filePath) {
		if (filePath != null) {
			int index = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
			if (index > 0) {
				filePath = filePath.substring(index + 1);
			}
		}
		return filePath;
	}
}
