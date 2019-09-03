# 利用 Spring Boot & Redis 实现短链接服务

> 原作者: 杨斌
>
> 原文地址: https://juejin.im/post/5d6ba1436fb9a06b28636915
> 
> 原仓库地址: https://github.com/y0ngb1n/spring-boot-samples/tree/master/spring-boot-samples-url-shortener

**运行效果**

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190904001549.png)

## 准备工作

- Spring Boot 2.1.0+
- Redis
- Lombok
- Guava 28.0
- Common Validator 1.6

## 添加依赖项

**pom.xml**

```
<dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <dependency>
      <groupId>commons-validator</groupId>
      <artifactId>commons-validator</artifactId>
    </dependency>

    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
    </dependency>
</dependencies>



```

**application.yml**

```yaml
spring:
  # Redis Config
  redis:
    url: 127.0.0.1
    port: 6379
    password: your_password
logging:
  level:
    io.github.y0ngb1n.*: debug
```

## 核心代码

```java
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
```

## 使用方式

**Step 0: 安装并启动 Redis**

```
# on Windows
scoop install redis
redis-server

# on Mac
brew install redis
redis-server
```

**Step 1: 启动 url-shortener 服务**

```
$ mvn install
...
[INFO] BUILD SUCCESS
...
$ mvn spring-boot:run
...
2019-08-21 21:03:50.215  INFO 10244 --- [ main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2019-08-21 21:03:50.219  INFO 10244 --- [ main] i.g.y.s.u.UrlShortenerApplication        : Started UrlShortenerApplication in 6.01 seconds (JVM running for 12.165)
```

**Step 2: 生成短链**

```shell
$ curl -X POST http://127.0.0.1:8080/v1 \
  -H 'Content-Type: text/plain' \
  -d https://y0ngb1n.github.io
515bbe2b
```

**Step 3: 还原短链**

```shell
$ curl -X GET http://127.0.0.1:8080/v1/515bbe2b
https://y0ngb1n.github.io
```

查看日志

```
...
2019-08-21 21:42:26.788 DEBUG 10244 --- [nio-8080-exec-2] i.g.y.s.u.c.UrlShortenerController       : URL Id generated: 515bbe2b
2019-08-21 21:42:40.748 DEBUG 10244 --- [nio-8080-exec-3] i.g.y.s.u.c.UrlShortenerController       : URL Retrieved: https://y0ngb1n.github.io
```
