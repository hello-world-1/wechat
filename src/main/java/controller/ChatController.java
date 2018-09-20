package controller;

import com.alibaba.fastjson.JSONObject;
import entity.User;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import util.FileUploadingUtil;
import websocket.MyWebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Controller
public class ChatController {


    private static final Logger logger = LoggerFactory
            .getLogger(ChatController.class);

    //单文件上传
    @RequestMapping(value = "/file_upload", method = {RequestMethod.POST, RequestMethod.GET})
    public void queryFileData(
            HttpServletRequest request, HttpServletResponse response) {
        iniFileDir(request);
        try {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            Map<String, String> uploadedFiles = FileUploadingUtil.upload(mRequest.getFileMap());
            Iterator<Map.Entry<String, String>> iter = uploadedFiles.entrySet().iterator();
            String filepath = "/files/";
            while (iter.hasNext()) {
                Map.Entry<String, String> each = iter.next();
                System.out.print("Uploaded File Name = " + each.getKey());
                System.out.println(", Saved Path in Server = " + each.getValue());
                filepath = filepath +  each.getKey();
            }
            logger.info("文件进行串串了");
            JSONObject jsonObject = new JSONObject();  //创建Json对象
            jsonObject.put("success", "true");         //设置Json对象的属性
            jsonObject.put("file_path", filepath);
            System.out.println(jsonObject.toString());
            response.getWriter().write(jsonObject.toString());
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void iniFileDir(HttpServletRequest request) {
        FileUploadingUtil.FILEDIR = request.getSession().getServletContext().getRealPath("/") + "files/";
        if (FileUploadingUtil.FILEDIR == null) {
            FileUploadingUtil.FILEDIR = request.getSession().getServletContext().getRealPath("/") + "files/";
        }
    }


}
