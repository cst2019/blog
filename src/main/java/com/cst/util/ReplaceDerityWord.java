package com.cst.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * @description:
 * @author: cst
 * @date: Created in 2020/4/20 6:43 下午
 * @version:
 * @modified By:
 */
public class ReplaceDerityWord {
    //系统设置的敏感词
    private static String[] sensitive = {"我操","去死吧你","打倒共产党","反对社会主义",
            "打倒胡锦涛","打倒温家宝","打倒江泽民","打倒毛泽东","不得好死","干你妹","你妈","shabi","傻逼","煞笔","沙比",
"你吗","nima","草拟","草泥","操你","操尼","艹你","艹拟" ,"艹泥","死了","si"   };
    public static String dirtyStr ;  //要检验的字符串
    final int threadSize = 20 ;      //总共线程数量
    Integer completeSize = 0 ;    //已经完成的线程数量
    boolean tmp = false;   //记录字符串是否属于敏感词
    Object completeLock = new Object();  //同步锁辅助对象
    private static String[] sensitives;
    private static char[] ch = {'~','!','@','#','$','%','^','&','*','(',')',' ','　','!','！','`',
            '_','⋯','+','-','=','/','\\','！','·','¥','—','（','）',',','，','.','。'};
    public ReplaceDerityWord(){
        initSensitives();
        String sourceStr = "去#死#吧你!全家不!得`好#死·！干。。你。。妹！";
        String input = JOptionPane.showInputDialog("请输入你想说的话，脏话有涉政治的话将会被屏蔽！",sourceStr);
        //获取输入字符串
        dirtyStr=input ;
        try {
            input = checkStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, input);
        System.out.println("dirtyStr="+dirtyStr);
        System.exit(0);
    }
    //创建敏感词
    public String[] initSensitives(){
        //为了显示效率高 将关键字个数增加到300000个
        sensitives = new String[300010];
        for (int i = 0; i < 300010 ; i++) {
            if(i<sensitive.length){
                sensitives[i]=sensitive[i];
            }else{
                sensitives[i] = "敏感" + (i + 1);
            }
        }
        return sensitives;
    }
    //检查词是否铭感
    public String checkStr() throws Exception{
        int everyLength = sensitives.length / threadSize;   //每条线程匹配的长度
        for (int i = 0; i < threadSize ; i++) {
            int start = i * everyLength ;
            int end = start + everyLength ;
            if(end > sensitives.length)
                end = sensitives.length ;
            new CheckThread(start , end).start();
        }
        synchronized (completeLock) {
            while(completeSize < threadSize){    //保证线程都执行完了
                completeLock.wait();
            }
        }
        return dirtyStr;
    }
    //线程判断字符是否属于敏感词
    class CheckThread extends Thread{
        int startIndex ;
        int endIndex ;
        String newdirtyStr ;
        public CheckThread(int startIndex , int endIndex ){
            this.startIndex = startIndex ;
            this.endIndex = endIndex;
            this.newdirtyStr = ignoreChars(dirtyStr);
        }
        public void run() {
            //过滤掉符号字符,避免通过中间用符号分割后骂人
            //得到分割和的字符在原字符的位置,并把位置保存在map中（用于替换时方便）
            List<Map<Character,Integer>> list = getPosition(dirtyStr,newdirtyStr);
            for (int i = startIndex; i < endIndex && !tmp; i++) {
                List<Integer> aList = getIndexOfStrForOtherStr(dirtyStr,newdirtyStr, sensitives[i],list);
                if(aList!=null&&aList.size() > 0) {
                    for (int j = 0; j < aList.size(); j++) {
                        //替换掉字符
                        dirtyStr = replaceByIndex(dirtyStr, aList.get(j));
                    }
                }
            }
            synchronized (completeLock) {
                completeSize++;
                completeLock.notifyAll();
                System.out.println("线程" + Thread.currentThread().getName() + "执行完毕");
            }
        }
    }



    /**
     * 得到非符号字符在源字符中的位置,将每一个字符的位置存放在Map中
     * 形成list{map1{操，1}，map1{你，3}，map1{妈，5}}
     * @param oldStr
     * @param newStr
     * @return
     */
    public  List<Map<Character,Integer>> getPosition(String oldStr,String newStr ) {
        int pos = 0 ;
        char c ;
        List<Map<Character,Integer>>  list = new ArrayList<Map<Character,Integer>>();
        for (int i = 0,len= newStr.length(); i < len;i++) {
            Map<Character,Integer> map = new HashMap<Character, Integer>(1);
            c = newStr.charAt(i);
            pos = oldStr.indexOf(c,pos);
            map.put(c,pos);
            list.add(map);
        }
        return list;
    }
    //得到一个子字符串中，所有字符在父字符串中的索引集合
    public  List<Integer> getIndexOfStrForOtherStr(String oldStr,String bigStr, String smallStr,List<Map<Character, Integer>> list) {
        List<Integer> aList = null;
        //判断这两个字符串是否有必要判断，即大字符串的长度是否小于小字符串的长度
        if(bigStr.length() >= smallStr.length()) {
            aList = new ArrayList<Integer>();
            //将去除符号的大字符串用变量保存
            //判断小字符串是否为大字符串的子串
            int smPos = bigStr.indexOf(smallStr);
            Map<Character,Integer> tempMap = null;
            while(smPos >= 0) {
                int ind = bigStr.indexOf(smallStr.charAt(0),smPos);
                for(int j = 0,len = smallStr.length(); j <len ; j++){
                    tempMap = list.get(ind);
                    aList.add(tempMap.get(smallStr.charAt(j)));
                    ind++;
                }
                smPos = bigStr.indexOf(smallStr, ind);
            }
        }
        return aList;
    }

    /*
     * 将指定索引的字符串，变成符号　*
     */
    private static synchronized  String replaceByIndex(String str, int index) {
        //此操作是将脏话的每个字，都变成　”*“
        return str.substring(0,index) + "*" +str.substring(index+1);
    }



    /**
     * 忽略字符串中的符号 ,得到无符号的字符串
     */
    private  String ignoreChars(String oldStr) {
        String newStr = oldStr;
        //将旧字符串变成字符数组
        for(char c : ch){
            newStr = newStr.replaceAll("\\"+String.valueOf(c), "");
        }
        return newStr;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(IkAnalyzerUtil.getIk("中国人都是傻逼"));
      //  ReplaceDerityWord ts = new ReplaceDerityWord();
    }

}
