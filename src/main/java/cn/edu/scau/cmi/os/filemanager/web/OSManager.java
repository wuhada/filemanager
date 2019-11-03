package cn.edu.scau.cmi.os.filemanager.web;

import cn.edu.scau.cmi.os.filemanager.dao.FAT;
import cn.edu.scau.cmi.os.filemanager.domain.FileOrCatalogAttibutes;
import cn.edu.scau.cmi.os.filemanager.domain.OpenFileTable;
import cn.edu.scau.cmi.os.filemanager.domain.ResultInfo;
import cn.edu.scau.cmi.os.filemanager.service.FileOrCatalogManage;
import cn.edu.scau.cmi.os.filemanager.service.impl.FileOrCatalogManageImpl;
import cn.edu.scau.cmi.os.filemanager.utils.ManageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
public class OSManager {

    private FileOrCatalogManageImpl manage = new FileOrCatalogManageImpl();
    private FAT fat = FAT.getFat();
    //记录当前的目录
    private FileOrCatalogAttibutes catalog = fat.getRoot();

    @PostMapping("/createFile")
    public void  create_file(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        if (parameter.get("attr")[0].equals("1")){
            ri = manage.create_file(parameter.get("filename")[0],"只读文件");
        }else {
            ri = manage.create_file(parameter.get("filename")[0],"可读可写文件");
        }

        System.out.println(ri.getCount() + ri.getErrorMsg());
        System.out.println(parameter.get("attr")[0]);

        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/openFile")
    public void  open_file(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        if (parameter.get("attr")[0].equals("1")){
            ri = manage.open_file(parameter.get("filename")[0],"以读操作方式打开文件");
        }else {
            ri = manage.open_file(parameter.get("filename")[0],"以写操作方式打开文件");
        }

        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
        System.out.println("ssss");
    }

    @PostMapping("/readFile")
    public void  read_file(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        ri = manage.read_file(parameter.get("filename")[0],Integer.valueOf(parameter.get("size")[0]));

        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/writeFile")
    public void  write_file(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;
        boolean flag = true;

        if (parameter.get("clear")[0].equals("1")){
            flag = false;
        }

        ri = manage.write_file(parameter.get("filename")[0],parameter.get("content")[0]
                ,Integer.valueOf(parameter.get("size")[0]),flag);

        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/closeFile")
    public void  close_file(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        ri = manage.close_file(parameter.get("filename")[0]);

        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/deleteFile")
    public void  delete_file(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        ri = manage.delete_file(parameter.get("filename")[0]);

        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/typeFile")
    public void  type_file(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        if(parameter.get("type")[0].equals("write")){
            ri = manage.type_file(parameter.get("filename")[0],"以写操作方式打开文件");
        }else {
            ri = manage.type_file(parameter.get("filename")[0],"以读操作方式打开文件");
        }


        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/change")
    public void  change(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        if (parameter.get("attr")[0].equals("1")){
            ri = manage.change(parameter.get("filename")[0],"只读文件");
        }else {
            ri = manage.change(parameter.get("filename")[0],"可读可写文件");
        }

        System.out.println(ri.getCount() + ri.getErrorMsg());
        System.out.println(parameter.get("attr")[0]);

        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/createCatalog")
    public void  create_catalog(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;


        ri = manage.create_catalog(parameter.get("catalogname")[0]);


        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/deleteFreeCatalog")
    public void  delete_freeCatalog(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        ri = manage.delete_freeCatalog(parameter.get("catalogname")[0]);


        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/dirCatalog")
    public void  dir_catalog(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri;

        ri = manage.dir_catalog(parameter.get("catalogname")[0]);


        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

    @PostMapping("/findFileOrCatalog")
    public void  findFileOrCatalog(HttpServletRequest request,HttpServletResponse response) throws IOException {

        Map<String, String[]> parameter = request.getParameterMap();
        ResultInfo ri = new ResultInfo();
        OpenFileTable openFileTable = new OpenFileTable();
        String find = parameter.get("find")[0];
        System.out.println(find);
//        System.out.println(find);
        String[] split = ManageUtil.split(find);
        if (split[split.length-1].contains(".")){
            int exist = manage.isExist(find, 1);
            if (exist == 1){
                ri.setErrorMsg("抱歉，您所搜索的文件不存在！");
            }else {
                openFileTable.setSize(catalog.getSize());
                openFileTable.setPathName(split[split.length-1]);
                openFileTable.setStartNum(catalog.getStartLocation());
                openFileTable.setPathName(split[split.length-1]);
                openFileTable.setType(catalog.getType());
                ri.setOpenFileTable(openFileTable);
                ri.setErrorMsg(null);
                ri.setFlag(true);
                ri.setFileType(split[split.length-1].split("\\.")[1] + "文件");
            }

        }else {
            int exist = manage.isExist(find, 1);
            if (exist == 1){
                ri.setErrorMsg("抱歉，您所搜索的目录不存在！");
            }else {
                openFileTable.setPathName(split[split.length-1]);
                openFileTable.setStartNum(catalog.getStartLocation());
                openFileTable.setType("文件夹");
                ri.setFlag(false);
                ri.setErrorMsg(null);
                ri.setOpenFileTable(openFileTable);
            }
        }

        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(ri);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(s);
    }

}
