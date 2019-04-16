package everything.cmd;

//写个入口程序

import everything.core.EverythingManager;
import everything.core.model.Condition;

import java.util.Scanner;

public class EverythingHsqCmdApplication {
    public static void main(String[] args) throws InterruptedException {
        //EverythingManager
        EverythingManager manager = new EverythingManager();
        //        //Scanner
        Scanner scanner = new Scanner(System.in);
        //用户交互输入
        System.out.println("欢迎使用everything-hsq");
        while (true){
            System.out.println(">>");
            String line = scanner.nextLine();
            switch (line){
                case "help":{
                    manager.help();
                    break;
                }
                case "index":{
                    manager.buildIndex();
                    break;
                }
                case "quit":{
                    manager.quit();
                    break;
                }
                default:{
                    if (line.startsWith("search")){
                        //解析参数
                        String[] segments = line.split(" ");
                        if (segments.length>=2){
                            Condition condition = new Condition();
                            String name = segments[1];
                            condition.setName(name);
                            if (segments.length>=3){
                                String type = segments[2];
                                condition.setFileType(type.toUpperCase());
                            }
                            manager.search(condition).forEach(thing->{
                                System.out.println(thing.getPath());
                            });
                        }else{
                            manager.help();
                        }
                    }else {
                        manager.help();
                    }
                }
            }
        }

    }
}
