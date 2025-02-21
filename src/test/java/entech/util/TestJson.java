package entech.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.StringReader;
import java.util.Map;
import org.junit.jupiter.api.Test;
import frc.entech.util.MapPresetReader;


class TestJson {
    @Test
    void testGson(){

        String jsonString = "{ \"speed\": 0.85, \"turnRate\": 0.5, \"armPosition\": 45.0 }";
        StringReader sr = new StringReader(jsonString);
        Map<String,Double> data = MapPresetReader.readPresets(sr);
        assertEquals(0.85, data.get("speed"), 0.00001);
   }
}
