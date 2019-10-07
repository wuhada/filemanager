package cn.edu.scau.cmi.os.filemanager.dao;

import cn.edu.scau.cmi.os.filemanager.domain.FileOrCatalogAttibutes;

public class FAT {

    /**
     * 文件分配表
     */
    private int[] value = new int[128];

    //将根目录放在fat表的第一项，即第二个位置
    private FileOrCatalogAttibutes root = FileOrCatalogAttibutes.getRoot();

    private static FAT fat;

    private FAT(){

        for (int i=0; i< value.length; i++){
            value[i] = 0;
        }

        value[0] = 126; //表示当前可用的磁盘数
        value[1] = 255; // 该磁盘已被根目录占用
        root.setParent(root); //表示根目录为顶级目录
    }


    /**
     * 分配给文件或目录的磁盘块
     * @param size
     * @return
     */
    public int allocate(int size){

        int[] startLocation = new int[128];
        int j=2;

        for(int i = 0;i < size; j++){
            if(value[j] == 0){
                startLocation[i] = j;
                if(i > 0){
                    value[startLocation[i-1]] = j;  //让上一块磁盘指向下一块磁盘
                }
                i++;
            }
        }

        value[j-1] = 255;//表示结束
        value[0] -= size;

        return startLocation[0];
    }


    /**
     * 当追加内容时对fat表进行修改
     * @param startLocation
     * @param size
     */
    public void add(int startLocation, int size){

        //找到文件结束时的磁盘块
        while(value[startLocation] != 255){
            startLocation = value[startLocation];
        }

        for(int i = 0 , j=2; i < size; j++){

            if(value[j] == 0){
                value[startLocation] = j;
                i++;
                startLocation = j;
                value[startLocation] = 255;
            }
        }

        value[0] -= size;
    }

    /**
     * 删除文件或目录
     * @param startLocation
     */
    public void delete(int startLocation) {

        int temp;

        while(value[startLocation] != 0){
            temp = startLocation;
            startLocation = value[startLocation];
            value[temp] = 0;
            value[0]++;
            if(startLocation == 255) break;
        }
    }

    /**
     * 查找文件在fat的所有位置
     * @param startLocation
     * @return
     */
    public int[] findLocation(int startLocation){
        int[] allLocation = new int[126];
        int count = 0;
        allLocation[count++] = startLocation;

        while (value[startLocation] != 255){
            startLocation = value[startLocation];
            allLocation[count++] = startLocation;
        }
        return allLocation;
    }

    /**
     * 获取当前剩余的磁盘块
     * @return
     */
    public int getCount(){
        return value[0];
    }

    public FileOrCatalogAttibutes getRoot() {
        return root;
    }

    public static FAT getFat(){
        if (fat == null){
            fat = new FAT();
        }
        return fat;
    }

    //
//    /**
//     * 当创建文件时修改文件分配表
//     * @param fileOrCatalogAttibutes
//     */
//    public void createFile(FileOrCatalogAttibutes fileOrCatalogAttibutes){
//
//        int startAt = 2; // 0-1号为系统数据区
//        int flag = 0; // 标志位，表示是否为分配给文件的第一个磁盘块
//        int size = fileOrCatalogAttibutes.getSize(); // 文件大小
//
//        for (int i = startAt; i < value.length; i++){
//
//            if (value[i] == 0 && size != 0) {
//
//                if (flag == 0) {
//                    fileOrCatalogAttibutes.setStartLocation(i);
//                    flag = 1;
//                }else {
//                    value[fileOrCatalogAttibutes.getStartLocation()] = i;
//                }
//
//                size = size - 64;
//                value[0]--;
//
//                if(size <= 0){
//                    value[i] = 255;
//                    break;
//                }
//            }else if (value[i] == 0 && size == 0){ //建立空表
//
//            }
//        }
//    }
//
//    /**
//     * 删除文件时，将文件分配表相应位置清空
//     * @param location
//     */
//    public void deleteFile(int location){
//
//        int temp;
//
//        while(value[location] != 0){
//
//            temp = location;
//            location = value[location];
//            value[temp] = 0;
//            value[0]++;
//        }
//
//    }
//
//
//    /**
//     * 磁盘损坏
//     * @param diskNum
//     */
//    public void destroyDisk(int diskNum) {
//
//        value[diskNum] = (int) (Math.random() * 255 + 128);
//        value[0]++;
//    }
//
//    /**
//     * 创建文件
//     * @param fileName
//     * @param type
//     */
//    public void create_file(String fileName,String type){
//        if (value[0] <= 0) {
//            return;
//        }
//        String[] catalogs = fileName.split("/");
////        System.out.println(Arrays.asList(catalogs));
//    }


}
