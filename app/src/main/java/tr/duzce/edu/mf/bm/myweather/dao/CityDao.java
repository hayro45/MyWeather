package tr.duzce.edu.mf.bm.myweather.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tr.duzce.edu.mf.bm.myweather.Model.City;

@Dao
public interface CityDao {

    @Insert
    void insert(City city);

    @Query("SELECT * FROM cities")
    List<City> getAllCities();

    @Query("DELETE FROM cities WHERE cityName = :cityName")
    void deleteCityByName(String cityName);
}
