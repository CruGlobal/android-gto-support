package org.ccci.gto.android.common.testing;

import android.text.TextUtils;
import android.util.Pair;

import com.google.common.base.Joiner;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

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
                return Joiner.on(invocation.getArgument(0).toString())
                        .join((Iterable) invocation.getArgument(1));
            }
        });
        when(TextUtils.join(any(CharSequence.class), any(Object[].class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Joiner.on(invocation.getArgument(0).toString())
                        .join((Object[]) invocation.getArgument(1));
            }
        });
    }

    public static void mockPair() throws Exception {
        mockStatic(Pair.class);

        when(Pair.create(any(Object.class), any(Object.class))).then(new Answer<Pair<?, ?>>() {
            @Override
            public Pair<?, ?> answer(InvocationOnMock invocation) throws Throwable {
                final Pair pair = mock(Pair.class);
                Whitebox.setInternalState(pair, "first", (Object) invocation.getArgument(0));
                Whitebox.setInternalState(pair, "second", (Object) invocation.getArgument(1));
                return pair;
            }
        });
    }
}
