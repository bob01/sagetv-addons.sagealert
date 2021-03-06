/*
 *      Copyright 2010-2011 Battams, Derek
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

import gkusnick.sagetv.api.API;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import sage.SageTVPluginRegistry;

import com.google.code.sagetvaddons.sagealert.server.events.AppStartedEvent;
import com.google.code.sagetvaddons.sagealert.server.events.ClientConnectionEvent;
import com.google.code.sagetvaddons.sagealert.server.events.ConflictStatusEvent;
import com.google.code.sagetvaddons.sagealert.server.events.FavEvent;
import com.google.code.sagetvaddons.sagealert.server.events.ImportCompletedEvent;
import com.google.code.sagetvaddons.sagealert.server.events.MediaFileDeletedEvent;
import com.google.code.sagetvaddons.sagealert.server.events.MediaFileDeletedUserEvent;
import com.google.code.sagetvaddons.sagealert.server.events.MediaFileEvent;
import com.google.code.sagetvaddons.sagealert.server.events.PlaylistEvent;
import com.google.code.sagetvaddons.sagealert.server.events.PluginEvent;
import com.google.code.sagetvaddons.sagealert.server.events.RecordingEvent;
import com.google.code.sagetvaddons.sagealert.server.events.SystemMessageEvent;
import com.google.code.sagetvaddons.sagealert.server.events.UnresolvedConflictStatusEvent;
import com.google.code.sagetvaddons.sagealert.shared.SageAlertEventMetadata;

/**
 * @author dbattams
 *
 */
public final class CoreEventsManager {
	static private final Logger LOG = Logger.getLogger(CoreEventsManager.class);
	static private final CoreEventsManager INSTANCE = new CoreEventsManager();
	static public final CoreEventsManager get() { return INSTANCE; }
	
	// This is the master list of core events; those with a double slash at the end have been implemented in SageAlert
	static final public String REC_STARTED = "RecordingStarted"; //
	static final public String REC_COMPLETED = "RecordingCompleted"; //
	static final public String REC_STOPPED = "RecordingStopped"; //
	static final public String PLUGINS_LOADED = "AllPluginsLoaded"; //
	static final public String CONFLICTS = "ConflictStatusChanged"; //
	static final public String UNRESOLVED_CONFLICTS = "UnresolvedConflicts"; //
	static final public String SYSMSG_POSTED = "SystemMessagePosted"; //
	static final public String INFO_SYSMSG_POSTED = "InfoSysMsgPosted"; //
	static final public String WARN_SYSMSG_POSTED = "WarnSysMsgPosted"; //
	static final public String ERROR_SYSMSG_POSTED = "ErrorSysMsgPosted"; //
	static final public String EPG_UPDATED = "EPGUpdateCompleted"; //
	static final public String PLAYBACK_STARTED = "PlaybackStarted"; //
	static final public String PLAYBACK_STOPPED = "PlaybackStopped"; //
	static final public String PLAYBACK_PAUSED = "PlaybackPaused"; //
	static final public String PLAYBACK_RESUMED = "PlaybackResumed"; //
	static final public String CLIENT_CONNECTED = "ClientConnected"; //
	static final public String CLIENT_DISCONNECTED = "ClientDisconnected"; //
	static final public String MEDIA_DELETED = "MediaFileRemoved"; //
	static final public String MEDIA_DELETED_LOW_SPACE = "Diskspace"; //
	static final public String MEDIA_DELETED_KEEP_AT_MOST = "KeepAtMost"; //
	static final public String MEDIA_DELETED_USER = "User"; //
	static final public String MEDIA_DELETED_VERIFY_FAILED = "VerifyFailed"; //
	static final public String MEDIA_DELETED_PARTIAL_OR_UNWANTED = "PartialOrUnwanted"; //
	static final public String MEDIA_DELETED_IMPORT_LOST = "ImportLost"; //
	static final public String PLUGIN_STARTED = "PluginStarted"; //
	static final public String MEDIA_FILE_IMPORTED = "MediaFileImported"; //
	static final public String IMPORT_STARTED = "ImportingStarted"; //
	static final public String IMPORT_COMPLETED = "ImportingCompleted"; //
	static final public String REC_SEG_ADDED = "RecordingSegmentAdded"; //
	static final public String PLUGIN_STOPPED = "PluginStopped"; //
	static final public String REC_SCHED_CHANGED = "RecordingScheduleChanged"; //
	static final public String PLAYBACK_FINISHED = "PlaybackFinished";
	static final public String FAV_ADDED = "FavoriteAdded"; //
	static final public String FAV_MODDED = "FavoriteModified"; //
	static final public String FAV_REMOVED = "FavoriteRemoved"; //
	static final public String PLAYLIST_ADDED = "PlaylistAdded"; //
	static final public String PLAYLIST_MODDED = "PlaylistModified"; //
	static final public String PLAYLIST_REMOVED = "PlaylistRemoved"; //
	
	static final public String PLAYLIST_ADDED_SUBJ = "A new playlist has been created by '$1.getAlias()'";
	static final public String PLAYLIST_ADDED_SHORT_MSG = "Playlist created by '$1.getAlias()': $0.GetName()";
	static final public String PLAYLIST_ADDED_MED_MSG = PLAYLIST_ADDED_SHORT_MSG;
	static final public String PLAYLIST_ADDED_LONG_MSG = PLAYLIST_ADDED_SHORT_MSG;

	static final public String PLAYLIST_MODDED_SUBJ = "A playlist has been modified by '$1.getAlias()'";
	static final public String PLAYLIST_MODDED_SHORT_MSG = "Playlist '$0.GetName()' modified by '$1.getAlias()'.";
	static final public String PLAYLIST_MODDED_MED_MSG = PLAYLIST_MODDED_SHORT_MSG;
	static final public String PLAYLIST_MODDED_LONG_MSG = PLAYLIST_MODDED_SHORT_MSG;

	static final public String PLAYLIST_REMOVED_SUBJ = "A playlist has been removed by '$1.getAlias()'";
	static final public String PLAYLIST_REMOVED_SHORT_MSG = "Playlist '$0.GetName()' removed by '$1.getAlias()'.";
	static final public String PLAYLIST_REMOVED_MED_MSG = PLAYLIST_REMOVED_SHORT_MSG;
	static final public String PLAYLIST_REMOVED_LONG_MSG = PLAYLIST_REMOVED_SHORT_MSG;

	static final public String FAV_ADDED_SUBJ = "A favourite has been added";
	static final public String FAV_ADDED_SHORT_MSG = "New favourite added: $0.GetFavoriteDescription()";
	static final public String FAV_ADDED_MED_MSG = FAV_ADDED_SHORT_MSG;
	static final public String FAV_ADDED_LONG_MSG = FAV_ADDED_SHORT_MSG;

	static final public String FAV_MODDED_SUBJ = "A favourite has been modified";
	static final public String FAV_MODDED_SHORT_MSG = "Favourite modified: $0.GetFavoriteDescription()";
	static final public String FAV_MODDED_MED_MSG = FAV_MODDED_SHORT_MSG;
	static final public String FAV_MODDED_LONG_MSG = FAV_MODDED_SHORT_MSG;

	static final public String FAV_REMOVED_SUBJ = "A favourite has been removed";
	static final public String FAV_REMOVED_SHORT_MSG = "Favourite removed: $0.GetFavoriteDescription()";
	static final public String FAV_REMOVED_MED_MSG = FAV_REMOVED_SHORT_MSG;
	static final public String FAV_REMOVED_LONG_MSG = FAV_REMOVED_SHORT_MSG;

	static final public String IMPORT_COMPLETED_SUBJ = "A media import scan has completed";
	static final public String IMPORT_COMPLETED_SHORT_MSG = "A media import scan has completed. [full reindex = $0.toString()]";
	static final public String IMPORT_COMPLETED_MED_MSG = IMPORT_COMPLETED_SHORT_MSG;
	static final public String IMPORT_COMPLETED_LONG_MSG = IMPORT_COMPLETED_SHORT_MSG;
	
	static final public String REC_SEG_ADDED_SUBJ = "A new recording segment has been started";
	static final public String REC_SEG_ADDED_SHORT_MSG = "Recording segment added: $0.GetMediaTitle()$utils.concatIfNotEmpty(\": \", $2.GetShowEpisode())";
	static final public String REC_SEG_ADDED_MED_MSG = REC_SEG_ADDED_SHORT_MSG;
	static final public String REC_SEG_ADDED_LONG_MSG = REC_SEG_ADDED_SHORT_MSG;
	
