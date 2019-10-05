package cn.edu.scau.cmi.os.filemanager.domain;

public class OpenFileTable {

    /**
     *文件路径名
     */
    private String pathName;
    /**
     *文件属性
     */
    private String type;
    /**
     *起始盘块号
     */
    private int startNum;
    /**
     * 文件长度
     */
    private int size;
    /**
     * 操作类型
     */
    private String managerType;

//    private static OpenFileTable openFileTable;
//
//    private OpenFileTable(){
//    }
//
//    public static OpenFileTable getOpenFileTable(){
//        if (openFileTable==null){
//            openFileTable = new OpenFileTable();
//        }
//        return openFileTable;
//    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getManagerType() {
        return managerType;
    }

    public void setManagerType(String managerType) {
        this.managerType = managerType;
    }
}
