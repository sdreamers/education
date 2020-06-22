package com.sixing.base.security.utils;

import com.sixing.base.utils.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class NotNeedAuthenticationUrl {

    private static Set<Pattern> notNeedAuthenticationPatternList;

    private static final String fileName = "not_need_authentication.ini";

    @PostConstruct
    private void getFixedAuthRule() throws IOException {
        try {
            ClassPathResource cp = new ClassPathResource(fileName);
            Set<Pattern> set = new HashSet<>();
            if (cp.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(cp.getInputStream()));
                String line;
                while (StringUtils.isNotBlank(line = reader.readLine())) {
                    set.add(Pattern.compile(line.trim()));
                }
            }
            notNeedAuthenticationPatternList = Collections.unmodifiableSet(set);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Set<Pattern> get() {
        return notNeedAuthenticationPatternList;
    }

    public static Boolean contain(String uri) {
        if (StringUtils.isBlank(uri)) {
            return false;
        }
        Optional<Pattern> optional = NotNeedAuthenticationUrl.get().stream().filter(item -> item.matcher(uri).matches()).findFirst();
        return optional.isPresent();
    }

    public static Boolean notContain(String uri) {
        return !contain(uri);
    }
}
