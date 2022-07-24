import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        try{
            Runtime rer=Runtime.getRuntime();
            rer.exec("D:\\Navicat Premium 15\\navicat.exe");
            System.out.println("启动成功");
        }
        catch(Throwable t){
            System.out.println(t.getMessage());
            System.out.println("启动失败");
        }
        System.out.println("正在运行");
        Thread.currentThread().sleep(10000);
        try{
            System.out.println("强制关闭");
            Runtime rer2=Runtime.getRuntime();
            String command = "cmd.exe /c c:\\windows\\system32\\taskkill  /f /im  navicat.exe";
            rer2.exec(command);
            System.out.println("关闭成功");
        }
        catch(Throwable t){
            System.out.println(t.getMessage());
            System.out.println("异常关闭");
        }
        System.out.println("程序结束了");
    }

}