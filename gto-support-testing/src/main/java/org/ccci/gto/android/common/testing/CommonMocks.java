package org.ccci.gto.android.common.testing;

import android.text.TextUtils;
import android.util.Pair;

import com.google.common.base.Joiner;

import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

public class CommonMocks {
    public static void mockTextUtils() throws Exception {
        mockStatic(TextUtils.class);

        when(TextUtils.join(any(CharSequence.class), any(Iterable.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Joiner.on(invocation.getArgumentAt(0, CharSequence.class).toString())
                        .join(invocation.getArgumentAt(1, Iterable.class));
            }
        });
        when(TextUtils.join(any(CharSequence.class), any(Object[].class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Joiner.on(invocation.getArgumentAt(0, CharSequence.class).toString())
                        .join(invocation.getArgumentAt(1, Object[].class));
            }
        });
    }

    public static void mockPair() throws Exception {
        mockStatic(Pair.class);

        when(Pair.create(any(Object.class), any(Object.class))).then(new Answer<Pair<?, ?>>() {
            @Override
            public Pair<?, ?> answer(InvocationOnMock invocation) throws Throwable {
                final Pair pair = mock(Pair.class);
                Whitebox.setInternalState(pair, "first", invocation.getArgumentAt(0, Object.class));
                Whitebox.setInternalState(pair, "second", invocation.getArgumentAt(1, Object.class));
                return pair;
            }
        });
    }
}
