package cn.edu.scau.cmi.os.filemanager.service.impl;

import cn.edu.scau.cmi.os.filemanager.dao.FAT;
import cn.edu.scau.cmi.os.filemanager.domain.FileOrCatalogAttibutes;
import cn.edu.scau.cmi.os.filemanager.domain.OpenFileTable;
import cn.edu.scau.cmi.os.filemanager.domain.ResultInfo;

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

    //根目录
    private File root = new File("D:/root/");

    //返回操作的成功与否
    private ResultInfo resultInfo = new ResultInfo();

    @Override
    public ResultInfo create_file(String fileName, String type) throws IOException {

        catalog = fat.getRoot();
        if (!root.exists()){
            root.mkdir();
        }

        int flag = 0;
        int i;
        int startLocation;
        FileOrCatalogAttibutes subCatalog = null;

        if (fat.getCount() >= 1) {
            String[] split = ManageUtil.split(fileName);

            if(split[0].contains("$")){
                System.out.println("空目录，文件创建失败！");
                resultInfo.setCount(fat.getCount());
                resultInfo.setErrorMsg("空目录，文件创建失败！");
                resultInfo.setFlag(false);
                return resultInfo;
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
                resultInfo.setCount(fat.getCount());
                resultInfo.setErrorMsg("创建文件失败，文件目录不存在！");
                resultInfo.setFlag(false);
                return resultInfo;
            }else {
                map = catalog.getTable();
                if (map.size() > 0 && map.size() < 8){
                    FileOrCatalogAttibutes file = map.get(split[split.length-1]);
                    System.out.println(file != null && split[split.length-1].equals(file.getName()));
                    if (file != null && split[split.length-1].equals(file.getName())){
                        System.out.println("文件已存在！");
                        resultInfo.setCount(fat.getCount());
                        resultInfo.setErrorMsg("文件已存在！");
                        resultInfo.setFlag(false);
                        return resultInfo;
                    }
                }else if (map.size() >= 8){
                    System.out.println("该目录下一存满8个文件或子目录，无法继续创建文件！");
                    resultInfo.setCount(fat.getCount());
                    resultInfo.setErrorMsg("该目录下一存满8个文件或子目录，无法继续创建文件！");
                    resultInfo.setFlag(false);
                    return resultInfo;
                }

                startLocation =  fat.allocate(1);
                map.put(split[split.length-1],new FileOrCatalogAttibutes(split[split.length-1],type,startLocation,1,1));
                catalog.setTable(map);
                catalog.setType(type);

                OpenFileTable openFileTable = new OpenFileTable();
                openFileTable.setPathName(fileName);

                if (type.contains("写")){
                    openFileTable.setManagerType("以写操作方式建立文件");
                }else {
                    openFileTable.setManagerType("以读操作方式建立文件");
                }

                openFileTable.setSize(1);
                openFileTable.setType(type);
                openFileTable.setStartNum(startLocation);

                openFileTables.put(fileName,openFileTable);

                File file = new File("D:/root/" + fileName);
                file.createNewFile();
                System.out.println("创建文件成功！" + fat.getCount());
                resultInfo.setCount(fat.getCount());
                resultInfo.setErrorMsg("创建文件成功！");
                resultInfo.setFlag(true);
                resultInfo.setOpenFileTable(openFileTable);
                return resultInfo;
            }
        }else {
            System.out.println("磁盘空间不足，无法创建文件！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("磁盘空间不足，无法创建文件！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
    }

    @Override
    public ResultInfo open_file(String fileName, String type) {

        catalog = fat.getRoot();
        int flag = isOpen(fileName);

        if (flag == 1) {
            System.out.println("文件不存在，打开失败！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件不存在，打开失败！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        if (catalog.getType().equalsIgnoreCase("read") && type.equalsIgnoreCase("write")){
            System.out.println("文件是只读属性，打开失败！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件是只读属性，打开失败！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable != null) {
            System.out.println("文件已经打开！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件已经打开！");
            resultInfo.setFlag(false);
            return resultInfo;
        }

        if (openFileTables.size() >= 5){
            System.out.println("已经打开了5个，无法再打开文件文件！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("已经打开了5个，无法再打开文件文件！");
            resultInfo.setFlag(false);
            return resultInfo;
        }

        openFileTable = new OpenFileTable();

        openFileTable.setStartNum(catalog.getStartLocation());
        openFileTable.setType(catalog.getType());
        openFileTable.setManagerType(type);
        openFileTable.setSize(catalog.getSize());
        openFileTable.setPathName(fileName);

        openFileTables.put(fileName,openFileTable);

        resultInfo.setFlag(true);
        resultInfo.setOpenFileTable(openFileTable);
        resultInfo.setErrorMsg("文件打开成功！");
        resultInfo.setCount(fat.getCount());
        return resultInfo;
    }

    @Override
    public ResultInfo read_file(String fileName, int size) throws FileNotFoundException {
        catalog = fat.getRoot();
        OpenFileTable openFileTable = openFileTables.get(fileName);

        if (openFileTable == null){
            open_file(fileName,"以读操作方式打开文件");
        }

        if (openFileTable.getManagerType().contains("写")){
            System.out.println("读写失败！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件以写方式打开，无法读取！");
            resultInfo.setFlag(false);
            return resultInfo;
        }

        File f = new File("D:/root/" + fileName);
//        BufferedReader reader = null;
        FileInputStream fileInputStream = new FileInputStream(f);
        StringBuffer stringBuffer = new StringBuffer();
        byte[] bytes = new byte[1000];
        int n = -1;
        try {
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),
//                    "GBK"));
            while ((n = fileInputStream.read(bytes,0,size)) != -1){
                stringBuffer.append(new String(bytes,0,n));
            }
//            for (int i=0;i < size;i++){
//                int read = reader.read();
//                if (read == -1) break;
//                stringBuffer.append(new String(read));
//                System.out.println(stringBuffer.toString());
//                System.out.write(read);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
//                reader.close();
                fileInputStream.close();
                resultInfo.setFlag(true);
                resultInfo.setOpenFileTable(openFileTable);
                resultInfo.setErrorMsg("文件内容已经显示在右边方框内！");
                resultInfo.setCount(fat.getCount());
                resultInfo.setContent(stringBuffer.toString());
                return resultInfo;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultInfo;
    }

    @Override
    public ResultInfo write_file(String fileName, String buffer, int size ,boolean flag) {

        catalog = fat.getRoot();
        FileOutputStream fileOutputStream = null;
        OpenFileTable openFileTable = openFileTables.get(fileName);

        if (openFileTable == null){
            open_file(fileName,"以写操作方式打开文件");
        }else {
            String managerType = openFileTable.getManagerType();
            if (!managerType.contains("写")){
                System.out.println("文件不是以写操作的方式打开，无法打开！");

                resultInfo.setCount(fat.getCount());
                resultInfo.setErrorMsg("文件不是以写操作的方式打开，无法打开！");
                resultInfo.setFlag(false);
                return resultInfo;
            }
        }

        if(size > 8){
            isExist(fileName,1);
            if (flag == true){
                if (size % 8 == 0){
                    catalog.setSize(size/8);
                    openFileTable.setSize(size/8);
                    fat.add(catalog.getStartLocation(),size/8);
                }else {
                    catalog.setSize(1 + size/8);
                    openFileTable.setSize(1 + size/8);
                    fat.add(catalog.getStartLocation(),size/8 + 1);
                }
            }else {
                if (size % 8 == 0){
                    catalog.setSize(catalog.getSize() + size/8);
                    openFileTable.setSize(catalog.getSize() + size/8);
                    fat.add(catalog.getStartLocation(),size/8 );
                }else {
                    catalog.setSize(catalog.getSize() + size/8);
                    openFileTable.setSize(catalog.getSize() + size/8);
                    fat.add(catalog.getStartLocation(),size/8 + 1);
                }
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
                resultInfo.setFlag(true);
                resultInfo.setOpenFileTable(openFileTable);
                resultInfo.setErrorMsg("写入文件成功！");
                resultInfo.setCount(fat.getCount());
                return resultInfo;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultInfo;
    }

    @Override
    public ResultInfo close_file(String fileName) {
        catalog = fat.getRoot();
        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable == null){
            System.out.println("文件没有打开，不用关闭！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件没有打开，不用关闭！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        openFileTables.remove(fileName,openFileTable);
        resultInfo.setFlag(true);
        resultInfo.setOpenFileTable(openFileTable);
        resultInfo.setErrorMsg("文件关闭成功！");
        resultInfo.setCount(fat.getCount());
        return resultInfo;
    }

    @Override
    public ResultInfo delete_file(String fileName) {
        catalog = fat.getRoot();
        int flag = isOpen(fileName);
        if (flag == 1){
            System.out.println("文件不存在，删除操作失败！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件不存在，删除操作失败！");
            resultInfo.setFlag(false);
            return resultInfo;
        }

        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable != null){
            System.out.println("文件已打开，删除操作失败！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件已打开，删除操作失败！！");
            resultInfo.setFlag(false);
            return resultInfo;
        }

        File file = new File("D:/root/"+fileName);
        boolean delete = file.delete();

        if (delete == true){
            fat.delete(catalog.getStartLocation());
            System.out.println("文件删除成功！" + fat.getCount());
            openFileTables.remove(fileName,openFileTable);
            catalog = fat.getRoot();
            int exist = isExist(fileName, 0);
            if (exist != 1){
                String[] split = ManageUtil.split(fileName);
                map = catalog.getTable();
                map.remove(split[split.length-1]);
                catalog.setTable(map);
            }
            resultInfo.setFlag(true);
            resultInfo.setOpenFileTable(openFileTable);
            resultInfo.setErrorMsg("文件删除成功！");
            resultInfo.setCount(fat.getCount());
            return resultInfo;
        }
        return resultInfo;
    }

    @Override
    public ResultInfo type_file(String fileName) throws FileNotFoundException {
        catalog = fat.getRoot();
        int flag = isOpen(fileName);
        if (flag == 1){
            System.out.println("文件不存在，无法显示内容！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件不存在，无法读取内容！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable != null){
            System.out.println("文件已打开，无法显示内容！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件已被其他应用打开，无法读取内容！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        File f = new File("D:/root/" + fileName);
//        BufferedReader reader = null;
        FileInputStream fileInputStream = new FileInputStream(f);
        StringBuffer stringBuffer = new StringBuffer();
        try {
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),
//                    "GBK"));
            int read = -1;
            int count = 0;
            byte[] bytes = new byte[256];
            while ((read = fileInputStream.read(bytes,0,256)) != -1){
                stringBuffer.append(new String(bytes,0,read));
                System.out.write(read);
                count++;
                if (count % 8 == 0){
                    System.out.println();
//                    stringBuffer.append("#");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileInputStream.close();
                openFileTables.remove(fileName,openFileTable);
                resultInfo.setFlag(true);
                resultInfo.setOpenFileTable(openFileTable);
                resultInfo.setErrorMsg("文件内容已成功显示在右边方框里！");
                resultInfo.setCount(fat.getCount());
                resultInfo.setContent(stringBuffer.toString());

                return resultInfo;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultInfo;
    }

    @Override
    public ResultInfo change(String fileName,String type) {
        catalog = fat.getRoot();
        int flag = isOpen(fileName);
        System.out.println(catalog.getName());
        if (flag == 1) {
            System.out.println("文件不存在，无法修改！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件不存在，无法修改！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        OpenFileTable openFileTable = openFileTables.get(fileName);
        if (openFileTable != null){
            System.out.println("文件已打开，无法修改！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("文件已打开，无法修改！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        catalog.setType(type);

        openFileTable = new OpenFileTable();

        openFileTable.setStartNum(catalog.getStartLocation());
        openFileTable.setType(catalog.getType());
        openFileTable.setManagerType(type);
        openFileTable.setSize(catalog.getSize());
        openFileTable.setPathName(fileName);

        resultInfo.setFlag(true);
        resultInfo.setOpenFileTable(openFileTable);
        resultInfo.setErrorMsg("文件属性已被修改！");
        resultInfo.setCount(fat.getCount());
        return resultInfo;
    }

    @Override
    public ResultInfo create_catalog(String catalogName) {
        catalog = fat.getRoot();
        if (!root.exists()){
            root.mkdir();
        }
        if (fat.getCount() > 0){
            String[] split = ManageUtil.split(catalogName);
            int flag = isExist(catalogName,0);
            if (flag == 1){
                System.out.println("父目录不存在，创建新目录失败！");
                resultInfo.setCount(fat.getCount());
                resultInfo.setErrorMsg("父目录不存在，创建新目录失败！");
                resultInfo.setFlag(false);
                return resultInfo;
            }else {
                map = catalog.getTable();
                if (map.size() > 0 && map.size() < 8){
                    catalog = map.get(split[split.length-1]);
                    if (catalog != null && split[split.length-1].equalsIgnoreCase(catalog.getName())){
                        System.out.println("目录已存在，无法创建！");
                        resultInfo.setCount(fat.getCount());
                        resultInfo.setErrorMsg("目录已存在，无法创建！");
                        resultInfo.setFlag(false);
                        return resultInfo;
                    }
                }else if (map.size() >= 8){
                    System.out.println("该目录下一存满8个文件或子目录，无法继续创建子目录！");
                    resultInfo.setCount(fat.getCount());
                    resultInfo.setErrorMsg("该目录下一存满8个文件或子目录，无法继续创建子目录！");
                    resultInfo.setFlag(false);
                    return resultInfo;
                }
                map.put(split[split.length-1]
                        ,new FileOrCatalogAttibutes(split[split.length-1],fat.allocate(1)));
                if (catalog != null){
                    catalog.setTable(map);
                }
                File file = new File("D:/root/" + catalogName);
                file.mkdir();
                System.out.println("目录创建成功！");
                resultInfo.setFlag(true);
                resultInfo.setErrorMsg("目录创建成功！");
                resultInfo.setCount(fat.getCount());
                return resultInfo;
            }
        }else {
            System.out.println("磁盘空间不足，无法创建目录！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("磁盘空间不足，无法创建目录！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
    }

    @Override
    public ResultInfo dir_catalog(String catalogName) {
        catalog = fat.getRoot();
        int flag = isExist(catalogName,1);
        if (flag == 1){
            System.out.println("目录不存在！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("目录不存在！");
            resultInfo.setFlag(false);
            return resultInfo;
        }
        map = catalog.getTable();
        Set<Map.Entry<String, FileOrCatalogAttibutes>> entries = map.entrySet();
        Iterator<Map.Entry<String, FileOrCatalogAttibutes>> iterator = entries.iterator();
        StringBuffer stringBuffer = new StringBuffer();
        while (iterator.hasNext()){
            Map.Entry<String, FileOrCatalogAttibutes> next = iterator.next();
            System.out.println(next.getKey());
            stringBuffer.append(next.getKey() + "#");
        }
        resultInfo.setFlag(true);
        resultInfo.setErrorMsg("目录n内容已成功显示在右边方框里！");
        resultInfo.setCount(fat.getCount());
        resultInfo.setContent(stringBuffer.toString());
        return resultInfo;
    }

    @Override
    public ResultInfo delete_freeCatalog(String catalogName) {
        catalog = fat.getRoot();
        int flag = isExist(catalogName,1);
        if (flag == 1){
            System.out.println("目录不存在，删除失败！");
            resultInfo.setCount(fat.getCount());
            resultInfo.setErrorMsg("目录不存在，删除失败！");
            resultInfo.setFlag(false);
            return resultInfo;
        }else {
            if ("root".equalsIgnoreCase(catalog.getName())){
                System.out.println("该目录为根目录，无法删除！");
                resultInfo.setCount(fat.getCount());
                resultInfo.setErrorMsg("该目录为根目录，无法删除！");
                resultInfo.setFlag(false);
                return resultInfo;
            }else if (catalog.getTable().size() > 0){
                System.out.println("该目录非空，无法删除!");
                resultInfo.setCount(fat.getCount());
                resultInfo.setErrorMsg("该目录非空，无法删除!");
                resultInfo.setFlag(false);
                return resultInfo;
            }
            File file = new File("D:/root/" + catalogName);
            boolean delete = file.delete();
            if (delete == true){
                System.out.println("删除目录成功！");
                fat.delete(catalog.getStartLocation());
                catalog = fat.getRoot();
                int exist = isExist(catalogName, 0);
                if (exist != 1){
                    String[] split = ManageUtil.split(catalogName);
                    map = catalog.getTable();
                    map.remove(split[split.length-1]);
                    catalog.setTable(map);
                }
                resultInfo.setFlag(true);
                resultInfo.setErrorMsg("删除目录成功！");
                resultInfo.setCount(fat.getCount());
                return resultInfo;
            }
        }
        return resultInfo;
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
            if (catalog == null){
                catalog = fat.getRoot();
            }
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

