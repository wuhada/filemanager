package cn.edu.scau.cmi.os.filemanager.domain;

public class ResultInfo {

    private boolean flag;
    private String errorMsg;
    private int count;
    private OpenFileTable openFileTable;
    private String content;
    private String fileType;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public OpenFileTable getOpenFileTable() {
        return openFileTable;
    }

    public void setOpenFileTable(OpenFileTable openFileTable) {
        this.openFileTable = openFileTable;
    }
}