	static final public String IMPORT_STARTED_SUBJ = "A media import scan has started";
	static final public String IMPORT_STARTED_SHORT_MSG = "A media import scan has started.";
	static final public String IMPORT_STARTED_MED_MSG = IMPORT_STARTED_SHORT_MSG;
	static final public String IMPORT_STARTED_LONG_MSG = IMPORT_STARTED_SHORT_MSG;
	
	static final public String MEDIA_FILE_IMPORTED_SUBJ = "A new media file has been imported";
	static final public String MEDIA_FILE_IMPORTED_SHORT_MSG = "New media file imported: $0.GetMediaTitle()";
	static final public String MEDIA_FILE_IMPORTED_MED_MSG = MEDIA_FILE_IMPORTED_SHORT_MSG;
	static final public String MEDIA_FILE_IMPORTED_LONG_MSG = MEDIA_FILE_IMPORTED_SHORT_MSG;
	
	static final public String PLUGINS_LOADED_SUBJ = "All enabled plugins have been loaded";
	static final public String PLUGINS_LOADED_SHORT_MSG = "All enabled plugins have been loaded.";
	static final public String PLUGINS_LOADED_MED_MSG = PLUGINS_LOADED_SHORT_MSG;
	static final public String PLUGINS_LOADED_LONG_MSG = PLUGINS_LOADED_SHORT_MSG;
	
	static final public String PLUGIN_STARTED_SUBJ = "A plugin has been started";
	static final public String PLUGIN_STARTED_SHORT_MSG = "Plugin '$0.GetPluginName()' has been started.";
	static final public String PLUGIN_STARTED_MED_MSG = PLUGIN_STARTED_SHORT_MSG;
	static final public String PLUGIN_STARTED_LONG_MSG = PLUGIN_STARTED_SHORT_MSG;

	static final public String PLUGIN_STOPPED_SUBJ = "A plugin has been stopped";
	static final public String PLUGIN_STOPPED_SHORT_MSG = "Plugin '$0.GetPluginName()' has been stopped.";
	static final public String PLUGIN_STOPPED_MED_MSG = PLUGIN_STOPPED_SHORT_MSG;
	static final public String PLUGIN_STOPPED_LONG_MSG = PLUGIN_STOPPED_SHORT_MSG;

	static final public String CLIENT_CONNECTED_SUBJ = "New client connected to SageTV server";
	static final public String CLIENT_CONNECTED_SHORT_MSG = "Client '$0.getAlias()' has connected to the SageTV server.";
	static final public String CLIENT_CONNECTED_MED_MSG = CLIENT_CONNECTED_SHORT_MSG;
	static final public String CLIENT_CONNECTED_LONG_MSG = CLIENT_CONNECTED_SHORT_MSG;
	
	static final public String CLIENT_DISCONNECTED_SUBJ = "Client disconnected from SageTV server";
	static final public String CLIENT_DISCONNECTED_SHORT_MSG = "Client '$0.getAlias()' has disconnected from the SageTV server.";
	static final public String CLIENT_DISCONNECTED_MED_MSG = CLIENT_DISCONNECTED_SHORT_MSG;
	static final public String CLIENT_DISCONNECTED_LONG_MSG = CLIENT_DISCONNECTED_SHORT_MSG;

	static final public String REC_STARTED_SUBJ = "A new recording has started";
	static final public String REC_STARTED_SHORT_MSG = "A new recording has started: $0.GetMediaTitle()";
	static final public String REC_STARTED_MED_MSG = "A new recording has started: $0.GetMediaTitle()$utils.concatIfNotEmpty(\": \", $2.GetShowEpisode())";
	static final public String REC_STARTED_LONG_MSG = REC_STARTED_MED_MSG;

	static final public String REC_STOPPED_SUBJ = "A recording has stopped";
	static final public String REC_STOPPED_SHORT_MSG = "A recording has stopped: $0.GetMediaTitle()";
	static final public String REC_STOPPED_MED_MSG = "A recording has stopped: $0.GetMediaTitle()$utils.concatIfNotEmpty(\": \", $2.GetShowEpisode())";
	static final public String REC_STOPPED_LONG_MSG = REC_STOPPED_MED_MSG;

	static final public String REC_COMPLETED_SUBJ = "A recording has completed";
	static final public String REC_COMPLETED_SHORT_MSG = "A recording has completed: $0.GetMediaTitle()";
	static final public String REC_COMPLETED_MED_MSG = "A recording has completed: $0.GetMediaTitle()$utils.concatIfNotEmpty(\": \", $2.GetShowEpisode())";
	static final public String REC_COMPLETED_LONG_MSG = REC_COMPLETED_MED_MSG;

	static final public String EPG_UPDATED_SUBJ = "An EPG update has completed";
	static final public String EPG_UPDATED_SHORT_MSG = "An EPG update has completed.";
	static final public String EPG_UPDATED_MED_MSG = "An EPG update has completed successfully.";
	static final public String EPG_UPDATED_LONG_MSG = EPG_UPDATED_MED_MSG;

	static final public String CONFLICTS_SUBJ = "Conflict status changed in recording schedule";
	static final public String CONFLICTS_LONG_MSG = "The conflict status of your SageTV recording schedule has changed.  There are now $0.toString() conflict(s); $1.toString() are unresolved.";
	static final public String CONFLICTS_MED_MSG = "Conflict status change: $0.toString() total conflicts; $1.toString() unresolved.";
	static final public String CONFLICTS_SHORT_MSG = CONFLICTS_MED_MSG;

	static final public String UNRESOLVED_CONFLICTS_SUBJ = "There are unresolved conflicts in your recording schedule";
	static final public String UNRESOLVED_CONFLICTS_LONG_MSG = "The conflict status of your SageTV recording schedule has changed.  There are now $0.toString() conflict(s); $1.toString() are unresolved.";
	static final public String UNRESOLVED_CONFLICTS_MED_MSG = "Conflict status change: $0.toString() total conflicts; $1.toString() unresolved.";
	static final public String UNRESOLVED_CONFLICTS_SHORT_MSG = UNRESOLVED_CONFLICTS_MED_MSG;

	static final public String SYSMSG_POSTED_SUBJ = "New $utils.sysMsgLevelToString($0.GetSystemMessageLevel()) system message generated";
	static final public String SYSMSG_POSTED_LONG_MSG = "$0.GetSystemMessageString()";
	static final public String SYSMSG_POSTED_MED_MSG = "A new system message generated: $0.GetSystemMessageTypeName(); see SageTV server for details.";
	static final public String SYSMSG_POSTED_SHORT_MSG = SYSMSG_POSTED_MED_MSG;

	static final public String MEDIA_DELETED_LOW_SPACE_SUBJ = "Media file deleted (min space)";
	static final public String MEDIA_DELETED_LOW_SPACE_LONG_MSG = "The following media file was deleted because the minimum space setting was violated: $0.GetMediaTitle()/MediaID: $0.GetMediaFileID()/AiringID: $1.GetAiringID()/ShowEID: $2.GetShowExternalID()";
	static final public String MEDIA_DELETED_LOW_SPACE_MED_MSG = "File deleted (min space): $0.GetMediaTitle()/MediaID: $0.GetMediaFileID()/AiringID: $1.GetAiringID()/ShowEID: $2.GetShowExternalID()";
	static final public String MEDIA_DELETED_LOW_SPACE_SHORT_MSG = MEDIA_DELETED_LOW_SPACE_MED_MSG;

	static final public String MEDIA_DELETED_KEEP_AT_MOST_SUBJ = "Media file deleted (keep at most)";
	static final public String MEDIA_DELETED_KEEP_AT_MOST_LONG_MSG = "The following media file was deleted because the keep at most setting was exceeded: $0.GetMediaTitle()/MediaID: $0.GetMediaFileID()/AiringID: $1.GetAiringID()/ShowEID: $2.GetShowExternalID()";
	static final public String MEDIA_DELETED_KEEP_AT_MOST_MED_MSG = "File deleted (keep at most): $0.GetMediaTitle()/MediaID: $0.GetMediaFileID()/AiringID: $1.GetAiringID()/ShowEID: $2.GetShowExternalID()";
	static final public String MEDIA_DELETED_KEEP_AT_MOST_SHORT_MSG = "File deleted (keep at most): $0.GetMediaTitle()$utils.concatIfNotEmpty(\": \", $2.GetShowEpisode())";

