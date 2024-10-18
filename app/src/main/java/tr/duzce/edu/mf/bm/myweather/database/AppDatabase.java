package tr.duzce.edu.mf.bm.myweather.database;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import tr.duzce.edu.mf.bm.myweather.Model.City;
import tr.duzce.edu.mf.bm.myweather.dao.CityDao;

@Database(entities = {City.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CityDao cityDao();
}