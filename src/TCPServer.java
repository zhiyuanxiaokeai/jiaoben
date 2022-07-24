import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 *
 聊天室服务端
 *
 */
public class TCPServer {
    /*
     * java.net.ServerSocket
     *  运行在服务端的ServerSocket主要有两个作用
     * 1：申请服务端口
     * 2：监听服务端口，一旦一个客户端通过该端口建立连接，则创建一个Socket用于与该客户端通讯
     */
    private ServerSocket server;//ServerSocket类实现服务器套接字
    //private ServerSocket server2;
    /*
     * 该集合用来存放所有客户端的输出流，用于将消息广播给所有客户端
     */
    private List<PrintWriter> allOut;//创建列表
    public TCPServer() throws IOException{
        /*
         * 初始化ServerSocket的同时需要指定服务端口
         * 该端口号不能与系统其它应用程序已申请的端口号重复，否则会抛出异常。
         */
        server = new ServerSocket(8088);//创建一个服务器套接字，绑定到8088端口。端口号为0时表示端口号是自动分配的
        //server = new ServerSocket(8089);
        allOut = new ArrayList<PrintWriter>();//构造初始容量为10的PrintWriter文本输出流空列表
    }
    public void start(){
        Socket socket=null;
        try {
            /*
             * ServerSocket提供方法：
             * Socket accept()
             * 该方法会监听ServerSocket申请的服务端口。 这是一个阻塞方法，直到一个客户端通过该端口连接, 才会返回一个Socket。这个返回的Socket是用于与连接的客户端进行通讯的
             */
            while(true){
                System.out.println("等待客户端连接...");
                socket = server.accept();//监听到这个套接字的连接并接受。该方法将阻塞，直到建立连接为止。
                System.out.println("一个客户端连接了！");
                /*
                 * 启动一个线程 与该客户端交互
                 */
                Thread t = new Thread(new ClientHandler(socket));//定义一个线程t执行类ClientHandler定义的不含参数的run实例与指定socket的客户端互交
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    /**
     * 将给定的消息广播给所有客户端
     *
     */
    private void sendMessage(String message){
        synchronized(allOut){
            //列表同步，转发给所有客户端
            for(PrintWriter o: allOut){//对列表的文本输出流o
                o.println(message);
            }
        }
    }

    public static void main(String[] args) {
        try {
            TCPServer server = new TCPServer();//聊天室服务端
            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *与指定客户端互交
     *
     */
    private class ClientHandler implements Runnable{//Runnable接口通过类ClientHandler实现，这个类ClientHandler定义了不含参数的run实例方法通过线程t来执行。
        /*
         *当前线程通过这个Socket与指定客户端交互
         */
        private Socket socket;
        /*
         * 远程计算机地址信息，这里是客户端的地址
         */
        private String host ;
        public ClientHandler(Socket socket){
            this.socket = socket;
            /*
             *通过Socket可以获取远端计算机地址信息
             */
            InetAddress address = socket.getInetAddress();
            /*
             * 获取远端计算机IP地址的字符串格式
             */
            host = address.getHostAddress();
        }
        /**
         * 客户端读取远端数据，类ClientHandler定义的不含参数的run实例方法
         *
         */
        public void run() {
            PrintWriter pw = null;
            try {
                /*
                 * InputStream getInputStream()
                 * Socket提供的该方法可以获取一个输入流
                 * 通过该流客户读取到远端计算机发送过来的数据
                 */
                Scanner sc = new Scanner(System.in);
                InputStream in = socket.getInputStream();//返回套接字输入字节流给in，InputStream将操作委托给通道套接字
                InputStreamReader isr = new InputStreamReader(in,"UTF-8");//从字节流in中读取多个字节转换为字符流到isr，使用指定的名称为UTF-8的字符集，也可以使用平台默认的字符集
                final BufferedReader br = new  BufferedReader(isr);//从字符输入流isr中读取文本到br，缓冲字符提供对字符、数组、和行的有效读取。
                /*
                 * 通过Socket获取输出流，用于将数据发送给客户端
                 */
                OutputStream out = socket.getOutputStream();//返回此套接字的输出流给out，OutputStream将操作委托给通道套接字
                OutputStreamWriter osw = new OutputStreamWriter(out,"UTF-8");//从字符流out中写入多个字符转换成的字节流到osw，使用指定的名称为UTF-8的字符集，也可以使用平台默认的字符集
                pw = new PrintWriter(osw,true);//将格式化后的字节流osw表示输出到文本输出流pw
                System.out.println("请输入系统消息:");
                /*
                 * 将该客户端的输出流存入到共享集合中
                 * 由于多个线程都会调用该集合的add方法向其中添加输出流
                 * ，所以为了保证线程安全，可以将该集合加锁。
                 */
                synchronized(allOut){
                    allOut.add(pw);
                }
                String outMsg = null;
                sendMessage(host+"上线了！当前在线人数为"+allOut.size()+"人");
                while(true){
                    //System.out.println(host+"说："+message);
                    //回复给当前客户端
                    Thread t2 = new Thread(new Runnable(){//定义接口Runnable()的线程t2为类ClientHandler定义的不含参数的run实例与指定socket的客户端互交
                        public void run() {
                            String message = null;
                            try {
                                while((message = br.readLine())!=null){	//从文本br读取消息
                                    System.out.println("["+host+"]"+"说："+message);

                                }
                            } catch (IOException e) {
                                System.out.println("gg");
                                e.printStackTrace();
                            }
                        }
                    }
                    );
                    t2.start();
                    outMsg = sc.nextLine();
                    pw.println(outMsg);
                    System.out.println("你(服务器)说:"+outMsg);
                    //sendMessage(host+"说"+message);
                }
            } catch (Exception e) {

            }finally{
                //处理客户端断开连接以后的工作
                //将该客户端的输出流从共享集合中删除
                synchronized(allOut){
                    allOut.remove(pw);
                }
                sendMessage(host+"下线了");
                if(socket!=null){
                    try {
                        socket.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }
}


