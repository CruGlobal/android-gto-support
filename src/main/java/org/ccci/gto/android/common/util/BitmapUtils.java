package org.ccci.gto.android.common.util;

import org.ccci.gto.android.common.model.Dimension;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BitmapUtils {
    public static int calcScale(final Dimension size, final Dimension fullSize) {
        return calcScale(size, fullSize, null);
    }

    public static int calcScale(final Dimension size, final Dimension fullSize, final Dimension maxSize) {
        int scale = 1;

        // calculate min scale if a max size was specified & applies
        if (maxSize != null && (fullSize.width > scale * maxSize.width || fullSize.height > scale * maxSize.height)) {
            final BigDecimal widthScale = maxSize.width > 0 ?
                    new BigDecimal(fullSize.width).divide(new BigDecimal(maxSize.width), RoundingMode.CEILING) :
                    new BigDecimal(1);
            final BigDecimal heightScale = maxSize.height > 0 ?
                    new BigDecimal(fullSize.height).divide(new BigDecimal(maxSize.height), RoundingMode.CEILING) :
                    new BigDecimal(1);
            scale = Math.max(scale, widthScale.max(heightScale).intValue());
        }

        // calculate target scale
        if (size != null && (fullSize.width > scale * size.width || fullSize.height > scale * size.height)) {
            final BigDecimal widthScale = size.width > 0 ?
                    new BigDecimal(fullSize.width).divide(new BigDecimal(size.width), RoundingMode.FLOOR) :
                    new BigDecimal(1);
            final BigDecimal heightScale = size.height > 0 ?
                    new BigDecimal(fullSize.height).divide(new BigDecimal(size.height), RoundingMode.FLOOR) :
                    new BigDecimal(1);
            scale = Math.max(scale, widthScale.min(heightScale).intValue());
        }

        return scale;
    }
}
