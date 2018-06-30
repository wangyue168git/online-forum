package com.bolo.test.crawler;

import com.bolo.crawler.abstractclass.AbstractCrawler;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Spider;
import com.bolo.crawler.poolmanager.SpiderManager;
import com.bolo.crawler.utils.ContextUtil;
import com.bolo.entity.ActorInfo;
import com.bolo.entity.Fans;
import com.bolo.util.MatchUtils;
import com.mks.api.response.modifiable.ModifiableAPIExceptionContainer;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BaiduCrawler extends AbstractCrawler {

    public static Map<String, List<ActorInfo>> map = new ConcurrentHashMap<>();

    public static List<ActorInfo> list = new ArrayList<>();


    public BaiduCrawler(Spider spider) {
        super(spider);
        spider.getSite().setFollowRedirect(true);
        spider.getSite().setRetryTimes(1);
        spider.getSite().setRndSleepTime(50);
    }


    public void getActorInfo(String name,ActorInfo actorInfo){

        getUrl("https://www.baidu.com/s?wd=" + URLEncoder.encode(name),null,build(new CrawlerObserver() {
            @Override
            public void afterRequest(SimpleObject context) throws Exception {
                Document document = ContextUtil.getDocumentOfContent(context);
                String lemmaNumber = MatchUtils.filterNumber(document.select("div.nums")
                        .first().select("span").first().text());

                actorInfo.setLemmaNumber(Integer.parseInt(lemmaNumber));
//                getUrl(url,null,build(new CrawlerObserver() {
//                    @Override
//                    public void afterRequest(SimpleObject context) throws Exception {
//                        Document document = ContextUtil.getDocumentOfContent(context);
//                        String date = document.select("div.basic-info").first()
//                                .select("dd").get(9).text();
//                        String type = document.select("div.basic-info").first()
//                                .select("dd").get(10).text();
//                        String constellation = document.select("div.basic-info").first()
//                                .select("dd").get(4).text();
//                        actorInfo.setDate(date);
//                        actorInfo.setType(type);
//                        actorInfo.setConstellation(constellation);
//
//                        getUrl("http://index.chinaz.com/?words=" + name,null,build(new CrawlerObserver() {
//                            @Override
//                            public void afterRequest(SimpleObject context) throws Exception {
//                                Document document = ContextUtil.getDocumentOfContent(context);
//
//                                String index = document.select("ul.zs-nodule").first().select("li")
//                                        .get(1).select("strong").first().text();
//
//                                String index1 = document.select("ul.zs-nodule").first().select("li")
//                                        .get(1).select("span").first().text();
//
//                                actorInfo.setIndex(Double.parseDouble(index));
//
//
//                            }
//                        }));
//                    }
//                }));
            }
        }));
    }

    public void getHot(){
        ActorInfo actorInfo = new ActorInfo();
        actorInfo.setId("001");
        actorInfo.setName("周冬雨");
        actorInfo.setFans(2003848);
        actorInfo.setUserImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1530292930871&di=07d0145f92d71ec386c2aea39455d186&imgtype=0&src=http%3A%2F%2Fimg1.dzwww.com%3A8080%2Ftupian%2F20170427%2F24%2F16736333610533935820.jpg");
        actorInfo.setHot(99.17);
        actorInfo.setType(new String[]{"演员","歌手"});
        actorInfo.setIndex(300);
        actorInfo.setTicket_office(30000000);
        actorInfo.setUserPoint(10);
        actorInfo.setLastweek(new String[]{"6.30","6.29","6.28","6.27","6.26","6.25","6.24"});
        actorInfo.setPortrait(new String[]{"清纯","灵气", "率直","有天赋","水瓶座","实力蜕变","调皮天真"});

        Fans fans = new Fans();
        fans.setFemale_occupation(0.65);
        fans.setMale_occupation(0.35);



        List<String> list1 = new ArrayList<>();
        list1.add("20-25岁");
        list1.add("20岁以下");
        list1.add("25-30岁");
        list1.add("30岁以上");
        fans.setAgeMap(list1);

        Map<String,Double> map2 = new HashMap<>();
        map2.put("北京",34000.0);
        map2.put("上海",26000.0);
        map2.put("广州",25000.0);
        map2.put("深圳",16000.0);
        fans.setAddress(map2);

        fans.setMale_occupation(0.6);
        fans.setFemale_occupation(0.4);

        actorInfo.setFans(fans);
        actorInfo.setForces(new float[]{234,342,354,456,542,476,328});
        getActorInfo("周冬雨",actorInfo);

        ActorInfo actorInfo1= new ActorInfo();
        actorInfo1.setId("002");
        actorInfo1.setName("杨幂");
        actorInfo1.setFans(81636030);
        actorInfo1.setUserImage("http://tva4.sinaimg.cn/crop.0.0.512.512.180/473df571jw8eku7gao4irj20e80e8dg7.jpg");
        actorInfo1.setHot(86.39);
        actorInfo1.setType(new String[]{"演员","歌手"});
        actorInfo1.setIndex(400);
        actorInfo1.setTicket_office(90000000);
        actorInfo1.setUserPoint(10);
        actorInfo1.setLastweek(new String[]{"6.30","6.29","6.28","6.27","6.26","6.25","6.24"});
        actorInfo1.setPortrait(new String[]{"可爱","性感", "美腿","聪明","自黑","情商高","处女座"});
        actorInfo1.setForces(new float[]{321,332,354,475,435,483,328});

        Fans fans1 = new Fans();
        fans.setFemale_occupation(0.65);
        fans.setMale_occupation(0.35);
        List<String> list2 = new ArrayList<>();
        list2.add("20-25岁");
        list2.add("25-30岁");
        list2.add("20岁以下");
        list2.add("30岁以上");
        fans1.setAgeMap(list2);


        Map<String,Double> map4 = new HashMap<>();
        map4.put("北京",30000.0);
        map4.put("上海",25000.0);
        map4.put("广州",24000.0);
        map4.put("深圳",50000.0);
        fans1.setAddress(map4);

        fans1.setMale_occupation(0.6);
        fans1.setFemale_occupation(0.4);

        actorInfo1.setFans(fans1);
        getActorInfo("杨幂",actorInfo1);

        ActorInfo actorInfo2 = new ActorInfo();
        actorInfo2.setId("003");
        actorInfo2.setName("朱一龙");
        actorInfo2.setFans(5550701);
        actorInfo2.setUserImage("http://tvax4.sinaimg.cn/crop.0.0.1125.1125.180/5f034df1ly8fqekrxd168j20v90v9dir.jpg");
        actorInfo2.setHot(92.61);
        actorInfo2.setType(new String[]{"演员","歌手"});
        actorInfo2.setIndex(100);
        actorInfo2.setTicket_office(60000000);
        actorInfo2.setUserPoint(10);
        getActorInfo("朱一龙",actorInfo2);

        ActorInfo actorInfo3 = new ActorInfo();
        actorInfo3.setId("004");
        actorInfo3.setName("曾舜晞");
        actorInfo3.setFans(1190369);
        actorInfo3.setUserImage("http://tvax4.sinaimg.cn/crop.0.0.960.960.180/69245c84ly8fkd3zr6c0kj20qo0qoq49.jpg");
        actorInfo3.setHot(89.91);
        actorInfo3.setType(new String[]{"演员","歌手"});
        actorInfo3.setIndex(150);
        actorInfo3.setTicket_office(60000000);
        actorInfo3.setUserPoint(10);
        getActorInfo("曾舜晞",actorInfo3);

        ActorInfo actorInfo4 = new ActorInfo();
        actorInfo4.setId("005");
        actorInfo4.setName("白宇");
        actorInfo4.setFans(3767580);
        actorInfo4.setUserImage("http://tvax1.sinaimg.cn/crop.0.0.750.750.180/7c13f06dly8fsfjm8us5aj20ku0kudgy.jpg");
        actorInfo4.setHot(95.53);
        actorInfo4.setType(new String[]{"演员","歌手"});
        actorInfo4.setIndex(120);
        actorInfo4.setTicket_office(60000000);
        actorInfo4.setUserPoint(10);
        getActorInfo("白宇",actorInfo4);

        ActorInfo actorInfo5 = new ActorInfo();
        actorInfo5.setId("006");
        actorInfo5.setName("陈学冬");
        actorInfo5.setFans(28500742);
        actorInfo5.setUserImage("http://tvax3.sinaimg.cn/crop.0.0.512.512.180/5b6bc44cly8fhd9rb7x34j20e80e8t8y.jpg");
        actorInfo5.setHot(85.65);
        actorInfo5.setType(new String[]{"演员","歌手"});
        actorInfo5.setIndex(110);
        actorInfo5.setTicket_office(60000000);
        actorInfo5.setUserPoint(10);
        getActorInfo("陈学冬",actorInfo5);

        ActorInfo actorInfo6 = new ActorInfo();
        actorInfo6.setId("007");
        actorInfo6.setName("鹿晗");
        actorInfo6.setFans(44728709);
        actorInfo6.setUserImage("http://tvax3.sinaimg.cn/crop.13.0.614.614.180/5ba8d1cbly8fesjytq8a0j20hs0h2754.jpg");
        actorInfo6.setHot(85.54);
        actorInfo6.setType(new String[]{"演员","歌手"});
        actorInfo6.setIndex(140);
        actorInfo6.setTicket_office(10000000);
        actorInfo6.setUserPoint(10);
        getActorInfo("鹿晗",actorInfo6);

        ActorInfo actorInfo7 = new ActorInfo();
        actorInfo7.setId("008");
        actorInfo7.setName("刘昊然");
        actorInfo7.setFans(16932393);
        actorInfo7.setUserImage("http://tva2.sinaimg.cn/crop.0.0.1242.1242.180/ab179aaejw8f3xb0otda9j20yi0yj0yl.jpg");
        actorInfo7.setHot(84.58);
        actorInfo7.setType(new String[]{"演员","歌手"});
        actorInfo7.setIndex(280);
        actorInfo7.setTicket_office(60000000);
        actorInfo7.setUserPoint(10);
        getActorInfo("刘昊然",actorInfo7);

        ActorInfo actorInfo8 = new ActorInfo();
        actorInfo8.setId("009");
        actorInfo8.setName("胡歌");
        actorInfo8.setFans(61099245);
        actorInfo8.setUserImage("http://tva2.sinaimg.cn/crop.0.1.510.510.180/48e837eejw8ex30o7eoylj20e60e8wet.jpg");
        actorInfo8.setHot(84.50);
        actorInfo8.setType(new String[]{"演员","歌手"});
        actorInfo8.setIndex(380);
        actorInfo8.setTicket_office(10000000);
        actorInfo8.setUserPoint(10);
        getActorInfo("胡歌",actorInfo8);

        ActorInfo actorInfo9 = new ActorInfo();
        actorInfo9.setId("010");
        actorInfo9.setName("王俊凯");
        actorInfo9.setFans(46346670);
        actorInfo9.setUserImage("http://tvax2.sinaimg.cn/crop.0.0.512.512.180/69e273f8ly8fr3qffljhwj20e80e8t90.jpg");
        actorInfo9.setHot(84.40);
        actorInfo9.setType(new String[]{"演员","歌手"});
        actorInfo9.setIndex(280);
        actorInfo9.setTicket_office(60000000);
        actorInfo9.setUserPoint(10);
        getActorInfo("王俊凯",actorInfo9);

        list.add(actorInfo);
        list.add(actorInfo1);
        list.add(actorInfo2);
        list.add(actorInfo3);
        list.add(actorInfo4);
        list.add(actorInfo5);
        list.add(actorInfo6);
        list.add(actorInfo7);
        list.add(actorInfo8);
        list.add(actorInfo9);



    }

    public static void sort(){
        getMax();
        for (ActorInfo actorInfo : list){
            actorInfo.setForce((float)(Math.round(getForce(actorInfo)*1000)/1000));
        }
        Collections.sort(list, new Comparator<ActorInfo>() {
            @Override
            public int compare(ActorInfo o1, ActorInfo o2) {
                return o1.getForce() < o2.getForce() ? 1 : -1;
            }
        });

        map.put("data",list);
    }

    public static double getForce(ActorInfo actorInfo){
        double force = actorInfo.getFans()/maxFans + actorInfo.getLemmaNumber()/maxLemmaNumberarray +
                actorInfo.getHot()/maxHot + actorInfo.getTicket_office()/maxTicket + actorInfo.getIndex()/maxIndex +
                actorInfo.getUserPoint()/maxUserPoint;
        return force * 100;
    }



    static int maxFans;
    static double maxIndex;
    static int maxTicket;
    static double maxHot;
    static int maxLemmaNumberarray;
    static int maxUserPoint;


    public static void getMax(){

        int[] fansarray = new int[list.size()];
        double[] indexarray = new double[list.size()];
        int[] ticketarray = new int[list.size()];
        double[] hotarray = new double[list.size()];
        int[] lemmaNumberarray = new int[list.size()];
        int[] userPointarray = new int[list.size()];

        for (int i = 0; i < list.size(); i++){
            fansarray[i] = list.get(i).getFans();
            indexarray[i] = list.get(i).getIndex();
            ticketarray[i] = list.get(i).getTicket_office();
            hotarray[i] = list.get(i).getHot();
            lemmaNumberarray[i] = list.get(i).getLemmaNumber();
            userPointarray[i] = list.get(i).getUserPoint();
        }

        maxFans = getIntArrayMAx(fansarray);
        maxIndex = getDoubleArrayMAx(indexarray);
        maxTicket = getIntArrayMAx(ticketarray);
        maxHot = getDoubleArrayMAx(hotarray);
        maxLemmaNumberarray = getIntArrayMAx(lemmaNumberarray);
        maxUserPoint = getIntArrayMAx(userPointarray);
    }


    public static int getIntArrayMAx(int[] array){
        int max = array[0];
        for (int i : array){
            if (i > max)
                max = i;
        }
        return max;
    }

    public static double getDoubleArrayMAx(double[] array){
        double max = array[0];
        for (double i : array){
            if (i > max)
                max = i;
        }
        return max;
    }


}
