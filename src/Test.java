import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

public class Test {
    public static void main(String[] args) throws IOException {
        BilibiliBar bilibiliBar=new BilibiliBar(true,false);
        bilibiliBar.analyze();
        /*String s="2019-03-11 22:40";
        System.out.println(s.matches("\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d"));*/
    }
}
