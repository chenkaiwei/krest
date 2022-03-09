package com.chenkaiwei.krestdemo1.error;

import com.chenkaiwei.krest.exceptions.KrestErrorController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
public class MyErrorController extends KrestErrorController {

    @Override
    protected Map<String, Object> getErrorResponseBody(HttpServletRequest request, Map<String, Object> errorAttributes, Throwable exception) {
        return super.getErrorResponseBody(request, errorAttributes, exception);
    }
}
