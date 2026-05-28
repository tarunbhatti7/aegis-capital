package com.aegis.capital.util;

import com.aegis.capital.config.AccountsConstants;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AccountNumberGenerator {

    private AccountNumberGenerator() {}

    public static String generate() {
        var digits = IntStream.range(0, AccountsConstants.ACCOUNT_RANDOM_DIGITS)
                .map(i -> ThreadLocalRandom.current().nextInt(10))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
        return AccountsConstants.ACCOUNT_PREFIX + digits;
    }
}
