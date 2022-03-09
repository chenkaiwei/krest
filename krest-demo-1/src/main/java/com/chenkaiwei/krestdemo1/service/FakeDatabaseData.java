package com.chenkaiwei.krestdemo1.service;

import com.chenkaiwei.krestdemo1.entity.User;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Component
public class FakeDatabaseData {

//    User(id=25144be3bcfc4e43901cb395525741a8, username=zhang3, password=a7d59dfc5332749cb801f86a24f5f590, salt=e5ykFiNwShfCXvBRPr3wXg==, roles=[admin])

    private List<User> allUserList=new ArrayList<>(Arrays.asList(
            new User().builder().id("111")
                .username("zhang3")
                .password("a7d59dfc5332749cb801f86a24f5f590")
                .salt("e5ykFiNwShfCXvBRPr3wXg==")
                .roles(Arrays.asList("admin")).build(),
            new User().builder().id("222")
                    .username("li4")
                    .password("41a89d161a3ab10a5155083663c01b1a")
                    .salt("y.M#pCAlj3N`KVtb'|+2RtH;")
                    .roles(Arrays.asList("user")).build()

    ));


}
