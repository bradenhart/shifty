package io.bradenhart.shifty.adapter;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by bradenhart on 2/04/17.
 */

public class MySectionAdapter extends SectionedRecyclerViewAdapter {

    public MySectionAdapter() {
        super();
    }

    public void addSectionAtStart(String tag, Section section) {
        LinkedHashMap<String, Section> currentSections = super.getSectionsMap();
//        LinkedHashMap<String, Section> newSections = new LinkedHashMap<>();

        super.removeAllSections();
//        super.addSection(tag, section);

        for (Map.Entry<String, Section> entry : currentSections.entrySet()) {
            Log.e("TAG", entry.getKey());
            super.addSection(entry.getValue());
        }

//        super.notifyDataSetChanged();

    }


}
