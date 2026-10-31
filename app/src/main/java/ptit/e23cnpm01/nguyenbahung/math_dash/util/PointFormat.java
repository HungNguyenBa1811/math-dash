package ptit.e23cnpm01.nguyenbahung.math_dash.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

// Điểm: 13 → "13", 12.5 → "12,5"; giây: 44 → "44,0", 38.24 → "38,2"
public final class PointFormat {
    private static final DecimalFormatSymbols VI = new DecimalFormatSymbols(new Locale("vi", "VN"));
    private static final DecimalFormat FORMAT = new DecimalFormat("0.#", VI);
    private static final DecimalFormat SECONDS = new DecimalFormat("0.0", VI);

    private PointFormat() {
    }

    public static String format(double points) {
        return FORMAT.format(points);
    }

    public static String formatSeconds(double seconds) {
        return SECONDS.format(Math.round(seconds * 10) / 10.0);
    }
}
