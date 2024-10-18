package tr.duzce.edu.mf.bm.myweather.Model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "cities")
public class City {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String cityName;
    public double latitude;
    public double longitude;
}
