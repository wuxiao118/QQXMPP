package com.zyxb.qqxmpp.db3;

import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.Context;

import com.zyxb.qqxmpp.bean3.FriendGroupInfo;
import com.zyxb.qqxmpp.bean3.MessageInfo;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroup;
import com.zyxb.qqxmpp.bean3.po.DB3FriendGroupMapping;
import com.zyxb.qqxmpp.bean3.po.DB3Group;
import com.zyxb.qqxmpp.bean3.po.DB3GroupMapping;
import com.zyxb.qqxmpp.bean3.po.DB3Message;
import com.zyxb.qqxmpp.bean3.po.DB3SystemGroup;
import com.zyxb.qqxmpp.bean3.po.DB3User;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3FriendGroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3GroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3GroupMappingDAO;
import com.zyxb.qqxmpp.db3.dao.DB3MessageDAO;
import com.zyxb.qqxmpp.db3.dao.DB3SystemGroupDAO;
import com.zyxb.qqxmpp.db3.dao.DB3UserDAO;
import com.zyxb.qqxmpp.engine.DB3FriendGroupEngine;
import com.zyxb.qqxmpp.engine.DB3MessageEngine;
import com.zyxb.qqxmpp.util.MD5Encoder;

public class DB3Init {
    // 用户列表
    private static final DB3User[] users;
    // 用户分组
    private static final DB3FriendGroup[] fgs;
    // 分组成员
    private static final DB3FriendGroupMapping[] fgMappings;
    // 群
    private static final DB3Group[] groups;
    // 群成员
    private static final DB3GroupMapping[] members;
    // 系统消息类型
    private static final DB3SystemGroup[] sysgroups;
    // 消息
    private static final DB3Message[] messages;

