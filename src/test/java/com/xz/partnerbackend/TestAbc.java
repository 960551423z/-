package com.xz.partnerbackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: 阿庆
 * @Date: 2024/3/6 17:40
 * @ToDo:
 */

public class TestAbc {

    @Test
    void test1() throws JsonProcessingException {
        String a = "[\"男\",\"java\"]\n";
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = mapper.readValue(a, new TypeReference<List<String>>(){});
        System.out.println(list);
    }
}
