package cn.edu.scau.cmi.os.filemanager.utils;

public class ManageUtil {

    /**
     * 划分出子目录与文件
     * @param name
     * @return
     */
    public static  String[] split(String name){

        String[] subString = name.split("/");
        return subString;

    }
}
