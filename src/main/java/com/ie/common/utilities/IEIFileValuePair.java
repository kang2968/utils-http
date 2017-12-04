package com.ie.common.utilities;

import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.io.Serializable;

/**
 * File Http Message
 * @author bradly
 * @version 1.0
 */
public interface IEIFileValuePair extends Serializable{

	/**
	 * Field
	 * @return
	 */
	String getName();

	/**
	 * File Name
	 * @return
	 */
	String getFileName();

	/**
	 * File Stream
	 * @return
	 */
	InputStream getStream();

	/**
	 * File Content Type
	 * @return
	 */
	String getContentType();

	/**
	 * Close Stream
	 */
	@PreDestroy
	default void destroyStream() {
		if (getStream() != null) {
			try {
				getStream().close();
			} catch (Exception e) {
			}
		}
	}
}
