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
package com.google.code.sagetvaddons.sagealert.server.events;

import gkusnick.sagetv.api.MediaFileAPI;

import com.google.code.sagetvaddons.sagealert.server.CoreEventsManager;
import com.google.code.sagetvaddons.sagealert.shared.SageAlertEvent;

/**
 * @author dbattams
 *
 */
final public class RecordingStartedEvent implements SageAlertEvent {

	private MediaFileAPI.MediaFile mf;
	
	public RecordingStartedEvent(MediaFileAPI.MediaFile mf) {
		this.mf = mf;
	}
	
	public String getLongDescription() {
		StringBuilder msg = new StringBuilder("A new recording has started: '" + mf.GetMediaTitle());
		String subtitle = mf.GetMediaFileAiring().GetShow().GetShowEpisode();
		if(subtitle != null && subtitle.length() > 0)
			msg.append(": " + subtitle);
		msg.append("'");
		return msg.toString();
	}

	public String getMediumDescription() {
		return "A new recording has started: '" + mf.GetMediaTitle() + "'";
	}

	public String getShortDescription() {
		return getMediumDescription();
	}

	public String getSource() {
		return CoreEventsManager.REC_STARTED;
	}

	public String getSubject() {
		return "A new recording has started";
	}

}