package cn.edu.scau.cmi.os.filemanager;



import cn.edu.scau.cmi.os.filemanager.service.impl.FileOrCatalogManageImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilemanagerApplicationTests {

    @Test
    public void contextLoads() throws IOException {
//        String[] split = ManageUtil.split("a/b/c/d.txt");
//        System.out.println(split[split.length-1]);
//
//
//        FAT fat = new FAT();
//        System.out.println(fat.getCount());
//        fat.allocate(2);
//        fat.add(2,2);
//        System.out.println(fat.getCount());

            FileOrCatalogManageImpl fileManage = new FileOrCatalogManageImpl();
//            fileManage.create_file("aaa.txt","txt");
            fileManage.read_file("aaa.txt",7);
//            fileManage.create_catalog("bbb");
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        fileManage.delete_freeCatalog("bbb");

//            fileManage.create_file("aaa/bbb.txt","写",3);
//            fileManage.create_file("aaa/ccc.txt","写",3);
//            fileManage.create_file("aaa/ddd.txt","写",3);
//            fileManage.dir_catalog("bbb");
//            fileManage.write_file("aaa.txt","skmaksmcfaslkmvcalmcaalsmvvasnvaksvp",7,true);
//            fileManage.close_file("aaa.txt");
//            fileManage.type_file("aaa.txt");
//        fileManage.change("aaa.txt","sss");

    }

}
