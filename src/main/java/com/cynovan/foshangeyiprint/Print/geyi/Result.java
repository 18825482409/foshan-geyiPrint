package com.cynovan.foshangeyiprint.Print.geyi;

public class Result {
    private boolean flag;
    private String data;

    public Result(boolean flag, String data) {
        this.flag = flag;
        this.data = data;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "flag=" + flag +
                ", data='" + data + '\'' +
                '}';
    }
}
