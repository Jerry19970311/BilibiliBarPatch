import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageDownload {
    public static void main(String[] args){
        File dir=new File("image");
        File[] imgs=dir.listFiles();
        for(int i=0;i<imgs.length;i++){
            System.out.println(imgs[i].getName());
            downloadPicture(imgs[i].getName());
        }
    }
    public static String downloadPicture(String imgname) {
        String urlList="https://imgsa.baidu.com/forum/pic/item/"+imgname;
        URL url = null;
        int imageNumber = 0;

        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            String imageName =  "originalimage/"+imgname;
            System.out.println(imageName);

            File file=new File(imageName);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            byte[] context=output.toByteArray();
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            return imageName;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
