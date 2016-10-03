package com.pdumanager.slawek.pdumanager;

/**
 * Created by slawek on 08.09.16.
 */
public class Constants {
        public static final String USERNAME_KEY = "username";
        //url Slawka
        public static final String PDU_MANAGER_URL = "http://192.168.5.116:8000";

        //url Matiego
        //public static final String PDU_MANAGER_URL = "http://192.168.5.116:8000";

        //url Kacpra
        //public static final String PDU_MANAGER_URL = "http://192.168.0.101:8000";
        //public static final String PDU_MANAGER_URL = "http://192.168.1.143:8000";
        public static final String GROUPS_URL = PDU_MANAGER_URL + "/api/groups";
        public static final String MY_GROUPS_URL = PDU_MANAGER_URL + "/api/group/get_user_groups";
}
