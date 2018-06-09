package com.example.okmac.greymap.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.okmac.greymap.fragments.GeoTag;

import java.util.List;


@Dao
public interface GeoTagDao {

    @Insert
    void insert(GeoTag tag);

    @Query("SELECT * from geo_tag_table")
    List<GeoTag> getAllTags();
}