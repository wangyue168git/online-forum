package com.bolo.controller;


import com.bolo.entity.ActorInfo;

import com.bolo.entity.BillBoardMap;
import com.bolo.entity.JsonObj;
import com.bolo.test.crawler.BaiduCrawler;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope
public class HotController {

    @RequestMapping("hotInfo")
    public @ResponseBody
    JsonObj<BillBoardMap> getHot() throws InterruptedException {
        BaiduCrawler.sort();
        JsonObj<BillBoardMap> billBoard = new JsonObj<>();
        BillBoardMap billBoardMap = new BillBoardMap();
        billBoardMap.setData(BaiduCrawler.list);
        billBoardMap.setDate(new Date().toString());
        billBoard.setMap(billBoardMap);
        billBoard.setCode(200);
        billBoard.setMsg("success");
        return billBoard;
    }

    @RequestMapping(value="addpoint",method = RequestMethod.GET)
    public @ResponseBody
    JsonObj<BillBoardMap> addPoint(@RequestParam("id") String id){
        JsonObj<BillBoardMap> billBoard = new JsonObj<>();
        billBoard.setCode(200);
        billBoard.setMsg("success");
        addUserPoint(id);
        BaiduCrawler.sort();
        BillBoardMap billBoardMap = new BillBoardMap();
        billBoardMap.setData(BaiduCrawler.list);
        billBoardMap.setDate(new Date().toString());
        billBoard.setMap(billBoardMap);
        return billBoard;
    }


    @RequestMapping("ActorInfo1")
    public @ResponseBody
    JsonObj<Map<String,ActorInfo>> getActorInfo(@RequestParam("id1") String id1,
                                                @RequestParam("id2") String id2 ) throws InterruptedException {

        JsonObj<Map<String,ActorInfo>> map = new JsonObj<>();

        map.setCode(200);
        map.setMsg("success");

        Map<String,ActorInfo> map1 = new HashMap<>();
        for (ActorInfo actorInfo : BaiduCrawler.list){
            if (actorInfo.getId().equals(id1)){
                map1.put(id1,actorInfo);
            }
            if (actorInfo.getId().equals(id2)) {
                map1.put(id2, actorInfo);
            }
        }
        map.setMap(map1);
        return map;
    }

    @RequestMapping("ActorInfo")
    public @ResponseBody
    JsonObj<Map<String,ActorInfo>> getActorInfo(@RequestParam("id1") String id1) throws InterruptedException {

        JsonObj<Map<String,ActorInfo>> map = new JsonObj<>();

        map.setCode(200);
        map.setMsg("success");

        Map<String,ActorInfo> map1 = new HashMap<>();
        for (ActorInfo actorInfo : BaiduCrawler.list){
            if (actorInfo.getId().equals(id1)){
                map1.put(id1,actorInfo);
            }
        }
        map.setMap(map1);
        return map;
    }



    private synchronized void addUserPoint(String id){
        for (ActorInfo actorInfo : BaiduCrawler.list) {
            if (actorInfo.getId().equals(id)) {
                int i = actorInfo.getUserPoint();
                actorInfo.setUserPoint(i+=10);
                break;
            }
        }
    }
}
