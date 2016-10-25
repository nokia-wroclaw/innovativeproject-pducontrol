package com.pdumanager.slawek.pdumanager;

/**
 * Created by slawek on 08.09.16.
 */
public class Constants {
        //url Slawka
        //praca
        //public static final String PDU_MANAGER_URL = "http://192.168.5.116:8000";
        //dom
        public static final String PDU_MANAGER_URL = "http://192.168.8.102:8000";

        //url Matiego
        //public static final String PDU_MANAGER_URL = "http://192.168.1.30:3500";

        //url Kacpra
        //public static final String PDU_MANAGER_URL = "http://192.168.0.101:8000";
        //public static final String PDU_MANAGER_URL = "http://192.168.1.143:8000";
        //public static final String PDU_MANAGER_URL = "http://172.16.12.217:8000";

        public static final String GROUPS_URL = PDU_MANAGER_URL + "/api/groups/";
        public static final String MY_GROUPS_URL = PDU_MANAGER_URL + "/api/group/get_user_groups/?username=";
        public static final String LOGIN_URL = PDU_MANAGER_URL + "/api/login_by_rest/?";
        public static final String DEVICES_URL = PDU_MANAGER_URL + "/api/pdus/";
        public static final String OUTLETS_URL = PDU_MANAGER_URL + "/api/pdu_outlets/";
        public static final String IMPORT_GROUP_URL = PDU_MANAGER_URL + "/api/group/mobile_edit_user_groups/?username=";
        public static final String SWITCH_ON = PDU_MANAGER_URL + "/pdu_communicator/switch_outlet_on/";
        public static final String SWITCH_OFF = PDU_MANAGER_URL + "/pdu_communicator/switch_outlet_off/";
        public static final String RESET = PDU_MANAGER_URL + "/pdu_communicator/reset_outlet/";
        public static final String CHECK_STATE = PDU_MANAGER_URL + "/pdu_communicator/check_state/";
}
