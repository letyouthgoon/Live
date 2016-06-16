/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */

package com.showworld.live.main.module;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BasePojo implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("code")
    public int code;

}
