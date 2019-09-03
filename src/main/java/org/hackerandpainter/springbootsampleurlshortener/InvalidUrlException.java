package org.hackerandpainter.springbootsampleurlshortener;

/**
 * @Description Invalid Url Exception. 无效url异常
 * @Author Gao Hang Hang
 * @Date 2019-09-03 23:38
 **/
public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String message) {
        super(message);
    }
}
