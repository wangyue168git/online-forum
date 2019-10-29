package com.bolo.test.rpc;

import java.util.List;
import java.util.Map;

/**
 * Created by wangyue on 2019/2/26.
 */
public interface InfoUserService {
    List<InfoUser> insertInfoUser(InfoUser infoUser);
    InfoUser getInfoUserById(String id);
    void deleteInfoUserById(String id);
    String getNameById(String id);
    Map<String,InfoUser> getAllUser();

}
