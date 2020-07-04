package com.lxk.controller.center;

import com.lxk.controller.BaseController;
import com.lxk.pojo.Users;
import com.lxk.pojo.bo.center.CenterUserBO;
import com.lxk.resource.FileUpload;
import com.lxk.service.center.CenterUserService;
import com.lxk.utils.CookieUtils;
import com.lxk.utils.DateUtil;
import com.lxk.utils.JsonUtils;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songshiyu
 * @date 2020/7/4 10:41
 **/
@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    public ResultJSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "centerUserBO", value = "表单对象", required = true)
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request, HttpServletResponse response
    ) {

        //判断BingResult是否保存错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorsMap = getErrors(result);
            return ResultJSONResult.errorMap(errorsMap);
        }

        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);
        userResult = setNullProperty(userResult);

        //TODO 后续要改，增加令牌token，会整合进redis，分布式会话
        //设置cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);
        return ResultJSONResult.ok();
    }


    @ApiOperation(value = "修改用户头像", notes = "修改用户头像", httpMethod = "POST")
    @PostMapping("uploadFace")
    public ResultJSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
                    MultipartFile file,
            HttpServletRequest request,HttpServletResponse response
    ) {
        //定义头像保存的位置
        String fileSpace = fileUpload.getImageUserFaceLocation();
        //在路径上为每一个用户增加一个userId，用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;
        //开始进行文件上传
        if (file != null) {
            FileOutputStream fileOutputStream = null;
            try {
                //获得上传文件的名称
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件重命名
                    String[] splits = fileName.split("\\.");
                    //获取文件的后缀
                    String suffix = splits[splits.length - 1];

                    if (!suffix.equalsIgnoreCase("png") &&
                            !suffix.equalsIgnoreCase("jpg")&&
                            !suffix.equalsIgnoreCase("jpeg")){
                        return ResultJSONResult.errorMsg("图片格式不正确");
                    }
                    //文件名重组
                    String newFileName = "face-" + userId + "." + suffix;
                    //上传的头像最终的保存位置
                    String finalFacepath = fileSpace + uploadPathPrefix + File.separator + newFileName;

                    //用于提供给web服务的访问地址
                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finalFacepath);
                    if (outFile.getParentFile() != null) {
                        outFile.getParentFile().mkdirs();
                    }
                    //文件保存到输出目录
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return ResultJSONResult.errorMsg("文件不能为空！");
        }

        //图片服务的地址
        String imageServiceUrl = fileUpload.getImageServiceUrl();
        //由于浏览器可能存在缓存，所以在这里，我们要加上时间戳来保证更新后的图片可以及时的在页面显示
        String finalUserFaceUrl = imageServiceUrl + uploadPathPrefix + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
        //更新用户头像到数据库
        Users userResult = centerUserService.updataUserFace(userId, finalUserFaceUrl);

        //TODO 后续要改，增加令牌token，会整合进redis，分布式会话
        //设置cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);
        return ResultJSONResult.ok();
    }

    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError error : fieldErrors) {
            //发生验证错误的字段
            String errorField = error.getField();
            //验证错误的信息
            String defaultMessage = error.getDefaultMessage();
            map.put(errorField, defaultMessage);
        }
        return map;
    }

    private Users setNullProperty(Users usersResult) {
        usersResult.setPassword(null);
        usersResult.setEmail(null);
        usersResult.setRealname(null);
        usersResult.setMobile(null);
        usersResult.setUpdatedTime(null);
        usersResult.setCreatedTime(null);
        usersResult.setBirthday(null);

        return usersResult;
    }
}
