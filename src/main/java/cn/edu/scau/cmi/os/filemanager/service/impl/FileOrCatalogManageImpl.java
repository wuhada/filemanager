package cn.edu.scau.cmi.os.filemanager.service.impl;

import cn.edu.scau.cmi.os.filemanager.dao.FAT;
import cn.edu.scau.cmi.os.filemanager.domain.FileOrCatalogAttibutes;
import cn.edu.scau.cmi.os.filemanager.domain.OpenFileTable;
import cn.edu.scau.cmi.os.filemanager.service.FileOrCatalogManage;
import cn.edu.scau.cmi.os.filemanager.utils.ManageUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FileOrCatalogManageImpl implements FileOrCatalogManage {


    private FAT fat = FAT.getFat();

    //记录当前的目录
    private FileOrCatalogAttibutes catalog = fat.getRoot();

    private Map<String,FileOrCatalogAttibutes> map = new HashMap<>();

    private Map<String,OpenFileTable> openFileTables = new HashMap<>();

    private File root = new File("D:/root/");

    @Override
    public void create_file(String fileName, String type,int size) throws IOException {
        catalog = fat.getRoot();
        if (!root.exists()){
            root.mkdir();
        }

        int flag = 0;
        int i;
        int startLocation;
        FileOrCatalogAttibutes subCatalog = null;

        if (fat.getCount() >= size) {
            String[] split = ManageUtil.split(fileName);

            if(split[0].contains("$")){
                System.out.println("空目录，文件创建失败！");
                return;
            }

//            if(type.equalsIgnoreCase("read")){
//                System.out.println("文件属性是只读属性，建立失败！");
//                return;
//            }

            int num = split.length;

            for (i=0;i < num-1;i++){
                map = catalog.getTable();
                subCatalog = map.get(split[i]);

                if (subCatalog != null){
                    catalog = subCatalog;
                }else {
                    flag = 1;
                    break;
                }
            }

            if(flag == 1){
                System.out.println("创建文件失败，文件目录不存在！");
                return;
            }else {
                map = catalog.getTable();
                if (map.size() > 0 && map.size() < 8){
                    FileOrCatalogAttibutes file = map.get(split[split.length-1]);
                    if (file != null && split[split.length-1].equals(file.getName())){
                        System.out.println("文件已存在！");
                        return;
                    }
                }else if (map.size() >= 8){
                    System.out.println("该目录下一存满8个文件或子目录，无法继续创建文件！");
                    return;
                }

                startLocation =  fat.allocate(size);
                map.put(split[split.length-1],new FileOrCatalogAttibutes(split[split.length-1],type,startLocation,size,1));
                catalog.setTable(map);

                OpenFileTable openFileTable = new OpenFileTable();
                openFileTable.setPathName(fileName);
                openFileTable.setManagerType("以读写方式创建文件");
                openFileTable.setSize(size);
                openFileTable.setType(type);
                openFileTable.setStartNum(startLocation);

                openFileTables.put(fileName,openFileTable);

                File file = new File("D:/root/" + fileName);
                file.createNewFile();
                System.out.println("创建文件成功！" + fat.getCount());
            }
        }else {
            System.out.println("磁盘空间不足，无法创建文件！");
        }

    }

    @Override
    public void open_file(String fileName, String type) {

        catalog = fat.getRoot();
        int flag = isOpen(fileName);

        if (flag == 1) {
            System.out.println("文件不存在，打开失败！");
            return;
        }
        if (catalog.getType().equalsIgnoreCase("read") && type.equalsIgnoreCase("write")){
            System.out.println("文件是只读属性，打开失败！");
            return;
        }
        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable != null) {
            System.out.println("文件已经打开！");
            return;
        }

        openFileTable.setStartNum(catalog.getStartLocation());
        openFileTable.setType(catalog.getType());
        openFileTable.setManagerType(type);
        openFileTable.setSize(catalog.getSize());
        openFileTable.setPathName(fileName);

        System.out.println("文件打开成功！");
    }

    @Override
    public void read_file(String fileName, String type, int size) throws FileNotFoundException {
        catalog = fat.getRoot();
        OpenFileTable openFileTable = openFileTables.get(fileName);

        if (openFileTable == null){
            open_file(fileName,type);
        }

        if (type.contains("写")){
            System.out.println("读写失败！");
            return;
        }

        File f = new File("D:/root/" + fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),
                    "GBK"));
            for (int i=0;i < size;i++){
                int read = reader.read();
                if (read == -1) break;
                System.out.write(read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void write_file(String fileName, String buffer, int size ,boolean flag) {

        catalog = fat.getRoot();
        FileOutputStream fileOutputStream = null;
        OpenFileTable openFileTable = openFileTables.get(fileName);

        if (openFileTable == null){
            open_file(fileName,"以写方式打开");
        }else {
            String managerType = openFileTable.getManagerType();
            if (!managerType.contains("写")){
                System.out.println("文件不是以写的方式打开，无法打开！");
                return;
            }
        }

        try {
            fileOutputStream = new FileOutputStream("D:/root/" + fileName,flag);
            byte[] bytes = buffer.getBytes();
            fileOutputStream.write(bytes,0,size);
//            fileOutputStream.write('#');
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close_file(String fileName) {
        catalog = fat.getRoot();
        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable == null){
            System.out.println("文件没有打开，不用关闭！");
            return;
        }
        openFileTables.remove(fileName,openFileTable);
        System.out.println("文件关闭成功！");
    }

    @Override
    public void delete_file(String fileName) {
        catalog = fat.getRoot();
        int flag = isOpen(fileName);
        if (flag == 1){
            System.out.println("文件不存在，删除操作失败！");
            return;
        }

        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable != null){
            System.out.println("文件已打开，删除操作失败！");
            return;
        }

        File file = new File("D:/root/"+fileName);
        boolean delete = file.delete();

        if (delete == true){
            fat.delete(catalog.getStartLocation());
            System.out.println("文件删除成功！" + fat.getCount());
        }
    }

    @Override
    public void type_file(String fileName) {
        catalog = fat.getRoot();
        int flag = isOpen(fileName);
        if (flag == 1){
            System.out.println("文件不存在，无法显示内容！");
            return;
        }
        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable != null){
            System.out.println("文件已打开，无法显示内容！");
            return;
        }
        File f = new File("D:/root/" + fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),
                    "GBK"));
            int read = -1;
            int count = 0;
            while ((read = reader.read()) != -1){
                System.out.write(read);
                count++;
                if (count % 8 == 0){
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void change(String fileName,String type) {
        catalog = fat.getRoot();
        int flag = isOpen(fileName);
        System.out.println(catalog.getName());
        if (flag == 1) {
            System.out.println("文件不存在，无法修改！");
            return;
        }
        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable != null){
            System.out.println("文件已打开，无法修改！");
            return;
        }
        catalog.setType(type);
    }

    @Override
    public void create_catalog(String catalogName) {
        catalog = fat.getRoot();
        if (!root.exists()){
            root.mkdir();
        }
        if (fat.getCount() > 0){
            String[] split = ManageUtil.split(catalogName);
            int flag = isExist(catalogName,0);
            if (flag == 1){
                System.out.println("父目录不存在，创建新目录失败！");
                return;
            }else {
                map = catalog.getTable();
                if (map.size() > 0 && map.size() < 8){
                    catalog = map.get(split[split.length-1]);
                    if (catalog != null && split[split.length-1].equalsIgnoreCase(catalog.getName())){
                        System.out.println("目录已存在，无法创建！");
                        return;
                    }
                }else if (map.size() >= 8){
                    System.out.println("该目录下一存满8个文件或子目录，无法继续创建子目录！");
                    return;
                }
                map.put(split[split.length-1]
                        ,new FileOrCatalogAttibutes(split[split.length-1],fat.allocate(1)));
                catalog.setTable(map);
                File file = new File("D:/root/" + catalogName);
                file.mkdir();
                System.out.println("目录创建成功！");
            }
        }else {
            System.out.println("磁盘空间不足，无法创建目录！");
        }
    }

    @Override
    public void dir_catalog(String catalogName) {
        catalog = fat.getRoot();
        int flag = isExist(catalogName,1);
        if (flag == 1){
            System.out.println("目录不存在！");
            return;
        }
        map = catalog.getTable();
        Set<Map.Entry<String, FileOrCatalogAttibutes>> entries = map.entrySet();
        Iterator<Map.Entry<String, FileOrCatalogAttibutes>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, FileOrCatalogAttibutes> next = iterator.next();
            System.out.println(next.getKey());
        }
    }

    @Override
    public void delete_freeCatalog(String catalogName) {
        catalog = fat.getRoot();
        int flag = isExist(catalogName,1);
        if (flag == 1){
            System.out.println("目录不存在，删除失败！");
            return;
        }else {
            if ("root".equalsIgnoreCase(catalog.getName())){
                System.out.println("该目录为根目录，无法删除！");
                return;
            }else if (catalog.getTable().size() > 0){
                System.out.println("该目录非空，无法删除!");
                return;
            }
            File file = new File("D:/root/" + catalogName);
            boolean delete = file.delete();
            if (delete == true){
                System.out.println("删除目录成功！");
            }
        }
    }

    public int isOpen(String fileName){
        int flag = 0;
        int i;
        FileOrCatalogAttibutes subCatalog;
        String[] split = ManageUtil.split(fileName);
        int num = split.length;

        for (i = 0; i < num; i++) {
            map = catalog.getTable();
            subCatalog = map.get(split[i]);

            if (subCatalog != null) {
                catalog = subCatalog;
            } else {
                flag = 1;
                break;
            }
        }
        return flag;
    }

    public int isExist(String catalogName,int next){
        int flag = 0;
        String[] split = ManageUtil.split(catalogName);
        int length = split.length;
        if (next == 0){
            length--;
        }
        for (int i=0;i<length;i++){
            map = catalog.getTable();
            catalog = map.get(split[i]);
            if (catalog == null){
                flag = 1;
                break;
            }
        }
        return flag;
    }
}

