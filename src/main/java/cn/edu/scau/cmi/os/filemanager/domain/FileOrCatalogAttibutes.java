package cn.edu.scau.cmi.os.filemanager.domain;


import java.util.HashMap;
import java.util.Map;

public class FileOrCatalogAttibutes {
    /**
     * 文件的名字
     */
    private String name;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 文件属性
     */
    private int attribute;
    /**
     * 文件在FAT开始位置
     */
    private int startLocation;
    /**
     * 文件大小
     */
    private int size;
    /**
     * 用来记录文件或者目录的父目录
     */
    private FileOrCatalogAttibutes parent = null;

    /**
     * 存储目录的信息
     */
    private Map<String, FileOrCatalogAttibutes> table = new HashMap<>();

    private static FileOrCatalogAttibutes root;

    public FileOrCatalogAttibutes() {}

    /**
     * 文件登记项
     * @param name
     * @param type
     * @param startLocation
     * @param size
     * @param attribute
     */
    public FileOrCatalogAttibutes(String name, String type, int startLocation, int size,int attribute){
        this.name = name;
        this.type = type;
        this.startLocation = startLocation;
        this.size = size;
        this.attribute = attribute;
    }

    /**
     * 目录登记项
     * @param name
     * @param startLocation
     */
    public FileOrCatalogAttibutes(String name,int startLocation){
        this.name = name;
        this.startLocation = startLocation;
        this.size = 1;
    }

    private FileOrCatalogAttibutes(String name,int startLocation,String root){
        this.name = name;
        this.startLocation = startLocation;
        this.size = 1;
    }


    public static FileOrCatalogAttibutes getRoot(){
        if (root == null){
            root = new FileOrCatalogAttibutes("root",1,"root");
        }
        return root;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(int startLocation) {
        this.startLocation = startLocation;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public FileOrCatalogAttibutes getParent() {
        return parent;
    }

    public void setParent(FileOrCatalogAttibutes parent) {
        this.parent = parent;
    }

    public Map<String, FileOrCatalogAttibutes> getTable() {
        return table;
    }

    public void setTable(Map<String, FileOrCatalogAttibutes> table) {
        this.table = table;
    }
}
