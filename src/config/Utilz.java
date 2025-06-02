package config;

import java.text.NumberFormat;
import java.util.Locale;

public class Utilz {
    public static String convertRupiah(Double input) {
        Locale localId = new Locale("in", "ID");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(localId);
        String strFormat = formatter.format(input);
        return strFormat;
    }
}
