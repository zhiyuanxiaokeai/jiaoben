import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;//无法确定主机IP地址时抛出异常
import java.util.Scanner;
/**
 * 聊天室客户端
 *
 */
public class TCPClient {
 Boolean ZZ=true;
    String line="";
    /*
     * java.net.Socket
     * 封装了TCP通讯协议，使用它与远程计算机进行网络通讯
     */
    private Socket socket;//实现客户端套接字，实际工作是由Socketlmpl类的一个实例执行的
    /**
     * 构造方法，用来初始化客户端
     * @throws IOException
     * @throws UnknownHostException
     */
    public TCPClient() throws UnknownHostException, IOException{
        /*
         * 实例化Socket是需要传入两个参数
         * 1：服务端IP地址
         * 2：服务端端口（0~65535）
         * 通过IP地址可以找到网络上的服务端所在的计算机
         * 通过端口可以连接到该计算机上的服务端应用程序
         * 实例化Socket的过程就是建立连接的过程，所以若连接服务端失败，这里会抛出异常。
         */
        System.out.println("正在与服务端建立连接...");
        socket = new Socket("localhost",8088);//创建一个流套接字，并将其连接到localhost上的8088端口。
        System.out.println("与服务端连接成功");
    }
    /**
     * 客户端的启动方法，从这里开始执行客户端逻辑
     */
    public void start() {
        try {
            Scanner sc = new Scanner(System.in);
            OutputStream out = socket.getOutputStream();//返回此套接字的输出流给out，OutputStream将操作委托给通道套接字
            OutputStreamWriter osw = new OutputStreamWriter(out,"utf-8");
            //从字符流out中写入多个字符转换成的字节流到osw，使用指定的名称为UTF-8的字符集，也可以使用平台默认的字符集
            PrintWriter pw = new PrintWriter(osw,true);//将osw字节流格式化表示输出到文本输出流pw
            ServerHandler handler = new ServerHandler();//接收客户端消息输出到客户端控制台的线程
            Thread t = new Thread(handler);//创建新的线程
            t.start();//启动读取服务端发送过来消息的线程
            System.out.println("开始聊天吧！");
            while(true){
                line = sc.nextLine();//此方法返回当前行的其余部分，不包括末尾的任何行解析器。位置设置为下一行的开头。
//                line="222";
                System.out.println("你说:"+line);
                pw.println(line);//文本输出
            }
        } catch (Exception e) {

        }
    }
    public static void main(String[] args) {
        try {
            TCPClient client = new TCPClient();//使用初始化的TCPClient()方法
            client.start();//客户端启用方法
        } catch (Exception e) {
            e.printStackTrace();//错误回溯
            System.out.println("客户端启动失败！");
        }
    }
    /**
     * 该线程用来循环接收服务端发送过来的消息并输出到客户端自己的控制台上
     */
    private class ServerHandler implements Runnable{
        public void run() {
            try {
                OutputStream out = socket.getOutputStream();//返回此套接字的输出流给out，OutputStream将操作委托给通道套接字
                OutputStreamWriter osw = new OutputStreamWriter(out,"utf-8");
                //从字符流out中写入多个字符转换成的字节流到osw，使用指定的名称为UTF-8的字符集，也可以使用平台默认的字符集
                PrintWriter pw = new PrintWriter(osw,true);//将osw字节流格式化表示输出到文本输出流pw
                InputStream in = socket.getInputStream();//返回套接字输入字节流给in，InputStream将操作委托给通道套接字
                InputStreamReader isr = new InputStreamReader(in,"UTF-8");//从字节流in中读取多个字节转换为字符流到isr，使用指定的名称为UTF-8的字符集，也可以使用平台默认的字符集
                BufferedReader br = new BufferedReader(isr);//从字符输入流isr中读取文本到br，缓冲字符提供对字符、数组、和行的有效读取。
                String message = null;
                while((message = br.readLine())!=null){//从字符文本集中获取消息文本
                    System.out.println("[服务端]说:"+message);
                    if(message.equals("启动程序")){
                        ZZ=true;
                        try{
                            Runtime rer=Runtime.getRuntime();
                            rer.exec("D:\\Navicat Premium 15\\navicat.exe");
                            System.out.println("启动成功");
                            line="启动成功";
                            pw.println(line);//文本输出
                        }
                        catch(Throwable t){
                            System.out.println(t.getMessage());
                            System.out.println("启动失败");
                            line="启动失败";
                            pw.println(line);//文本输出
                        }
                    }
                    if(message.equals("关闭程序")){
                        ZZ=true;
                        try{
                            System.out.println("强制关闭");
                            Runtime rer2=Runtime.getRuntime();
                            String command = "cmd.exe /c c:\\windows\\system32\\taskkill  /f /im  navicat.exe";
                            rer2.exec(command);
                            System.out.println("关闭成功");
                            line="关闭成功";
                            pw.println(line);//文本输出
                        }
                        catch(Throwable t){
                            System.out.println(t.getMessage());
                            System.out.println("异常关闭");
                            line="异常关闭";
                            pw.println(line);//文本输出
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
