package org.ccci.gto.android.common.util.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ViewUtilsTest {
    private static final Exception[] TOUCH_EXCEPTIONS_TO_SUPPRESS = {
            new IllegalArgumentException("pointerIndex out of range"),
            new IllegalArgumentException("pointerIndex out of range pointerIndex=-1 pointerCount=1"),
            new IllegalArgumentException(
                    "invalid pointerIndex -1 for MotionEvent { action=MOVE, id[0]=1, x[0]=281.459, y[0]=212.818, " +
                            "historySize=2, eventTime=22300703589796, downTime=22299744899000, deviceId=3, " +
                            "source=TOUCHSCREEN, displayId=0, eventId=313846789}"
            )
    };

    private static final Exception[] TOUCH_EXCEPTIONS_TO_PROPAGATE = {
            new RuntimeException(),
            new IllegalArgumentException()
    };

    @Test
    public void verifyHandleOnInterceptTouchEventExceptionSuppressed() throws Exception {
        for (final Exception e : TOUCH_EXCEPTIONS_TO_SUPPRESS) {
            assertFalse(ViewUtils.handleOnInterceptTouchEventException(e));
        }
    }

    @Test
    public void verifyHandleOnInterceptTouchEventExceptionPropagated() throws Exception {
        for (final Exception e : TOUCH_EXCEPTIONS_TO_PROPAGATE) {
            try {
                ViewUtils.handleOnInterceptTouchEventException(e);
                fail();
            } catch (Exception thrown) {
                assertEquals(e, thrown);
            }
        }
    }

    @Test
    public void verifyHandleOnTouchEventExceptionSuppressed() throws Exception {
        for (final Exception e : TOUCH_EXCEPTIONS_TO_SUPPRESS) {
            assertTrue(ViewUtils.handleOnTouchEventException(e));
        }
    }

    @Test
    public void verifyHandleOnTouchEventExceptionPropagated() throws Exception {
        for (final Exception e : TOUCH_EXCEPTIONS_TO_PROPAGATE) {
            try {
                ViewUtils.handleOnTouchEventException(e);
                fail();
            } catch (Exception thrown) {
                assertEquals(e, thrown);
            }
        }
    }
}