    static {

        String[][] infos = {
                // nickname age gender email pwd account login_days level
                // register_time expert_days location renew
                {"wuxiao", "30", "M", "wuxiao@gmail.com", "1234", "100000",
                        "1230", "32",
                        new Date(2005 - 1900, 4, 5).getTime() + "", "81",
                        "湖北 武汉", "上班时,下班后。[发呆]"},
                {"test", "26", "F", "test@gmail.com", "1234", "100001",
                        "1230", "28",
                        new Date(2008 - 1900, 8, 15).getTime() + "", "73",
                        "湖北 武汉", "不得，是不求；求了，还是不得，是妄求。"},
                {"xiaobai", "32", "M", "xiaobai@gmail.com", "1234", "100002",
                        "1230", "16",
                        new Date(2009 - 1900, 11, 10).getTime() + "", "26",
                        "北京", "上传一张照片到<picture>"},
                {"kerry", "20", "F", "kerry@gmail.com", "1234", "100003",
                        "1230", "64",
                        new Date(2002 - 1900, 2, 8).getTime() + "", "182",
                        "广东 深圳", "最新分享美式复古风公寓"},
                {"Tony", "24", "M", "tony@163.com", null, "100004", "680",
                        "25", new Date(2008 - 1900, 2, 8).getTime() + "", "29",
                        "湖南 长沙", "更新了说说"},
                {"Yang", "24", "M", "yangli@sina.com", null, "100006", "520",
                        "20", new Date(2006 - 1900, 2, 8).getTime() + "", "21",
                        "湖北 广水", "[爱心][爱心][爱心]"},
                {"睿智柴", "28", "M", "carry@tom.com", null, "100007", "700",
                        "35", new Date(2003 - 1900, 2, 8).getTime() + "",
                        "180", "上海", "亲们，手机摔坏了，这几天估计联系不上了"},
                {"杨帆远行", "32", "M", null, null, "100008", "600", "34",
                        new Date(2005 - 1900, 2, 8).getTime() + "", "100",
                        "浙江 杭州", "不急不躁自然有所得"},
                {"hair", "29", "M", null, null, "100009", "500", "32",
                        new Date(2004 - 1900, 5, 15).getTime() + "", "90",
                        "美国", "更新了相册"},
                {"xiaoqian", "25", "F", null, null, "100010", "320", "24",
                        new Date(2009 - 1900, 8, 8).getTime() + "", "39",
                        "广东 广州", "[工作]开启工作模式[工作]"},
                {"圆圆", "24", "F", null, null, "100011", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "unknown", "真的是有11:11的生物钟嘛?每次一抬头就看见11:11"},

                {"F->Mars", "24", "M", null, null, "100012", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 黄冈", null},
                {"愿得一人心", "24", "F", null, null, "100013", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 广水", "上传13张照片到《伟大的牛牛》"},
                {"阿南", "24", "F", null, null, "100014", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 广水", null},
                {"Cc星彤", "24", "F", null, null, "100015", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 随州", "更新了说说"},
                {"姊V。", "24", "F", null, null, "100016", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 黄冈", "回来过端午节[大笑]"},
                {"轻.落", "24", "F", null, null, "100017", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 随州", "更新了说说"},
                {"Moon", "24", "F", null, null, "100018", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 大悟", "不急不躁自然有所得"},
                {"TeeN。", "24", "F", null, null, "100019", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 大悟", "更新了相册"},
                {"Azang", "24", "F", null, null, "100020", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 广水", "一大早就变成猪头|师傅快来救救我"},
                {"梦婷V", "24", "F", null, null, "100021", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 广水", "已经不敢相信我的体重了[惊吓]"},
                {"仙丝蕾纱", "24", "F", null, null, "100022", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 广水", "仙丝蕾纱入围第九届中国品牌节"},
                {"傻傻的奶油", "24", "F", null, null, "100023", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 广水", "有种无奈少有人懂"},
                {"笑笑", "24", "F", null, null, "100024", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 鄂州", "今天有个路人甲问我是不是怀孕"},
                {"SUPER莎", "24", "F", null, null, "100025", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "武汉 广水", "更新了说说"},
                {"daiQ", "24", "F", null, null, "100026", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 大悟", "更新了说说"},
                {"H。", "24", "F", null, null, "100027", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 黄石", "真的是有11:11的生物钟嘛?每次一抬头就看见11:11"},
                {"怎么丢了你", "24", "F", null, null, "100028", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "云南 大理", "你若走，我不送"},
                {"独行客", "24", "F", null, null, "100029", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "unknown", "跑起来，什么都不会想了"},
                {"TDU", "24", "F", null, null, "100030", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "上海", null},
                {"唯", "24", "F", null, null, "100031", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖南 长沙",
                        "I belive that the future belong to us forever."},
                {"追缘", "24", "F", null, null, "100032", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "河南 驻马店", "最新分享，含泪的相思"},

                // 添加群测试数据
                {"汉口.Moring", "24", "M", null, null, "100033", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 汉口", "更新了说说"},
                {"创业街-六度", "24", "M", null, null, "100034", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"汉口-hr eva", "24", "F", null, null, "100035", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 汉口", null},
                {"汉口-麦兜飞飞", "24", "F", null, null, "100036", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 汉口", null},
                {"汉口-小薇", "24", "F", null, null, "100037", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 汉口", null},
                {"汉口-邪邪", "24", "M", null, null, "100038", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 汉口", null},
                {"武昌-朵朵", "24", "F", null, null, "100039", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"爱吃西瓜的妞", "24", "F", null, null, "100040", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"安静的等", "24", "F", null, null, "100041", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"帮", "30", "M", null, null, "100042", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"ben", "24", "F", null, null, "100043", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"北京-阿木", "24", "F", null, null, "100044", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "北京", null},
                {"初级大菜鸟", "24", "M", null, null, "100045", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"打哈欠的猫", "24", "F", null, null, "100046", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"ego", "24", "F", null, null, "100047", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"芳芳", "24", "F", null, null, "100048", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"风清扬", "24", "M", null, null, "100049", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"光谷-菜鸟", "24", "F", null, null, "100050", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"光谷-SUN", "24", "M", null, null, "100051", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"HelloWorld", "24", "M", null, null, "100052", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"海龟上的蜗牛", "24", "M", null, null, "100053", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"汉街-不哭", "24", "F", null, null, "100054", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"HR-程曦", "24", "F", null, null, "100055", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"洪山-董松", "24", "F", null, null, "100056", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"杭州-流逝", "24", "F", null, null, "100057", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"Ivan", "24", "M", null, null, "100058", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"江夏-蘑菇", "24", "F", null, null, "100059", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"KITTY", "24", "F", null, null, "100060", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"凌波微步", "24", "F", null, null, "100061", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"路人甲", "24", "F", null, null, "100062", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"漫不经心的神", "24", "F", null, null, "100063", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"南湖-xiuxiu", "24", "F", null, null, "100064", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},

                {"陪你看大海", "24", "F", null, null, "100065", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "山东 青岛", null},
                {"七七", "24", "F", null, null, "100066", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"骑着狼放羊", "24", "M", null, null, "100067", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"软件园-梅毅", "24", "F", null, null, "100068", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"RobyMan", "24", "M", null, null, "100069", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"上海-Redmine", "24", "F", null, null, "100070", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "上海", null},
                {"石头", "24", "F", null, null, "100071", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"深圳-华晨", "24", "F", null, null, "100072", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "广东 深圳", null},
                {"提拉米苏", "24", "F", null, null, "100073", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"Vicent", "24", "F", null, null, "100074", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"武昌-阿呆", "24", "F", null, null, "100075", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"武昌-蓝代", "24", "F", null, null, "100076", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"武昌-喵喵", "24", "F", null, null, "100077", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"武昌-tomcat", "24", "F", null, null, "100078", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"武昌-星辰", "24", "F", null, null, "100079", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"武汉 可可", "24", "F", null, null, "100080", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"武汉 panda", "24", "F", null, null, "100081", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"武汉-Javadroid", "24", "F", null, null, "100082", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"无厘头的男子", "24", "F", null, null, "100083", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"文艺范", "24", "F", null, null, "100084", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"小白", "24", "F", null, null, "100085", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"徐东-远义", "24", "F", null, null, "100086", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"小美-VICKY", "24", "F", null, null, "100087", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"小小人", "24", "F", null, null, "100088", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"小鱼", "24", "F", null, null, "100089", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"yellow泡泡", "24", "F", null, null, "100090", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"一路向北", "24", "F", null, null, "100091", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"鱼->水->海", "24", "F", null, null, "100092", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"照", "24", "F", null, null, "100093", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"中媒文化HR", "24", "F", null, null, "100094", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"重庆-小轩", "24", "F", null, null, "100095", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "重庆", null},
                {" 致远", "24", "F", null, null, "100096", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"11111", "24", "F", null, null, "100097", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"$叶子$", "24", "F", null, null, "100098", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"*蓝天白云*", "24", "F", null, null, "100099", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"[微笑]-一峰", "24", "F", null, null, "100100", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"^_^", "24", "F", null, null, "100101", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"~@_@~", "24", "F", null, null, "100102", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"100-群", "24", "F", null, null, "100103", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"#Raine#", "24", "F", null, null, "100104", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"♂唯爱♀", "24", "F", null, null, "100105", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"⑦exboy。", "24", "F", null, null, "100106", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"「°夜。妖", "24", "F", null, null, "100107", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null},
                {"【醉】新、鲜", "24", "F", null, null, "100108", "1200", "43",
                        new Date(2006 - 1900, 9, 8).getTime() + "", "290",
                        "湖北 武昌", null}

        };

        users = new DB3User[infos.length];
        DB3User user = null;
        for (int i = 0; i < infos.length; i++) {
            user = new DB3User();
            user.setNickname(infos[i][0]);
            user.setAge(Integer.parseInt(infos[i][1]));
            user.setGender(infos[i][2]);
            user.setEmail(infos[i][3]);
            if (infos[i][4] != null) {
                user.setPwd(MD5Encoder.encode(infos[i][4]));
            }
            user.setAccount(infos[i][5]);
            user.setLoginDays(Integer.parseInt(infos[i][6]));
            user.setLevel(Integer.parseInt(infos[i][7]));
            user.setRegisterTime(Long.parseLong(infos[i][8]));
            user.setExportDays(Integer.parseInt(infos[i][9]));
            user.setLocation(infos[i][10]);
            user.setRenew(infos[i][11]);
            users[i] = user;
            user = null;
        }

        // friendgroup
        String[] fgnames = {"我的设备", "我的好友", "2008", "2009", "初中同学", "高中同学",
                "大学同学", "Familiar", "亲戚朋友", "公司同事", "好久不见", "熟悉的陌生人"};
        DB3User[][] fgusers = {{users[0], users[1], users[3], users[2]},
                {users[0], users[1], users[3], users[2]}, {users[0]},
                {users[0]}, {users[0]}, {users[0]},
                {users[0], users[2], users[3]}, {users[0]},
                {users[0], users[1], users[2], users[3]},
                {users[0], users[1], users[2], users[3]}, {users[2]},
                {users[3]}};
        int size = 0;
        for (int i = 0; i < fgusers.length; i++) {
            size += fgusers[i].length;
        }

        fgs = new DB3FriendGroup[size];
        DB3FriendGroup fg = null;
        int k = 0;
        for (int i = 0; i < fgnames.length; i++) {
            for (int j = 0; j < fgusers[i].length; j++) {
                fg = new DB3FriendGroup();
                fg.setName(fgnames[i]);
                fg.setAccount((k + 1) + "");
                fg.setPosition(i);
                fg.setUser(fgusers[i][j]);
                fgs[k++] = fg;
                fg = null;
            }
        }

        // friend group mapping
        DB3FriendGroup[] friendgs = {
                // wuxiao
                fgs[4], fgs[8], fgs[9], fgs[10], fgs[11], fgs[12], fgs[15],
                fgs[16], fgs[20],
                // test
                fgs[5], fgs[17], fgs[21],
                // xiaobai
                fgs[7], fgs[13], fgs[18], fgs[22], fgs[24],
                // kerry
                fgs[6], fgs[14], fgs[19], fgs[23], fgs[25]};
        DB3User[][] fgUsers = {
                // wuxiao
                {users[2], users[3], users[4], users[27], users[28],
                        users[29], users[30], users[31]},
                {users[11], users[12], users[15], users[16]},
                {users[17], users[18], users[19], users[20], users[21],
                        users[22]},
                {users[23], users[24]},
                {users[25], users[26]},
                {users[8], users[9], users[10]},
                {users[13], users[14]},
                {users[5]},
                {users[1]},

                // test
                {users[6], users[7], users[11], users[13], users[16],
                        users[17], users[19]},
                {users[21], users[22], users[23]},
                {users[0], users[5]},

                // xiaobai
                {users[11], users[13], users[19], users[20], users[21]},
                {users[22], users[23], users[24]},
                {users[11], users[26]},
                {users[0], users[3]},
                {users[7], users[8], users[9]},

                // kerry
                {users[0], users[2], users[25], users[26], users[27],
                        users[28]}, {users[15], users[16], users[17]},
                {users[18], users[19]}, {users[13], users[14]},
                {users[4], users[10], users[29], users[30]}

        };
        String[][] fgstates = {
                // wuxiao state:channel:remark
                {"1:5:李也白", "5:4:龙淑珍", "5:7:李如青", "5:1", "1:0", "1:0", "0:0",
                        "0:0"},
                {"5:1:方奇", "5:1:李丰", "5:1:董慧", "5:1:黄冉"},
                {"5:1:乐靓", "5:1:刘婷", "5:1:史甜蜜", "5:1:肖梦婷", "5:1:吴颜秋波",
                        "5:1:张珂"},
                {"1:0:李含笑", "1:0:周莎"},
                {"0:0:代群", "0:0:黄青"},
                {"0:1:李政", "0:4:肖倩", "0:0:高圆圆"},
                {"5:1:陈林子", "5:2:陈星彤"},
                {"2:4:杨力"},
                {"0:0:测试"},

                // test
                {"4:3:柴睿", "4:2:成志扬", "5:1:方奇", "5:1:陈林子", "5:1:黄冉", "5:1:乐靓",
                        "5:1:史甜蜜"},
                {"5:1:吴颜秋波", "5:1:张珂", "1:0:李含笑"},
                {"5:6:吴小雄", "2:4:杨力"},

                // xiaobai
                {"5:1:方奇", "5:1:陈林子", "5:1:史甜蜜", "5:1:肖梦婷", "5:1:吴颜秋波"},
                {"5:1:张珂", "1:0:李含笑", "1:0:周莎"},
                {"5:1:方奇", "0:0:黄青"},
                {"5:6:吴小雄", "5:4:龙淑珍"},
                {"4:2:成志扬", "0:1:李政", "0:4:肖倩"},

                // kerry
                {"5:6:吴小雄", "1:5:李也白", "0:0:代群", "0:0:黄青", "5:1", "1:0"},
                {"5:1:董慧", "5:1:黄冉", "5:1:乐靓"}, {"5:1:刘婷", "5:1:史甜蜜"},
                {"5:1:陈林子", "5:2:陈星彤"},
                {"5:7:李如青", "0:0:高圆圆", "1:0", "0:0"}};

        size = 0;
        for (int i = 0; i < fgUsers.length; i++) {
            size += fgUsers[i].length;
        }
        fgMappings = new DB3FriendGroupMapping[size];
        DB3FriendGroupMapping mapping = null;
        int fgmCount = 100;
        k = 0;
        for (int i = 0; i < friendgs.length; i++) {
            for (int j = 0; j < fgstates[i].length; j++) {
                String[] temp = fgstates[i][j].split(":");

                mapping = new DB3FriendGroupMapping();
                mapping.setAccount(fgmCount++ + "");
                mapping.setFriendGroup(friendgs[i]);
                mapping.setLoginState(Integer.parseInt(temp[0]));
                mapping.setLoginChannel(Integer.parseInt(temp[1]));
                if (temp.length == 3) {
                    mapping.setRemark(temp[2]);
                }
                // mapping.setUser(friendgs[i].getUser());
                mapping.setUser(fgUsers[i][j]);
                fgMappings[k++] = mapping;
                mapping = null;
            }
        }

        // group
        String[][] groupDatas = {
                // account,name,desp,icon,create_time,classification
                {"100000001", "android研发群", "android开发技术讨论", null,
                        new Date(2009 - 1900, 5, 8).getTime() + "", "IT/互联网"},
                {"100000002", "java技术交流", "java技术讨论交流", null,
                        new Date(2004 - 1900, 7, 9).getTime() + "", "IT/互联网"},
                {"100000003", "好好工作", "公司交流群", null,
                        new Date(2007 - 1900, 7, 9).getTime() + "", "IT/互联网"},
                {"100000004", "99级11班", "高中同学群", null,
                        new Date(2003 - 1900, 7, 9).getTime() + "", "同学"},
                {"100000005", "亲朋好友", "亲戚朋友", null,
                        new Date(2008 - 1900, 7, 9).getTime() + "", "亲友"},
                {"100000006", "随意喷鼻", "好友随聊", null,
                        new Date(2006 - 1900, 7, 9).getTime() + "", "同学"},
                {"100000007", "nova", "随聊", null,
                        new Date(2009 - 1900, 7, 9).getTime() + "", "粉丝"},
                {"100000008", "计02A-2", "大学同学", null,
                        new Date(2006 - 1900, 7, 9).getTime() + "", "同学"}};
        groups = new DB3Group[groupDatas.length];
        DB3Group g = null;
        for (int i = 0; i < groupDatas.length; i++) {
            g = new DB3Group();
            g.setAccount(groupDatas[i][0]);
            g.setName(groupDatas[i][1]);
            g.setDesp(groupDatas[i][2]);
            g.setIcon(groupDatas[i][3]);
            g.setCreateTime(Long.parseLong(groupDatas[i][4]));
            g.setClassification(groupDatas[i][5]);
            groups[i] = g;
            g = null;
        }

        // group friend
        DB3User[][] memberUsers = {
                // android研发群数据添加
                {users[4], users[0], users[2], users[3], users[6], users[1],
                        users[5], users[28], users[30]},
                // java技术群
                {users[0], users[7], users[5], users[9], users[11], users[27],
                        users[31]},
                {users[0], users[2], users[3], users[1]},
                {users[0], users[25], users[26]}, {users[0], users[5]},
                {users[0], users[9], users[8], users[10]},
                {users[0], users[5]},
                {users[0], users[9], users[8], users[10]}};
        String[][] memberstates = {
                // state,channel,grouptitle,level,intertime,remark
                {
                        "2:4:2:0:" + new Date(2013 - 1900, 2, 4).getTime()
                                + ":李如青",
                        "5:6:6:3:" + new Date(2012 - 1900, 1, 7).getTime()
                                + ":吴小雄",
                        "5:4:4:2:" + new Date(2013 - 1900, 1, 5).getTime()
                                + ":李也白",
                        "5:7:4:1:" + new Date(2014 - 1900, 5, 11).getTime()
                                + ":龙淑珍",
                        "4:2:5:1:" + new Date(2014 - 1900, 1, 12).getTime()
                                + ":柴睿",
                        "1:5:5:1:" + new Date(2013 - 1900, 6, 7).getTime()
                                + ":测试",
                        "4:3:4:1:" + new Date(2013 - 1900, 8, 9).getTime()
                                + ":杨力",
                        "1:0:1:0:" + new Date(2014 - 1900, 10, 12).getTime(),
                        "0:0:0:0:" + new Date(2014 - 1900, 11, 5).getTime()},

                {
                        "5:6:5:2:" + new Date(2008 - 1900, 7, 6).getTime()
                                + ":吴小雄",
                        "0:1:4:1:" + new Date(2009 - 1900, 3, 6).getTime()
                                + ":成志扬",
                        "1:5:5:3:" + new Date(2008 - 1900, 4, 15).getTime()
                                + ":测试",
                        "0:0:2:0:" + new Date(2009 - 1900, 1, 5).getTime()
                                + ":肖倩",
                        "5:1:1:0:" + new Date(2010 - 1900, 6, 17).getTime()
                                + ":方奇",
                        "5:1:0:0:" + new Date(2010 - 1900, 8, 21).getTime(),
                        "0:0:0:0:" + new Date(2012 - 1900, 3, 27).getTime()},

                {
                        "5:6:4:2:" + new Date(2007 - 1900, 2, 5).getTime()
                                + ":吴小雄",
                        "5:4:6:3:" + new Date(2007 - 1900, 1, 1).getTime()
                                + ":李也白",
                        "5:7:4:1:" + new Date(2008 - 1900, 2, 6).getTime()
                                + ":龙淑珍",
                        "1:5:4:1:" + new Date(2008 - 1900, 5, 18).getTime()
                                + ":测试"},

                {
                        "5:6:4:3:" + new Date(2003 - 1900, 7, 1).getTime()
                                + ":吴小雄",
                        "0:0:1:0:" + new Date(2003 - 1900, 7, 12).getTime()
                                + ":代群",
                        "0:0:1:0:" + new Date(2003 - 1900, 8, 9).getTime()
                                + ":黄冉"},

                {
                        "5:6:2:3:" + new Date(2009 - 1900, 8, 1).getTime()
                                + ":吴小雄",
                        "4:3:1:0:" + new Date(2010 - 1900, 3, 1).getTime()
                                + ":杨力"},

                {
                        "5:6:2:3:" + new Date(2010 - 1900, 10, 5).getTime()
                                + ":吴小雄",
                        "0:0:1:1:" + new Date(2011 - 1900, 2, 8).getTime()
                                + ":肖倩",
                        "0:4:1:1:" + new Date(2011 - 1900, 2, 9).getTime()
                                + ":李政",
                        "0:0:1:1:" + new Date(2011 - 1900, 2, 15).getTime()
                                + ":高圆圆"},

                {
                        "5:6:2:2:" + new Date(2012 - 1900, 2, 18).getTime()
                                + ":吴小雄",
                        "4:3:1:3:" + new Date(2012 - 1900, 1, 18).getTime()
                                + ":杨力"},

                {
                        "5:6:2:1:" + new Date(2006 - 1900, 2, 25).getTime()
                                + ":吴小雄",
                        "0:0:2:1:" + new Date(2006 - 1900, 2, 18).getTime()
                                + ":肖倩",
                        "0:4:1:3:" + new Date(2006 - 1900, 1, 12).getTime()
                                + ":李政",
                        "0:0:2:1:" + new Date(2006 - 1900, 2, 26).getTime()
                                + ":高圆圆"}

        };
        size = 0;
        for (int i = 0; i < memberstates.length; i++) {
            size += memberstates[i].length;
        }
        members = new DB3GroupMapping[size];
        DB3GroupMapping gm = null;
        k = 1000;
        int m = 0;
        for (int i = 0; i < memberstates.length; i++) {
            for (int j = 0; j < memberstates[i].length; j++) {
                String[] temp = memberstates[i][j].split(":");
                gm = new DB3GroupMapping();
                gm.setAccount(k++ + "");
                gm.setGroup(groups[i]);
                gm.setUser(memberUsers[i][j]);
                gm.setLoginState(Integer.parseInt(temp[0]));
                gm.setLoginChannel(Integer.parseInt(temp[1]));
                gm.setGroupTitle(Integer.parseInt(temp[2]));
                gm.setLevel(Integer.parseInt(temp[3]));
                gm.setInterTime(Long.parseLong(temp[4]));
                if (temp.length == 6) {
                    gm.setRemark(temp[5]);
                }
                members[m++] = gm;
                gm = null;
            }
        }

        // 系统类型
        String[] sysnames = {"系统", "QQ新闻", "群助手", "QQ空间", "QQ邮箱", "QQ附近",
                "QQ悄悄话", "吃喝玩乐", "一声问候"};
        int[] systypes = {0, 0, 1, 1, 1, 1, 1, 1, 1};
        sysgroups = new DB3SystemGroup[sysnames.length];
        DB3SystemGroup sysgroup = null;
        m = 10000;
        for (int i = 0; i < sysnames.length; i++) {
            sysgroup = new DB3SystemGroup();
            sysgroup.setAccount(m++ + "");
            sysgroup.setName(sysnames[i]);
            sysgroup.setType(systypes[i]);
            sysgroups[i] = sysgroup;
            sysgroup = null;
        }

        // message
        String[][] msgcontents = {
                // 系统消息
                {"系统将于凌晨2点维护,给您带来的不便,敬请谅解",
                        DB3Columns.MESSAGE_STATE_READED + "", "1434101370000",
                        DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"系统将于凌晨2点维护,给您带来的不便,敬请谅解",
                        DB3Columns.MESSAGE_STATE_READED + "", "1434101370000",
                        DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"系统将于凌晨2点维护,给您带来的不便,敬请谅解",
                        DB3Columns.MESSAGE_STATE_READED + "", "1434101370000",
                        DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"系统将于凌晨2点维护,给您带来的不便,敬请谅解",
                        DB3Columns.MESSAGE_STATE_READED + "", "1434101370000",
                        DB3Columns.MESSAGE_TYPE_SYS + ""},

                {"港姐首轮面试启动,佳丽抢镜", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"港姐首轮面试启动,佳丽抢镜", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"港姐首轮面试启动,佳丽抢镜", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"港姐首轮面试启动,佳丽抢镜", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_SYS + ""},

                {"[一个群有新消息]", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101320000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"[一个群有新消息]", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101320000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"[一个群有新消息]", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101320000", DB3Columns.MESSAGE_TYPE_SYS + ""},

                {"[空间未读精华]龙淑珍：我眼里有你，你眼里有谁?",
                        DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101270000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"[空间未读精华]龙淑珍：我眼里有你，你眼里有谁?",
                        DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101270000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"[空间未读精华]龙淑珍：我眼里有你，你眼里有谁?",
                        DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101270000", DB3Columns.MESSAGE_TYPE_SYS + ""},

                {"财经新闻：以智慧激活财富,富人理财新手法",
                        DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101150000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"财经新闻：以智慧激活财富,富人理财新手法",
                        DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101150000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"财经新闻：以智慧激活财富,富人理财新手法",
                        DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101150000", DB3Columns.MESSAGE_TYPE_SYS + ""},

                {"你点歌,我来唱", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"你点歌,我来唱", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"你点歌,我来唱", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"你点歌,我来唱", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_SYS + ""},

                {"查看匿名悄悄话", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434100080000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"查看匿名悄悄话", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434100080000", DB3Columns.MESSAGE_TYPE_SYS + ""},

                {"优惠|重返童年,吃喝玩乐,爽!", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101080000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"优惠|重返童年,吃喝玩乐,爽!", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101080000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"优惠|重返童年,吃喝玩乐,爽!", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101080000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"优惠|重返童年,吃喝玩乐,爽!", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101080000", DB3Columns.MESSAGE_TYPE_SYS + ""},

                {"朋友生日要到了,送个祝福吧", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434100780000", DB3Columns.MESSAGE_TYPE_SYS + ""},
                {"朋友生日要到了,送个祝福吧", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434100780000", DB3Columns.MESSAGE_TYPE_SYS + ""},

                // 群消息
                {"看看这个问题怎么解决？", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"什么问题?", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101371000", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"??????", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101371200", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"sharp定义了实线,但是显示不出来", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101373000", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"这个没遇到过,我看看", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101374000", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"上次貌似遇到过这个问题,我想想", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101375000", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"想起来了,size的height必须大于stoke width,否则不显示",
                        DB3Columns.MESSAGE_STATE_READED + "", "1434101376000",
                        DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"哦哦,原来如此", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101370000", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"学习了", DB3Columns.MESSAGE_STATE_READED + "", "1434101378000",
                        DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"前两天还遇到一个更操蛋的问题,低版本下圆角矩形,bottomLeft居然是右下",
                        DB3Columns.MESSAGE_STATE_READED + "", "1434101379000",
                        DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"是啊,以前也遇到过,真是日了狗了", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101380000", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"居然会犯这种错误....", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101380200", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"看见高手也有这么2B的时候,我也就放心了", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101381000", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"[大笑]", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101381500", DB3Columns.MESSAGE_TYPE_GROUP + ""},
                {"[微笑]", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101382500", DB3Columns.MESSAGE_TYPE_GROUP + ""},

                // 聊天消息
                {"在不?", DB3Columns.MESSAGE_STATE_READED + "", "1434101381000",
                        DB3Columns.MESSAGE_TYPE_CONTACT + ""},
                {"在", DB3Columns.MESSAGE_STATE_READED + "", "1434101382000",
                        DB3Columns.MESSAGE_TYPE_CONTACT + ""},
                {"问你个事", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101383000", DB3Columns.MESSAGE_TYPE_CONTACT + ""},
                {"说", DB3Columns.MESSAGE_STATE_READED + "", "1434101384000",
                        DB3Columns.MESSAGE_TYPE_CONTACT + ""},
                {"有木有做过facebook登陆", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101384500", DB3Columns.MESSAGE_TYPE_CONTACT + ""},
                {"这个真木有,你问问小鹏吧,他貌似做过", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101385000", DB3Columns.MESSAGE_TYPE_CONTACT + ""},
                {"OK,[谢谢]", DB3Columns.MESSAGE_STATE_READED + "",
                        "1434101386000", DB3Columns.MESSAGE_TYPE_CONTACT + ""},
                {"帮我测试一下这个", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101485000", DB3Columns.MESSAGE_TYPE_CONTACT + ""},
                {"在不", DB3Columns.MESSAGE_STATE_RECEIVED + "",
                        "1434101484000", DB3Columns.MESSAGE_TYPE_CONTACT + ""}

        };
        Object[][] fromtos = {
                // 系统消息
                {sysgroups[0], users[0]}, {sysgroups[0], users[1]},
                {sysgroups[0], users[2]}, {sysgroups[0], users[3]},
                {sysgroups[1], users[0]}, {sysgroups[1], users[1]},
                {sysgroups[1], users[2]}, {sysgroups[1], users[3]},
                {sysgroups[2], users[0]},
                {sysgroups[2], users[1]},
                {sysgroups[2], users[2]},
                {sysgroups[3], users[0]},
                {sysgroups[3], users[2]},
                {sysgroups[3], users[3]},
                {sysgroups[4], users[0]},
                {sysgroups[4], users[1]},
                {sysgroups[4], users[3]},
                {sysgroups[5], users[0]},
                {sysgroups[5], users[1]},
                {sysgroups[5], users[2]},
                {sysgroups[5], users[3]},
                {sysgroups[6], users[0]},
                {sysgroups[6], users[3]},
                {sysgroups[7], users[0]},
                {sysgroups[7], users[1]},
                {sysgroups[7], users[2]},
                {sysgroups[7], users[3]},
                {sysgroups[8], users[0]},
                {sysgroups[8], users[1]},

                // 群消息
                {users[6], groups[0]}, {users[3], groups[0]},
                {users[2], groups[0]}, {users[6], groups[0]},
                {users[3], groups[0]}, {users[2], groups[0]},
                {users[2], groups[0]}, {users[6], groups[0]},
                {users[3], groups[0]},
                {users[6], groups[0]},
                {users[2], groups[0]},
                {users[3], groups[0]},
                {users[6], groups[0]},
                {users[3], groups[0]},
                {users[2], groups[0]},

                // 好友聊天
                {users[8], users[0]}, {users[0], users[8]},
                {users[8], users[0]}, {users[0], users[8]},
                {users[8], users[0]}, {users[0], users[8]},
                {users[8], users[0]}, {users[1], users[0]},
                {users[1], users[0]}};

        messages = new DB3Message[msgcontents.length];
        DB3Message message = null;
        k = 100000000;
        for (int i = 0; i < messages.length; i++) {
            message = new DB3Message();

            // message.setAccount("msg-" + (k++));
            message.setMsg(msgcontents[i][0]);
            message.setState(Integer.parseInt(msgcontents[i][1]));
            message.setCreateTime(Long.parseLong(msgcontents[i][2]));
            int type = Integer.parseInt(msgcontents[i][3]);
            message.setType(type);

            // 根据类型来设置
            switch (type) {
                case DB3Columns.MESSAGE_TYPE_SYS:
                    message.setFromGroup((DB3SystemGroup) fromtos[i][0]);
                    message.setTo((DB3User) fromtos[i][1]);
                    message.setAccount("sys-" + (k++));
                    break;
                case DB3Columns.MESSAGE_TYPE_GROUP:
                    message.setFrom((DB3User) fromtos[i][0]);
                    message.setToGroup((DB3Group) fromtos[i][1]);
                    message.setAccount("grp-" + (k++));
                    break;
                case DB3Columns.MESSAGE_TYPE_CONTACT:
                    message.setFrom((DB3User) fromtos[i][0]);
                    message.setTo((DB3User) fromtos[i][1]);
                    message.setAccount("cot-" + (k++));
                    break;
            }

            messages[i] = message;
            message = null;
        }
    }

    public static void addUser(Context context) {
        DB3UserDAO dao = DAOFactory.getDB3UserDAO(context);
        for (int i = 0; i < users.length; i++) {
            dao.add(users[i]);
        }

        dao.close();
    }

    public static void addFriendGroup(Context context) {
        DB3FriendGroupDAO dao = DAOFactory.getDB3FriendGroupDAO(context);
        for (int i = 0; i < fgs.length; i++) {
            dao.add(fgs[i]);
        }

        dao.close();
    }

    public static void addFriendState(Context context) {
        DB3FriendGroupMappingDAO dao = DAOFactory
                .getDB3FriendGroupMappingDAO(context);

        for (int i = 0; i < fgMappings.length; i++) {
            dao.add(fgMappings[i]);
        }

        dao.close();
    }

    public static void addGroup(Context context) {
        DB3GroupDAO dao = DAOFactory.getDB3GroupDAO(context);
        for (int i = 0; i < groups.length; i++) {
            dao.add(groups[i]);
        }

        dao.close();
    }

    public static void addGroupState(Context context) {
        DB3GroupMappingDAO dao = DAOFactory.getDB3GroupMappingDAO(context);
        // System.out.println(members.length);//64?
        for (int i = 0; i < members.length; i++) {
            dao.add(members[i]);
        }

        //group中添加 33~108数据
        Random rd = new Random();
        DB3GroupMapping mapping = null;
        long baseTime = new Date(2008, 10, 1).getTime();
        int minutes = 6 * 365 * 24 * 60 * 60;
        int account = 1035;
        int count = 0;
        for (int i = 32; i < 108; i++) {
            mapping = new DB3GroupMapping();
            mapping.setAccount(account++ + "");
            mapping.setUser(users[i]);
            mapping.setGroup(groups[0]);
            mapping.setGroupTitle(rd.nextInt(7));
            mapping.setInterTime(rd.nextInt(minutes) * 1000L + baseTime);
            if (count < 6) {
                int n = rd.nextInt(3);
                mapping.setLevel(n);
                if (n == 2) {
                    count++;
                }
            } else {
                mapping.setLevel(rd.nextInt(2));
            }

            mapping.setLoginChannel(rd.nextInt(8));
            mapping.setLoginState(rd.nextInt(6));

            dao.add(mapping);
            mapping = null;
        }

        dao.close();
    }

    public static void addSystemGroup(Context context) {
        DB3SystemGroupDAO dao = DAOFactory.getDB3SystemGroupDAO(context);
        for (int i = 0; i < sysgroups.length; i++) {
            dao.add(sysgroups[i]);
        }

        dao.close();
    }

    public static void addMessage(Context context) {
        DB3MessageDAO dao = DAOFactory.getDB3MessageDAO(context);
        for (int i = 0; i < messages.length; i++) {
            dao.add(messages[i]);
        }

        dao.close();
    }

    public static void create(Context context) {
        addUser(context);
        addFriendGroup(context);
        addFriendState(context);
        addGroup(context);
        addGroupState(context);
        addSystemGroup(context);
        addMessage(context);
    }

    public static void clear(Context context) {
        DB3UserDAO userDao = DAOFactory.getDB3UserDAO(context);
        userDao.clear();
        userDao.close();

        DB3FriendGroupDAO fgDao = DAOFactory.getDB3FriendGroupDAO(context);
        fgDao.clear();
        fgDao.close();

        DB3FriendGroupMappingDAO fgmDao = DAOFactory
                .getDB3FriendGroupMappingDAO(context);
        fgmDao.clear();
        fgmDao.close();

        DB3GroupDAO groupDao = DAOFactory.getDB3GroupDAO(context);
        groupDao.clear();
        groupDao.close();

        DB3GroupMappingDAO gmDao = DAOFactory.getDB3GroupMappingDAO(context);
        gmDao.clear();
        gmDao.close();

        DB3SystemGroupDAO sgDao = DAOFactory.getDB3SystemGroupDAO(context);
        sgDao.clear();
        sgDao.close();

        DB3MessageDAO mDao = DAOFactory.getDB3MessageDAO(context);
        mDao.clear();
        mDao.close();
    }

    public static List<DB3Message> getGroupMessages(Context context) {
        DB3MessageDAO dao = DAOFactory.getDB3MessageDAO(context);

        List<DB3Message> messages = dao.findGroup(users[0], groups[0]);
        dao.close();

        return messages;
    }

    public static List<DB3Message> getContactMessages(Context context) {
        DB3MessageDAO dao = DAOFactory.getDB3MessageDAO(context);

        List<DB3Message> messages = dao.findContact(users[0], users[1]);
        dao.close();

        return messages;
    }

    public static List<DB3Message> getSystemMessages(Context context) {
        DB3MessageDAO dao = DAOFactory.getDB3MessageDAO(context);

        List<DB3Message> messages = dao.findSystemGroup(users[0], sysgroups[0]);
        dao.close();

        return messages;
    }

    public static List<DB3Message> getNewest(Context context) {
        DB3MessageDAO dao = DAOFactory.getDB3MessageDAO(context);

        List<DB3Message> messages = dao.findNewest(users[0]);
        dao.close();

        return messages;
    }

    public static boolean isEmpty(Context context) {
        DB3UserDAO dao = DAOFactory.getDB3UserDAO(context);
        boolean empty = dao.isEmpty();
        dao.close();

        return empty;
    }

    public static List<FriendGroupInfo> getFriendGroups(Context context) {
        DB3FriendGroupEngine engine = new DB3FriendGroupEngine(context);
        return engine.getFriends(users[0]);
    }

    public static List<MessageInfo> getMessageInfos(Context context) {
        DB3MessageEngine engine = new DB3MessageEngine(context);
        return engine.getNewest(users[0]);
    }

}
