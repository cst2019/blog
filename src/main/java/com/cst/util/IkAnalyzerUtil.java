package com.cst.util;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.dic.Dictionary;
/**
 * @description:
 * @author: cst
 * @date: Created in 2020/4/20 7:55 下午
 * @version:
 * @modified By:
 */
public class IkAnalyzerUtil {

    /**
     * 敏感过滤
     * @param text 文本内容
     * @return
     */
    public static boolean getIk(String text)
    {

        boolean a=false;
        IKSegmenter ik = new IKSegmenter(new StringReader(text),true);

        Dictionary h=Dictionary.getSingleton();

        List<String> ls=new ArrayList<String>();
        Lexeme lex = null;

        try {
            while((lex=ik.next())!=null){
                if( h.matchInMainDict(lex.getLexemeText().toCharArray()).isMatch()==true)
                {
                    ls.add(lex.getLexemeText());
                    a=true;
                    break ;
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        //转换**
//        for(int i=0;i<ls.size();i++)
//        {
//            String xing="";
//            for(int y=0;y<ls.get(i).length();y++)
//            {
//                xing+="*";
//            }
//            text=text.replace(ls.get(i), xing);
//        }

        return a;
    }





}
