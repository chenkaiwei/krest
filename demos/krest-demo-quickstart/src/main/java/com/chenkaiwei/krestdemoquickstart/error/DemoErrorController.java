package com.chenkaiwei.krestdemoquickstart.error;

import com.chenkaiwei.krest.exceptions.KrestErrorController;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class DemoErrorController extends KrestErrorController {

    @Override
    protected Map<String, Object> getErrorResponseBody(HttpServletRequest request, Map<String, Object> errorAttributes, Throwable exception) {
        //TODO 自定义错误返回时的数据格式。
        return super.getErrorResponseBody(request, errorAttributes, exception);
    }
}