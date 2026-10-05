package jdplus.main.ws.v1;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

public class ConvertersTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @ParameterizedTest
    @EnumSource(Frequency.class)
    public void testTsUnit(Frequency freq) {
        if (freq == Frequency.UNRECOGNIZED) {
            assertThatRuntimeException()
                    .isThrownBy(() -> Converters.toTsUnit(freq));
        } else {
            assertThat(Converters.fromTsUnit(Converters.toTsUnit(freq)))
                    .isEqualTo(freq);
        }
    }
}
