import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class BilibiliBar {
    //楼层
    public static final String CLASSNAME="d_post_content j_d_post_content ";
    //评论正文文本所在的元素（不含楼中楼）
    public static final String PARENT_CLASSNAME="l_post l_post_bright j_l_post clearfix  ";
    //要爬取的页面地址
    public static final String URL_BASE_NAME="https://tieba.baidu.com/p/6063219615";
    public static final String DEFAULT_OUTPUT="Result.md";
    private Queue<SimpleFloor> floors;
    private String url;
    private boolean seeLz;
    private boolean floorInFloor;
    public BilibiliBar() throws IOException {
        this.url=URL_BASE_NAME;
        seeLz=false;
        floorInFloor=false;
        init();
    }
    public BilibiliBar(boolean seeLz,boolean floorInFloor) throws IOException {
        this.url=URL_BASE_NAME;
        this.seeLz=seeLz;
        this.floorInFloor=floorInFloor;
        init();
    }
    public BilibiliBar(String url) throws IOException {
        this.url=url;
        seeLz=false;
        floorInFloor=false;
        init();
    }
    public BilibiliBar(String url,boolean seeLz,boolean floorInFloor) throws IOException {
        this.url=url;
        this.seeLz=seeLz;
        this.floorInFloor=floorInFloor;
        init();
    }
    public void init() throws IOException {
        floors=new LinkedBlockingQueue<SimpleFloor>();
        BufferedWriter reader=forFileInWriter(DEFAULT_OUTPUT,false);
        reader.write("原帖地址"+this.url);
        reader.newLine();
        reader.write("===================================");
        reader.newLine();
        reader.flush();
        this.url=this.url+"?";
        if(seeLz){
            this.url=this.url+"see_lz=1&";
            reader.write("(本文档只收录楼主的回帖记录，不收录一切楼中楼和其它人的楼层)");
            reader.newLine();
            reader.write("-----------------------------------");
            reader.newLine();
            reader.flush();
        }
        this.url=this.url+"pn=";
        reader.close();
    }
    public void analyze() throws IOException {
        BufferedWriter writer=forFileInWriter(DEFAULT_OUTPUT,true);
        int totalPage=Integer.MAX_VALUE;
        for(int nowPage=1;nowPage<=totalPage;nowPage++) {
            String html = getHtmlTextFromURL(this.url+nowPage, "utf-8");
            Document document = Jsoup.parse(html);
            //由于该帖活动可能较为频繁，在爬取过程中可能会更新总页数，故每次翻页都更新totalPage变量。
            Elements totalCommentsLocal=document.select("li[class=\"l_reply_num\"]");
            totalPage=Integer.parseInt(totalCommentsLocal.first().children().eachText().get(1));
            //获取一个页面的所有楼层。
            Elements userModels=document.select("div[class=\""+PARENT_CLASSNAME+"\"]");
            Iterator<Element> modelIterator=userModels.iterator();
            //分析每个楼层。
            while (modelIterator.hasNext()){
                SimpleFloor floorBean=new SimpleFloor();
                Element floor=modelIterator.next();
                Element pAuthor=floor.selectFirst("ul[class=\"p_author\"]");
                //Element authorElement=pAuthor.selectFirst("div[class=\"p_author_face \"]");
                Element authorElement=pAuthor.selectFirst("img");
                String author=authorElement.attr("username");
                floorBean.setAuthor(author);
                System.out.println(floor.child(0).attr("class"));
                Element mainElement=floor.selectFirst("div[class=\"d_post_content_main \"]");
                if(null==mainElement){
                    mainElement=floor.selectFirst("div[class=\"d_post_content_main  d_post_content_firstfloor\"]");
                }
                Element textElement=mainElement.selectFirst("div[class=\""+CLASSNAME+"\"]");
                String text=textElement.text();
                floorBean.setText(text);
                Element wrapElement=mainElement.selectFirst("div[class=\"post-tail-wrap\"]");
                Elements tailInfo=wrapElement.select("span[class=\"tail-info\"]");
                for(Iterator<Element> iterator=tailInfo.iterator();iterator.hasNext();){
                    Element temp=iterator.next();
                    if(temp.text().contains("楼")){
                        floorBean.setFloor(temp.text());
                        System.out.println(temp.text());
                    }
                    if(temp.text().matches("\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d")){
                        floorBean.setTime(temp.text());
                    }
                }
                write(writer,floorBean);
            }
            try {
                Thread.sleep((long) (2000+2000*Math.random()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writer.flush();
        writer.close();
    }
    public void write(BufferedWriter writer,SimpleFloor simpleFloor) throws IOException {
        writer.write("###"+simpleFloor.getFloor()+"("+simpleFloor.getAuthor()+")"+"\t\t"+simpleFloor.getTime());
        writer.newLine();
        String[] texts=simpleFloor.getText().split("\n");
        for(int i=0;i<texts.length;i++){
            writer.write(texts[i]);
            writer.newLine();
        }
        writer.flush();
    }
    public String getHtmlTextFromURL(String s,String charset) throws IOException {
        URL url=new URL(s);
        URLConnection urlConnection=url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/31.0 (compatible; MSIE 10.0; Windows NT; DigExt)");
        InputStream inputStream=urlConnection.getInputStream();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,charset));
        String ss;
        String temp="";
        while ((ss=bufferedReader.readLine())!=null){
            temp=temp+ss;
        }
        bufferedReader.close();
        inputStream.close();
        return temp;
    }
    public BufferedWriter forFileInWriter(String path,boolean isAppend) throws IOException {
        File file=new File(path);
        file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file,isAppend);
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
        BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
        return bufferedWriter;
    }
    public String unicodeToCn(String unicode) {
        String[] strs = unicode.split("\\\\u");
        String returnStr = strs[0];
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            if(strs[i].length()==4) {
                returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
            }else{
                String temp1=strs[i].substring(0,4);
                returnStr += (char) Integer.valueOf(temp1, 16).intValue();
                String temp2=strs[i].substring(4);
                returnStr += temp2;
            }
        }
        return returnStr;
    }
}
