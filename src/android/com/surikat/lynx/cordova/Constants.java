package com.ptvag.navigation.ritest;

public class Constants {

	// message IDs
		public static final int MSG_RI_ADD_STATION = 100;
		public static final int MSG_RI_DELETE_ALL_STATIONS = 101;
		static final int MSG_RI_SKIP_STATION = 102;
		
		static final int MSG_RI_ADD_VALUE_BY_KEY = 110;
		static final int MSG_RI_GET_VALUE_BY_KEY = 111;
		
		static final int MSG_RI_SET_USERHINT_TTS = 120;
		static final int MSG_RI_SET_USERHINT_BIG_SIGN = 121;
		static final int MSG_RI_SET_USERHINT_SMALL_SIGN = 122;		
		static final int MSG_RI_SHOW_MODAL = 123;
		
		static final int MSG_RI_SEND_LOCATION = 203;
		static final int MSG_RI_SET_LOCATION_FROM_RI = 204;
		static final int MSG_RI_SET_LOCATION_FROM_DEVICE = 205;
		
		public static final int MSG_RI_START_NAVIGATION = 300;
		static final int MSG_RI_LOAD_BCR = 304;
		public static final int MSG_RI_STOP_NAVIGATION = 305;
		static final int MSG_RI_DOWNLOAD_BCR = 306;
		public final int MSG_RI_DOWNLOAD_AND_START_BCR = 307;
		static final int MSG_RI_GET_ROUTE_TRACE_AHEAD = 308;	
		static final int MSG_RI_SET_ENSURE_NAVIGATION = 309;
		static final int MSG_RI_GET_CURRENT_TOUR = 310;

		static final int MSG_RI_QUERY_PROFILES = 400;
		static final int MSG_RI_GET_CURRENT_PROFILE = 401;
		public static final int MSG_RI_SET_CURRENT_PROFILE = 402;
		
		static final int MSG_RI_QUERY_MAP_POSITION = 410;
		
		public static final int MSG_RI_GEOCODE = 500;
		static final int MSG_RI_INVGEOCODE = 501;
		
		public static final int MSG_RI_GO_TO_MAIN = 600;
		static final int MSG_RI_BRING_NAVIGATOR_TO_FRONT = 601;
		
		static final int MSG_RI_REGISTER_EVENT_LISTENER = 701;
		static final int MSG_RI_UNREGISTER_EVENT_LISTENER = 702;
		static final int MSG_RI_EVENT_DATA = 703;
		static final int MSG_RI_REGISTER_ROUTE_CORRIDOR_PASSED_LISTENER = 704;
		static final int MSG_RI_UNREGISTER_ROUTE_CORRIDOR_PASSED_LISTENER = 705;			
		static final int MSG_RI_SHOW_ASSISTANT = 800;
		
		static final int MSG_RI_ADD_BUDDY = 900;
		static final int MSG_RI_DELETE_BUDDY = 901;
		static final int MSG_RI_POSITION_BUDDY = 902;
		static final int MSG_RI_ADD_BUDDY_ALERT = 903;
		static final int MSG_RI_DELETE_BUDDY_ALERT = 904;	
		
		static final int MSG_RI_GET_DEVICE_ID = 1000;
		static final int MSG_RI_GET_NAVIGATOR_VERSION = 1001;
		static final int MSG_RI_GET_INSTALLED_FEATURES = 1002;
		static final int MSG_RI_GET_USER_ID = 1003;
		static final int MSG_RI_GET_LANGUAGE = 1004;
		static final int MSG_RI_GET_SPEAKER = 1005;
		static final int MSG_RI_GET_FREE_SPACE = 1006;
		static final int MSG_RI_CHECK_UP_TO_DATE = 1007;
		static final int MSG_RI_GET_IMEI = 1008;

		static final int MSG_RI_SET_WLAN_USAGE_ALLOWED = 1100;
		
		// error codes
		public static final int RI_ERROR_NONE = 0;
		static final int RI_ERROR_NOT_IMPLEMENTED = 1;
		static final int RI_ERROR_LONGTIME_OPERATION_IN_PROGRESS = 2;
		static final int RI_ERROR_FAILED = 3;
		static final int RI_ERROR_NOT_INITIALIZED = 4;
		static final int RI_ERROR_NOT_ALLOWED = 5;
		static final int RI_ERROR_IS_INITIALIZING = 6;
		static final int RI_ERROR_RI_DISABLED = 7;		
		static final int RI_ERROR_OPERATION_NOT_ALLOWED = 100;
		static final int RI_ERROR_NO_SUCH_PROFILE = 101;
		static final int RI_ERROR_NO_FILE_FOUND = 102;
		static final int RI_ERROR_NO_RESULT_FOUND = 103;
		static final int RI_BCR_ERROR = 104;
		static final int RI_ROUTE_CALC_ERROR = 105;
		static final int RI_ERROR_NO_STREET_FOUND = 106;
		static final int RI_ERROR_NO_SUCH_KEY = 107;		
		static final int RI_ERROR_INVGEOCODE_FAILED = 108;
		static final int RI_ERROR_NOT_DURING_NAVIGATION = 109;
		static final int RI_ERROR_NOT_IN_THIS_ACTIVITY = 110;
		static final int RI_ERROR_SEND_LOCATION_NOT_ALLOWED = 111;		
		static final int RI_ERROR_UNSUPPORTED_EVENT_TYPE = 112;		
		static final int RI_ERROR_INVALID_PARAMETER = 113;
		static final int RI_ERROR_NO_ROUTE_FOUND = 114;
		static final int RI_ERROR_DIALOG_DISMISSED = 120;
		static final int RI_ERROR_ROUTE_CALC_IDENTICAL_START_AND_END = 200;
		static final int RI_ERROR_ROUTE_CALC_NO_TARGET = 201;
		
		public static final int RI_WARNING_ALREADY_IN_MAIN_ACTIVITY = 100000;
		
		static final int EVENT_GPS_INFO = 1;
		static final int EVENT_NAVIGATION_INFO = 2;
		static final int EVENT_SPEEDLIMIT = 3;
		static final int EVENT_DESTINATION_REACHED = 4;
		static final int EVENT_NAVIGATION_STOPPED = 5;
		static final int EVENT_REROUTE = 6;
		static final int EVENT_AREA_ALERT = 7;
		static final int EVENT_BORDER_CROSSED = 8;
		static final int EVENT_ROUTE_CORRIDOR_CROSSED = 9;
		static final int EVENT_BUDDY_ALERT = 10;

		static final int EVENT_CURRENT_TOUR_ADDED_STATION = 11;
		static final int EVENT_CURRENT_TOUR_INSERTED_STATION = 12;
		static final int EVENT_CURRENT_TOUR_DELETED_STATION = 13;
		static final int EVENT_CURRENT_TOUR_MOVED_STATION = 14;
		public static int EVENT_CURRENT_TOUR_CHANGED_STATION = 15;
		public static int EVENT_CURRENT_TOUR_CHANGED_STATUS_OF_STATION = 16;

		public static final int EARTH_RADIUS = 6371000;

}

