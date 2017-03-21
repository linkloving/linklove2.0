package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Greendao {
    public static void main(String[] args)throws  Exception{
        Schema schema = new Schema(11, "Trace.GreenDao");
        schema.enableActiveEntitiesByDefault();
        schema.enableKeepSectionsByDefault();
        addNote(schema) ;

        new DaoGenerator().generateAll(schema, "/datang/linklovingApp/app/src/main/java-gen");
    }

    public static void addNote(Schema schema){
        Entity note = schema.addEntity("Note");
        note.addIdProperty().autoincrement();
        note.addStringProperty("date");
        note.addDateProperty("startDate");
        note.addDateProperty("runDate");
        note.addIntProperty("type");
        note.addDoubleProperty("latitude");
        note.addDoubleProperty("longitude");

        Entity heartrate = schema.addEntity("heartrate");
        heartrate.addIdProperty().autoincrement();
        heartrate.addIntProperty("startTime");
        heartrate.addIntProperty("max");
        heartrate.addIntProperty("avg");

    }
}
