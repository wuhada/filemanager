package cn.edu.scau.cmi.os.filemanager.service;

import cn.edu.scau.cmi.os.filemanager.domain.ResultInfo;
import com.sun.org.apache.regexp.internal.RE;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileOrCatalogManage {

    /**
     * 建立文件
     * @param fileName
     * @param type
     */
    ResultInfo create_file(String fileName, String type) throws IOException;

    /**
     * 打开文件
     * @param fileName
     * @param type
     */
    ResultInfo open_file(String fileName,String type);

    /**
     * 读文件
     * @param fileName
     * @param size
     */
    ResultInfo read_file(String fileName,int size) throws FileNotFoundException;

    /**
     * 写文件
     * @param fileName
     * @param buffer
     * @param size
     */
    ResultInfo write_file(String fileName,String buffer,int size ,boolean flag);

    /**
     * 关闭文件
     * @param fileName
     */
    ResultInfo close_file(String fileName);

    /**
     * 删除文件
     * @param fileName
     */
    ResultInfo delete_file(String fileName);

    /**
     * 显示文件内容
     * @param fileName
     */
    ResultInfo  type_file(String fileName,String type) throws FileNotFoundException;

    /**
     * 改变文件属性
     * @param fileName
     */
    ResultInfo change(String fileName,String type);
    /**
     * 建立目录
     * @param catalogName
     */
    ResultInfo create_catalog(String catalogName);

    /**
     * 显示目录内容
     * @param catalogName
     */
    ResultInfo dir_catalog(String catalogName);

    /**
     * 删除空目录
     * @param catalogName
     */
    ResultInfo delete_freeCatalog(String catalogName);
}