	static final public String MEDIA_DELETED_USER_SUBJ = "Media file deleted by user '$3.getAlias()'";
	static final public String MEDIA_DELETED_USER_LONG_MSG = "File deleted by '$3.getAlias()': $0.GetMediaTitle()$utils.concatIfNotEmpty(\": \", $2.GetShowEpisode())/MediaID: $0.GetMediaFileID()/AiringID: $1.GetAiringID()/ShowEID: $2.GetShowExternalID()";
	static final public String MEDIA_DELETED_USER_MED_MSG = "File deleted by '$3.getAlias()': $0.GetMediaTitle()$utils.concatIfNotEmpty(\": \", $2.GetShowEpisode())/MediaID: $0.GetMediaFileID()/ShowEID: $2.GetShowExternalID()";
	static final public String MEDIA_DELETED_USER_SHORT_MSG = "File deleted by '$3.getAlias()': $0.GetMediaTitle()$utils.concatIfNotEmpty(\": \", $2.GetShowEpisode())";
	
	static final public String MEDIA_DELETED_VERIFY_FAILED_SUBJ = "Media file deleted (verify failed)";
	static final public String MEDIA_DELETED_VERIFY_FAILED_LONG_MSG = "The following media file was deleted because verification failed: $0.GetMediaTitle()/$0.GetMediaFileID()";
	static final public String MEDIA_DELETED_VERIFY_FAILED_MED_MSG = "File deleted (verify failed): $0.GetMediaTitle()/$0.GetMediaFileID()";
	static final public String MEDIA_DELETED_VERIFY_FAILED_SHORT_MSG = MEDIA_DELETED_VERIFY_FAILED_MED_MSG;

	static final public String MEDIA_DELETED_PARTIAL_OR_UNWANTED_SUBJ = "Media file deleted (partial/unwanted)";
	static final public String MEDIA_DELETED_PARTIAL_OR_UNWANTED_LONG_MSG = "The following media file was deleted because it is partial or unwanted: $0.GetMediaTitle()/MediaID: $0.GetMediaFileID()/AiringID: $1.GetAiringID()/ShowEID: $2.GetShowExternalID()";
	static final public String MEDIA_DELETED_PARTIAL_OR_UNWANTED_MED_MSG = "File deleted (partial/unwanted): $0.GetMediaTitle()/MediaID: $0.GetMediaFileID()/AiringID: $1.GetAiringID()/ShowEID: $2.GetShowExternalID()";
	static final public String MEDIA_DELETED_PARTIAL_OR_UNWANTED_SHORT_MSG = MEDIA_DELETED_PARTIAL_OR_UNWANTED_MED_MSG;

	static final public String MEDIA_DELETED_IMPORT_LOST_SUBJ = "Media file deleted (import lost)";
	static final public String MEDIA_DELETED_IMPORT_LOST_LONG_MSG = "The following media file was deleted because the import location was lost: $0.GetMediaTitle()/MediaID: $0.GetMediaFileID()";
	static final public String MEDIA_DELETED_IMPORT_LOST_MED_MSG = "File deleted (import lost): $0.GetMediaTitle()/$0.GetMediaFileID()";
	static final public String MEDIA_DELETED_IMPORT_LOST_SHORT_MSG = MEDIA_DELETED_IMPORT_LOST_MED_MSG;

	static final public String REC_SCHED_CHANGED_SUBJ = "Recording schedule updated";
	static final public String REC_SCHED_CHANGED_LONG_MSG = "The recording schedule has changed.";
	static final public String REC_SCHED_CHANGED_MED_MSG = REC_SCHED_CHANGED_LONG_MSG;
	static final public String REC_SCHED_CHANGED_SHORT_MSG = REC_SCHED_CHANGED_LONG_MSG;
	
	// If you change any of the defaults for PLAYBACK_* events below then you must also update them in ClientListenerSubscriptionForm
	
	static final public String PLAYBACK_TV_STARTED_SUBJ = "TV playback has started";
	static final public String PLAYBACK_TV_STARTED_LONG_MSG = "TV playback of '$0.GetMediaTitle()' has started on client '$3.getAlias()'";
	static final public String PLAYBACK_TV_STARTED_MED_MSG = "TV playback started ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_TV_STARTED_SHORT_MSG = PLAYBACK_TV_STARTED_MED_MSG;

	static final public String PLAYBACK_TV_STOPPED_SUBJ = "TV playback has stopped";
	static final public String PLAYBACK_TV_STOPPED_LONG_MSG = "TV playback of '$0.GetMediaTitle()' has stopped on client '$3.getAlias()'";
	static final public String PLAYBACK_TV_STOPPED_MED_MSG = "TV playback stopped ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_TV_STOPPED_SHORT_MSG = PLAYBACK_TV_STOPPED_MED_MSG;

	static final public String PLAYBACK_TV_PAUSED_SUBJ = "TV playback paused";
	static final public String PLAYBACK_TV_PAUSED_LONG_MSG = "TV playback of '$0.GetMediaTitle()' has been paused on client '$3.getAlias()'";
	static final public String PLAYBACK_TV_PAUSED_MED_MSG = "TV playback paused ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_TV_PAUSED_SHORT_MSG = PLAYBACK_TV_PAUSED_MED_MSG;

	static final public String PLAYBACK_TV_RESUMED_SUBJ = "TV playback has resumed";
	static final public String PLAYBACK_TV_RESUMED_LONG_MSG = "TV playback of '$0.GetMediaTitle()' has resumed on client '$3.getAlias()'";
	static final public String PLAYBACK_TV_RESUMED_MED_MSG = "TV playback resumed ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_TV_RESUMED_SHORT_MSG = PLAYBACK_TV_RESUMED_MED_MSG;

	static final public String PLAYBACK_IMPORT_STARTED_SUBJ = "Import playback has started";
	static final public String PLAYBACK_IMPORT_STARTED_LONG_MSG = "Import playback of '$0.GetMediaTitle()' has started on client '$3.getAlias()'";
	static final public String PLAYBACK_IMPORT_STARTED_MED_MSG = "Import playback started ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_IMPORT_STARTED_SHORT_MSG = PLAYBACK_IMPORT_STARTED_MED_MSG;

	static final public String PLAYBACK_IMPORT_STOPPED_SUBJ = "Import playback has stopped";
	static final public String PLAYBACK_IMPORT_STOPPED_LONG_MSG = "Import playback of '$0.GetMediaTitle()' has stopped on client '$3.getAlias()'";
	static final public String PLAYBACK_IMPORT_STOPPED_MED_MSG = "Import playback stopped ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_IMPORT_STOPPED_SHORT_MSG = PLAYBACK_IMPORT_STOPPED_MED_MSG;

	static final public String PLAYBACK_IMPORT_PAUSED_SUBJ = "Import playback paused";
	static final public String PLAYBACK_IMPORT_PAUSED_LONG_MSG = "Import playback of '$0.GetMediaTitle()' has been paused on client '$3.getAlias()'";
	static final public String PLAYBACK_IMPORT_PAUSED_MED_MSG = "Import playback paused ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_IMPORT_PAUSED_SHORT_MSG = PLAYBACK_IMPORT_PAUSED_MED_MSG;

	static final public String PLAYBACK_IMPORT_RESUMED_SUBJ = "Import playback has resumed";
	static final public String PLAYBACK_IMPORT_RESUMED_LONG_MSG = "Import playback of '$0.GetMediaTitle()' has resumed on client '$3.getAlias()'";
	static final public String PLAYBACK_IMPORT_RESUMED_MED_MSG = "Import playback resumed ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_IMPORT_RESUMED_SHORT_MSG = PLAYBACK_IMPORT_RESUMED_MED_MSG;

	static final public String PLAYBACK_DVD_STARTED_SUBJ = "DVD/BluRay playback has started";
	static final public String PLAYBACK_DVD_STARTED_LONG_MSG = "DVD/BluRay playback of '$0.GetMediaTitle()' has started on client '$3.getAlias()'";
	static final public String PLAYBACK_DVD_STARTED_MED_MSG = "DVD/BluRay playback started ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_DVD_STARTED_SHORT_MSG = PLAYBACK_DVD_STARTED_MED_MSG;

	static final public String PLAYBACK_DVD_STOPPED_SUBJ = "DVD/BluRay playback has stopped";
	static final public String PLAYBACK_DVD_STOPPED_LONG_MSG = "DVD/BluRay playback of '$0.GetMediaTitle()' has stopped on client '$3.getAlias()'";
	static final public String PLAYBACK_DVD_STOPPED_MED_MSG = "DVD/BluRay playback stopped ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_DVD_STOPPED_SHORT_MSG = PLAYBACK_DVD_STOPPED_MED_MSG;

