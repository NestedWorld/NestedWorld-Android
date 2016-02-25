package com.nestedworld.nestedworld.api.models.apiResponse.monsters;


import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.models.Monster;

import java.util.ArrayList;

public class MonstersResponse {
    @Expose
    public ArrayList<Monster> monsters;
}
