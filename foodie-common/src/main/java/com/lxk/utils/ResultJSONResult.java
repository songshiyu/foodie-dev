package com.lxk.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author songshiyu
 * @date 2020/6/17 22:10
 *
 * @description: 自定义相应数据结构
 *                 本类可提供给H5/ios/安卓/公众号/小程序使用
 *                 前端接受此类数据后，可自行根据业务区实现相关功能。
 *
 *                 200： 表示成功
 *                 500： 表示错误，错误信息在msg字段中
 *                 501： bean验证错误，不管多少个错误都以map形式返回
 *                 502： 拦截器拦截到用户token出错
 *                 555： 抛出异常信息
 *                 556： 用户qq校验异常
 *                 557:  校验用户是否在CAS端登录
 **/
public class ResultJSONResult {

    /**定义jackson对象*/
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**响应业务状态*/
    private Integer status;

    /**响应消息*/
    private String msg;

    /**响应中的数据*/
    private Object data;

    @JsonIgnore
    /**不使用*/
    private String ok;

    public ResultJSONResult() {

    }

    public ResultJSONResult(Integer status, String msg, Object data, String ok) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.ok = ok;
    }

    public ResultJSONResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public ResultJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static ResultJSONResult build(Integer status, String msg, Object data){
        return new ResultJSONResult(status,msg,data);
    }

    public static ResultJSONResult build(Integer status, String msg, Object data, String ok) {
        return new ResultJSONResult(status, msg, data, ok);
    }

    public static ResultJSONResult ok(Object data) {
        return new ResultJSONResult(data);
    }

    public static ResultJSONResult ok() {
        return new ResultJSONResult(null);
    }

    public static ResultJSONResult errorMsg(String msg) {
        return new ResultJSONResult(500, msg, null);
    }

    public static ResultJSONResult errorMap(Object data) {
        return new ResultJSONResult(501, "error", data);
    }

    public static ResultJSONResult errorTokenMsg(String msg) {
        return new ResultJSONResult(502, msg, null);
    }

    public static ResultJSONResult errorException(String msg) {
        return new ResultJSONResult(555, msg, null);
    }

    public static ResultJSONResult errorUserQQ(String msg) {
        return new ResultJSONResult(556, msg, null);
    }

    public static ResultJSONResult errorUserTicket(String msg) {
        return new ResultJSONResult(557, msg, null);
    }

    public static ObjectMapper getMAPPER() {
        return MAPPER;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }
}