	static final public String PLAYBACK_DVD_PAUSED_SUBJ = "DVD/BluRay playback paused";
	static final public String PLAYBACK_DVD_PAUSED_LONG_MSG = "DVD/BluRay playback of '$0.GetMediaTitle()' has been paused on client '$3.getAlias()'";
	static final public String PLAYBACK_DVD_PAUSED_MED_MSG = "DVD/BluRay playback paused ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_DVD_PAUSED_SHORT_MSG = PLAYBACK_DVD_PAUSED_MED_MSG;

	static final public String PLAYBACK_DVD_RESUMED_SUBJ = "DVD/BluRay playback has resumed";
	static final public String PLAYBACK_DVD_RESUMED_LONG_MSG = "DVD/BluRay playback of '$0.GetMediaTitle()' has resumed on client '$3.getAlias()'";
	static final public String PLAYBACK_DVD_RESUMED_MED_MSG = "DVD/BluRay playback resumed ($3.getAlias()): $0.GetMediaTitle()";
	static final public String PLAYBACK_DVD_RESUMED_SHORT_MSG = PLAYBACK_DVD_RESUMED_MED_MSG;

	static final public String PLAYBACK_MUSIC_STARTED_SUBJ = "Music playback has started";
	static final public String PLAYBACK_MUSIC_STARTED_LONG_MSG = "Music playback of '$0.GetMediaTitle()'$utils.concatIfNotEmpty(\" by \", $2.GetPeopleInShow()) has started on client '$3.getAlias()'";
	static final public String PLAYBACK_MUSIC_STARTED_MED_MSG = "Music playback started ($3.getAlias()): $0.GetMediaTitle()$utils.concatIfNotEmpty(\" by \", $2.GetPeopleInShow())";
	static final public String PLAYBACK_MUSIC_STARTED_SHORT_MSG = PLAYBACK_MUSIC_STARTED_MED_MSG;

	static final public String PLAYBACK_MUSIC_STOPPED_SUBJ = "Music playback has stopped";
	static final public String PLAYBACK_MUSIC_STOPPED_LONG_MSG = "Music playback of '$0.GetMediaTitle()'$utils.concatIfNotEmpty(\" by \", $2.GetPeopleInShow()) has stopped on client '$3.getAlias()'";
	static final public String PLAYBACK_MUSIC_STOPPED_MED_MSG = "Music playback stopped ($3.getAlias()): $0.GetMediaTitle()$utils.concatIfNotEmpty(\" by \", $2.GetPeopleInShow())";
	static final public String PLAYBACK_MUSIC_STOPPED_SHORT_MSG = PLAYBACK_MUSIC_STOPPED_MED_MSG;

	static final public String PLAYBACK_MUSIC_PAUSED_SUBJ = "Music playback paused";
	static final public String PLAYBACK_MUSIC_PAUSED_LONG_MSG = "Music playback of '$0.GetMediaTitle()'$utils.concatIfNotEmpty(\" by \", $2.GetPeopleInShow()) has been paused on client '$3.getAlias()'";
	static final public String PLAYBACK_MUSIC_PAUSED_MED_MSG = "Music playback paused ($3.getAlias()): $0.GetMediaTitle()$utils.concatIfNotEmpty(\" by \", $2.GetPeopleInShow())";
	static final public String PLAYBACK_MUSIC_PAUSED_SHORT_MSG = PLAYBACK_MUSIC_PAUSED_MED_MSG;

	static final public String PLAYBACK_MUSIC_RESUMED_SUBJ = "Music playback has resumed";
	static final public String PLAYBACK_MUSIC_RESUMED_LONG_MSG = "Music playback of '$0.GetMediaTitle()'$utils.concatIfNotEmpty(\" by \", $2.GetPeopleInShow()) has resumed on client '$3.getAlias()'";
	static final public String PLAYBACK_MUSIC_RESUMED_MED_MSG = "Music playback resumed ($3.getAlias()): $0.GetMediaTitle()$utils.concatIfNotEmpty(\" by \", $2.GetPeopleInShow())";
	static final public String PLAYBACK_MUSIC_RESUMED_SHORT_MSG = PLAYBACK_MUSIC_RESUMED_MED_MSG;

	private final SageTVPluginRegistry PLUGIN_REG = (SageTVPluginRegistry)API.apiNullUI.pluginAPI.GetSageTVPluginRegistry();
	private final CustomEventLoader CUSTOM_LOADER = new CustomEventLoader();
	
	private CoreEventsManager() {}
	
