package ptit.e23cnpm01.nguyenbahung.math_dash.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Hiển thị điểm theo kiểu Việt Nam: 13 → "13", 12.5 → "12,5".
 */
public final class PointFormat {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.#", new DecimalFormatSymbols(new Locale("vi", "VN")));

    private PointFormat() {
    }

    public static String format(double points) {
        return FORMAT.format(points);
    }
}
