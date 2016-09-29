package com.pdumanager.slawek.pdumanager.model;

import java.io.Serializable;

/**
 * Created by kujvinho on 28.09.16.
 */

public class Result implements Serializable {
    public int[] outlets_in_group;
    public String group_name;
    public int group_id;
    public int[] pdus_in_group;
}