	public void init() {
		DataStore ds = DataStore.getInstance();
		SageAlertEventMetadataManager mgr = SageAlertEventMetadataManager.get();

		PLUGIN_REG.eventSubscribe(AppEventsListener.get(), PLUGINS_LOADED);
		LOG.info("Subscribed to " + PLUGINS_LOADED + " event!");
		
		PLUGIN_REG.eventSubscribe(CUSTOM_LOADER, PLUGIN_STARTED);
		LOG.info("Subscribed to " + PLUGIN_STARTED + " event!");
		
		PLUGIN_REG.eventSubscribe(AppEventsListener.get(), AppStartedEvent.EVENT_ID);
		mgr.putMetadata(new SageAlertEventMetadata(AppStartedEvent.EVENT_ID, "SageAlert App Started", "Event fired when SageAlert has successfully started.", new ArrayList<String>(), ds.getSetting(AppStartedEvent.EVENT_ID + SageAlertEventMetadata.SUBJ_SUFFIX, AppStartedEvent.SUBJ), ds.getSetting(AppStartedEvent.EVENT_ID + SageAlertEventMetadata.SHORT_SUFFIX, AppStartedEvent.SHORT_MSG), ds.getSetting(AppStartedEvent.EVENT_ID + SageAlertEventMetadata.MED_SUFFIX, AppStartedEvent.MED_MSG), ds.getSetting(AppStartedEvent.EVENT_ID + SageAlertEventMetadata.LONG_SUFFIX, AppStartedEvent.LONG_MSG)));
		LOG.info("Subscribed to " + AppStartedEvent.EVENT_ID + " event!");
		
		PLUGIN_REG.eventSubscribe(MediaFileEventsListener.get(), REC_STARTED);
		mgr.putMetadata(new SageAlertEventMetadata(REC_STARTED, "Recording Started", "Event fired when the SageTV system starts a recording.", Arrays.asList(RecordingEvent.EVENT_ARG_TYPES), ds.getSetting(REC_STARTED + SageAlertEventMetadata.SUBJ_SUFFIX, REC_STARTED_SUBJ), ds.getSetting(REC_STARTED + SageAlertEventMetadata.SHORT_SUFFIX, REC_STARTED_SHORT_MSG), ds.getSetting(REC_STARTED + SageAlertEventMetadata.MED_SUFFIX, REC_STARTED_MED_MSG), ds.getSetting(REC_STARTED + SageAlertEventMetadata.LONG_SUFFIX, REC_STARTED_LONG_MSG)));
		LOG.info("Subscribed to " + REC_STARTED + " event!");

		PLUGIN_REG.eventSubscribe(MediaFileEventsListener.get(), REC_STOPPED);
		mgr.putMetadata(new SageAlertEventMetadata(REC_STOPPED, "Recording Stopped", "Event fired when the SageTV system stops a recording for any other reason besides it being fully completed.", Arrays.asList(RecordingEvent.EVENT_ARG_TYPES), ds.getSetting(REC_STOPPED + SageAlertEventMetadata.SUBJ_SUFFIX, REC_STOPPED_SUBJ), ds.getSetting(REC_STOPPED + SageAlertEventMetadata.SHORT_SUFFIX, REC_STOPPED_SHORT_MSG), ds.getSetting(REC_STOPPED + SageAlertEventMetadata.MED_SUFFIX, REC_STOPPED_MED_MSG), ds.getSetting(REC_STOPPED + SageAlertEventMetadata.LONG_SUFFIX, REC_STOPPED_LONG_MSG)));
		LOG.info("Subscribed to " + REC_STOPPED + " event!");
		
		PLUGIN_REG.eventSubscribe(MediaFileEventsListener.get(), REC_COMPLETED);
		mgr.putMetadata(new SageAlertEventMetadata(REC_COMPLETED, "Recording Completed", "Event fired when the SageTV system completes a recording.", Arrays.asList(RecordingEvent.EVENT_ARG_TYPES), ds.getSetting(REC_COMPLETED + SageAlertEventMetadata.SUBJ_SUFFIX, REC_COMPLETED_SUBJ), ds.getSetting(REC_COMPLETED + SageAlertEventMetadata.SHORT_SUFFIX, REC_COMPLETED_SHORT_MSG), ds.getSetting(REC_COMPLETED + SageAlertEventMetadata.MED_SUFFIX, REC_COMPLETED_MED_MSG), ds.getSetting(REC_COMPLETED + SageAlertEventMetadata.LONG_SUFFIX, REC_COMPLETED_LONG_MSG)));
		LOG.info("Subscribed to " + REC_COMPLETED + " event!");
		
		PLUGIN_REG.eventSubscribe(AppEventsListener.get(), EPG_UPDATED);
		mgr.putMetadata(new SageAlertEventMetadata(EPG_UPDATED, "EPG Updated", "Event fired when the SageTV EPG data has been successfully updated.", new ArrayList<String>(), ds.getSetting(EPG_UPDATED + SageAlertEventMetadata.SUBJ_SUFFIX, EPG_UPDATED_SUBJ), ds.getSetting(EPG_UPDATED + SageAlertEventMetadata.SHORT_SUFFIX, EPG_UPDATED_SHORT_MSG), ds.getSetting(EPG_UPDATED + SageAlertEventMetadata.MED_SUFFIX, EPG_UPDATED_MED_MSG), ds.getSetting(EPG_UPDATED + SageAlertEventMetadata.LONG_SUFFIX, EPG_UPDATED_LONG_MSG)));
		LOG.info("Subscribed to " + EPG_UPDATED + " event!");
		
		PLUGIN_REG.eventSubscribe(ClientEventsListener.get(), CLIENT_CONNECTED);
		mgr.putMetadata(new SageAlertEventMetadata(CLIENT_CONNECTED, "Client Connected", "Event fired when a client, extender, or placeshifter connects to the server.", ClientConnectionEvent.EVENT_ARG_TYPES, ds.getSetting(CLIENT_CONNECTED + SageAlertEventMetadata.SUBJ_SUFFIX, CLIENT_CONNECTED_SUBJ), ds.getSetting(CLIENT_CONNECTED + SageAlertEventMetadata.SHORT_SUFFIX, CLIENT_CONNECTED_SHORT_MSG), ds.getSetting(CLIENT_CONNECTED + SageAlertEventMetadata.MED_SUFFIX, CLIENT_CONNECTED_MED_MSG), ds.getSetting(CLIENT_CONNECTED + SageAlertEventMetadata.LONG_SUFFIX, CLIENT_CONNECTED_LONG_MSG)));
		LOG.info("Subscribed to " + CLIENT_CONNECTED + " event!");
		
		PLUGIN_REG.eventSubscribe(ClientEventsListener.get(), CLIENT_DISCONNECTED);
		mgr.putMetadata(new SageAlertEventMetadata(CLIENT_DISCONNECTED, "Client Disconnected", "Event fired when a client, extender, or placeshifter disconnects from the server.", ClientConnectionEvent.EVENT_ARG_TYPES, ds.getSetting(CLIENT_DISCONNECTED + SageAlertEventMetadata.SUBJ_SUFFIX, CLIENT_DISCONNECTED_SUBJ), ds.getSetting(CLIENT_DISCONNECTED + SageAlertEventMetadata.SHORT_SUFFIX, CLIENT_DISCONNECTED_SHORT_MSG), ds.getSetting(CLIENT_DISCONNECTED + SageAlertEventMetadata.MED_SUFFIX, CLIENT_DISCONNECTED_MED_MSG), ds.getSetting(CLIENT_DISCONNECTED + SageAlertEventMetadata.LONG_SUFFIX, CLIENT_DISCONNECTED_LONG_MSG)));
		LOG.info("Subscribed to " + CLIENT_DISCONNECTED + " event!");
		
		PLUGIN_REG.eventSubscribe(PlaybackEventsListener.get(), PLAYBACK_STARTED);
		LOG.info("Subscribed to " + PLAYBACK_STARTED + " event!");
		
		PLUGIN_REG.eventSubscribe(PlaybackEventsListener.get(), PLAYBACK_STOPPED);
		LOG.info("Subscribed to " + PLAYBACK_STOPPED + " event!");

		if(!API.apiNullUI.configuration.GetProperty("version", "").startsWith("SageTV V7.1"))
			LOG.warn("Pause and Resume events will not work properly; requires SageTV 7.1 or newer!");
		
		PLUGIN_REG.eventSubscribe(PlaybackEventsListener.get(), PLAYBACK_PAUSED);
		LOG.info("Subscribed to " + PLAYBACK_PAUSED + " event!");

		PLUGIN_REG.eventSubscribe(PlaybackEventsListener.get(), PLAYBACK_RESUMED);
		LOG.info("Subscribed to " + PLAYBACK_RESUMED + " event!");
		
		PLUGIN_REG.eventSubscribe(SystemMessageEventsListener.get(), SYSMSG_POSTED);
		mgr.putMetadata(new SageAlertEventMetadata(INFO_SYSMSG_POSTED, "System Message Posted (INFO)", "Event fired when a system message with level INFO is posted.", Arrays.asList(SystemMessageEvent.ARG_TYPES), ds.getSetting(INFO_SYSMSG_POSTED + SageAlertEventMetadata.SUBJ_SUFFIX, SYSMSG_POSTED_SUBJ), ds.getSetting(INFO_SYSMSG_POSTED + SageAlertEventMetadata.SHORT_SUFFIX, SYSMSG_POSTED_SHORT_MSG), ds.getSetting(INFO_SYSMSG_POSTED + SageAlertEventMetadata.MED_SUFFIX, SYSMSG_POSTED_MED_MSG), ds.getSetting(INFO_SYSMSG_POSTED + SageAlertEventMetadata.LONG_SUFFIX, SYSMSG_POSTED_LONG_MSG)));
		mgr.putMetadata(new SageAlertEventMetadata(WARN_SYSMSG_POSTED, "System Message Posted (WARN)", "Event fired when a system message with level WARN is posted.", Arrays.asList(SystemMessageEvent.ARG_TYPES), ds.getSetting(WARN_SYSMSG_POSTED + SageAlertEventMetadata.SUBJ_SUFFIX, SYSMSG_POSTED_SUBJ), ds.getSetting(WARN_SYSMSG_POSTED + SageAlertEventMetadata.SHORT_SUFFIX, SYSMSG_POSTED_SHORT_MSG), ds.getSetting(WARN_SYSMSG_POSTED + SageAlertEventMetadata.MED_SUFFIX, SYSMSG_POSTED_MED_MSG), ds.getSetting(WARN_SYSMSG_POSTED + SageAlertEventMetadata.LONG_SUFFIX, SYSMSG_POSTED_LONG_MSG)));
		mgr.putMetadata(new SageAlertEventMetadata(ERROR_SYSMSG_POSTED, "System Message Posted (ERROR)", "Event fired when a system message with level ERROR is posted.", Arrays.asList(SystemMessageEvent.ARG_TYPES), ds.getSetting(ERROR_SYSMSG_POSTED + SageAlertEventMetadata.SUBJ_SUFFIX, SYSMSG_POSTED_SUBJ), ds.getSetting(ERROR_SYSMSG_POSTED + SageAlertEventMetadata.SHORT_SUFFIX, SYSMSG_POSTED_SHORT_MSG), ds.getSetting(ERROR_SYSMSG_POSTED + SageAlertEventMetadata.MED_SUFFIX, SYSMSG_POSTED_MED_MSG), ds.getSetting(ERROR_SYSMSG_POSTED + SageAlertEventMetadata.LONG_SUFFIX, SYSMSG_POSTED_LONG_MSG)));
		LOG.info("Subscribed to " + SYSMSG_POSTED + " event!");
		
		PLUGIN_REG.eventSubscribe(AppEventsListener.get(), CONFLICTS);
		mgr.putMetadata(new SageAlertEventMetadata(CONFLICTS, "Recording Conflicts", "Fired when the conflict status of your recording schedule changes.", Arrays.asList(ConflictStatusEvent.ARG_TYPES), ds.getSetting(CONFLICTS + SageAlertEventMetadata.SUBJ_SUFFIX, CONFLICTS_SUBJ), ds.getSetting(CONFLICTS + SageAlertEventMetadata.SHORT_SUFFIX, CONFLICTS_SHORT_MSG), ds.getSetting(CONFLICTS + SageAlertEventMetadata.MED_SUFFIX, CONFLICTS_MED_MSG), ds.getSetting(CONFLICTS + SageAlertEventMetadata.LONG_SUFFIX, CONFLICTS_LONG_MSG)));
		mgr.putMetadata(new SageAlertEventMetadata(UNRESOLVED_CONFLICTS, "Recording Conflicts (Unresolved)", "Fired when unresolved recording conflicts are detected in your recording schedule.", Arrays.asList(UnresolvedConflictStatusEvent.ARG_TYPES), ds.getSetting(UNRESOLVED_CONFLICTS + SageAlertEventMetadata.SUBJ_SUFFIX, UNRESOLVED_CONFLICTS_SUBJ), ds.getSetting(UNRESOLVED_CONFLICTS + SageAlertEventMetadata.SHORT_SUFFIX, UNRESOLVED_CONFLICTS_SHORT_MSG), ds.getSetting(UNRESOLVED_CONFLICTS + SageAlertEventMetadata.MED_SUFFIX, UNRESOLVED_CONFLICTS_MED_MSG), ds.getSetting(UNRESOLVED_CONFLICTS + SageAlertEventMetadata.LONG_SUFFIX, UNRESOLVED_CONFLICTS_LONG_MSG)));
		LOG.info("Subscribed to " + CONFLICTS + " event!");
		
		PLUGIN_REG.eventSubscribe(MediaDeletedEventsListener.get(), MEDIA_DELETED);
		mgr.putMetadata(new SageAlertEventMetadata(MEDIA_DELETED_LOW_SPACE, "Media Deleted (Low Space)", "Event fired when a media file is deleted by the core due to low disk space.", Arrays.asList(MediaFileDeletedEvent.ARG_TYPES), ds.getSetting(MEDIA_DELETED_LOW_SPACE + SageAlertEventMetadata.SUBJ_SUFFIX, MEDIA_DELETED_LOW_SPACE_SUBJ), ds.getSetting(MEDIA_DELETED_LOW_SPACE + SageAlertEventMetadata.SHORT_SUFFIX, MEDIA_DELETED_LOW_SPACE_SHORT_MSG), ds.getSetting(MEDIA_DELETED_LOW_SPACE + SageAlertEventMetadata.MED_SUFFIX, MEDIA_DELETED_LOW_SPACE_MED_MSG), ds.getSetting(MEDIA_DELETED_LOW_SPACE + SageAlertEventMetadata.LONG_SUFFIX, MEDIA_DELETED_LOW_SPACE_LONG_MSG)));
		mgr.putMetadata(new SageAlertEventMetadata(MEDIA_DELETED_KEEP_AT_MOST, "Media Delete (Keep at Most)", "Event fired when a media file is deleted by the core due to a keep at most rule.", Arrays.asList(MediaFileDeletedEvent.ARG_TYPES), ds.getSetting(MEDIA_DELETED_KEEP_AT_MOST + SageAlertEventMetadata.SUBJ_SUFFIX, MEDIA_DELETED_KEEP_AT_MOST_SUBJ), ds.getSetting(MEDIA_DELETED_KEEP_AT_MOST + SageAlertEventMetadata.SHORT_SUFFIX, MEDIA_DELETED_KEEP_AT_MOST_SHORT_MSG), ds.getSetting(MEDIA_DELETED_KEEP_AT_MOST + SageAlertEventMetadata.MED_SUFFIX, MEDIA_DELETED_KEEP_AT_MOST_MED_MSG), ds.getSetting(MEDIA_DELETED_KEEP_AT_MOST + SageAlertEventMetadata.LONG_SUFFIX, MEDIA_DELETED_KEEP_AT_MOST_LONG_MSG)));
		mgr.putMetadata(new SageAlertEventMetadata(MEDIA_DELETED_USER, "Media Deleted (User)", "Event fired when a user deletes a media file.", Arrays.asList(MediaFileDeletedUserEvent.ARG_TYPES), ds.getSetting(MEDIA_DELETED_USER + SageAlertEventMetadata.SUBJ_SUFFIX, MEDIA_DELETED_USER_SUBJ), ds.getSetting(MEDIA_DELETED_USER + SageAlertEventMetadata.SHORT_SUFFIX, MEDIA_DELETED_USER_SHORT_MSG), ds.getSetting(MEDIA_DELETED_USER + SageAlertEventMetadata.MED_SUFFIX, MEDIA_DELETED_USER_MED_MSG), ds.getSetting(MEDIA_DELETED_USER + SageAlertEventMetadata.LONG_SUFFIX, MEDIA_DELETED_USER_LONG_MSG)));
		mgr.putMetadata(new SageAlertEventMetadata(MEDIA_DELETED_VERIFY_FAILED, "Media Deleted (Verify Failed)", "Event fired when a media file is deleted by the core due to a verification failure.", Arrays.asList(MediaFileDeletedEvent.ARG_TYPES), ds.getSetting(MEDIA_DELETED_VERIFY_FAILED + SageAlertEventMetadata.SUBJ_SUFFIX, MEDIA_DELETED_VERIFY_FAILED_SUBJ), ds.getSetting(MEDIA_DELETED_VERIFY_FAILED + SageAlertEventMetadata.SHORT_SUFFIX, MEDIA_DELETED_VERIFY_FAILED_SHORT_MSG), ds.getSetting(MEDIA_DELETED_VERIFY_FAILED + SageAlertEventMetadata.MED_SUFFIX, MEDIA_DELETED_VERIFY_FAILED_MED_MSG), ds.getSetting(MEDIA_DELETED_VERIFY_FAILED + SageAlertEventMetadata.LONG_SUFFIX, MEDIA_DELETED_VERIFY_FAILED_LONG_MSG)));
		mgr.putMetadata(new SageAlertEventMetadata(MEDIA_DELETED_PARTIAL_OR_UNWANTED, "Media Deleted (Partial/Unwanted)", "Event fired when a media file is deleted by the core because it is a partial file or it's unwanted.", Arrays.asList(MediaFileDeletedEvent.ARG_TYPES), ds.getSetting(MEDIA_DELETED_PARTIAL_OR_UNWANTED + SageAlertEventMetadata.SUBJ_SUFFIX, MEDIA_DELETED_PARTIAL_OR_UNWANTED_SUBJ), ds.getSetting(MEDIA_DELETED_PARTIAL_OR_UNWANTED + SageAlertEventMetadata.SHORT_SUFFIX, MEDIA_DELETED_PARTIAL_OR_UNWANTED_SHORT_MSG), ds.getSetting(MEDIA_DELETED_PARTIAL_OR_UNWANTED + SageAlertEventMetadata.MED_SUFFIX, MEDIA_DELETED_PARTIAL_OR_UNWANTED_MED_MSG), ds.getSetting(MEDIA_DELETED_PARTIAL_OR_UNWANTED + SageAlertEventMetadata.LONG_SUFFIX, MEDIA_DELETED_PARTIAL_OR_UNWANTED_LONG_MSG)));
		mgr.putMetadata(new SageAlertEventMetadata(MEDIA_DELETED_IMPORT_LOST, "Media Deleted (Import Lost)", "Event fired when a media file is removed from the core database because the import dir was removed.", Arrays.asList(MediaFileDeletedEvent.ARG_TYPES), ds.getSetting(MEDIA_DELETED_IMPORT_LOST + SageAlertEventMetadata.SUBJ_SUFFIX, MEDIA_DELETED_IMPORT_LOST_SUBJ), ds.getSetting(MEDIA_DELETED_IMPORT_LOST + SageAlertEventMetadata.SHORT_SUFFIX, MEDIA_DELETED_IMPORT_LOST_SHORT_MSG), ds.getSetting(MEDIA_DELETED_IMPORT_LOST + SageAlertEventMetadata.MED_SUFFIX, MEDIA_DELETED_IMPORT_LOST_MED_MSG), ds.getSetting(MEDIA_DELETED_IMPORT_LOST + SageAlertEventMetadata.LONG_SUFFIX, MEDIA_DELETED_IMPORT_LOST_LONG_MSG)));
		LOG.info("Subscribed to " + MEDIA_DELETED + " event!");
		
		PLUGIN_REG.eventSubscribe(AppEventsListener.get(), REC_SCHED_CHANGED);
		mgr.putMetadata(new SageAlertEventMetadata(REC_SCHED_CHANGED, "Recording Schedule Changed", "Fired when the recording schedule has been modified.", new ArrayList<String>(), ds.getSetting(REC_SCHED_CHANGED + SageAlertEventMetadata.SUBJ_SUFFIX, REC_SCHED_CHANGED_SUBJ), ds.getSetting(REC_SCHED_CHANGED + SageAlertEventMetadata.SHORT_SUFFIX, REC_SCHED_CHANGED_SHORT_MSG), ds.getSetting(REC_SCHED_CHANGED + SageAlertEventMetadata.MED_SUFFIX, REC_SCHED_CHANGED_MED_MSG), ds.getSetting(REC_SCHED_CHANGED + SageAlertEventMetadata.LONG_SUFFIX, REC_SCHED_CHANGED_LONG_MSG)));
		LOG.info("Subscribed to " + REC_SCHED_CHANGED + " event!");
		
		PLUGIN_REG.eventSubscribe(PluginEventsListener.get(), PLUGIN_STARTED);
		mgr.putMetadata(new SageAlertEventMetadata(PLUGIN_STARTED, "Plugin Started", "Fires when the core starts a plugin (not including initial startup of all plugins).", PluginEvent.ARG_TYPES, ds.getSetting(PLUGIN_STARTED + SageAlertEventMetadata.SUBJ_SUFFIX, PLUGIN_STARTED_SUBJ), ds.getSetting(PLUGIN_STARTED + SageAlertEventMetadata.SHORT_SUFFIX, PLUGIN_STARTED_SHORT_MSG), ds.getSetting(PLUGIN_STARTED + SageAlertEventMetadata.MED_SUFFIX, PLUGIN_STARTED_MED_MSG), ds.getSetting(PLUGIN_STARTED + SageAlertEventMetadata.LONG_SUFFIX, PLUGIN_STARTED_LONG_MSG)));
		LOG.info("Subscribed to " + PLUGIN_STARTED + " event!");
		
		PLUGIN_REG.eventSubscribe(PluginEventsListener.get(), PLUGIN_STOPPED);
		mgr.putMetadata(new SageAlertEventMetadata(PLUGIN_STOPPED, "Plugin Stopped", "Fires when the core stops a plugin (not including shutdown of all plugins).", PluginEvent.ARG_TYPES, ds.getSetting(PLUGIN_STOPPED + SageAlertEventMetadata.SUBJ_SUFFIX, PLUGIN_STOPPED_SUBJ), ds.getSetting(PLUGIN_STOPPED + SageAlertEventMetadata.SHORT_SUFFIX, PLUGIN_STOPPED_SHORT_MSG), ds.getSetting(PLUGIN_STOPPED + SageAlertEventMetadata.MED_SUFFIX, PLUGIN_STOPPED_MED_MSG), ds.getSetting(PLUGIN_STOPPED + SageAlertEventMetadata.LONG_SUFFIX, PLUGIN_STOPPED_LONG_MSG)));
		LOG.info("Subscribed to " + PLUGIN_STOPPED + " event!");
		
		PLUGIN_REG.eventSubscribe(MediaFileEventsListener.get(), MEDIA_FILE_IMPORTED);
		mgr.putMetadata(new SageAlertEventMetadata(MEDIA_FILE_IMPORTED, "Media File Imported", "Fires when the core imports a new media file.", MediaFileEvent.ARG_TYPES, ds.getSetting(MEDIA_FILE_IMPORTED + SageAlertEventMetadata.SUBJ_SUFFIX, MEDIA_FILE_IMPORTED_SUBJ), ds.getSetting(MEDIA_FILE_IMPORTED + SageAlertEventMetadata.SHORT_SUFFIX, MEDIA_FILE_IMPORTED_SHORT_MSG), ds.getSetting(MEDIA_FILE_IMPORTED + SageAlertEventMetadata.MED_SUFFIX, MEDIA_FILE_IMPORTED_MED_MSG), ds.getSetting(MEDIA_FILE_IMPORTED + SageAlertEventMetadata.LONG_SUFFIX, MEDIA_FILE_IMPORTED_LONG_MSG)));
		LOG.info("Subscribed to " + MEDIA_FILE_IMPORTED + " event!");
		
		PLUGIN_REG.eventSubscribe(AppEventsListener.get(), IMPORT_STARTED);
		mgr.putMetadata(new SageAlertEventMetadata(IMPORT_STARTED, "Media Import Scan Started", "Fires when the core starts a media import scan.", new ArrayList<String>(), ds.getSetting(IMPORT_STARTED + SageAlertEventMetadata.SUBJ_SUFFIX, IMPORT_STARTED_SUBJ), ds.getSetting(IMPORT_STARTED + SageAlertEventMetadata.SHORT_SUFFIX, IMPORT_STARTED_SHORT_MSG), ds.getSetting(IMPORT_STARTED + SageAlertEventMetadata.MED_SUFFIX, IMPORT_STARTED_MED_MSG), ds.getSetting(IMPORT_STARTED + SageAlertEventMetadata.LONG_SUFFIX, IMPORT_STARTED_LONG_MSG)));
		LOG.info("Subscribed to " + IMPORT_STARTED + " event!");
		
		PLUGIN_REG.eventSubscribe(AppEventsListener.get(), IMPORT_COMPLETED);
		mgr.putMetadata(new SageAlertEventMetadata(IMPORT_COMPLETED, "Media Import Scan Completed", "Fires when the core completes a media import scan.", Arrays.asList(ImportCompletedEvent.ARG_TYPES), ds.getSetting(IMPORT_COMPLETED + SageAlertEventMetadata.SUBJ_SUFFIX, IMPORT_COMPLETED_SUBJ), ds.getSetting(IMPORT_COMPLETED + SageAlertEventMetadata.SHORT_SUFFIX, IMPORT_COMPLETED_SHORT_MSG), ds.getSetting(IMPORT_COMPLETED + SageAlertEventMetadata.MED_SUFFIX, IMPORT_COMPLETED_MED_MSG), ds.getSetting(IMPORT_COMPLETED + SageAlertEventMetadata.LONG_SUFFIX, IMPORT_COMPLETED_LONG_MSG)));
		LOG.info("Subscrbied to " + IMPORT_COMPLETED + " event!");
		
		PLUGIN_REG.eventSubscribe(MediaFileEventsListener.get(), REC_SEG_ADDED);
		mgr.putMetadata(new SageAlertEventMetadata(REC_SEG_ADDED, "Recording Segment Added", "Fires when a new segment is created for an active recording.", Arrays.asList(RecordingEvent.EVENT_ARG_TYPES), ds.getSetting(REC_SEG_ADDED + SageAlertEventMetadata.SUBJ_SUFFIX, REC_SEG_ADDED_SUBJ), ds.getSetting(REC_SEG_ADDED + SageAlertEventMetadata.SHORT_SUFFIX, REC_SEG_ADDED_SHORT_MSG), ds.getSetting(REC_SEG_ADDED + SageAlertEventMetadata.MED_SUFFIX, REC_SEG_ADDED_MED_MSG), ds.getSetting(REC_SEG_ADDED + SageAlertEventMetadata.LONG_SUFFIX, REC_SEG_ADDED_LONG_MSG)));
		LOG.info("Subscribed to " + REC_SEG_ADDED + " event!");
		
		PLUGIN_REG.eventSubscribe(FavEventsListener.get(), FAV_ADDED);
		mgr.putMetadata(new SageAlertEventMetadata(FAV_ADDED, "Favourite Added", "Fires when a new favourite is created.", Arrays.asList(FavEvent.ARG_TYPES), ds.getSetting(FAV_ADDED + SageAlertEventMetadata.SUBJ_SUFFIX, FAV_ADDED_SUBJ), ds.getSetting(FAV_ADDED + SageAlertEventMetadata.SHORT_SUFFIX, FAV_ADDED_SHORT_MSG), ds.getSetting(FAV_ADDED + SageAlertEventMetadata.MED_SUFFIX, FAV_ADDED_MED_MSG), ds.getSetting(FAV_ADDED + SageAlertEventMetadata.LONG_SUFFIX, FAV_ADDED_LONG_MSG)));
		LOG.info("Subscribed to " + FAV_ADDED + " event!");
		
		PLUGIN_REG.eventSubscribe(FavEventsListener.get(), FAV_MODDED);
		mgr.putMetadata(new SageAlertEventMetadata(FAV_MODDED, "Favourite Modified", "Fires when a favourite is modified.", Arrays.asList(FavEvent.ARG_TYPES), ds.getSetting(FAV_MODDED + SageAlertEventMetadata.SUBJ_SUFFIX, FAV_MODDED_SUBJ), ds.getSetting(FAV_MODDED + SageAlertEventMetadata.SHORT_SUFFIX, FAV_MODDED_SHORT_MSG), ds.getSetting(FAV_MODDED + SageAlertEventMetadata.MED_SUFFIX, FAV_MODDED_MED_MSG), ds.getSetting(FAV_MODDED + SageAlertEventMetadata.LONG_SUFFIX, FAV_MODDED_LONG_MSG)));
		LOG.info("Subscribed to " + FAV_MODDED + " event!");
	
		PLUGIN_REG.eventSubscribe(FavEventsListener.get(), FAV_REMOVED);
		mgr.putMetadata(new SageAlertEventMetadata(FAV_REMOVED, "Favourite Removed", "Fires when a favourite is removed.", Arrays.asList(FavEvent.ARG_TYPES), ds.getSetting(FAV_REMOVED + SageAlertEventMetadata.SUBJ_SUFFIX, FAV_REMOVED_SUBJ), ds.getSetting(FAV_REMOVED + SageAlertEventMetadata.SHORT_SUFFIX, FAV_REMOVED_SHORT_MSG), ds.getSetting(FAV_REMOVED + SageAlertEventMetadata.MED_SUFFIX, FAV_REMOVED_MED_MSG), ds.getSetting(FAV_REMOVED + SageAlertEventMetadata.LONG_SUFFIX, FAV_REMOVED_LONG_MSG)));
		LOG.info("Subscribed to " + FAV_REMOVED + " event!");
		
		PLUGIN_REG.eventSubscribe(PlaylistEventsListener.get(), PLAYLIST_ADDED);
		mgr.putMetadata(new SageAlertEventMetadata(PLAYLIST_ADDED, "Playlist Created", "Fires when a playlist is created.", Arrays.asList(PlaylistEvent.ARG_TYPES), ds.getSetting(PLAYLIST_ADDED + SageAlertEventMetadata.SUBJ_SUFFIX, PLAYLIST_ADDED_SUBJ), ds.getSetting(PLAYLIST_ADDED + SageAlertEventMetadata.SHORT_SUFFIX, PLAYLIST_ADDED_SHORT_MSG), ds.getSetting(PLAYLIST_ADDED + SageAlertEventMetadata.MED_SUFFIX, PLAYLIST_ADDED_MED_MSG), ds.getSetting(PLAYLIST_ADDED + SageAlertEventMetadata.LONG_SUFFIX, PLAYLIST_ADDED_LONG_MSG)));
		LOG.info("Subscribed to " + PLAYLIST_ADDED + " event!");

		PLUGIN_REG.eventSubscribe(PlaylistEventsListener.get(), PLAYLIST_MODDED);
		mgr.putMetadata(new SageAlertEventMetadata(PLAYLIST_MODDED, "Playlist Modified", "Fires when a playlist is modified.", Arrays.asList(PlaylistEvent.ARG_TYPES), ds.getSetting(PLAYLIST_MODDED + SageAlertEventMetadata.SUBJ_SUFFIX, PLAYLIST_MODDED_SUBJ), ds.getSetting(PLAYLIST_MODDED + SageAlertEventMetadata.SHORT_SUFFIX, PLAYLIST_MODDED_SHORT_MSG), ds.getSetting(PLAYLIST_MODDED + SageAlertEventMetadata.MED_SUFFIX, PLAYLIST_MODDED_MED_MSG), ds.getSetting(PLAYLIST_MODDED + SageAlertEventMetadata.LONG_SUFFIX, PLAYLIST_MODDED_LONG_MSG)));
		LOG.info("Subscribed to " + PLAYLIST_MODDED + " event!");

		PLUGIN_REG.eventSubscribe(PlaylistEventsListener.get(), PLAYLIST_REMOVED);
		mgr.putMetadata(new SageAlertEventMetadata(PLAYLIST_REMOVED, "Playlist Removed", "Fires when a playlist is removed.", Arrays.asList(PlaylistEvent.ARG_TYPES), ds.getSetting(PLAYLIST_REMOVED + SageAlertEventMetadata.SUBJ_SUFFIX, PLAYLIST_REMOVED_SUBJ), ds.getSetting(PLAYLIST_REMOVED + SageAlertEventMetadata.SHORT_SUFFIX, PLAYLIST_REMOVED_SHORT_MSG), ds.getSetting(PLAYLIST_REMOVED + SageAlertEventMetadata.MED_SUFFIX, PLAYLIST_REMOVED_MED_MSG), ds.getSetting(PLAYLIST_REMOVED + SageAlertEventMetadata.LONG_SUFFIX, PLAYLIST_REMOVED_LONG_MSG)));
		LOG.info("Subscribed to " + PLAYLIST_REMOVED + " event!");
	}
	
