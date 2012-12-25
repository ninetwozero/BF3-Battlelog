package com.ninetwozero.bf3droid.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.provider.table.Personas;

public class PersonasDAO {

    public static SimplePersona simplePersonaFrom(Cursor cursor){
        long personaId = cursor.getLong(cursor.getColumnIndex(Personas.Columns.PERSONA_ID));
        String personaName = cursor.getString(cursor.getColumnIndex(Personas.Columns.NAME));
        String platform = cursor.getString(cursor.getColumnIndex(Personas.Columns.PLATFORM));
        String image = cursor.getString(cursor.getColumnIndex(Personas.Columns.IMAGE));
        return new SimplePersona(personaName, personaId, platform, image);
    }

    public static ContentValues simplePersonaToDB(SimplePersona persona, long userId){
        ContentValues values = new ContentValues();
        values.put(Personas.Columns.PERSONA_ID, persona.getPersonaId());
        values.put(Personas.Columns.USER_ID, userId);
        values.put(Personas.Columns.NAME, persona.getPersonaName());
        values.put(Personas.Columns.PLATFORM, persona.getPlatform());
        values.put(Personas.Columns.IMAGE, persona.getPersonaImage());
        return values;
    }
}
