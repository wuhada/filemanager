package cn.edu.scau.cmi.os.filemanager.service;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileOrCatalogManage {

    /**
     * 建立文件
     * @param fileName
     * @param type
     */
    void create_file(String fileName,String type,int size) throws IOException;

    /**
     * 打开文件
     * @param fileName
     * @param type
     */
    void open_file(String fileName,String type);

    /**
     * 读文件
     * @param fileName
     * @param size
     */
    void read_file(String fileName,String type,int size) throws FileNotFoundException;

    /**
     * 写文件
     * @param fileName
     * @param buffer
     * @param size
     */
    void write_file(String fileName,String buffer,int size ,boolean flag);

    /**
     * 关闭文件
     * @param fileName
     */
    void close_file(String fileName);

    /**
     * 删除文件
     * @param fileName
     */
    void delete_file(String fileName);

    /**
     * 显示文件内容
     * @param fileName
     */
    void  type_file(String fileName);

    /**
     * 改变文件属性
     * @param fileName
     */
    void change(String fileName,String type);
    /**
     * 建立目录
     * @param catalogName
     */
    void create_catalog(String catalogName);

    /**
     * 显示目录内容
     * @param catalogName
     */
    void dir_catalog(String catalogName);

    /**
     * 删除空目录
     * @param catalogName
     */
    void delete_freeCatalog(String catalogName);
}


