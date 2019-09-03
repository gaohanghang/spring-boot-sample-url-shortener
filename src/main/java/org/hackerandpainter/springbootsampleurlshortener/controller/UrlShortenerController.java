package org.hackerandpainter.springbootsampleurlshortener.controller;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.hackerandpainter.springbootsampleurlshortener.InvalidUrlException;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-09-03 23:38
 **/
@Slf4j
@RestController
@RequestMapping(path = "/v1")
public class UrlShortenerController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping(path = "/{id}")
    public String getUrl(@PathVariable String id) {
        String url = redisTemplate.opsForValue().get(id);
        log.debug("URL Retrieved: {}", url);
        return url;
    }

    @PostMapping
    public String create(@RequestBody String url) {
        UrlValidator urlValidator = new UrlValidator(
                new String[]{"http", "https"}
        );
        if (urlValidator.isValid(url)) {
            String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            log.debug("URL Id generated: {}", id);
            redisTemplate.opsForValue().set(id, url);
            return id;
        }
        throw new InvalidUrlException("URL Invalid: " + url);
    }
}
