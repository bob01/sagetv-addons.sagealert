/*
 *      Copyright 2010 Battams, Derek
 *       
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */
package com.google.code.sagetvaddons.sagealert.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.google.code.sagetvaddons.sagealert.plugin.Plugin;



/**
 * @author dbattams
 *
 */
abstract class LogFileServer implements SageAlertEventHandler {
	static private final Logger LOG = Logger.getLogger(LogFileServer.class);
	static private final String FILE_ENC = "UTF-8";
	
	File target;
	Writer w;
	
	/**
	 * 
	 */
	protected LogFileServer(File target) {
		if(target.isAbsolute())
			this.target = target;
		else
			this.target = new File(new File(Plugin.RES_DIR, "alertLogs"), target.getPath());
		if(!this.target.getParentFile().isDirectory() && !this.target.getParentFile().mkdirs())
			throw new IllegalArgumentException("Unable to create base directory for log! [" + this.target.getParent() + "]");
		if(this.target.exists() && !this.target.canWrite())
			throw new IllegalArgumentException("Unable to write to target log file! [" + this.target + "]");
		try {
			w = new OutputStreamWriter(new FileOutputStream(this.target, true), FILE_ENC);
		} catch(IOException e) {
			LOG.error("IO Error", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the target
	 */
	public File getTarget() {
		return target;
	}
	
	public Writer getWriter() {
		return w;
	}
		
	public void destroy() {
		if(w != null) {
			try {
				w.close();
			} catch(IOException e) {
				LOG.error("IO Error", e);
			}
		}		
	}
}