	public void destroy() {
		PLUGIN_REG.eventUnsubscribe(AppEventsListener.get(), PLUGINS_LOADED);
		LOG.info("Unsubscribed from " + PLUGINS_LOADED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(CUSTOM_LOADER, PLUGIN_STARTED);
		LOG.info("Unsubscribed from " + CUSTOM_LOADER + " event!");
		
		PLUGIN_REG.eventUnsubscribe(AppEventsListener.get(), AppStartedEvent.EVENT_ID);
		LOG.info("Unsubscrbied from " + AppStartedEvent.EVENT_ID + " event!");
		
		PLUGIN_REG.eventUnsubscribe(MediaFileEventsListener.get(), REC_STARTED);
		LOG.info("Unsubscribed from " + REC_STARTED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(MediaFileEventsListener.get(), REC_STOPPED);
		LOG.info("Unsubscribed from " + REC_STOPPED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(MediaFileEventsListener.get(), REC_COMPLETED);
		LOG.info("Unsubscribed from " + REC_COMPLETED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(AppEventsListener.get(), EPG_UPDATED);
		LOG.info("Unsubscribed from " + EPG_UPDATED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(ClientEventsListener.get(), CLIENT_CONNECTED);
		LOG.info("Unsubscribed from " + CLIENT_CONNECTED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(ClientEventsListener.get(), CLIENT_DISCONNECTED);
		LOG.info("Unsubscribed from " + CLIENT_DISCONNECTED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PlaybackEventsListener.get(), PLAYBACK_STARTED);
		LOG.info("Unsubscribed from " + PLAYBACK_STARTED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PlaybackEventsListener.get(), PLAYBACK_STOPPED);
		LOG.info("Unsubscribed from " + PLAYBACK_STOPPED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(SystemMessageEventsListener.get(), SYSMSG_POSTED);
		LOG.info("Unsubscribed from " + SYSMSG_POSTED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(AppEventsListener.get(), CONFLICTS);
		LOG.info("Unsubscribed from " + CONFLICTS + " event!");
		
		PLUGIN_REG.eventUnsubscribe(MediaDeletedEventsListener.get(), MEDIA_DELETED);
		LOG.info("Unsubscribed from " + MEDIA_DELETED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(AppEventsListener.get(), REC_SCHED_CHANGED);
		LOG.info("Unsubscribed from " + REC_SCHED_CHANGED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PluginEventsListener.get(), PLUGIN_STARTED);
		LOG.info("Unsubscribed from " + PLUGIN_STARTED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PluginEventsListener.get(), PLUGIN_STOPPED);
		LOG.info("Unsubscribed from " + PLUGIN_STOPPED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(MediaFileEventsListener.get(), MEDIA_FILE_IMPORTED);
		LOG.info("Unsubscribed from " + MEDIA_FILE_IMPORTED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(AppEventsListener.get(), IMPORT_STARTED);
		LOG.info("Unsubscribed from " + IMPORT_STARTED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(AppEventsListener.get(), IMPORT_COMPLETED);
		LOG.info("Unsubscribed from " + IMPORT_COMPLETED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(MediaFileEventsListener.get(), REC_SEG_ADDED);
		LOG.info("Unsubscribed from " + REC_SEG_ADDED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(FavEventsListener.get(), FAV_ADDED);
		LOG.info("Unsubscribed from " + FAV_ADDED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(FavEventsListener.get(), FAV_MODDED);
		LOG.info("Unsubscribed from " + FAV_MODDED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(FavEventsListener.get(), FAV_REMOVED);
		LOG.info("Unsubscrbied from " + FAV_REMOVED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PlaylistEventsListener.get(), PLAYLIST_ADDED);
		LOG.info("Unsubscribed from " + PLAYLIST_ADDED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PlaylistEventsListener.get(), PLAYLIST_REMOVED);
		LOG.info("Unsubscribed from " + PLAYLIST_REMOVED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PlaylistEventsListener.get(), PLAYLIST_MODDED);
		LOG.info("Unsubscribed from " + PLAYLIST_MODDED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PlaybackEventsListener.get(), PLAYBACK_PAUSED);
		LOG.info("Unsubscribed from " + PLAYBACK_PAUSED + " event!");
		
		PLUGIN_REG.eventUnsubscribe(PlaybackEventsListener.get(), PLAYBACK_RESUMED);
		LOG.info("Unsubscribed from " + PLAYBACK_RESUMED + " event!");
	}
}
